package com.atlant1c.connect;

import com.atlant1c.utils.HttpUtils;

import java.util.HashMap;
import java.util.Map;

public class SpringConnect {
    public static String executeCommand(String url, String pass, String command,String value) {
        try {
            // Prepare the URL parameters and headers
            String urlParameters = value+"&"+pass + "=" + command;
            Map<String, String> headers = new HashMap<>();
            String response = HttpUtils.sendPostRequestForSpring(url, urlParameters, headers);

            String result = response.substring(9);
            // Return the response content
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "命令执行失败";
        }
    }
}
