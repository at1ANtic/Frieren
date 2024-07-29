package com.atlant1c.view;

import com.atlant1c.utils.DatabaseHelper;
import com.atlant1c.model.Shell;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.*;
import java.util.List;

import static com.atlant1c.utils.DatabaseHelper.getShellENVValueById;

public class ViewMain extends JFrame {

    private JTable shellTable;
    private DefaultTableModel tableModel;

    public ViewMain() {
        if (!isDatabaseFileExists()) {
            DatabaseHelper.initializeDatabase();
        }
        createDefaultPayloadFolder();

        setTitle("Frieren");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();

        // 创建“目标”菜单
        JMenu targetMenu = new JMenu("目标");
        JMenuItem addTargetItem = new JMenuItem("添加");
        addTargetItem.addActionListener(e -> displayAddTarget());
        targetMenu.add(addTargetItem);

        // 创建“管理”菜单
        JMenu manageMenu = new JMenu("管理");
        JMenuItem generateShellItem = new JMenuItem("生成");
        generateShellItem.addActionListener(e -> displayGenerateShell());
        manageMenu.add(generateShellItem);

        // 创建“配置”菜单
        JMenu configMenu = new JMenu("配置");
        JMenuItem appSettingItem = new JMenuItem("程序配置");
        appSettingItem.addActionListener(e -> displayAppSetting());
        configMenu.add(appSettingItem);

        // 将菜单添加到菜单栏
        menuBar.add(targetMenu);
        menuBar.add(manageMenu);
        menuBar.add(configMenu);

        // 设置菜单栏
        setJMenuBar(menuBar);

        // 初始化表格模型
        tableModel = new DefaultTableModel(new String[]{"ID", "URL", "Password", "Payload", "Encoding", "Headers", "ConnTimeout", "ReadTimeout", "CreateTime", "UpdateTime"}, 0);
        shellTable = new JTable(tableModel);

        // 禁用列的可调整大小
        for (int i = 0; i < shellTable.getColumnCount(); i++) {
            shellTable.getColumnModel().getColumn(i).setResizable(false);
        }

        // 添加右键菜单
        shellTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = shellTable.rowAtPoint(e.getPoint());
                shellTable.setRowSelectionInterval(row, row);
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            private void showPopup(MouseEvent e) {
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem enterItem = new JMenuItem("进入");
                JMenuItem copyItem = new JMenuItem("复制选中");
                JMenuItem removeItem = new JMenuItem("移除");
                JMenuItem editItem = new JMenuItem("编辑");
                JMenuItem refreshItem = new JMenuItem("刷新");

                // 添加菜单项的操作
                enterItem.addActionListener(event -> enter());
                copyItem.addActionListener(event -> {
                    try {
                        copySelected();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                removeItem.addActionListener(event -> removeSelected());
                editItem.addActionListener(event -> editSelected());
                refreshItem.addActionListener(event -> loadDataFromDatabase());

                popupMenu.add(enterItem);
                popupMenu.add(copyItem);
                popupMenu.add(removeItem);
                popupMenu.add(editItem);
                popupMenu.add(refreshItem);
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }

            private void enter() {
                int selectedRow = shellTable.getSelectedRow();
                if (selectedRow != -1) {
                    String id = (String) tableModel.getValueAt(selectedRow, 0);
                    String url = (String) tableModel.getValueAt(selectedRow, 1);
                    String password = (String) tableModel.getValueAt(selectedRow, 2);
                    String payload = (String) tableModel.getValueAt(selectedRow, 3);
                    String value;
                    try {
                        value = (String) getShellENVValueById(id);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    // 创建并显示新的管理界面
                    JFrame managementFrame = new JFrame("管理");
                    managementFrame.setSize(1200, 600);
                    managementFrame.setLocationRelativeTo(null);  // 使新窗口居中
                    managementFrame.setContentPane(new Management(id, url, password, payload, value));
                    managementFrame.setVisible(true);
                }

            }

            private void enterSequential() {
                // 调用新的类或方法
            }

            private void copySelected() throws SQLException {
                int selectedRow = shellTable.getSelectedRow();
                if (selectedRow != -1) {
                    String id = (String) tableModel.getValueAt(selectedRow, 0);
                    String queryResult = DatabaseHelper.getShellById(id);
                    if (queryResult != null) {
                        copyToClipboard(queryResult);
                        JOptionPane.showMessageDialog(ViewMain.this, "Selected row copied to clipboard.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(ViewMain.this, "Failed to copy selected row.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            private void copyToClipboard(String text) {
                StringSelection stringSelection = new StringSelection(text);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            }
            private void removeSelected() {
                int selectedRow = shellTable.getSelectedRow();
                if (selectedRow != -1) {
                    String id = (String) tableModel.getValueAt(selectedRow, 0);
                    try {
                        DatabaseHelper.deleteShell(id);
                        tableModel.removeRow(selectedRow);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(ViewMain.this, "Failed to delete shell", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            private void editSelected() {
                int selectedRow = shellTable.getSelectedRow();
                if (selectedRow != -1) {
                    String id = (String) tableModel.getValueAt(selectedRow, 0);
                    String url = (String) tableModel.getValueAt(selectedRow, 1);
                    String password = (String) tableModel.getValueAt(selectedRow, 2);
                    String payload = (String) tableModel.getValueAt(selectedRow, 3);
                    String encoding = (String) tableModel.getValueAt(selectedRow, 4);
                    String headers = (String) tableModel.getValueAt(selectedRow, 5);
                    int connTimeout = (int) tableModel.getValueAt(selectedRow, 6);
                    int readTimeout = (int) tableModel.getValueAt(selectedRow, 7);
                    String value;
                    try {
                        value = getShellENVValueById(id);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    JFrame editTargetFrame = new JFrame("编辑目标");
                    editTargetFrame.setSize(400, 300);
                    editTargetFrame.setLocationRelativeTo(null);
                    EditTarget editTarget = new EditTarget(ViewMain.this, id, url, password, payload, encoding, headers, connTimeout, readTimeout, value);
                    editTargetFrame.setContentPane(editTarget);
                    editTargetFrame.setVisible(true);
                }
            }
        });

        // 从数据库加载数据
        loadDataFromDatabase();

        // 将表格放在滚动面板中
        JScrollPane scrollPane = new JScrollPane(shellTable);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void displayAddTarget() {
        JFrame addTargetFrame = new JFrame("添加目标");
        addTargetFrame.setSize(400, 300);
        addTargetFrame.setLocationRelativeTo(null);
        AddTarget addTarget = new AddTarget(this); // 传递当前实例
        addTargetFrame.setContentPane(addTarget);
        addTargetFrame.setVisible(true);
    }

    private void displayGenerateShell() {
        JFrame generateShellFrame = new JFrame("生成");
        generateShellFrame.setSize(400, 300);
        generateShellFrame.setLocationRelativeTo(null);
        generateShellFrame.setContentPane(new GenerateShell(generateShellFrame));
        generateShellFrame.setVisible(true);
    }

    private void displayAppSetting() {
        JFrame appSettingFrame = new JFrame("程序配置");
        appSettingFrame.setSize(400, 300);
        appSettingFrame.setLocationRelativeTo(null);
        appSettingFrame.setContentPane(new AppSetting());
        appSettingFrame.setVisible(true);
    }

    public void loadDataFromDatabase() {
        tableModel.setRowCount(0); // 清除现有数据
        try {
            List<Shell> shells = DatabaseHelper.getAllShells();
            for (Shell shell : shells) {
                tableModel.addRow(new Object[]{
                        shell.getId(), shell.getUrl(), shell.getPassword(), shell.getPayload(), shell.getEncoding(),
                        shell.getHeaders(), shell.getConnTimeout(), shell.getReadTimeout(), shell.getCreateTime(), shell.getUpdateTime()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isDatabaseFileExists() {
        File dbFile = new File("data.db");
        return dbFile.exists();
    }
    private void createDefaultPayloadFolder() {
        File payloadFolder = new File("payload");
        if (!payloadFolder.exists()) {
            if (payloadFolder.mkdirs()) {
                System.out.println("Payload folder created successfully.");
            } else {
                System.err.println("Failed to create payload folder.");
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewMain::new);
    }
}
