/**
 * Copyright (c) 2011-2014 SmeshLink Technology Corporation.
 * All rights reserved.
 * 
 * This file is part of the Misty, a sensor cloud for WSN.
 */
package com.smeshlink.misty.service.channel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.smeshlink.misty.formatter.JSONFormatter;
import com.smeshlink.misty.service.ICredential;
import com.smeshlink.misty.service.IServiceRequest;
import com.smeshlink.misty.service.IServiceResponse;
import com.smeshlink.misty.service.JsonRequest;
import com.smeshlink.misty.service.JsonResponse;
import com.smeshlink.misty.service.MistyService;

/**
 * TCP channel.
 * 
 * @author Longshine
 * 
 */
public class TcpChannel implements IServiceChannel {
	private String host;
	private int port = 9011;
	private InetSocketAddress address;
	private int retryInterval = 10000;
	private int maxPoolSize = 1;
	private List pool = new ArrayList();
	private JSONFormatter formatter = new JSONFormatter();
	private IRequestListener requestListener;
	private int timeout = 3000;
	
	public TcpChannel(String host) {
        setHost(host);
    }
	
	public void setRequestListener(IRequestListener listener) {
		requestListener = listener;
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout < 0 ? 0 : timeout;
	}
	
	public int getTimeout() {
		return timeout;
	}
	
	public synchronized IServiceResponse execute(IServiceRequest request) {
		Connector connector = getConnector();
		
		request.getHeaders().put(MistyService.HEADER_CONTENT_TYPE,
				MistyService.getContentType(request.getFormat()));
        request.getHeaders().put(MistyService.HEADER_USER_AGENT,
        		MistyService.VERSION);
        
        ICredential cred = request.getCredential();
        if (cred != null) {
        	ICredential.Pair pair = cred.getCredential();
        	request.getHeaders().put(pair.getKey(), pair.getValue());
        }
		
		Pair pair = connector.execute(request);
		connector.free();
		
		return pair.getResponse();
	}
	
	private synchronized Connector getConnector() {
		while (true) {
			for (Iterator it = pool.iterator(); it.hasNext(); ) {
				Connector c = (Connector) it.next();
				if (c.isFree() && c.isConnected()) {
					c.take();
					return c;
				}
			}
			
			if (pool.size() < maxPoolSize) {
				final Connector c = new Connector();
				pool.add(c);
				Thread t = new Thread(c);
				t.setDaemon(true);
				t.start();
				
				if (c.isConnected()) {
					c.take();
					return c;
				}
			}
			
			try {
				wait(3000);
			} catch (InterruptedException e) {
				break;
			}
		}
		return null;
	}
	
	private synchronized void removeConnector(Connector c, Exception ex) {
		System.out.println("Removing connector " + ex.getMessage());
		try {
			c.close();
		} catch (IOException e) {
			System.out.println("Removing connector " + e);
		}
		pool.remove(c);
	}
	
	private synchronized void wakeup() {
		notifyAll();
	}

	private boolean waitRetry() {
		if (retryInterval > 0) {
			try {
				Thread.sleep(retryInterval);
			} catch (InterruptedException ex) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
	
	class Pair {
		IServiceRequest request;
		private IServiceResponse response;
		
		public Pair(IServiceRequest request) {
			this.request = request;
		}
		
		public synchronized IServiceResponse getResponse() {
			if (response == null) {
				notifyAll();
				try {
					wait(timeout);
				} catch (InterruptedException e) {
					// ignore
				}
			}
			return response;
		}
		
		public synchronized void setResponse(IServiceResponse response) {
			this.response = response;
			notifyAll();
		}
	}
	
	class Connector implements Runnable {
		private Socket socket;
		private boolean connected = false;
		private boolean free = true;
		private Map waitingRequests = new HashMap();
		
		public boolean isFree() {
			return free;
		}
		
		public boolean isConnected() {
			return connected;
		}
		
		public void take() {
			free = false;
		}
		
		public void free() {
			free = true;
			wakeup();
		}
		
		private synchronized void setResponse(IServiceResponse response) {
			Pair pair = (Pair) waitingRequests.remove(response.getToken());
			if (pair != null)
				pair.setResponse(response);
		}
		
		private void processRequest(IServiceRequest request) {
			if (requestListener != null) {
				IServiceResponse response = requestListener.process(request);
				if (response != null) {
					if (response.getToken() == null)
						response.setToken(request.getToken());
					send(response);
				}
			}
		}
		
		private IoBuffer packetBuffer = IoBuffer.allocate(10240).setAutoExpand(true);
		private byte[] byteBuffer = new byte[1];
        private int counter;
        
        public void close() throws IOException {
        	if (socket != null)
        		socket.close();
        }
        
		public void run() {
			while (true) {
				System.out.println("Connecting to " + address);
				try {
					socket = new Socket();
					socket.connect(address);
				} catch (IOException ex) {
					socket = null;
				}
				
				if (socket == null) {
					if (waitRetry())
						continue;
				} else {
					connected = true;
				}
				
				break;
			}
			
			if (connected) {
				free();
				
				InputStream in;
				try {
					in = socket.getInputStream();
				} catch (IOException ex) {
					removeConnector(this, ex);
					return;
				}
				
				while (!socket.isClosed()) {
					try {
						int bytesToRead = in.available();
						if (bytesToRead == 0)
							bytesToRead = 1;
						for (int i = 0; i < bytesToRead; i++) {
							int read = in.read(byteBuffer, 0, 1);
							if (read < 0)
								throw new IOException("EOF");
							else if (read == 0)
								continue;
							
							packetBuffer.put(byteBuffer[0]);
							
							if ('{' == byteBuffer[0]) {
								counter++;
							} else if ('}' == byteBuffer[0]) {
								counter--;
								
								if (counter == 0) {
									packetBuffer.flip();
									String json = new String(packetBuffer.array(), packetBuffer.arrayOffset(), packetBuffer.limit(), "utf-8");
									JSONObject jsonObj = new JSONObject(json);
									if (jsonObj.has("status"))
										setResponse(new JsonResponse(jsonObj));
									else if (jsonObj.has("method"))
										processRequest(new JsonRequest(jsonObj));
									packetBuffer.clear();
									break;
								}
							}
						}
					} catch (IOException ex) {
						removeConnector(this, ex);
					}
				}
			}
		}
		
		public synchronized Pair execute(IServiceRequest request) {
			Pair pair = new Pair(request);
			waitingRequests.put(request.getToken(), pair);
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			formatter.format(stream, request);
			
			try {
				socket.getOutputStream().write(stream.toByteArray());
			} catch (IOException ex) {
				removeConnector(this, ex);
				return null;
			}
			
			return pair;
		}
		
		private synchronized void send(IServiceResponse response) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			formatter.format(stream, response);
			
			try {
				socket.getOutputStream().write(stream.toByteArray());
			} catch (IOException ex) {
				removeConnector(this, ex);
			}
		}
	}
	
	public static int toInt32B(byte[] value, int offset, int length) {
        return toInt(value, offset, length, false);
    }
	
	public static int toInt(byte[] value, int offset, int length, boolean littleEndian) {
		int iOutcome = 0;
        
        if (littleEndian) {
        	for (int i = offset + length - 1; i >= offset; i--) {
                if (i < value.length)
                    iOutcome = (iOutcome << 8) + (value[i] & 0xFF);
            }
        } else {
        	for (int i = 0; i < length; i++) {
        		if (offset + i >= value.length)
                    break;
                iOutcome = (iOutcome << 8) + (value[offset + i] & 0xFF);
            }
        }
        
        return iOutcome;
	}
	
	public static byte[] toBytesB(int value, int bytesLength) {
        return toBytes(value, bytesLength, false);
    }
	
	public static byte[] toBytes(int value, int bytesLength, boolean littleEndian) {
        byte[] bs = new byte[bytesLength];

        if (littleEndian) {
            for (int i = 0; i < bytesLength; i++) {
                bs[i] = (byte)(value & 0xFF);
                value >>= 8;
            }
        } else {
            for (int i = bytesLength - 1; i >= 0; i--) {
                bs[i] = (byte)(value & 0xFF);
                value >>= 8;
            }
        }
        
        return bs;
    }

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		if (host == null) {
			this.host = null;
		} else {
			int index = host.indexOf(':');
			if (index < 0)
				this.host = host;
			else {
				this.host = host.substring(0, index);
				this.port = Integer.parseInt(host.substring(index + 1));
			}
			
			if (this.port != 0)
				address = new InetSocketAddress(this.host, this.port);
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
		if (host != null && port != 0)
			address = new InetSocketAddress(host, port);
	}
	
	public InetSocketAddress getAddress() {
		return address;
	}
	
	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}
}
