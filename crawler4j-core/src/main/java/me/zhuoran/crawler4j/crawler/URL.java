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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * URL - Uniform Resource Locator (Immutable, ThreadSafe)
 * 
 * @author Zoran
 *
 */
public final class URL implements Serializable {

	private static final long serialVersionUID = -1985165475234945635L;

	private final String protocol;

	private final String host;
	
	private final String path;

	private final Map<String, String> parameters;
	
	private volatile transient String full;

	protected URL() {
		this.protocol = null;
		this.host = null;
		this.path = null;
		this.parameters = null;
	}

	public URL(String protocol, String host, String path, Map<String, String> parameters) {
		this.protocol = protocol;
		this.host = host;
		this.path = path;
		// trim the beginning "/"
		while (path != null && path.startsWith("/")) {
			path = path.substring(1);
		}
		if (parameters == null) {
			parameters = new HashMap<String, String>();
		} else {
			parameters = new HashMap<String, String>(parameters);
		}
		this.parameters = Collections.unmodifiableMap(parameters);
	}

	/**
	 * Validate the url string.
	 * @param url
	 * @return true is URL ,otherwise is not a URL
	 */
	public static boolean validate(String url) {
		try {
			new java.net.URI(url);
		} catch (URISyntaxException e) {
			return false;
		}
		return true;
	}

	/**
	 * Parse url string
	 * 
	 * @param url URL string
	 * @return URL instance
	 * @see URL
	 */
	public static URL valueOf(String url) {
		if (url == null || (url = url.trim()).length() == 0) {
			throw new IllegalArgumentException("url == null");
		}
		if (!validate(url)) {
			throw new IllegalArgumentException("url format error");
		}
		String protocol = null;
		String host = null;
		String path = null;
		Map<String, String> parameters = null;
		int i = url.indexOf("?"); // seperator between body and parameters 
		if (i >= 0) {
			String[] parts = url.substring(i + 1).split("\\&");
			parameters = new HashMap<String, String>();
			for (String part : parts) {
				part = part.trim();
				if (part.length() > 0) {
					int j = part.indexOf('=');
					if (j >= 0) {
						parameters.put(part.substring(0, j), part.substring(j + 1));
					} else {
						parameters.put(part, part);
					}
				}
			}
			url = url.substring(0, i);
		}
		i = url.indexOf("://");
		if (i >= 0) {
			if (i == 0) {
				throw new IllegalStateException("url missing protocol: \"" + url + "\"");
			}
			protocol = url.substring(0, i);
			url = url.substring(i + 3);
		} else {
			// case: file:/path/to/file.txt
			i = url.indexOf(":/");
			if (i >= 0) {
				if (i == 0) {
					throw new IllegalStateException("url missing protocol: \"" + url + "\"");
				}
				protocol = url.substring(0, i);
				url = url.substring(i + 1);
			}
		}

		i = url.indexOf("/");
		if (i >= 0) {
			path = url.substring(i + 1);
			url = url.substring(0, i);
		}

		if (url.length() > 0) {
			host = url;
		}
		return new URL(protocol, host, path, parameters);
	}

	public String getProtocol() {
		return protocol;
	}

	public String getHost() {
		return host;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}
	
	public String getParameter(String key) {
        String value = parameters.get(key);
        return value;
    }

	public String getPath() {
		return path;
	}

	
	public String toFullString() {
	    if (full != null) {
	        return full;
	    }
		return full = buildString(true);
	}
	
	public String toFullString(String... parameters) {
        return buildString(true, parameters);
    }
	
	private String buildString(boolean appendParameter,String... parameters){
		StringBuilder buf = new StringBuilder();
		if (protocol != null && protocol.length() > 0) {
			buf.append(protocol);
			buf.append("://");
		}
		String host = getHost();
		if(host != null && host.length() > 0) {
    		buf.append(host);
		}
		String path = getPath();
		if (path != null && path.length() > 0) {
			buf.append("/");
			buf.append(path);
		}
		if (appendParameter) {
		    buildParameters(buf, true, parameters);
		}
		return buf.toString();
	}
	
	public URL addParameter(String key, String value) {
		if (key == null || key.length() == 0 || value == null || value.length() == 0) {
			return this;
		}
		// 如果没有修改，直接返回。
		if (value.equals(getParameters().get(key))) { // value != null
			return this;
		}

		Map<String, String> map = new HashMap<String, String>(getParameters());
		map.put(key, value);
		return new URL(protocol, host, path, map);
	}
	
	
	private void buildParameters(StringBuilder buf, boolean concat, String[] parameters) {
	    if (getParameters() !=null && getParameters().size() > 0) {
            List<String> includes = (parameters == null || parameters.length == 0 ? null : Arrays.asList(parameters));
            boolean first = true;
            for (Map.Entry<String, String> entry : new TreeMap<String, String>(getParameters()).entrySet()) {
                if (entry.getKey() != null && entry.getKey().length() > 0
                        && (includes == null || includes.contains(entry.getKey()))) {
                    if (first) {
                        if (concat) {
                            buf.append("?");
                        }
                        first = false;
                    } else {
                        buf.append("&");
                    }
                    buf.append(entry.getKey());
                    buf.append("=");
                    buf.append(entry.getValue() == null ? "" : entry.getValue().trim());
                }
            }
        }
	}
	
	  public URL addParameterAndEncoded(String key, String value) {
	        if(value == null || value.length() == 0) {
	            return this;
	        }
	        return addParameter(key, encode(value));
	    }
	
	public static String encode(String value) {
		if (value == null || value.length() == 0) {
			return "";
		}
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static String decode(String value) {
		if (value == null || value.length() == 0) {
			return "";
		}
		try {
			return URLDecoder.decode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		URL other = (URL)obj;
		
		if (host == null) {
			if (other.host != null) {
				return false;
			}
		} else if (!host.equals(other.host)) {
			return false;
		}
		if (parameters == null) {
			if (other.parameters != null) {
				return false;
			}
		} else if (!parameters.equals(other.parameters)) {
			return false;
		}
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.equals(other.path)) {
			return false;
		}
		if (protocol == null) {
			if (other.protocol != null) {
				return false;
			}
		} else if (!protocol.equals(other.protocol)) {
			return false;
		}
		return true;
	}

}