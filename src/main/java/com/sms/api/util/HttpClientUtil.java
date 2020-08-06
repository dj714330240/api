package com.sms.api.util;
import com.alibaba.druid.support.json.JSONUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: api
 * @description: 网络请求工具类
 * @author: dty
 * @create: 2020-06-20 22:31
 **/
public class HttpClientUtil {


    /***
     * @Description: get请求
     * @Param: [url]
     * @return: java.lang.String
     * @Author: dty
     * @Date: 2020-06-20 22:32
     */
    public static String get(String url) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {

                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                return content;
            }
        } finally {
            if (response != null) {
                response.close();
            }
            httpclient.close();
        }
        return null;
    }


    public static Map<String,String> getMap(String url) throws IOException {
        Map<String,String> data = new HashMap<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {

                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                data.put("data",content);
                Header header =  response.getHeaders("Set-Cookie")[0];
                data.put("cookie", header.getValue());
                return data;
            }
        } finally {
            if (response != null) {
                response.close();
            }
            httpclient.close();
        }
        return null;
    }



    public static String get(String url, Map<String,String> headMap) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();


        HttpGet httpGet = new HttpGet(url);
        if(headMap!=null&&headMap.size()>0){
            for (Map.Entry<String, String> head : headMap.entrySet()) {
                httpGet.addHeader(new BasicHeader(head.getKey(), head.getValue()));
            }
        }
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                return content;
            }
        } finally {
            if (response != null) {
                response.close();
            }
            httpclient.close();
        }
        return null;
    }


    public static Map<String,String> postMap(String url,Map<String,String> headMap,Map<String,String> paramss) {
        Map<String,String> data = new HashMap<>();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient client = HttpClients.createDefault();
        List params = new ArrayList();

        if(paramss!=null&&paramss.size()>0){
            for (Map.Entry<String, String> pam : paramss.entrySet()) {
                params.add(new BasicNameValuePair(pam.getKey(), pam.getValue()));
            }
        }
        if(headMap!=null&&headMap.size()>0){
            for (Map.Entry<String, String> head : headMap.entrySet()) {
                httpPost.addHeader(new BasicHeader(head.getKey(), head.getValue()));
            }
        }
        CloseableHttpResponse response = null;
        try {
            HttpEntity httpEntity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(httpEntity);
            response = client.execute(httpPost);
//            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                Header header =  response.getHeaders("Set-Cookie")[0];
                data.put("cookie", header.getValue());
                data.put("data",content);
                return data;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(client != null){
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
