package com.atlant1c.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import com.atlant1c.model.Shell;
import com.atlant1c.utils.DatabaseHelper;

public class EditTarget extends JPanel {
    private JTextField urlField;
    private JTextField passwordField;
    private JTextField connTimeoutField;
    private JTextField readTimeoutField;
    private JTextField springTextField; // 新的文本框

    private JComboBox<String> encodingComboBox;
    private JComboBox<String> payloadComboBox;
    private ViewMain viewMain;
    private String shellId;

    public EditTarget(ViewMain viewMain, String id, String url, String password, String payload, String encoding, String headers, int connTimeout, int readTimeout, String springText) {
        this.viewMain = viewMain;
        this.shellId = id;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // URL
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("URL"), gbc);
        urlField = new JTextField(url, 20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(urlField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("密码"), gbc);
        passwordField = new JTextField(password, 20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        // 连接超时
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("连接超时"), gbc);
        connTimeoutField = new JTextField(String.valueOf(connTimeout), 20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(connTimeoutField, gbc);

        // 读取超时
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("读取超时"), gbc);
        readTimeoutField = new JTextField(String.valueOf(readTimeout), 20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(readTimeoutField, gbc);

        // 编码
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("编码"), gbc);
        encodingComboBox = new JComboBox<>(new String[]{"UTF-8"});
        encodingComboBox.setSelectedItem(encoding);
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(encodingComboBox, gbc);

        // 有效载荷
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(new JLabel("有效载荷"), gbc);
        payloadComboBox = new JComboBox<>(new String[]{"JSP(Tomcat)", "PHP", "Spring"});
        payloadComboBox.setSelectedItem(payload);
        gbc.gridx = 1;
        gbc.gridy = 5;
        add(payloadComboBox, gbc);

        // Spring Text
        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel springTextLabel = new JLabel("路由与exp");
        add(springTextLabel, gbc);
        springTextLabel.setVisible("Spring".equals(payload)); // 初始显示或隐藏
        springTextField = new JTextField(springText, 20);
        gbc.gridx = 1;
        gbc.gridy = 6;
        add(springTextField, gbc);
        springTextField.setVisible("Spring".equals(payload)); // 初始显示或隐藏

        // 保存按钮
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTargetInDatabase();
            }
        });
        buttonPanel.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // 添加 ActionListener 来动态显示或隐藏 springTextLabel 和 springTextField
        payloadComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPayload = (String) payloadComboBox.getSelectedItem();
                boolean isSpring = "Spring".equals(selectedPayload);
                springTextLabel.setVisible(isSpring);
                springTextField.setVisible(isSpring);
                revalidate();
                repaint();
            }
        });
    }

    private void updateTargetInDatabase() {
        String url = urlField.getText();
        String pass = passwordField.getText();
        int connTimeout = Integer.parseInt(connTimeoutField.getText());
        int readTimeout = Integer.parseInt(readTimeoutField.getText());
        String encoding = (String) encodingComboBox.getSelectedItem();
        String payload = (String) payloadComboBox.getSelectedItem();
        String springText = springTextField.isVisible() ? springTextField.getText() : null;

        Shell shell = new Shell();
        shell.setId(shellId);
        shell.setUrl(url);
        shell.setPassword(pass);
        shell.setConnTimeout(connTimeout);
        shell.setReadTimeout(readTimeout);
        shell.setEncoding(encoding);
        shell.setPayload(payload);
        shell.setHeaders("");

        try {
            DatabaseHelper.updateShell(shell); // 更新基础 shell 信息

            if ("Spring".equals(payload)) {
                DatabaseHelper.insertShellENV(shellId, springText); // 插入或更新 spring 环境信息
            } else {
                DatabaseHelper.insertShellENV(shellId, null);
            }

            JOptionPane.showMessageDialog(this, "目标已更新");
            viewMain.loadDataFromDatabase();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "更新目标失败: " + ex.getMessage());
        }
    }
}
