package com.atlant1c.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtils {

    // 信任所有证书（用于测试目的）
    static {
        try {
            TrustManager[] trustAll = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAll, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String sendPostRequest(String url, String urlParameters, Map<String, String> headers) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        applyHeaders(con, headers);
        con.setDoOutput(true);
        con.getOutputStream().write(urlParameters.getBytes());

        int responseCode = con.getResponseCode();
        System.out.println("响应代码 : " + responseCode);

        StringBuilder headersResponse = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : con.getHeaderFields().entrySet()) {
            if (entry.getKey() != null) {
                headersResponse.append(entry.getKey()).append(": ").append(String.join(", ", entry.getValue())).append("\n");
            }
        }
        System.out.println("头信息: \n" + headersResponse.toString());

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String responseString = response.toString();
        System.out.println("响应内容: \n" + headersResponse.toString() + "\n" + responseString);

        return responseString;
    }


    public static String sendPostRequestForSpring(String url, String urlParameters, Map<String, String> headers) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        applyHeaders(con, headers);
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            os.write(urlParameters.getBytes());
        }

        int responseCode = con.getResponseCode();
        System.out.println("响应代码 : " + responseCode);

        // 处理响应头
        StringBuilder headersResponse = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : con.getHeaderFields().entrySet()) {
            if (entry.getKey() != null && entry.getKey().startsWith("Frieren")) {
                headersResponse.append(entry.getKey()).append(": ").append(String.join(", ", entry.getValue())).append("\n");
            }
        }
        return headersResponse.toString();
    }

    public static String sendGetRequest(String url, Map<String, String> headers) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        applyHeaders(con, headers);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    public static boolean sendPostRequestForTest(String url, String urlParameters, Map<String, String> headers, boolean checkHeader) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        applyHeaders(con, headers);
        con.setDoOutput(true);
        con.getOutputStream().write(urlParameters.getBytes());

        System.out.println(urlParameters);

        int responseCode = con.getResponseCode();
        System.out.println("响应代码 : " + responseCode);

        StringBuilder headersResponse = new StringBuilder();
        for (String header : con.getHeaderFields().keySet()) {
            if (header != null) {
                headersResponse.append(header).append(": ").append(con.getHeaderField(header)).append("\n");
            }
        }
        System.out.println("头信息: \n" + headersResponse.toString());


/*        System.out.println("响应内容: \n" + headersResponse.toString() + "\n" + responseString);*/

        System.out.println(headersResponse.toString().contains("Frieren"));

        String regex = "Frieren";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(headersResponse.toString());
        // 返回是否匹配
        System.out.println(matcher.find());

        if (checkHeader) {
            return headersResponse.toString().contains("Frieren");
        } else {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String responseString = response.toString();
/*            System.out.println(responseString);*/
            return responseString.startsWith("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\">");
        }
    }

    private static void applyHeaders(HttpURLConnection con, Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            con.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }
}
