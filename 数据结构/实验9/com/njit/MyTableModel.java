package com.njit;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 * 自定义表格模型：第一列（no）只读，其余列可编辑，
 * 并记录被修改过的行号。
 */
public class MyTableModel extends DefaultTableModel {
    private ArrayList<Integer> editedIndex = new ArrayList<Integer>();

    public MyTableModel() {
        super();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        // 第一列是主键，不允许修改
        return column != 0;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        super.setValueAt(aValue, row, column);

        int i, count = editedIndex.size();
        if (count == 0) {
            editedIndex.add(row);
        } else {
            for (i = 0; i < count; i++) {
                if (editedIndex.get(i).intValue() == row) {
                    break;
                } else if (editedIndex.get(i).intValue() > row) {
                    editedIndex.add(i, row);
                    break;
                }
            }
            if (i >= count) {
                editedIndex.add(row);
            }
        }
    }

    // 获取被修改过的行号列表
    public ArrayList<Integer> getEditedIndex() {
        return editedIndex;
    }

    // 清除被修改过的行号列表
    public void clearEditedIndex() {
        editedIndex.clear();
    }
}
