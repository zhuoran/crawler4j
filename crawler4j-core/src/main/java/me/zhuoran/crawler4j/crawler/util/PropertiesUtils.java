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
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PropertiesUtils
 * @author Zoran
 *
 */
public class PropertiesUtils {

	private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

	/**
	 * Load properties from default classpath:
	 * @param resourcesPaths  resource path
	 * @return Properties
	 * @throws IOException
	 */
	public static Properties loadProperties(String... resourcesPaths) throws IOException {
		Properties props = new Properties();
		for (String location : resourcesPaths) {
			logger.debug("Loading properties file from:" + location);
			InputStream is = null;
			try {
				is = PropertiesUtils.class.getClassLoader().getResourceAsStream(location);//new FileInputStream(location);
				if(is !=null){
					props.load(is);
				}else{
					logger.error("Could not load properties from classpath:" + location);
				}
				
			} catch (IOException e) {
				logger.error("Could not load properties from classpath:" + location + ": " + e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				logger.error("Could not load properties from classpath:" + location + ": " + e.getMessage());
				e.printStackTrace();
			} finally {
				if (is != null) {
					is.close();
				}
			}
		}
		return props;
	}
}
