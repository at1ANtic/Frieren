package com.atlant1c.view;

import com.atlant1c.generate.Php;
import com.atlant1c.generate.SpringShell;
import com.atlant1c.generate.Jsp;
import com.atlant1c.utils.Base64Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class GenerateShell extends JPanel {
    private JTextField passwordField;
    private JTextField keyField;
    private JComboBox<String> payloadComboBox;
    private JComboBox<String> encryptorComboBox;

    public GenerateShell(JFrame parentFrame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel passwordLabel = new JLabel("密码:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(passwordLabel, gbc);

        passwordField = new JTextField("pass", 10); // 设置默认值为 "pass"
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(passwordField, gbc);

/*        JLabel keyLabel = new JLabel("密钥:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(keyLabel, gbc);

        keyField = new JTextField("key", 10); // 设置默认值为 "key"
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(keyField, gbc);*/

        JLabel payloadLabel = new JLabel("有效载荷:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(payloadLabel, gbc);

        payloadComboBox = new JComboBox<>(new String[]{"Jsp(Tomcat)", "PHP", "Spring"});
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(payloadComboBox, gbc);

/*        JLabel encryptorLabel = new JLabel("加密器:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(encryptorLabel, gbc);

        encryptorComboBox = new JComboBox<>(new String[]{"JAVA_AES_BASE64", "OtherEncryptor"});
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(encryptorComboBox, gbc);*/

        JButton generateButton = new JButton("生成");
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(generateButton, gbc);

        JButton cancelButton = new JButton("取消");
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(cancelButton, gbc);

        // 添加监听器以处理按钮点击事件
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = passwordField.getText();
/*                String key = keyField.getText();*/
                String payload = (String) payloadComboBox.getSelectedItem();
/*                String encryptor = (String) encryptorComboBox.getSelectedItem();*/

                if ("PHP".equals(payload)) {
                    String currentDirectory = System.getProperty("user.dir");
                    File payloadDir = new File(currentDirectory, "payload");
                    if (!payloadDir.exists()) {
                        payloadDir.mkdir();
                    }
                    File payloadFile = new File(payloadDir, "payload.php");
                    Php.generatePayload(password, payloadFile.getAbsolutePath());
                    JOptionPane.showMessageDialog(GenerateShell.this,
                            "PHP payload 已生成在: " + payloadFile.getAbsolutePath());
                } else if ("Spring".equals(payload)) {
                    try {
                        byte[] classBytes = SpringShell.payload(password);
                        String encodedBytes = Base64Utils.encodeToString(classBytes);
                        String currentDirectory = System.getProperty("user.dir");
                        File payloadDir = new File(currentDirectory, "payload");
                        if (!payloadDir.exists()) {
                            payloadDir.mkdir();
                        }
                        File classFile = new File(payloadDir, "MyClassLoader.class.base64");
                        try (FileOutputStream fos = new FileOutputStream(classFile)) {
                            fos.write(encodedBytes.getBytes(StandardCharsets.UTF_8));
                        }
                        JOptionPane.showMessageDialog(GenerateShell.this,
                                "Spring payload 已生成在: " + classFile.getAbsolutePath());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(GenerateShell.this,
                                "生成 Spring payload 失败: " + ex.getMessage());
                    }
                } else if ("Jsp".equals(payload)){
                    String currentDirectory = System.getProperty("user.dir");
                    File payloadDir = new File(currentDirectory, "payload");
                    if (!payloadDir.exists()) {
                        payloadDir.mkdir();
                    }
                    File payloadFile = new File(payloadDir, "payload.jsp");
                    Jsp.generatePayload(password, payloadFile.getAbsolutePath());
                    JOptionPane.showMessageDialog(GenerateShell.this,
                            "JSP payload 已生成在: " + payloadFile.getAbsolutePath());
                }

                else {
                    // 在这里添加处理逻辑，例如生成其他类型的 shell
                    JOptionPane.showMessageDialog(GenerateShell.this,
                            "生成: " + password + ",  " + payload );
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.dispose();
            }
        });

        // 添加监听器以处理有效载荷选择变化
        payloadComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPayload = (String) payloadComboBox.getSelectedItem();
/*                updateEncryptorOptions(selectedPayload);*/
            }
        });
    }

/*    private void updateEncryptorOptions(String payload) {
        // 根据有效载荷选择更新加密器选项
        if ("JavaDynamicPayload".equals(payload)) {
            encryptorComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"JAVA_AES_BASE64", "OtherEncryptor"}));
        } else {
            encryptorComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"OtherEncryptor1", "OtherEncryptor2"}));
        }
    }*/
}
