package ru.softmg.workers.ui;

import ru.softmg.workers.http.WorkersApiService;
import ru.softmg.workers.model.Project;
import ru.softmg.workers.model.Task;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

public class AddReportDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox projectComboBox;
    private JComboBox taskComboBox;
    private JComboBox hoursComboBox;
    private JComboBox minutesComboBox;
    private JTextArea textArea1;

    private AddReportHandler addReportHandler;
    private WorkersApiService workersApiService;

    public AddReportDialog(WorkersApiService workersApiService) {
        this.workersApiService = workersApiService;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        IntStream.iterate(0, operand -> operand + 1).limit(24).forEach(value -> hoursComboBox.addItem(String.valueOf(value)));
        IntStream.iterate(0, operand -> operand + 1).limit(60).forEach(value -> minutesComboBox.addItem(String.valueOf(value)));

        workersApiService.getProjectList().thenAccept(o -> {
            List<Project> projects = (List<Project>)o;
            if(projects != null) {
                ProjectsComboBoxModel projectsComboBoxModel = new ProjectsComboBoxModel(projects);
                projectComboBox.setModel(projectsComboBoxModel);
            }
        });

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        projectComboBox.addItemListener(e -> {
            Integer projectId = Integer.parseInt(((ProjectsComboBoxModel)projectComboBox.getModel()).getSelected().getId());
            try {
                workersApiService.getTasks(projectId).thenAccept(o -> {
                    List<Task> tasks = (List<Task>)o;
                    TasksComboBoxModel tasksComboBoxModel = new TasksComboBoxModel(tasks);
                    taskComboBox.setModel(tasksComboBoxModel);
                });
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    private void onOK() {
        try {
            workersApiService.postCreateWorkReport(
                    Integer.parseInt(((ProjectsComboBoxModel)projectComboBox.getModel()).getSelected().getId()),
                    ((TasksComboBoxModel)taskComboBox.getModel()).getSelected().getId(),
                    Integer.parseInt((String)hoursComboBox.getSelectedItem()) * 60 + Integer.parseInt((String)minutesComboBox.getSelectedItem()),
                    textArea1.getText()
            ).thenAccept(o -> addReportHandler.reportAddHandler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAddReportHandler(AddReportHandler addReportHandler) {
        this.addReportHandler = addReportHandler;
    }

    private void onCancel() {
        dispose();
    }
}
