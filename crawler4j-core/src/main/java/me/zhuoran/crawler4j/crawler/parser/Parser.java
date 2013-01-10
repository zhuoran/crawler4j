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
package me.zhuoran.crawler4j.crawler.parser;

import me.zhuoran.crawler4j.crawler.http.HttpFetchResult;

/**
 * Parser for Fetch Result
 * 
 * @author Zoran
 *
 */
public interface Parser {

	/**
	 * After crawling do parse
	 * @param result the fetch result
	 * @param url the target url
	 * @param threadName the crawler name
	 * @param isUpdate true is update
	 */
	public void parse(final HttpFetchResult result, final String url, final String threadName, final boolean isUpdate);

}
