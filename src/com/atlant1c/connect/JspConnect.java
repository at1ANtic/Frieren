package com.atlant1c.connect;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class JspConnect {

    public static String executeCommand(String url, String pass, String command) {
        try {
            // 拼接参数
            String query = String.format("%s=%s",
                    URLEncoder.encode(pass, "UTF-8"),
                    URLEncoder.encode(command, "UTF-8"));
            String fullUrl = url + "frieren" + "?" + query;

            // 创建 URL 对象
            URL apiUrl = new URL(fullUrl);

            // 创建 HttpURLConnection 对象
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

            // 设置请求方法
            connection.setRequestMethod("GET");

            // 发送请求并获取响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            // 返回响应内容
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
