package com.atlant1c.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.UUID;

import com.atlant1c.connect.TestConnect;
import com.atlant1c.model.ShellENV;
import com.atlant1c.utils.DatabaseHelper;
import com.atlant1c.model.Shell;

public class AddTarget extends JPanel {
    private JTextField urlField;
    private JTextField passwordField;
    private JTextField connTimeoutField;
    private JTextField readTimeoutField;
    private JTextField springTextField; // 新的文本框

    private JComboBox<String> encodingComboBox;
    private JComboBox<String> payloadComboBox;
    private ViewMain viewMain;

    public AddTarget(ViewMain viewMain) {
        this.viewMain = viewMain;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // URL
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("URL"), gbc);
        urlField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(urlField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("密码"), gbc);
        passwordField = new JTextField("pass", 20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        // 连接超时
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("连接超时"), gbc);
        connTimeoutField = new JTextField("3000", 20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(connTimeoutField, gbc);

        // 读取超时
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("读取超时"), gbc);
        readTimeoutField = new JTextField("60000", 20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(readTimeoutField, gbc);

        // 编码
        gbc.gridx = 0;
        gbc.gridy = 6;
        add(new JLabel("编码"), gbc);
        encodingComboBox = new JComboBox<>(new String[]{"UTF-8"});
        gbc.gridx = 1;
        gbc.gridy = 6;
        add(encodingComboBox, gbc);

        // 有效载荷
        gbc.gridx = 0;
        gbc.gridy = 7;
        add(new JLabel("有效载荷"), gbc);
        payloadComboBox = new JComboBox<>(new String[]{"JSP(Tomcat)", "PHP", "Spring"});
        gbc.gridx = 1;
        gbc.gridy = 7;
        add(payloadComboBox, gbc);

        // Spring Text
        gbc.gridx = 0;
        gbc.gridy = 8;
        JLabel springTextLabel = new JLabel("路由与exp");
        add(springTextLabel, gbc);
        springTextLabel.setVisible(false); // 初始隐藏
        springTextField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 8;
        add(springTextField, gbc);
        springTextField.setVisible(false); // 初始隐藏

        // 添加和测试连接按钮
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("添加");
        JButton testButton = new JButton("测试连接");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTargetToDatabase();
            }
        });

        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testConnection();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(testButton);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // 添加 ActionListener 来动态显示或隐藏 springTextLabel 和 springTextField
        payloadComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPayload = (String) payloadComboBox.getSelectedItem();
                if ("Spring".equals(selectedPayload)) {
                    springTextLabel.setVisible(true);
                    springTextField.setVisible(true);
                } else {
                    springTextLabel.setVisible(false);
                    springTextField.setVisible(false);
                }
                revalidate();
                repaint();
            }
        });
    }

    private void testConnection() {
        String url = urlField.getText();
        String pass = passwordField.getText();
        String payload = (String) payloadComboBox.getSelectedItem();
        String value = springTextField.isVisible() ? springTextField.getText() : null;

        int result = TestConnect.testConnection(url, pass, payload,value);
        if (result == 1) {
            JOptionPane.showMessageDialog(this, "测试通过", "连接测试", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "测试失败", "连接测试", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTargetToDatabase() {
        String url = urlField.getText();
        String pass = passwordField.getText();
        int connTimeout = Integer.parseInt(connTimeoutField.getText());
        int readTimeout = Integer.parseInt(readTimeoutField.getText());
        String encoding = (String) encodingComboBox.getSelectedItem();
        String payload = (String) payloadComboBox.getSelectedItem();
        String springText = springTextField.isVisible() ? springTextField.getText() : null;

        String id = UUID.randomUUID().toString().replace("-", "");

        Shell shell = new Shell();
        shell.setId(id);
        shell.setUrl(url);
        shell.setPassword(pass);
        shell.setConnTimeout(connTimeout);
        shell.setReadTimeout(readTimeout);
        shell.setEncoding(encoding);
        shell.setPayload(payload);
        shell.setHeaders("");

        ShellENV shellENV = new ShellENV();
        shellENV.setId(id);

        if ("Spring".equals(payload)) {
            shellENV.setValue(springText); // 仅当 payload 为 Spring 时设置 springText
            try {
                DatabaseHelper.insertShell(shell); // 先插入基础 shell 信息
                DatabaseHelper.insertShellENV(id, springText); // 插入 spring 环境信息
                JOptionPane.showMessageDialog(this, "Spring 目标已添加");
                viewMain.loadDataFromDatabase();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "添加 Spring 目标失败: " + ex.getMessage());
            }
        } else {
            try {
                DatabaseHelper.insertShell(shell);
                DatabaseHelper.insertShellENV(id, null);
                JOptionPane.showMessageDialog(this, "目标已添加");
                viewMain.loadDataFromDatabase();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "添加目标失败: " + ex.getMessage());
            }
        }
    }
}
