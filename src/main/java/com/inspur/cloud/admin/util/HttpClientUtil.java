package com.inspur.cloud.admin.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;

import javax.management.RuntimeErrorException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

/**
 *
 * @author H__D
 * @date 2016年10月19日 上午11:27:25
 *
 */
@Component
public class HttpClientUtil {

	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	// utf-8字符编码
	public static final String CHARSET_UTF_8 = "utf-8";

	// HTTP内容类型。
	public static final String CONTENT_TYPE_TEXT_HTML = "text/xml";

	// HTTP内容类型。相当于form表单的形式，提交数据
	public static final String CONTENT_TYPE_FORM_URL = "application/x-www-form-urlencoded";

	// HTTP内容类型。相当于form表单的形式，提交数据
	public static final String CONTENT_TYPE_JSON_URL = "application/json;charset=utf-8";

	// 连接管理器
	private static PoolingHttpClientConnectionManager pool;

	// 请求配置
	private static RequestConfig requestConfig;

	static {

		try {
			// System.out.println("初始化HttpClientTest~~~开始");
			SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build());
			// 配置同时支持 HTTP 和 HTPPS
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslsf).build();
			// 初始化连接管理器
			pool = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			// 将最大连接数增加到200，实际项目最好从配置文件中读取这个值
			pool.setMaxTotal(200);
			// 设置最大路由
			pool.setDefaultMaxPerRoute(2);
			// 根据默认超时限制初始化requestConfig
			int socketTimeout = 10000;
			int connectTimeout = 10000;
			int connectionRequestTimeout = 10000;
			requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
					.setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();

			// System.out.println("初始化HttpClientTest~~~结束");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

		// 设置请求超时时间
		requestConfig = RequestConfig.custom().setSocketTimeout(50000).setConnectTimeout(50000)
				.setConnectionRequestTimeout(50000).build();
	}

	public static CloseableHttpClient getHttpClient() {

		CloseableHttpClient httpClient = HttpClients.custom()
				// 设置连接池管理
				.setConnectionManager(pool)
				// 设置请求配置
				.setDefaultRequestConfig(requestConfig)
				// 设置重试次数
				.setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).build();

		return httpClient;
	}

	/**
	 * 发送Get请求
	 *
	 * @param httpGet
	 * @return
	 */
	private static String sendHttpGet(HttpGet httpGet) {

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		// 响应内容
		String responseContent = null;
		try {
			// 创建默认的httpClient实例.
			httpClient = getHttpClient();
			// 配置请求信息
			httpGet.setConfig(requestConfig);
			// 执行请求
			response = httpClient.execute(httpGet);
			// 得到响应实例
			HttpEntity entity = response.getEntity();

			// 可以获得响应头
			// Header[] headers = response.getHeaders(HttpHeaders.CONTENT_TYPE);
			// for (Header header : headers) {
			// System.out.println(header.getName());
			// }

			// 得到响应类型
			// System.out.println(ContentType.getOrDefault(response.getEntity()).getMimeType());

			// 判断响应状态
			if (response.getStatusLine().getStatusCode() >= 300) {
				throw new Exception(
						"HTTP Request is not success, Response code is " + response.getStatusLine().getStatusCode());
			}

			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				responseContent = EntityUtils.toString(entity, CHARSET_UTF_8);
				EntityUtils.consume(entity);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 释放资源
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return responseContent;
	}

	/**
	 * 发送 get请求
	 *
	 * @param httpUrl
	 */
	public static String sendHttpGet(String httpUrl) {
		// 创建get请求
		HttpGet httpGet = new HttpGet(httpUrl);
		return sendHttpGet(httpGet);
	}

	/**
	 * 将map集合的键值对转化成：key1=value1&key2=value2 的形式
	 *
	 * @param parameterMap 需要转化的键值对集合
	 * @return 字符串
	 */
	public static String convertStringParamter(Map parameterMap) {
		StringBuffer parameterBuffer = new StringBuffer();
		if (parameterMap != null) {
			Iterator iterator = parameterMap.keySet().iterator();
			String key = null;
			String value = null;
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				if (parameterMap.get(key) != null) {
					value = (String) parameterMap.get(key);
				} else {
					value = "";
				}
				parameterBuffer.append(key).append("=").append(value);
				if (iterator.hasNext()) {
					parameterBuffer.append("&");
				}
			}
		}
		return parameterBuffer.toString();
	}

	public static void main(String[] args) throws Exception {

		System.out.println(sendHttpGet("https://172.16.41.26:6443/api/v1/nodes"));

	}

	/**
	 *
	 * 功能描述:
	 *
	 * @Description:获取get请求状态码
	 * @Param:
	 * @Return: void
	 * @Auther: mhy
	 * @Date: 2019/2/22 14:37
	 */
	public static int getStatus(String httpUrl) {
		// 创建get请求
		HttpGet httpGet = new HttpGet(httpUrl);
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		// 响应内容
		String responseContent = null;
		try {
			// 创建默认的httpClient实例.
			httpClient = getHttpClient();
			// 配置请求信息
			httpGet.setConfig(requestConfig);
			// 执行请求
			response = httpClient.execute(httpGet);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 释放资源
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (null != response) {
			int statusCode = response.getStatusLine().getStatusCode();
			return statusCode;
		} else {
			return 0;
		}
	}

	public static String sendHttpPostJson(String httpUrl, String paramsJson) throws Exception {
		HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost

		// 设置参数
		if (paramsJson != null && paramsJson.trim().length() > 0) {
			StringEntity stringEntity = new StringEntity(paramsJson, "UTF-8");
			stringEntity.setContentType(CONTENT_TYPE_JSON_URL);
			httpPost.setEntity(stringEntity);
		}
		return sendHttpPost(httpPost);
	}

	private static String sendHttpPost(HttpPost httpPost) throws Exception {

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		// 响应内容
		String responseContent = null;

		// 创建默认的httpClient实例.
		httpClient = getHttpClient();
		// 配置请求信息
		httpPost.setConfig(requestConfig);
		// 执行请求\
		httpPost.setHeader("User-Agent", "HTTPie/0.9.2");
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate");
		httpPost.setHeader("Connection", "keep-alive");
		httpPost.setHeader("Content-Type", "application/json");
		// httpPost.setHeader("Host", "");
		response = httpClient.execute(httpPost);
		// 得到响应实例
		HttpEntity entity = response.getEntity();
		responseContent = EntityUtils.toString(entity, CHARSET_UTF_8);
		// 可以获得响应头
		// Header[] headers = response.getHeaders(HttpHeaders.CONTENT_TYPE);
		// for (Header header : headers) {
		// System.out.println(header.getName());
		// }

		// 得到响应类型
		// System.out.println(ContentType.getOrDefault(response.getEntity()).getMimeType());

		// 判断响应状态
		if (response.getStatusLine().getStatusCode() >= 300) {
			throw new Exception(
					"HTTP Request is not success, Response code is " + response.getStatusLine().getStatusCode());
		}

		if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
			responseContent = EntityUtils.toString(entity, CHARSET_UTF_8);
			EntityUtils.consume(entity);
		}

		// 释放资源
		if (response != null) {
			response.close();
		}

		return responseContent;
	}

	public static boolean httpPostWithJson(String json, String url) {
		boolean isSuccess = false;

		HttpPost post = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();

			// 设置超时时间
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);

			post = new HttpPost(url);
			// 构造消息头
			post.setHeader("Content-type", "application/json;");
			post.setHeader("Connection", "keep-alive;");
			post.setHeader("Accept", "application/json;");
			post.setHeader("Accept-Encoding", "gzip, deflate;");
			post.setHeader("User-Agent", "HTTPie/0.9.2;");
			post.setHeader("Host", "10.221.129.134:8001;");
			// 构建消息实体
			StringEntity entity = new StringEntity(json, Charset.forName("UTF-8"));
			entity.setContentEncoding("UTF-8");
			// 发送Json格式的数据请求
			entity.setContentType("application/json");
			post.setEntity(entity);

			HttpResponse response = httpClient.execute(post);
			HttpEntity entity1 = response.getEntity();
			String responseContent = EntityUtils.toString(entity1, CHARSET_UTF_8);
			// 检验返回码
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				logger.info("请求出错: " + statusCode);
				isSuccess = false;
			} else {
				int retCode = 0;
				String sessendId = "";
				// 返回码中包含retCode及会话Id
				for (Header header : response.getAllHeaders()) {
					if (header.getName().equals("retcode")) {
						retCode = Integer.parseInt(header.getValue());
					}
					if (header.getName().equals("SessionId")) {
						sessendId = header.getValue();
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			isSuccess = false;
		}
		return isSuccess;
	}

}