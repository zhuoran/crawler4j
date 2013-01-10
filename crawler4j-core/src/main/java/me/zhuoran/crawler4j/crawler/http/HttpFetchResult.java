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
package me.zhuoran.crawler4j.crawler.http;

import java.io.EOFException;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;

/**
 * HttpFetchResult
 * 
 * @author Zoran
 *
 */
public class HttpFetchResult {

	protected HttpEntity entity;

	protected String html;

	protected byte[] contentData;

	protected StatusLine status;

	protected String charset;

	public HttpFetchResult(HttpResponse rep, String defaultCharset) throws Exception {
		entity = rep.getEntity();
		status = rep.getStatusLine();
		this.charset = defaultCharset;
		if (entity.getContentType() != null && entity.getContentType().getValue().contains("text/html")) {
			html = EntityUtils.toString(entity, charset);
		} else {
			contentData = EntityUtils.toByteArray(entity);
		}
	}

	public String getCharset() {
		return charset;
	}

	public byte[] getContentData() {
		return contentData;
	}

	public String getHtml() {
		return html;
	}

	public HttpEntity getEntity() {
		return entity;
	}

	public StatusLine getStatus() {
		return status;
	}

	/**
	 * Ensures that the entity content is fully consumed and the content stream, if exists, is closed.
	 * 
	 */
	public void consume() {
		try {
			if (entity != null) {
				EntityUtils.consume(entity);
			}
		} catch (EOFException e) {
			// We can ignore this exception. It can happen on compressed streams
			// which are not
			// repeatable
		} catch (IOException e) {
			// We can ignore this exception. It can happen if the stream is
			// closed.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
