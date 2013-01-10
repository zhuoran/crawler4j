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
package me.zhuoran.crawler4j.crawler;

import java.util.Set;

import me.zhuoran.crawler4j.crawler.config.CrawlerConfig;

/**
 * Interface for a Web Crawler
 * @see {@link AbstractWebCrawler}
 * @author Zoran
 *
 */
public interface WebCrawler extends Runnable{

	public void crawl() throws RuntimeException;

	public Set<URL> getListPageURLs() throws RuntimeException;

	public Set<URL> getTargetPageURLs() throws RuntimeException;
	
	public CrawlerConfig getConfig();
	
	public boolean isFinished();
	
	public int getTotalsToCrawl();
	
	public void onBeforeExit();
}
