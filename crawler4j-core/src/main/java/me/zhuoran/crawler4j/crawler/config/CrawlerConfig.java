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
package me.zhuoran.crawler4j.crawler.config;

import me.zhuoran.crawler4j.crawler.parser.Parser;

/**
 * 
 * Store configuration for WebCrawler
 * 
 * @author Zoran
 *
 */
public class CrawlerConfig {

	//thread name.
	private final String name;

	//default charset "UTF-8"
	private final String defaultCharset;
	
	//target url to crawl
	private final String url;

	//delay time for crawl page. default 1000 ms
	private final long delay;

	//next page url regex.
	private final String nextPageRegex;

	
	/**
	 * 
	 * elementIdOrRegex is used to extract the target page links.
	 * elementIdOrRegex can be a html element id or element style or a regex.
	 * Example <div id="list"></div> elementIdOrRegex="div#list"
	 * Example <div style="list"></div> elementIdOrRegex="div.list"
	 * 
	 */
	private final String elementIdOrRegex;

	//data parser for crawler
	private final Parser parser;

	// the max page number.
	private final int maxPageNo;

	//the Parser class full name.
	private final String parserName;

	//the Crawler class full name.
	private final String crawlerName;

	public CrawlerConfig() {
		this.name = "null";
		this.defaultCharset = "UTF-8";
		this.url = "";
		this.delay = 1000L;
		this.nextPageRegex = "";
		this.elementIdOrRegex = "";
		this.maxPageNo = 1;
		this.parserName = "";
		this.crawlerName = "";
		this.parser = null;
	}

	public CrawlerConfig(String name, String defaultCharset, String url, long delay, int maxPageNo,
			String nextPageRegex, String elementIdOrRegex, String parserClassName, String crawlerClassName,
			Parser parser) {
		this.name = name;
		this.defaultCharset = defaultCharset;
		this.url = url;
		this.delay = delay;
		this.nextPageRegex = nextPageRegex;
		this.elementIdOrRegex = elementIdOrRegex;
		this.maxPageNo = maxPageNo;
		this.parserName = parserClassName;
		this.crawlerName = crawlerClassName;
		this.parser = parser;
	}

	public String getName() {
		return name;
	}

	public String getDefaultCharset() {
		return defaultCharset;
	}

	public String getUrl() {
		return url;
	}

	public long getDelay() {
		return delay;
	}

	public String getNextPageRegex() {
		return nextPageRegex;
	}

	public String getElementIdOrRegex() {
		return elementIdOrRegex;
	}

	public Parser getParser() {
		return parser;
	}

	public int getMaxPageNo() {
		return maxPageNo;
	}

	public String getParserName() {
		return parserName;
	}

	public String getCrawlerName() {
		return crawlerName;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CrawlerConfig other = (CrawlerConfig)obj;
		if (url == null) {
			if (other.url != null) {
				return false;
			}
		} else if (!url.equals(other.url)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (parserName == null) {
			if (other.parserName != null) {
				return false;
			}
		} else if (!parserName.equals(other.parserName)) {
			return false;
		}
	
		return true;
	}
	

}