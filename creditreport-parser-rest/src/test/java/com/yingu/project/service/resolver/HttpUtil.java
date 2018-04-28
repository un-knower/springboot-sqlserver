package com.yingu.project.service.resolver;


import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by MMM on 2017/08/21.
 */
public class HttpUtil {

    private static final Logger log=  LoggerFactory.getLogger(HttpUtil.class);
    public static HttpUtil httpUtil;

    @PostConstruct
    public void init() {
        httpUtil = this;
    }

    /**
     * @param url
     * @param Params
     * @return
     * @throws IOException
     * @作用 使用urlconnection
     */
    public static String sendPost(String url, String Params) throws IOException {
        OutputStreamWriter out = null;
        BufferedReader reader = null;
        String response = "";
        try {
            URL httpUrl = null; //HTTP URL类 用这个类来创建连接
            //创建URL
            httpUrl = new URL(url);
            //建立连接
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);//连接超时 单位毫秒
            conn.setReadTimeout(30000);//读取超时
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setUseCaches(false);//设置不要缓存
            conn.setInstanceFollowRedirects(true);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            //POST请求
            out = new OutputStreamWriter(
                    conn.getOutputStream());
            out.write(Params);
            out.flush();
            //读取响应
            reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String lines;
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                response += lines;
            }
            reader.close();
            // 断开连接
            conn.disconnect();
        } catch (Exception e) {
            log.error("httppost error:" + e.getMessage(), e);
            log.error("post url:" + url);
            log.error("post param:" + Params);
            log.error("httppost.error.result:" + response);
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return response;
    }

    public static String sendGet(String url) {
        String response = "";
        try {
            URL httpUrl = new URL(url);
            SslUtils.ignoreSsl();
            HttpURLConnection uRLConnection = (HttpURLConnection) httpUrl.openConnection();
            uRLConnection.setConnectTimeout(10000);//连接超时 单位毫秒
            uRLConnection.setReadTimeout(30000);//读取超时
            InputStream is = uRLConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String readLine = null;
            while ((readLine = br.readLine()) != null) {
                //response = br.readLine();
                response = response + readLine;
            }
            is.close();
            br.close();
            uRLConnection.disconnect();
            return response;
        } catch (Exception e) {
            log.error("httpget error:" + e.getMessage(), e);
            log.error("httpget url" + url);
            log.error("httpget.error.result:" + response);
            return response;
        }
    }

    //sendPost statusCode
    private static RequestConfig requestConfig = null;

    static {
        //设置http的状态参数
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(60000)
                .setConnectTimeout(60000)
                .setConnectionRequestTimeout(60000)
                .build();
    }

   /* public static HttpResult post(String url, String Params) {
        HttpResult result = new HttpResult();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(Params, "UTF-8");//解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);

            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            result.setStatus(httpResponse.getStatusLine().getStatusCode());

            HttpEntity entity = httpResponse.getEntity();
            if (entity == null) {
                return null;
            }
            result.setResponse(EntityUtils.toString(entity, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }*/

}
