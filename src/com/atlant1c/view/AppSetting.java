package com.atlant1c.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AppSetting extends JPanel {

    private static final String HEADER_FILE_PATH = "src/com/atlant1c/utils/headers.bin";
    private JTextArea textArea;
    private JButton saveButton;

    public AppSetting() {
        setLayout(new BorderLayout());

        // Create and set up the title label
        JLabel titleLabel = new JLabel("协议头配置", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 16)); // 使用支持中文的字体
        add(titleLabel, BorderLayout.NORTH);

        // Create and set up the JTextArea
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12)); // 使用支持中文的字体
        textArea.setEditable(true);

        // Load the headers from the file and display in the text area
        loadHeadersFromFile();

        // Create and set up the save button
        saveButton = new JButton("保存修改");
        saveButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12)); // 使用支持中文的字体
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveHeadersToFile();
            }
        });

        // Add components to the panel
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
    }

    private void loadHeadersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HEADER_FILE_PATH))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            textArea.setText(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "无法加载文件内容", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveHeadersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HEADER_FILE_PATH))) {
            writer.write(textArea.getText());
            JOptionPane.showMessageDialog(this, "文件已保存", "信息", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "无法保存文件内容", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
