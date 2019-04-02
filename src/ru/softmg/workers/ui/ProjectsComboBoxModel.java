package ru.softmg.workers.ui;

import lombok.Data;
import ru.softmg.workers.model.Project;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectsComboBoxModel implements ComboBoxModel<Project> {
    private List<Project> projects = new ArrayList<>();
    private Project selected = null;
    private List<ListDataListener> listeners = new ArrayList<>();

    public ProjectsComboBoxModel(List<Project> projects) {
        this.projects = projects;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selected = (Project) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }

    @Override
    public int getSize() {
        return projects.size();
    }

    @Override
    public Project getElementAt(int index) {
        return projects.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        this.listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        this.listeners.remove(l);
    }
}
