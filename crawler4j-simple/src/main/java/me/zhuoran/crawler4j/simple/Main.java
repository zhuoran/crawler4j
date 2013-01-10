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

import me.zhuoran.crawler4j.crawler.CrawlController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
	
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		try {
			CrawlController controller = new CrawlController();
			controller.start();
		} catch (RuntimeException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			System.exit(1);
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
}
