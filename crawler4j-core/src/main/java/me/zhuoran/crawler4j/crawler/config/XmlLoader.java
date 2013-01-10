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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.zhuoran.crawler4j.crawler.DefaultWebCrawler;
import me.zhuoran.crawler4j.crawler.WebCrawler;
import me.zhuoran.crawler4j.crawler.parser.Parser;
import me.zhuoran.crawler4j.crawler.util.Reflections;
import me.zhuoran.crawler4j.crawler.util.StringUtils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is used to parse crawler4j.xml 
 * 
 * @author Zoran
 *
 */
public class XmlLoader {

	private static Logger logger = LoggerFactory.getLogger(XmlLoader.class);
	
	private static final String DEFAULT_CONFIG_FILE = "crawler4j.xml";

	private Document document = null;

	/**
	 * Load and parse the crawler4j.xml to get all crawlers.
	 * @return WebCrawler instance list;
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<WebCrawler> load() {

		List<WebCrawler> crawlers = new ArrayList<WebCrawler>();
		SAXReader reader = new SAXReader();
		try {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE);
			document = reader.read(is);
			Element root = document.getRootElement();
			for (Iterator<Element> i = root.elementIterator(); i.hasNext();) {
				Element taskElement = i.next();
				String name = taskElement.elementText("name");
				long delay = Long.parseLong(taskElement.elementText("delay"));
				String url = taskElement.elementText("url");
				String parserName = taskElement.elementText("parser").trim();
				String defaultCharset = taskElement.elementText("charset");
				String pageNoStr = taskElement.elementText("max_page");
				String crawlerName = taskElement.elementText("crawler");
				String nextPageRegex = taskElement.elementText("next_page_key");
				String extractLinksElementId = taskElement.elementText("extract_links_elementId");
				Class<Parser> c = (Class<Parser>)Class.forName(parserName);
				Parser parser = c.newInstance();

				if (parser == null) {
					throw new IllegalArgumentException("parser must not be null!");
				}

				int maxPageNo = 1;

				if (StringUtils.isNotEmpty(pageNoStr)) {
					maxPageNo = Integer.parseInt(pageNoStr);
				}

				CrawlerConfig config = new CrawlerConfig(name, defaultCharset, url, delay, maxPageNo, nextPageRegex,
						extractLinksElementId, parserName, crawlerName, parser);
				WebCrawler crawler = null;

				//get WebCrawler instance throw reflection
				if (StringUtils.isBlank(crawlerName)) {
					crawler = new DefaultWebCrawler(config);
				} else {
					crawler = (WebCrawler)Reflections.constructorNewInstance(crawlerName,
							new Class[] { CrawlerConfig.class }, new CrawlerConfig[] { config });
				}
				crawlers.add(crawler);
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(),e);
		}

		return crawlers;
	}

}
