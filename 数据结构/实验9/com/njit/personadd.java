package com.njit;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import db.db;

/**
 * 实验九：“添加”窗口。
 * 输入员工信息后，用 PreparedStatement 向 person 表插入一条记录。
 */
public class personadd extends JFrame {
    private JTextField userid, username;
    private JRadioButton sexmale, sexfemale;
    private JPanel sex, birth;
    private JTextField year, dept;
    private JComboBox<Integer> month, day;
    private JComboBox<String> prof;
    private JButton ok, cancel;

    public personadd() {
        super();
        setSize(350, 300);
        setTitle("新员工");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 设置组件布局
        Container cont = getContentPane();
        cont.setLayout(new GridLayout(7, 2));

        // 员工号
        cont.add(new JLabel("员工号:"));
        userid = new JTextField(10);
        cont.add(userid);

        // 姓名
        cont.add(new JLabel("姓名:"));
        username = new JTextField(10);
        cont.add(username);

        // 性别
        cont.add(new JLabel("性别:"));
        sexmale = new JRadioButton("男", true);
        sexfemale = new JRadioButton("女");
        ButtonGroup bg = new ButtonGroup();
        bg.add(sexmale);
        bg.add(sexfemale);
        sex = new JPanel(new GridLayout(1, 2));
        sex.add(sexmale);
        sex.add(sexfemale);
        cont.add(sex);

        // 出生日期
        cont.add(new JLabel("出生日期:"));
        year = new JTextField(4);
        month = new JComboBox<Integer>();
        for (int i = 1; i <= 12; i++) {
            month.addItem(i);
        }
        day = new JComboBox<Integer>();
        for (int i = 1; i <= 31; i++) {
            day.addItem(i);
        }
        birth = new JPanel();
        birth.add(year);
        birth.add(new JLabel("-"));
        birth.add(month);
        birth.add(new JLabel("-"));
        birth.add(day);
        cont.add(birth);

        // 职称
        cont.add(new JLabel("职称:"));
        prof = new JComboBox<String>();
        prof.addItem("初级");
        prof.addItem("中级");
        prof.addItem("高级");
        cont.add(prof);

        // 部门
        cont.add(new JLabel("部门:"));
        dept = new JTextField(10);
        cont.add(dept);

        // 按钮
        ok = new JButton("添加");
        cancel = new JButton("取消");
        cont.add(ok);
        cont.add(cancel);

        // 注册监听器
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String no = userid.getText().trim();
                String name = username.getText().trim();
                if (no.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(personadd.this,
                            "员工号和姓名不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                db dbcon = new db();
                PreparedStatement prestate = null;
                try {
                    String sql = "insert into person(no,name,sex,birthday,professor,deptno) values(?,?,?,?,?,?)";
                    prestate = dbcon.PrepareStatement(sql);
                    prestate.setString(1, no);
                    prestate.setString(2, name);
                    prestate.setString(3, sexmale.isSelected() ? "男" : "女");
                    prestate.setString(4, buildBirthday());
                    prestate.setString(5, (String) prof.getSelectedItem());
                    prestate.setString(6, dept.getText().trim());
                    prestate.executeUpdate();

                    JOptionPane.showMessageDialog(personadd.this, "插入成功！");
                    dispose();
                    new personselect().setVisible(true);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(personadd.this,
                            "插入失败：" + e.getMessage(),
                            "数据库错误", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                } finally {
                    if (prestate != null) {
                        try {
                            prestate.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        dbcon.closeConn();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dispose();
            }
        });
    }

    /** 年份为空表示不填生日，返回 null 会作为 SQL 的 NULL 写入。 */
    private String buildBirthday() {
        String y = year.getText().trim();
        if (y.isEmpty()) {
            return null;
        }
        int m = (Integer) month.getSelectedItem();
        int d = (Integer) day.getSelectedItem();
        return String.format("%s-%02d-%02d", y, m, d);
    }

    public static void main(String[] args) {
        personadd w = new personadd();
        w.setVisible(true);
    }
}
