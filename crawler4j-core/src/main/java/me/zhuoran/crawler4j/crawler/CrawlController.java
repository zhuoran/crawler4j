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

import java.util.ArrayList;
import java.util.List;

import me.zhuoran.crawler4j.crawler.config.XmlLoader;
import me.zhuoran.crawler4j.crawler.http.HttpConnectionManager;
import me.zhuoran.crawler4j.crawler.util.StringUtils;
import me.zhuoran.crawler4j.crawler.util.Threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The controller creates crawler threads and monitor their state.
 * 
 * @author Zoran
 *
 */
public class CrawlController {

	private List<WebCrawler> crawlers;

	private XmlLoader resourceLoader;

	private static Logger logger = LoggerFactory.getLogger(CrawlController.class);

	public CrawlController() {
		this.resourceLoader = new XmlLoader();
		
		crawlers = resourceLoader.load();
	}

	/**
	 * Start all crawlers thread and monitor their state.
	 * 
	 */
	public void start() {
		
		if (crawlers == null || crawlers.isEmpty()) {
			throw new IllegalArgumentException("crawlers initial failure!");
		}

		final ArrayList<Thread> threads = new ArrayList<Thread>(crawlers.size());
		for (WebCrawler crawler : crawlers) {
			Thread crawlerThread = new Thread(crawler, crawler.getConfig().getName());
			threads.add(crawlerThread);
		}

		final long startTime = System.currentTimeMillis();
		//starting crawler thread
		for (Thread t : threads) {
			t.start();
		}

		final int startedThreadSize = threads.size();

		Thread monitorThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String unfinishedCrawler = "";
					int unfinished = 0;
					while (true) {
						Threads.sleep(50);
						for (int i = 0; i < threads.size(); i++) {
							Thread t = threads.get(i);
							if (t != null) {
								WebCrawler crawler = crawlers.get(i);
								if (t.isAlive() && crawler.isFinished()) {
									logger.info(crawler.getConfig().getName() + " was crawled total of "
											+ crawler.getTotalsToCrawl() + " pages.");
									crawler.onBeforeExit();
									t.interrupt();
									crawlers.remove(i);
									threads.remove(i);
								} else if (!t.isAlive() && !crawler.isFinished()) {
									unfinished++;
									unfinishedCrawler += t.getName() + " ";
									crawlers.remove(i);
									threads.remove(i);
								}
							}
						}
						if (threads.isEmpty()) {
							break;
						}
					}

					long endTime = (System.currentTimeMillis() - startTime) / (1000 * 60);
					logger.info("All of the crawlers are stopped ... used " + endTime + " minutes," + startedThreadSize
							+ " threads are started , " + unfinished + " threads are unfinished. ");
					if (StringUtils.isNotEmpty(unfinishedCrawler)) {
						logger.info(unfinishedCrawler + " is unfinished !");
					}

					//shutdown the HTTP connection
					HttpConnectionManager.shutdown();

				} catch (Throwable e) {
					e.printStackTrace();
					logger.error("Monitor Thread Error " + e.getMessage(), e);
				}

			}

		}, "Monitor");
		monitorThread.start();
	}

}
