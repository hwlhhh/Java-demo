package http.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class HttpClientUtil {


    public static final String CHARASET_UTF_8 = "utf-8";
    public static final String CONTENT_TYPE_TEXT_HTML = "text/xml";
    public static final String CONTENT_TYPE_FORM_URL = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_JSON_URL = "application/json;charset=utf-8";

    // 连接管理器
    private static PoolingHttpClientConnectionManager pool;
    // 请求配置
    private static RequestConfig requestConfig;

    static {
        // 支持https协议
        LayeredConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Registry<ConnectionSocketFactory> socketFactoryRegistry;
        if (sslsf != null) {
            socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslsf)
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .build();
        } else {
            socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .build();
        }

        // 初始化连接管理器
        pool = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // 将最大连接数增大到200
        pool.setMaxTotal(10);
        // 设置最大路由
        pool.setDefaultMaxPerRoute(200);

        int socketTimeout = 120000;
        int connectTimeout = 120000;
        int connectRequeestTimeout = 120000;

        requestConfig = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectRequeestTimeout).build();
    }

    /**
     * 创建默认客户端
     */
    public static HttpClientBuilder getHttpClientBuilder() {
        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setConnectionManager(pool)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false));

        return httpClientBuilder;
    }


    public static CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(pool)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                .build();

        return httpClient;
    }

    /**
     * 发送Get请求
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static Map<String, String> sendHttpGet(String url) throws Exception {
        HttpGet httpGet = new HttpGet(url);
        return getHttpGet(httpGet);
    }

    private static Map<String, String> getHttpGet(HttpGet httpGet) throws Exception {
        Map<String, String> result = new HashMap<>();
        CloseableHttpResponse response = null;
        HttpEntity entity = null;

        try {
            CloseableHttpClient httpClient = getHttpClient();
            httpGet.setConfig(requestConfig);
            httpGet.setHeader(HttpHeaders.CONNECTION, "close");
            response = httpClient.execute(httpGet);
            result.put("statusCode", String.valueOf(response.getStatusLine().getStatusCode()));
            entity = response.getEntity();

            if (response.getStatusLine().getStatusCode() >= 300) {
                httpGet.abort();
                throw new Exception("HTTP request is not success");
            }
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String responseContent = EntityUtils.toString(entity, CHARASET_UTF_8);
                result.put("content", responseContent);
                EntityUtils.consume(entity);
            } else {
                httpGet.abort();
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(entity);
            }
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
            response = null;
            httpGet = null;
        }
        return result;
    }


    /**
     * Post方法，发送Json格式数据
     *
     * @param url
     * @param paramsJson
     * @return
     * @throws IOException
     */
    public static Map<String, String> sendHttpPostJson(String url, String paramsJson) throws Exception {
        return sendHttpPost(getHttpPost(url, paramsJson, CONTENT_TYPE_JSON_URL));
    }

    private static HttpPost getHttpPost(String url, String params, String paramsType) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        try {
            if (params != null && params.trim().length() > 0) {
                StringEntity stringEntity = new StringEntity(params, CHARASET_UTF_8);
                stringEntity.setContentType(paramsType);
                httpPost.setEntity(stringEntity);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
        return httpPost;
    }

    private static Map<String, String> sendHttpPost(HttpPost httpPost) throws Exception {
        Map<String, String> result = new HashMap<>();
        CloseableHttpResponse response = null;
        HttpEntity entity = null;

        try {
            CloseableHttpClient httpClient = getHttpClient();
            httpPost.setConfig(requestConfig);
            httpPost.setHeader(HttpHeaders.CONNECTION, "close");
            response = httpClient.execute(httpPost);
            result.put("statusCode", String.valueOf(response.getStatusLine().getStatusCode()));
            entity = response.getEntity();

            if (response.getStatusLine().getStatusCode() >= 300) {
                httpPost.abort();
                throw new Exception("HTTP request is not success");
            }
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String responseContent = EntityUtils.toString(entity, CHARASET_UTF_8);
                result.put("content", responseContent);
                EntityUtils.consume(entity);
            } else {
                httpPost.abort();
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(entity);
            }
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
            response = null;
            httpPost = null;
        }
        return result;
    }

}
