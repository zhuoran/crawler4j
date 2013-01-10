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
package me.zhuoran.crawler4j.simple;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import me.zhuoran.crawler4j.crawler.AbstractWebCrawler;
import me.zhuoran.crawler4j.crawler.URL;
import me.zhuoran.crawler4j.crawler.config.CrawlerConfig;


public class InfoqCrawler extends AbstractWebCrawler {

	public InfoqCrawler(CrawlerConfig config) {
		super(config);
	}
	
	/**
	 * Override getTargetPageURLs to add target URL.
	 */
	public Set<URL> getTargetPageURLs(){
		Set<URL> urls = new HashSet<URL>();
		String url = "http://www.infoq.com/news/2012/12/twemproxy;jsessionid=1652D82C3359CBAB67DA00B26BE7784B";
		urls.add(URL.valueOf(url));
		return urls;
	}
	
	
	/**
	 *  Override getListPageURLs to get paging URL
	 *  @see {@link AbstractWebCrawler.getListPageURLs}
	 */
	public Set<URL> getListPageURLs(){
		Set<URL> urls = new HashSet<URL>();
		String url = "http://www.infoq.com/infoq.action?newsidx=";
		int j = 10;
		for (int i = 1; i <= 10; i++) {
			String pagingUrl = url + (j * i);
			System.out.println(pagingUrl);
			urls.add(URL.valueOf(pagingUrl));
		}
		return urls;
	}

	
	/**
	 * Override to remove unnecessary URL.
	 *  
	 */
	@Override
	public Collection<URL> getUrlsToFilter() {
		Set<URL> filterSet = new HashSet<URL>();
		String url="http://www.infoq.com/news/2012/11/Panel-WinRT-Answers;jsessionid=91AB81A159E85692E6F1199644E2053C ";
		filterSet.add(URL.valueOf(url));
		return filterSet;
	}

	
	@Override
	public Collection<URL> getUrlsToUpdate() {
		return null;
	}

	@Override
	public void onBeforeExit() {
	}

	@Override
	public void onBeforeStart() {
	}

}
