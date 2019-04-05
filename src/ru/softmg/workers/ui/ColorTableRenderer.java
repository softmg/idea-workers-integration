package ru.softmg.workers.ui;

import org.jdesktop.swingx.HorizontalLayout;
import ru.softmg.workers.model.Report;
import ru.softmg.workers.ui.model.ReportsTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ColorTableRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        ReportsTableModel reportsTableModel = (ReportsTableModel)table.getModel();
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        Report selectedReport = reportsTableModel.getRowAt(row);
        component.setBackground(selectedReport.getIsTemporary() ? new Color(91, 117, 173, 50) : new Color(100, 173, 91, 50));

        if(column != 4) {
            return component;
        } else {
            if(selectedReport.getIsTemporary()) {
                JPanel panel = new JPanel();
                panel.setLayout(new HorizontalLayout());
                panel.add(new JButton("Edit"));
                panel.add(new JButton("Remove"));
                panel.setBackground(selectedReport.getIsTemporary() ? new Color(91, 117, 173, 50) : new Color(100, 173, 91, 50));
                return panel;
            }
        }
        return component;
    }
}
