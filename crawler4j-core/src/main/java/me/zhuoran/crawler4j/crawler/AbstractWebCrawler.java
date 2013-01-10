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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import me.zhuoran.crawler4j.crawler.config.CrawlerConfig;
import me.zhuoran.crawler4j.crawler.http.HttpClient;
import me.zhuoran.crawler4j.crawler.http.HttpFetchResult;
import me.zhuoran.crawler4j.crawler.parser.Parser;
import me.zhuoran.crawler4j.crawler.util.StringUtils;
import me.zhuoran.crawler4j.crawler.util.Threads;
import me.zhuoran.crawler4j.crawler.util.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is a basic abstract WEB crawler.
 * 
 * @author Zoran
 *
 */
public abstract class AbstractWebCrawler implements WebCrawler{

	/**
	 * true is the crawler task is finished,otherwise task failure.
	 */
	protected volatile boolean isFinished = false;

	/**
	 * urls to crawl
	 */
	protected final Set<URL> urlsToCrawl = Collections.synchronizedSet(new HashSet<URL>());

	/**
	 * Collection of list pages
	 */
	protected Set<URL> listPageUrls;
	
	/**
	 * total number of crawl.
	 */
	protected int toCrawlTotals;

	private static final Logger logger = LoggerFactory.getLogger(AbstractWebCrawler.class);

	/**
	 * The crawler configure.
	 */
	private final CrawlerConfig config;

	private final HttpClient httpClient = new HttpClient();
	
	public AbstractWebCrawler(final CrawlerConfig config) {
		this.config = config;
	}

	@Override
	public void run() {
		if (config == null) {
			throw new IllegalArgumentException("CrawlConfig instance can not be null!");
		}
		try {
			while (!Thread.interrupted()) {
				onBeforeStart();
				crawl();
				Thread.sleep(1000L);
			}
		} catch (InterruptedException e) {
		} catch (RuntimeException e) {
			logger.error(config.getName() + " " + e.getMessage());
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	
	/**
	 *  clear cache.
	 */
	public void clear() {
		if (urlsToCrawl != null) {
			urlsToCrawl.clear();
		}
		if (listPageUrls != null) {
			listPageUrls.clear();
		}
	}

	/**
	 * Crawl or update 
	 * @throws RuntimeException
	 */
	public void crawl() throws RuntimeException {
		try {
			listPageUrls = getListPageURLs();
			if (listPageUrls != null) {
				for (URL listURL : listPageUrls) {
					Set<URL> urls = Util.extracting(listURL, config.getElementIdOrRegex());
					if (urls != null) {
						urlsToCrawl.addAll(urls);
					}
				}
			}
			Set<URL> pageURLs = getTargetPageURLs();
			if (pageURLs != null) {
				urlsToCrawl.addAll(pageURLs);
			}
			Collection<URL> urlsToFilter = getUrlsToFilter();
			if (urlsToFilter != null && !urlsToFilter.isEmpty()) {
				urlsToCrawl.removeAll(urlsToFilter);
			}
			Collection<URL> urlsToUpdate = getUrlsToUpdate();
			

			if (urlsToCrawl == null) {
				throw new IllegalArgumentException("urlsToCrawl must not be null!");
			}
			
			UpdateThread uThread = null;
			if (urlsToUpdate != null && !urlsToUpdate.isEmpty()) {
				toCrawlTotals +=urlsToUpdate.size();
			    uThread = new UpdateThread(urlsToUpdate, config);
				uThread.start();
			}

			toCrawlTotals += urlsToCrawl.size();
			parsing(config.getName(), urlsToCrawl, config, false);

			if (uThread != null) {
				while (uThread.isAlive()) {
					isFinished = false;
					Threads.sleep(10);
				}
			}
			isFinished = true;
			
		} catch (RuntimeException e) {
			isFinished = false;
			throw e;
		}
	}

	/**
	 * This thread used to crawl update
	 *
	 */
	private class UpdateThread extends Thread {
		Collection<URL> set = null;
		CrawlerConfig config = null;

		public UpdateThread(Collection<URL> set, CrawlerConfig config) {
			this.set = set;
			this.config = config;
		}

		@Override
		public void run() {
			try {
				if (set != null && config != null) {
					String threadName = config.getName() + " update";
					parsing(threadName, set, config, true);
					isFinished = true;
				}
			} catch (Throwable e) {
				logger.error(config.getName() + " update " + e.getMessage());
				e.printStackTrace();
			}

		}
	}

	
	/**
	 * Crawl and parse the Web page in the specified collection.
	 * @param threadName
	 * @param set
	 * @param config
	 * @param isUpdate
	 * @throws RuntimeException
	 */
	@SuppressWarnings("unchecked")
	private void parsing(final String threadName, final Collection<URL> set, final CrawlerConfig config,
			final boolean isUpdate) throws RuntimeException {
		if (set == null) {
			throw new IllegalArgumentException("urlsToCrawl must not be null!");
		}
		if(isUpdate){
			logger.info(config.getName() + " ready to update " + set.size() + " pages");
		}else{
			logger.info(config.getName() + " ready to crawl " + set.size() + " pages");
		}

		try {
			Parser parser = config.getParser();
			if (parser == null) {
				logger.error(threadName + " config parser is null");
				Class<Parser> c = (Class<Parser>)Class.forName(config.getParserName());
				parser = c.newInstance();
			}
			for (URL url : set) {
				Threads.sleep(config.getDelay());
				HttpFetchResult result = httpClient.requestHttpGet(config, url);
				if (result != null && StringUtils.isNotEmpty(result.getHtml())) {
					parser.parse(result, url.toFullString(), threadName, isUpdate);
					result.consume();
				}
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	@Override
	public boolean isFinished() {
		return isFinished;
	}

	@Override
	public int getTotalsToCrawl() {
		return toCrawlTotals;
	}

	public CrawlerConfig getConfig() {
		return config;
	}

	/**
	 * Get a httpClient wrapper instance.
	 * @return httpClient wrapper instance.
	 */
	public HttpClient getHttpClient() {
		return httpClient;
	}

	/**
	 * Get list pages URL collection.
	 * @return Set a list pages URL set
	 * @throws RuntimeException
	 */
	public Set<URL> getListPageURLs() throws RuntimeException {
		if (StringUtils.isBlank(config.getUrl()) || config.getUrl().contains("|")) {
			return null;
		}

		Set<URL> set = new HashSet<URL>();
		String url = config.getUrl();
		if (StringUtils.isNotEmpty(url)) {
			if (url.contains(",")) {
				String[] urlArray = url.split(",");
				for (String urlStr : urlArray) {
					urlStr = urlStr.trim();
					set.add(URL.valueOf(urlStr));
				}
			} else {
				set.add(URL.valueOf(url));
			}
		}
		if (StringUtils.isNotEmpty(config.getNextPageRegex())) {
			addPagingUrl(set);
		}
		return set;
	};

	
	/**
	 * Add paging URL to specified collection.
	 * @param urlSet
	 * @throws RuntimeException
	 */
	private void addPagingUrl(Set<URL> urlSet) throws RuntimeException {
		int pageNo = config.getMaxPageNo();
		Set<URL> pagingSet = new HashSet<URL>();
		for (URL url : urlSet) {
			String urlStr = url.toFullString();
			while (pageNo >= 0) {
				String pageKey = config.getNextPageRegex().substring(0, config.getNextPageRegex().length() - 1)
						+ pageNo;
				String target = urlStr.replace(config.getNextPageRegex(), pageKey);
				pagingSet.add(URL.valueOf(target));
				pageNo--;
			}
		}
		urlSet.addAll(pagingSet);
	}

	
	/**
	 * Get a collection of pages for details.
	 * @return
	 * @throws RuntimeException
	 */
	public Set<URL> getTargetPageURLs() throws RuntimeException {
		Set<URL> set = new HashSet<URL>();
		String url = config.getUrl();
		if (StringUtils.isNotEmpty(url)) {
			if (url.contains("|")) {
				String[] urlArray = url.split("\\|");
				for (String target : urlArray) {
					target = target.trim();
					if(target!=null && target.length()>0){
						set.add(URL.valueOf(target));
					}
				}
			}
		}
		return set;
	}

	/**
	 * The urlsToCrawl will exclude the collection. 
	 * @return exclude collection
	 */
	public abstract Collection<URL> getUrlsToFilter();

	/**
	 * The urlsToUpdate collection will be return.
	 * @return urlsToUpdate
	 */
	public abstract Collection<URL> getUrlsToUpdate();

	
	/**
	 * execute before the crawler exit 
	 */
	public abstract void onBeforeExit();

	/**
	 * execute before the crawler start
	 */
	public abstract void onBeforeStart();
}
