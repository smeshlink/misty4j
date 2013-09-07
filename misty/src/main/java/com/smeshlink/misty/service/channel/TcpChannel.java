/**
 * Copyright (c) 2011-2013 SmeshLink Technology Corporation.
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import com.smeshlink.misty.entity.Feed;
import com.smeshlink.misty.entity.User;
import com.smeshlink.misty.formatter.JSONFormatter;
import com.smeshlink.misty.service.IServiceRequest;
import com.smeshlink.misty.service.IServiceResponse;
import com.smeshlink.misty.service.JsonResponse;
import com.smeshlink.misty.service.QueryOption;
import com.smeshlink.misty.service.ServiceException;
import com.smeshlink.misty.service.ServiceRequestImpl;
import com.smeshlink.misty.util.Base64;

/**
 * @author Longshine
 * 
 */
public class TcpChannel implements IServiceChannel {
	private String host = "api.misty.smeshlink.com";
	private int port = 9011;
	private InetSocketAddress address;
	private String apiKey;
	private String username;
	private String password;
	private String auth;
	private int retryInterval = 10000;
	private int maxPoolSize = 1;
	private List pool = new ArrayList();
	private JSONFormatter formatter = new JSONFormatter();
	
	public TcpChannel(String username, String password) {
		this.username = username;
		this.password = password;
		prepareUsernamePasswordCredentials();
	}
	
	public IFeedService feed() {
		return new FeedService(null);
	}

	public IFeedService feed(User user) {
		return new FeedService(user.getUsername());
	}

	public IFeedService feed(Feed parent) {
		return new FeedService(parent == null ? null : ("/feeds/" + parent.getPath()));
	}
	
	private IServiceResponse execute(IServiceRequest request) throws IOException {
		Connector connector = getConnector();
		IServiceResponse resp = connector.execute(request);
		connector.free();
		return resp;
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
	
	class Connector implements Runnable {
		private Socket socket;
		private boolean connected = false;
		private boolean free = true;
		private IServiceResponse response;
		
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
			this.response = response;
			notifyAll();
		}
		
		private synchronized IServiceResponse getResponse() {
			if (response == null) {
				notifyAll();
				try {
					wait(3000);
				} catch (InterruptedException e) {
					// ignore
				}
			}
			return response;
		}
		
		private byte[] packetBuffer = new byte[10000];
		private int packetBufferIndex;
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
							int read = in.read(packetBuffer, packetBufferIndex, 1);
							if (read < 0)
								throw new IOException("EOF");
							else if (read == 0)
								continue;
							
							if ('{' == packetBuffer[packetBufferIndex]) {
								counter++;
							} else if ('}' == packetBuffer[packetBufferIndex]) {
								counter--;
								
								if (counter == 0) {
									String json = new String(packetBuffer, 0, packetBufferIndex + 1, "utf-8");
									JSONObject jsonObj = new JSONObject(json);
									setResponse(new JsonResponse(jsonObj));
									packetBufferIndex = 0;
									break;
								}
							}
							
							packetBufferIndex++;
						}
					} catch (IOException ex) {
						removeConnector(this, ex);
					}
				}
			}
		}
		
		public synchronized IServiceResponse execute(IServiceRequest request) {
			response = null;
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			formatter.format(stream, request);
			
			try {
				socket.getOutputStream().write(stream.toByteArray());
			} catch (IOException ex) {
				removeConnector(this, ex);
				return null;
			}
			
			return getResponse();
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
	
	class FeedService extends AbstractFeedService {
		
		public FeedService(String context) {
			super(context);
		}

		public Feed find(String path, QueryOption opt) throws ServiceException {
			String url = getContext() + "/" + path;
			
			IServiceRequest request = newRequest("GET", url, null);
			
			// TODO add query option
			
			IServiceResponse response = null;
			try {
				response = execute(request);
			} catch (IOException e) {
				ServiceException.error(e);
			}
			
			if (response == null) {
				ServiceException.timeout("Timeout");
			} else if (response.getStatus() == 200) {
				return (Feed) response.getBody();
			} else if (response.getStatus() == 404) {
				return null;
			} else {
				throw new ServiceException(response.getStatus());
			}
			
			return null;
		}

		public Collection list(QueryOption opt) throws ServiceException {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean create(Feed feed) throws ServiceException {
			IServiceRequest request = newRequest("POST", getContext(), feed);
			
			IServiceResponse response = null;
			try {
				response = execute(request);
			} catch (IOException e) {
				ServiceException.error(e);
			}
			
			if (response == null) {
				ServiceException.timeout("Timeout");
			} else if (response.getStatus() == 201) {
				return true;
			} else {
				throw new ServiceException(response.getStatus());
			}
			
			return false;
		}

		public boolean update(Feed feed) throws ServiceException {
			String url = getContext() + "/" + feed.getName();
			
			IServiceRequest request = newRequest("PUT", url, feed);
			
			IServiceResponse response = null;
			try {
				response = execute(request);
			} catch (IOException e) {
				ServiceException.error(e);
			}
			
			if (response == null) {
				ServiceException.timeout("Timeout");
			} else if (response.getStatus() == 204) {
				return true;
			} else {
				throw new ServiceException(response.getStatus());
			}
			
			return false;
		}

		public boolean delete(String path) throws ServiceException {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	private IServiceRequest newRequest(String method, String resource, Object body) {
		ServiceRequestImpl request = new ServiceRequestImpl();
		request.setMethod(method);
		request.addHeader("authorization", auth);
		request.setResource(resource);
		request.setBody(body);
		return request;
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
	
	public void setUsername(String username) {
		this.username = username;
		prepareUsernamePasswordCredentials();
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
		prepareUsernamePasswordCredentials();
	}

	public String getPassword() {
		return password;
	}
	
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	private void prepareUsernamePasswordCredentials() {
		if (username != null && password != null) {
			auth = "BASIC " + Base64.encodeString(username + ":" + password);
		}
	}
}
