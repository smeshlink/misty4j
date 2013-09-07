package com.smeshlink.misty.test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.smeshlink.misty.entity.Entry;
import com.smeshlink.misty.entity.Feed;
import com.smeshlink.misty.formatter.XmlFormatter;

import junit.framework.TestCase;

public class FormatterTest extends TestCase {
	
	public void testXmlFormatter() throws Exception {
		XmlFormatter formatter = new XmlFormatter();
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xfml xmlns:opensearch=\"http://a9.com/-/spec/opensearch/1.1/\"><opensearch:totalResults>1</opensearch:totalResults><opensearch:startIndex>0</opensearch:startIndex><opensearch:itemsPerPage>10</opensearch:itemsPerPage><feed><name>SeaBo-01A4B1D5150000C0</name><created>2013-02-24T12:46:06.879Z</created><updated>2013-02-24T12:46:06.879Z</updated><children><feed><name>1</name><created>2013-02-24T12:46:06.894Z</created><updated>2013-02-24T12:46:06.894Z</updated><children><feed><name>parent</name><created>2013-02-24T12:46:06.894Z</created><updated>2013-02-26T07:50:54.465Z</updated><current><value><integer>0</integer></value></current></feed><feed><name>MXN880_STATUS</name><created>2013-02-24T12:46:06.894Z</created><updated>2013-02-24T12:46:06.894Z</updated><children><feed><name>lightInn</name><created>2013-02-24T12:46:06.894Z</created><updated>2013-02-26T07:50:54.465Z</updated><current><value><integer>2689</integer></value></current></feed><feed><name>tempInn</name><created>2013-02-24T12:46:06.894Z</created><updated>2013-02-26T07:50:54.465Z</updated><current><value><number>1.7154756784439087</number></value></current></feed></children></feed></children></feed></children></feed></xfml>";
		//String xml2 = "<xfml><feed><name>SeaBo-01A4B1D5150000C0</name><created>2013-02-24T12:46:06.879Z</created><updated>2013-02-24T12:46:06.879Z</updated><current><value><struct><member><name>1</name><value><struct><member><name>parent</name><value><integer>0</integer></value></member><member><name>MXN880_STATUS</name><value><struct><member><name>lightInn</name><value><integer>2689</integer></value></member><member><name>tempInn</name><value><number>1.7154756784439087</number></value></member></struct></value></member></struct></value></member></struct></value></current></feed></xfml>";
		//String xml3 = "<xfml><feed><name>SeaBo-01A4B1D5150000C0</name><created>2013-02-24T12:46:06.879Z</created><updated>2013-02-24T12:46:06.879Z</updated><children><feed><name>1</name><created>2013-02-24T12:46:06.894Z</created><updated>2013-02-24T12:46:06.894Z</updated><children><feed><name>parent</name><created>2013-02-24T12:46:06.894Z</created><updated>2013-02-26T07:50:54.465Z</updated><current><value><integer>0</integer></value></current><data><entry at=\"2013-02-26T07:50:54.449Z\"><value><integer>0</integer></value></entry><entry at=\"2013-02-26T07:50:52.436Z\"><value><integer>0</integer></value></entry><entry at=\"2013-02-26T07:50:50.423Z\"><value><integer>0</integer></value></entry><entry at=\"2013-02-26T07:50:48.409Z\"><value><integer>0</integer></value></entry><entry at=\"2013-02-26T07:50:46.409Z\"><value><integer>0</integer></value></entry><entry at=\"2013-02-26T07:50:44.417Z\"><value><integer>0</integer></value></entry><entry at=\"2013-02-26T07:50:42.395Z\"><value><integer>0</integer></value></entry><entry at=\"2013-02-26T07:50:40.394Z\"><value><integer>0</integer></value></entry><entry at=\"2013-02-26T07:50:38.393Z\"><value><integer>0</integer></value></entry><entry at=\"2013-02-26T07:50:36.388Z\"><value><integer>0</integer></value></entry></data></feed><feed><name>MXN880_STATUS</name><created>2013-02-24T12:46:06.894Z</created><updated>2013-02-24T12:46:06.894Z</updated><children><feed><name>lightInn</name><created>2013-02-24T12:46:06.894Z</created><updated>2013-02-26T07:50:54.465Z</updated><current><value><integer>2689</integer></value></current><data><entry at=\"2013-02-26T07:50:54.449Z\"><value><integer>2689</integer></value></entry><entry at=\"2013-02-26T07:50:52.436Z\"><value><integer>2689</integer></value></entry><entry at=\"2013-02-26T07:50:50.423Z\"><value><integer>2689</integer></value></entry><entry at=\"2013-02-26T07:50:48.409Z\"><value><integer>2689</integer></value></entry><entry at=\"2013-02-26T07:50:46.409Z\"><value><integer>2689</integer></value></entry><entry at=\"2013-02-26T07:50:44.417Z\"><value><integer>2689</integer></value></entry><entry at=\"2013-02-26T07:50:42.395Z\"><value><integer>2689</integer></value></entry><entry at=\"2013-02-26T07:50:40.394Z\"><value><integer>2689</integer></value></entry><entry at=\"2013-02-26T07:50:38.393Z\"><value><integer>2689</integer></value></entry><entry at=\"2013-02-26T07:50:36.388Z\"><value><integer>2689</integer></value></entry></data></feed><feed><name>tempInn</name><created>2013-02-24T12:46:06.894Z</created><updated>2013-02-26T07:50:54.465Z</updated><current><value><number>1.7154756784439087</number></value></current><data><entry at=\"2013-02-26T07:50:54.449Z\"><value><number>1.7154756784439087</number></value></entry><entry at=\"2013-02-26T07:50:52.436Z\"><value><number>1.7154756784439087</number></value></entry><entry at=\"2013-02-26T07:50:50.423Z\"><value><number>1.7154756784439087</number></value></entry><entry at=\"2013-02-26T07:50:48.409Z\"><value><number>1.7154756784439087</number></value></entry><entry at=\"2013-02-26T07:50:46.409Z\"><value><number>1.7154756784439087</number></value></entry><entry at=\"2013-02-26T07:50:44.417Z\"><value><number>1.7154756784439087</number></value></entry><entry at=\"2013-02-26T07:50:42.395Z\"><value><number>1.7154756784439087</number></value></entry><entry at=\"2013-02-26T07:50:40.394Z\"><value><number>1.7154756784439087</number></value></entry><entry at=\"2013-02-26T07:50:38.393Z\"><value><number>1.7154756784439087</number></value></entry><entry at=\"2013-02-26T07:50:36.388Z\"><value><number>1.7154756784439087</number></value></entry></data></feed></children></feed></children></feed></children></feed></xfml>";
	
		ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes("utf-8"));
		Feed feed = formatter.parseFeed(input);
		
		//print(feed);
		
		assertNotNull(feed);
		assertEquals("SeaBo-01A4B1D5150000C0", feed.getName());
		assertNull(feed.getCurrentValue());
		
		assertEquals(1, feed.getChildren().size());
		List list = new ArrayList(feed.getChildren());
		feed = (Feed) list.get(0);
		assertEquals("1", feed.getName());
		assertNull(feed.getCurrentValue());
		
		assertEquals(2, feed.getChildren().size());
		list = new ArrayList(feed.getChildren());
		feed = (Feed) list.get(0);
		assertEquals("parent", feed.getName());
		assertEquals(new Integer(0), feed.getCurrentValue());
		feed = (Feed) list.get(1);
		assertEquals("MXN880_STATUS", feed.getName());
		assertNull(feed.getCurrentValue());
		
		assertEquals(2, feed.getChildren().size());
		list = new ArrayList(feed.getChildren());
		feed = (Feed) list.get(0);
		assertEquals("lightInn", feed.getName());
		assertEquals(new Integer(2689), feed.getCurrentValue());
		feed = (Feed) list.get(1);
		assertEquals("tempInn", feed.getName());
		assertEquals(new Double(1.7154756784439087D), feed.getCurrentValue());
	}

	static void print(Feed feed) {
		System.out.println(feed.getName());
		System.out.println(feed.getCurrentValue());
		
		if (feed.getEntries() != null) {
			Iterator itValue = feed.getEntries().iterator();
			while (itValue.hasNext()) {
				Entry value = (Entry) itValue.next();
				System.out.print(value.getKey());
				System.out.print(" : ");
				System.out.println(value.getValue());
			}
		}
		
		Iterator itChild = feed.getChildren().iterator();
		while (itChild.hasNext()) {
			print((Feed) itChild.next());
		}
	}
}
