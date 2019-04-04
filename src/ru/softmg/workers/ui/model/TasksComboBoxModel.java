package ru.softmg.workers.ui.model;

import lombok.Getter;
import lombok.Setter;
import ru.softmg.workers.model.Task;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TasksComboBoxModel implements ComboBoxModel<Task> {
    private List<Task> tasks = new ArrayList<>();
    private Task selected = null;
    private List<ListDataListener> listeners = new ArrayList<>();

    public TasksComboBoxModel(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selected = (Task) anItem;
    }

    @Override
    public Task getSelectedItem() {
        return selected;
    }

    @Override
    public int getSize() {
        return tasks.size();
    }

    @Override
    public Task getElementAt(int index) {
        return tasks.get(index);
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }
}
