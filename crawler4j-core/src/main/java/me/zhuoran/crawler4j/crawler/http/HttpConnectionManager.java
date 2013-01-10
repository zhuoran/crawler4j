/*
 * Copyright (c) 2012 Zhuoran Wang <zoran.wang@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.zhuoran.crawler4j.crawler.http;


import me.zhuoran.crawler4j.crawler.util.PropertiesUtils;
import me.zhuoran.crawler4j.crawler.util.StringUtils;
import me.zhuoran.crawler4j.crawler.util.Util;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple HttpConnectionManager use Apache HttpClient
 * @author Zoran
 *
 */
public abstract class HttpConnectionManager {

	//default max total connection number.
	public static int MAX_TOTAL_CONN = 200;

	//default max total route number.
	public static int MAX_ROUTE_TOTAL = 40;

	//default connection timeout period.
	public static int CONNECTION_TIMEOUT = 20000;

	//default socket timeout period.
	public static int SO_TIMEOUT = 20000;

	//default user_agent
	public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11";

	private static Logger logger = LoggerFactory.getLogger(HttpConnectionManager.class);

	private static HttpParams httpParams;

	private static PoolingClientConnectionManager connectionManager;

	private static DefaultHttpClient httpclient;

	static {
		try {
			httpParams = new BasicHttpParams();
			if (Util.properties == null) {
				Util.properties = PropertiesUtils.loadProperties("crawler4j.properties");
			}
			if (StringUtils.isNotEmpty(Util.properties.getProperty("MAX_TOTAL_CONN"))) {
				MAX_TOTAL_CONN = Integer.parseInt(Util.properties.getProperty("MAX_TOTAL_CONN"));
			}
			if (StringUtils.isNotEmpty(Util.properties.getProperty("MAX_ROUTE_TOTAL"))) {
				MAX_ROUTE_TOTAL = Integer.parseInt(Util.properties.getProperty("MAX_ROUTE_TOTAL"));
			}
			if (StringUtils.isNotEmpty(Util.properties.getProperty("CONNECTION_TIMEOUT"))) {
				CONNECTION_TIMEOUT = Integer.parseInt(Util.properties.getProperty("CONNECTION_TIMEOUT"));
			}
			if (StringUtils.isNotEmpty(Util.properties.getProperty("SO_TIMEOUT"))) {
				SO_TIMEOUT = Integer.parseInt(Util.properties.getProperty("SO_TIMEOUT"));
			}

			if (StringUtils.isNotEmpty(Util.properties.getProperty("USER_AGENT"))) {
				USER_AGENT = Util.properties.getProperty("USER_AGENT");
			}

			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
			schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
			connectionManager = new PoolingClientConnectionManager(schemeRegistry);
			// Increase max total connection to 200  
			connectionManager.setMaxTotal(MAX_TOTAL_CONN);
			// Increase default max connection per route to 20  
			// Increase max connections for localhost:80 to 50  
			connectionManager.setDefaultMaxPerRoute(MAX_ROUTE_TOTAL);
			//cm.setMaxPerRoute(new HttpRoute(target), 20);
			httpParams.setParameter("User-Agent", USER_AGENT);
			httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
			httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
			httpclient = new DefaultHttpClient(connectionManager, httpParams);
			String proxyHost = Util.properties.getProperty("PROXY_HOST");
			if (proxyHost != null && !proxyHost.isEmpty()) {
				int proxyPort = Integer.parseInt(Util.properties.getProperty("PROXY_PORT"));
				String proxyUserName = Util.properties.getProperty("PROXY_USER_NAME");
				String proxyPassword = Util.properties.getProperty("PROXY_PASSWORD");
				if (proxyUserName != null) {
					httpclient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost, proxyPort),
							new UsernamePasswordCredentials(proxyUserName, proxyPassword));
				}
				HttpHost proxy = new HttpHost(proxyHost, proxyPort);
				httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
		} catch (Throwable e) {
			logger.error("DefaultHttpClient initial failure! " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Get apache HttpClient instance.
	 * @return
	 */
	public static org.apache.http.client.HttpClient getHttpClient() {
		if (httpclient == null) {
			httpclient = new DefaultHttpClient(connectionManager, httpParams);
			return httpclient;
		}
		return httpclient;
	}

	/**
	 * Shut down HttpClient
	 */
	public static void shutdown() {
		if (httpclient != null) {
			httpclient.getConnectionManager().shutdown();
		}
		// When HttpClient instance is no longer needed,
		// shut down the connection manager to ensure
		// immediate deallocation of all system resources
		// httpclient.getConnectionManager().shutdown();
	}

	

}
