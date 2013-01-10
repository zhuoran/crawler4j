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

import java.util.Collection;

import me.zhuoran.crawler4j.crawler.config.CrawlerConfig;
import me.zhuoran.crawler4j.crawler.http.HttpConnectionManager;


/**
 * This class is a default WebCrawler implement.
 * @author Zoran
 *
 */
public class DefaultWebCrawler extends AbstractWebCrawler {

	public DefaultWebCrawler(CrawlerConfig config) {
		super(config);
	}

	@Override
	public Collection<URL> getUrlsToFilter() {
		return null;
	}

	@Override
	public Collection<URL> getUrlsToUpdate() {
		return null;
	}

	@Override
	public void onBeforeExit() {
		super.clear();
		//close expired connections.
		HttpConnectionManager.getHttpClient().getConnectionManager().closeExpiredConnections();
	}

	@Override
	public void onBeforeStart() {

	}

	

}
