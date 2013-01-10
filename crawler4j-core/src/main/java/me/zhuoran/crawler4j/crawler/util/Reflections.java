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

import java.lang.reflect.Constructor;


/**
 * Reflection Util
 * 
 * @author Zoran
 *
 */
public class Reflections {

	private Reflections() {
	}

	/** 
	 * Instantiate an object by constructor.
	 * @param className       The full path name of the class    
	 * @param parameterTypes  parameter type 
	 * @param initargs        parameter values 
	 * @return 
	 */
	@SuppressWarnings("rawtypes")
	public static Object constructorNewInstance(String className, Class[] parameterTypes, Object[] initargs) {
		try {
			Constructor<?> constructor = Class.forName(className).getDeclaredConstructor(parameterTypes); //暴力反射  
			constructor.setAccessible(true);
			return constructor.newInstance(initargs);
		} catch (Exception ex) {
			throw new RuntimeException();
		}

	}

}
