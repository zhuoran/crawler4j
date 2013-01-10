package com.veemt.crawler4j.crawler;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import me.zhuoran.crawler4j.crawler.URL;

import org.junit.Test;


/**
 * 
 * @author Zoran
 *
 */
public class URLTest {

    @Test
    public void test_valueOf_noProtocolAndHost() throws Exception {
        URL url = URL.valueOf("/ready/lol?date=2012&name=vill");
        assertNull(url.getHost());
        assertEquals("ready/lol", url.getPath());

        assertNull(url.getProtocol());
        assertEquals(2, url.getParameters().size());
        assertEquals("2012", url.getParameter("date"));
        assertEquals("vill", url.getParameter("name"));

       
    }
    
    @Test
    public void test_equals() throws Exception {
        URL url1 = URL.valueOf("http://book.douban.com/annotation/write?sid=19928910");

        Map<String, String> params = new HashMap<String, String>();
        params.put("sid", "19928910");
        URL url2 = new URL("http", "book.douban.com", "annotation/write", params);

        assertEquals(url1, url2);
    }
    
    

    @Test
    public void test_addParameter() throws Exception {
        URL url = URL.valueOf("http://book.douban.com/annotation/write?sid=19928910");
        url = url.addParameter("k1", "v1");
        assertEquals("http", url.getProtocol());
        assertEquals("book.douban.com", url.getHost());
        assertEquals(2, url.getParameters().size());
        assertEquals("v1", url.getParameter("k1"));
    }


  



}