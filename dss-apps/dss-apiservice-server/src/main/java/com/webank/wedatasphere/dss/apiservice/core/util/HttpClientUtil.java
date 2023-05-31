/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.apiservice.core.util;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public final class HttpClientUtil {
	private final static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	public final static int connectTimeout = 5000;
	private static PoolingHttpClientConnectionManager connManager = null;
	private static CloseableHttpClient httpclient = null;


	static {
		httpclient = HttpClients.createDefault();
	}

    public static String postForm(String url, int timeout, Map<String, Object> headerMap, List<NameValuePair> paramsList, String encoding){
        HttpPost post = new HttpPost(url);
        try {
        	if(headerMap != null){
        		for(Entry<String, Object> entry : headerMap.entrySet()){
        			post.setHeader(entry.getKey(), entry.getValue().toString());
                }
        	}
            //post.setHeader("Content-type", "application/json");
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout)
                    .setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .setExpectContinueEnabled(false).build();
            post.setConfig(requestConfig);

            post.setEntity(new UrlEncodedFormEntity(paramsList, encoding));
            CloseableHttpResponse response = httpclient.execute(post);
            try {
                HttpEntity entity = response.getEntity();
                try {
                    if(entity != null){
                        String str = EntityUtils.toString(entity, encoding);
                        return str;
                    }
                } finally {
                    if(entity != null){
                        entity.getContent().close();
                    }
                }
            } finally {
                if(response != null){
                    response.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("invoke http post error!",e);
        } finally {
            post.releaseConnection();
        }
        return "";
    }

	public static String postJsonBody(String url, int timeout, Map<String, Object> headerMap,
			String paraData, String encoding) {

		logger.info("successfully  start post Json Body  url{} ", url);
		HttpPost post = new HttpPost(url);
		try {
			if (headerMap != null) {
				for (Entry<String, Object> entry : headerMap.entrySet()) {
					post.setHeader(entry.getKey(), entry.getValue().toString());
				}
			}
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false).build();
			StringEntity jsonEntity = new StringEntity(paraData, ContentType.APPLICATION_JSON);
			post.setConfig(requestConfig);
			post.setEntity(jsonEntity);
			CloseableHttpResponse response = httpclient.execute(post);
			try {
				HttpEntity entity = response.getEntity();
				try {
					if (entity != null) {
						String str = EntityUtils.toString(entity, encoding);
						return str;
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException", e);
			throw new RuntimeException("failed post json return blank!");
		} catch (Exception e) {
			logger.error("Exception", e);
			throw new RuntimeException("failed post json return blank!");
		} finally {
			post.releaseConnection();
		}
		logger.info("successfully  end post Json Body  url{} ", url);
		return "";
	}

	@SuppressWarnings("deprecation")
	public static String invokeGet(String url, Map<String, String> params, String encode, int connectTimeout,
			int soTimeout) {
		String responseString = null;
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(connectTimeout)
				.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();

		StringBuilder sb = new StringBuilder();
		sb.append(url);
		int i = 0;
		if (params != null) {
			for (Entry<String, String> entry : params.entrySet()) {
				if (i == 0 && !url.contains("?")) {
					sb.append("?");
				} else {
					sb.append("&");
				}
				sb.append(entry.getKey());
				sb.append("=");
				String value = entry.getValue();
				try {
					sb.append(URLEncoder.encode(value, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					logger.warn("encode http get params error, value is " + value, e);
					sb.append(URLEncoder.encode(value));
				}
				i++;
			}
		}
		HttpGet get = new HttpGet(sb.toString());
		get.setConfig(requestConfig);
		try {
			CloseableHttpResponse response = httpclient.execute(get);
			try {
				HttpEntity entity = response.getEntity();
				try {
					if (entity != null) {
						responseString = EntityUtils.toString(entity, encode);
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} catch (Exception e) {
				logger.error(String.format("[HttpUtils Get]get response error, url:%s", sb.toString()), e);
				return responseString;
			} finally {
				if (response != null) {
					response.close();
				}
			}
			// System.out.println(String.format("[HttpUtils Get]Debug url:%s ,
			// response string %s:", sb.toString(), responseString));
		} catch (SocketTimeoutException e) {
			logger.error(String.format("[HttpUtils Get]invoke get timout error, url:%s", sb.toString()), e);
			return responseString;
		} catch (Exception e) {
			logger.error(String.format("[HttpUtils Get]invoke get error, url:%s", sb.toString()), e);
		} finally {
			get.releaseConnection();
		}
		return responseString;
	}

	/**
	 * HTTPS请求，默认超时为5S
	 *
	 * @param reqURL
	 * @param params
	 * @return
	 */
	public static String connectPostHttps(String reqURL, Map<String, String> params) {

		String responseContent = null;
		HttpPost httpPost = new HttpPost(reqURL);
		try {
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(connectTimeout)
					.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, Consts.UTF_8));
			httpPost.setConfig(requestConfig);
			// 绑定到请求 Entry
			for (Entry<String, String> entry : params.entrySet()) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			CloseableHttpResponse response = httpclient.execute(httpPost);
			try {
				// 执行POST请求
				HttpEntity entity = response.getEntity(); // 获取响应实体
				try {
					if (null != entity) {
						responseContent = EntityUtils.toString(entity, Consts.UTF_8);
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} finally {
				if (response != null) {
					response.close();
				}
			}
			logger.info("requestURI : " + httpPost.getURI() + ", responseContent: " + responseContent);
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
		} finally {
			httpPost.releaseConnection();
		}
		return responseContent;

	}

	class Test {
		String v;
		String k;

		public String getV() {
			return v;
		}

		public void setV(String v) {
			this.v = v;
		}

		public String getK() {
			return k;
		}

		public void setK(String k) {
			this.k = k;
		}

	}

	// 随机4位数
	public static String getRandomValue() {
		String str = "0123456789";
		StringBuilder sb = new StringBuilder(4);
		for (int i = 0; i < 4; i++) {
			char ch = str.charAt(new Random().nextInt(str.length()));
			sb.append(ch);
		}
		return sb.toString();
	}

	// 当前时间到秒
	public static String getTimestamp() {

		Date date = new Date();
		String timestamp = String.valueOf(date.getTime() / 1000);
		return timestamp;
	}

	// 当前时间到秒
	public static String getNowDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(date);
	}

	public static String postJsonBody2(String url, int timeout, Map<String, Object> headerMap,
                                       List<NameValuePair> paramsList, String encoding) {
		logger.info("successfully  start post Json Body  url{} ", url);
		HttpPost post = new HttpPost(url);
		try {
			if (headerMap != null) {
				for (Entry<String, Object> entry : headerMap.entrySet()) {
					post.setHeader(entry.getKey(), entry.getValue().toString());
				}
			}
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false).build();
			post.setConfig(requestConfig);
			if (paramsList.size() > 0) {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramsList, encoding);
				post.setEntity(entity);
			}
			CloseableHttpResponse response = httpclient.execute(post);
			try {
				HttpEntity entity = response.getEntity();
				try {
					if (entity != null) {
						String str = EntityUtils.toString(entity, encoding);
						return str;
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException", e);
			throw new RuntimeException("failed post json return blank!");
		} catch (Exception e) {
			logger.error("Exception", e);
			throw new RuntimeException("failed post json return blank!");
		} finally {
			post.releaseConnection();
		}
		logger.info("successfully  end post Json Body  url{} ", url);
		return "";
	}

	public static String postJsonBody3(String url, int timeout, Map<String, Object> headerMap,
			Map<String, Object> paramsList, String encoding) {
		HttpPost post = new HttpPost(url);
		try {
			if (headerMap != null) {
				for (Entry<String, Object> entry : headerMap.entrySet()) {
					post.setHeader(entry.getKey(), entry.getValue().toString());
				}
			}
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
					.setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false).build();
			post.setConfig(requestConfig);
			if (paramsList.size() > 0) {
				//JSONArray jsonArray = JSONArray.fromObject(paramsList);
				//post.setEntity(new StringEntity(jsonArray.get(0).toString(), encoding));
				post.setEntity(new StringEntity(null, encoding));
				//logger.info("successfully  start post Json Body  url{},params ", url,jsonArray.get(0).toString());
				logger.info("successfully  start post Json Body  url{},params ", url,null);
			}
			CloseableHttpResponse response = httpclient.execute(post);
			try {
				HttpEntity entity = response.getEntity();
				try {
					if (entity != null) {
						String str = EntityUtils.toString(entity, encoding);
						return str;
					}
				} finally {
					if (entity != null) {
						entity.getContent().close();
					}
				}
			} finally {
				if (response != null) {
					response.close();
				}
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("UnsupportedEncodingException", e);
			throw new RuntimeException("failed post json return blank!");
		} catch (Exception e) {
			logger.error("Exception", e);
			throw new RuntimeException("failed post json return blank!");
		} finally {
			post.releaseConnection();
		}
		logger.info("successfully  end post Json Body  url{} ", url);
		return "";
	}

	public static String executeGet(String url)
	{
		String rtnStr = "";
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse httpResponse = httpclient.execute(httpGet);
			//获得返回的结果
			rtnStr = EntityUtils.toString(httpResponse.getEntity());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpGet.releaseConnection();
		}
		return rtnStr;
	}

}
