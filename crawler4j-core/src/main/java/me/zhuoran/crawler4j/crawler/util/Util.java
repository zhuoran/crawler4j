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
package me.zhuoran.crawler4j.crawler.util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import me.zhuoran.crawler4j.crawler.URL;
import me.zhuoran.crawler4j.crawler.http.HttpClient;
import me.zhuoran.crawler4j.crawler.http.HttpConnectionManager;
import me.zhuoran.crawler4j.crawler.http.HttpFetchResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Util 
 * 
 * @author Zoran
 *
 */
public abstract class Util {

	public static Properties properties = null;

	private static final Logger logger = LoggerFactory.getLogger(Util.class);

	private static HttpClient httpClient = new HttpClient();

	private static final String URL_PREFIX = "/";

	static {
		try {
			properties = PropertiesUtils.loadProperties("crawler4j.properties");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Fetch and parse a HTML page to JSOUP document
	 * @param url
	 * @return JSoup Document
	 */
	public static Document getJsoupDocumentByUrl(String url) {
		Document doc = null;
		int retry = 0;
		while (retry++ < 5) {
			try {
				doc = Jsoup.connect(url).userAgent(HttpConnectionManager.USER_AGENT).timeout(10000).get();
				if (doc != null) {
					return doc;
				}
			} catch (SocketTimeoutException e) {
				logger.debug(e.getMessage());
			} catch (IOException e) {
				logger.warn(e.getMessage());
				break;
			} catch (Throwable e) {
				logger.error(e.getMessage());
				break;
			}
		}
		return doc;
	}

	
	/**
	 * Use JSoup selector-syntax to extract links.
	 * @see {@link <a href="http://jsoup.org/cookbook/extracting-data/selector-syntax">jsoup</a>}
	 * @param doc
	 * @param query
	 * @return 
	 * @throws RuntimeException
	 */
	private static Set<URL> getAbsoluteURLsByJSoupQuery(Document doc, String query) throws RuntimeException {
		Set<URL> urlSet = new HashSet<URL>();
		try {
			if (query == null || doc == null) {
				return urlSet;
			}
			Elements urls = doc.select(query).select("a[href]");
			if (urls != null) {
				for (Element element : urls) {
					String url = element.attr("abs:href").trim();
					url = url.replace("../", "");
					url = url.replace("./", "");
					if (StringUtils.isNotEmpty(url)) {
						urlSet.add(URL.valueOf(url));
					}
				}
			}
		} catch (RuntimeException e) {
			throw e;
		}
		return urlSet;
	}

	/**
	 * Use regex to extract links.
	 * @param url
	 * @param elementIdOrRegex
	 * @return
	 * @throws Exception
	 */
	private static Set<URL> getAbsoluteURLsByRegex(URL url, String elementIdOrRegex) throws Exception {
		Set<URL> urlSet = new HashSet<URL>();
		String reg = elementIdOrRegex.substring(1);
		HttpFetchResult fetchResult = httpClient.requestHttpGet(url);
		if (fetchResult != null && fetchResult.getHtml() != null) {
			List<String> extractedList = StringUtils.regexExtractor(fetchResult.getHtml(), reg);
			for (String partUrlStr : extractedList) {
				int i = url.getPath().lastIndexOf(URL_PREFIX);
				String path = url.getPath().substring(0, i);
				String hostAndPath = url.getHost();
				if (!partUrlStr.contains(path)) {
					hostAndPath = url.getHost() + URL_PREFIX + path;
				}
				String fullStr = fixAbsoluteLink(partUrlStr, hostAndPath, url.getProtocol());
				if (fullStr != null && !fullStr.isEmpty()) {
					urlSet.add(URL.valueOf(fullStr));
				}
			}
		}
		return urlSet;
	}
	
	
	/**
	 * Fix relative path to absolute path.
	 * @param path
	 * @param hostAndPath
	 * @param protocol
	 * @return absolute path string
	 */
	public static String fixAbsoluteLink(String path, String hostAndPath, String protocol) {
		path = path.replace("../", "");
		path = path.replace("./", "");
		if (!path.startsWith(protocol)) {
			if ((hostAndPath.endsWith(URL_PREFIX) && !path.startsWith(URL_PREFIX))
					|| (!hostAndPath.endsWith(URL_PREFIX) && path.startsWith(URL_PREFIX))) {
				path = hostAndPath + path;
			} else if (!hostAndPath.endsWith(URL_PREFIX) && !path.startsWith(URL_PREFIX)) {
				path = hostAndPath + URL_PREFIX + path;
			} else if (hostAndPath.endsWith(URL_PREFIX) && path.startsWith(URL_PREFIX)) {
				path = hostAndPath + path.substring(1, path.length());
			}
			path = protocol + "://" + path;
			return path;
		}
		return path;
	}

	/**
	 * Extracting links use JSoup or regex 
	 * @param url
	 * @param queryOrRegex
	 * @return
	 */
	public static Set<URL> extracting(URL url, String queryOrRegex) {
		Set<URL> result = null;
		Document doc = null;
		try {
			if (!queryOrRegex.startsWith("#")) {
				doc = getJsoupDocumentByUrl(url.toFullString());
				result = getAbsoluteURLsByJSoupQuery(doc, queryOrRegex);
			} else {
				result = getAbsoluteURLsByRegex(url, queryOrRegex);
			}
		} catch (Throwable e) {
			logger.error(url.toFullString() + " extracting links failure " + e.getMessage());
		}
		return result;
	}


}
