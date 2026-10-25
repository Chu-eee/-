package com.njit;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import db.db;

/**
 * 练习题 8-2：登录界面。
 * 输入的用户名、密码与 pay 库 user 表中的某行一致时，登录成功。
 */
public class loginframe extends JFrame {
    private JTextField username;
    private JPasswordField password;
    private JButton login, register;

    public loginframe() {
        super();
        setSize(320, 200);
        setTitle("登录");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 3 行 2 列布局
        Container cont = getContentPane();
        cont.setLayout(new GridLayout(3, 2, 8, 8));

        cont.add(new JLabel("用户名：", SwingConstants.RIGHT));
        username = new JTextField(10);
        cont.add(username);

        cont.add(new JLabel("密码：", SwingConstants.RIGHT));
        password = new JPasswordField(10);
        cont.add(password);

        login = new JButton("登录");
        register = new JButton("注册");
        cont.add(login);
        cont.add(register);

        // 登录按钮事件
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                doLogin();
            }
        });

        // 在密码框按回车也能登录
        password.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                doLogin();
            }
        });

        register.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                JOptionPane.showMessageDialog(loginframe.this,
                        "注册界面见实验七的 registerframe");
            }
        });
    }

    /**
     * 到 user 表里查用户名和密码。
     * 用 ? 占位符而不是拼接字符串，防止 SQL 注入。
     */
    private void doLogin() {
        String name = username.getText().trim();
        String pwd = new String(password.getPassword());

        if (name.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名和密码");
            return;
        }

        db dbcon = null;
        try {
            dbcon = new db();
            ResultSet rs = dbcon.executeQuery(
                    "select Upower from [user] where Uname=? and Upwd=?",
                    name, pwd);

            if (rs.next()) {
                int power = rs.getInt("Upower");
                rs.close();
                dbcon.closeConn();
                dbcon = null;

                String powerText = (power == 1) ? "管理员" : "员工";
                JOptionPane.showMessageDialog(this, "登录成功！权限：" + powerText);

                // 登录成功后打开员工信息窗口
                new personselect().setVisible(true);
                dispose();
            } else {
                rs.close();
                JOptionPane.showMessageDialog(this, "用户名或密码错误");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "登录失败：" + e.getMessage(),
                    "数据库错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            if (dbcon != null) {
                try {
                    dbcon.closeConn();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new loginframe().setVisible(true);
            }
        });
    }
}
