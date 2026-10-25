package com.njit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import db.db;
import entity.PersonEntity;

/**
 * 实验九：在实验八的表格窗口上增加“添加 / 修改 / 删除”工具栏，
 * 以及按部门编号查询的查询栏。
 */
public class personselect extends JFrame {
    private JTable table;
    private MyTableModel tablemodel;
    private JButton b1;
    private JButton b2;
    private JButton b3;
    private JToolBar tool;
    private JTextField deptField;

    public personselect() {
        setSize(720, 360);
        setTitle("员工信息");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ---------- 1. 创建工具栏 ----------
        b1 = new JButton("添加", new ImageIcon("ico/ADD.png"));
        b1.setToolTipText("添加");
        b1.setFocusable(false);
        b1.setHorizontalTextPosition(SwingConstants.CENTER);
        b1.setVerticalTextPosition(SwingConstants.BOTTOM);
        b2 = new JButton("修改", new ImageIcon("ico/UPDATE.png"));
        b2.setToolTipText("修改");
        b2.setFocusable(false);
        b2.setHorizontalTextPosition(SwingConstants.CENTER);
        b2.setVerticalTextPosition(SwingConstants.BOTTOM);
        b3 = new JButton("删除", new ImageIcon("ico/DELETE.png"));
        b3.setToolTipText("删除");
        b3.setFocusable(false);
        b3.setHorizontalTextPosition(SwingConstants.CENTER);
        b3.setVerticalTextPosition(SwingConstants.BOTTOM);

        tool = new JToolBar();
        tool.setFloatable(false);
        tool.add(b1);
        tool.add(b2);
        tool.add(b3);
        tool.setRollover(true);
        getContentPane().add(tool, BorderLayout.NORTH);

        // ---------- 2. 表格 ----------
        tablemodel = getModel("");
        table = new JTable(tablemodel);
        table.setPreferredScrollableViewportSize(new Dimension(650, 220));
        getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

        // ---------- 3. 部门查询栏 ----------
        deptField = new JTextField(10);
        JButton queryButton = new JButton("查询");
        JButton allButton = new JButton("显示全部");
        JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        queryPanel.add(new JLabel("部门编号："));
        queryPanel.add(deptField);
        queryPanel.add(queryButton);
        queryPanel.add(allButton);
        getContentPane().add(queryPanel, BorderLayout.SOUTH);

        // ---------- 4. “添加”监听器 ----------
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new personadd().setVisible(true);
            }
        });

        // ---------- 5. “修改”监听器 ----------
        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 单元格还在编辑状态时，先把值提交给表格模型
                if (table.getCellEditor() != null) {
                    table.getCellEditor().stopCellEditing();
                }

                int count = tablemodel.getEditedIndex().size();
                if (count == 0) {
                    JOptionPane.showMessageDialog(personselect.this, "没有需要保存的修改。");
                    return;
                }

                db dbcon = new db();
                PreparedStatement prestate = null;
                try {
                    String sql = "update person set name=?,sex=?,birthday=?,professor=?,deptno=? where no=?";
                    prestate = dbcon.PrepareStatement(sql);
                    for (int i = 0; i < count; i++) {
                        int index = tablemodel.getEditedIndex().get(i);
                        prestate.setString(1, (String) table.getValueAt(index, 1));
                        prestate.setString(2, (String) table.getValueAt(index, 2));
                        prestate.setString(3, formatBirthday(table.getValueAt(index, 3)));
                        prestate.setString(4, (String) table.getValueAt(index, 4));
                        prestate.setString(5, (String) table.getValueAt(index, 5));
                        prestate.setString(6, (String) table.getValueAt(index, 0));
                        prestate.addBatch();
                    }
                    prestate.executeBatch();
                    tablemodel.clearEditedIndex();
                    JOptionPane.showMessageDialog(personselect.this, "修改已保存！");
                } catch (SQLException sqle) {
                    JOptionPane.showMessageDialog(personselect.this,
                            "保存修改失败：" + sqle.getMessage(),
                            "数据库错误", JOptionPane.ERROR_MESSAGE);
                } finally {
                    closeQuietly(prestate);
                    try {
                        dbcon.closeConn();
                    } catch (SQLException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });

        // ---------- 6. “删除”监听器 ----------
        b3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (table.getCellEditor() != null) {
                    table.getCellEditor().stopCellEditing();
                }

                int[] selRowIndexs = table.getSelectedRows();
                if (selRowIndexs.length == 0) {
                    JOptionPane.showMessageDialog(personselect.this, "请先选中要删除的行。");
                    return;
                }

                db dbcon = new db();
                PreparedStatement prestate = null;
                try {
                    prestate = dbcon.PrepareStatement("delete from person where no=?");
                    for (int i = 0; i < selRowIndexs.length; i++) {
                        prestate.setString(1, String.valueOf(table.getValueAt(selRowIndexs[i], 0)));
                        prestate.addBatch();
                    }
                    prestate.executeBatch();
                    // 重新加载数据到 JTable
                    reloadTable();
                } catch (SQLException sqle) {
                    JOptionPane.showMessageDialog(personselect.this,
                            "删除失败：" + sqle.getMessage(),
                            "数据库错误", JOptionPane.ERROR_MESSAGE);
                } finally {
                    closeQuietly(prestate);
                    try {
                        dbcon.closeConn();
                    } catch (SQLException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });

        // ---------- 7. 查询按钮 ----------
        ActionListener queryAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reloadTable();
            }
        };
        queryButton.addActionListener(queryAction);
        deptField.addActionListener(queryAction);   // 在文本框里按回车也能查询
        allButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deptField.setText("");
                reloadTable();
            }
        });
    }

    /** 按部门编号重新查询；deptno 为空串表示查询全部。 */
    private MyTableModel getModel(String deptno) {
        MyTableModel tableModel = new MyTableModel();
        db dbcon = null;
        PreparedStatement prestate = null;
        try {
            dbcon = new db();
            String sql;
            if (deptno == null || deptno.isEmpty()) {
                sql = "select * from person order by no";
                prestate = dbcon.PrepareStatement(sql);
            } else {
                sql = "select * from person where deptno=? order by no";
                prestate = dbcon.PrepareStatement(sql);
                prestate.setString(1, deptno);
            }
            ResultSet rs = prestate.executeQuery();

            // 获取查询结果中的列名，填充表格模型的列
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(rsmd.getColumnName(i));
            }

            // 获取查询结果中的元组，填充表格模型的行
            ArrayList<PersonEntity> list = new ArrayList<PersonEntity>();
            while (rs.next()) {
                PersonEntity person = new PersonEntity();
                person.setNo(rs.getString("no"));
                person.setName(rs.getString("name"));
                person.setSex(rs.getString("sex"));
                person.setBirthday(rs.getDate("birthday"));
                person.setProfessor(rs.getString("professor"));
                person.setDeptno(rs.getString("deptno"));
                list.add(person);
            }
            rs.close();

            for (PersonEntity p : list) {
                tableModel.addRow(new Object[]{
                        p.getNo(), p.getName(), p.getSex(),
                        p.getBirthday(), p.getProfessor(), p.getDeptno()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "查询 person 表失败：" + e.getMessage(),
                    "数据库错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            closeQuietly(prestate);
            if (dbcon != null) {
                try {
                    dbcon.closeConn();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return tableModel;
    }

    /** 重新按当前查询栏的条件加载表格。 */
    private void reloadTable() {
        tablemodel = getModel(deptField.getText().trim());
        table.setModel(tablemodel);
    }

    /**
     * 表格里的生日可能是 java.util.Date（数据库读出来的），
     * 也可能是用户改过的字符串。统一转成 yyyy-MM-dd 再写回数据库。
     */
    private String formatBirthday(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof java.util.Date) {
            return new SimpleDateFormat("yyyy-MM-dd").format((java.util.Date) value);
        }
        String text = value.toString().trim();
        return text.isEmpty() ? null : text;
    }

    private void closeQuietly(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new personselect().setVisible(true);
            }
        });
    }
}
