package ru.softmg.workers.ui.model;

import lombok.Data;
import ru.softmg.workers.model.Report;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ReportsTableModel implements TableModel {
    private List<Report> reports;
    private Set<TableModelListener> listeners = new HashSet<>();

    public ReportsTableModel(List<Report> reports) {
        this.reports = reports;
    }

    @Override
    public int getRowCount() {
        return reports.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Date";
            case 1:
                return "Project";
            case 2:
                return "Task";
            case 3:
                return "Spent";
            case 4:
                return "Actions";
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public Report getRowAt(int index) {
        return reports.get(index);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Report report = reports.get(rowIndex);
        switch (columnIndex) {
            case 0:
            {
                return LocalDateTime.parse(
                        report.getUpdatedAt(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                        .format(
                                DateTimeFormatter.ofPattern("dd.mm.yyyy HH:mm")
                        );
            }
            case 1:
                return report.getProjectName();
            case 2:
                return report.getJiraKey();
            case 3:
            {
                int spentTime = report.getSpentTime();
                int hours = spentTime / 60;
                int minutes = spentTime % 60;
                return hours + "h " + minutes + "m";
            }
            case 4:
                return "";
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }
}
