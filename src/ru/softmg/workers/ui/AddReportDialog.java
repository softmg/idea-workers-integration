package ru.softmg.workers.ui;

import ru.softmg.workers.http.WorkersApiService;
import ru.softmg.workers.model.Project;
import ru.softmg.workers.model.Task;

import javax.swing.*;
import java.awt.event.*;
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

    public AddReportDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        IntStream.iterate(0, operand -> operand + 1).limit(24).forEach(value -> hoursComboBox.addItem(String.valueOf(value)));
        IntStream.iterate(0, operand -> operand + 1).limit(60).forEach(value -> minutesComboBox.addItem(String.valueOf(value)));

        try (WorkersApiService workersApiService = new WorkersApiService()) {
            List<Project> projects = workersApiService.getProjectList();
            ProjectsComboBoxModel projectsComboBoxModel = new ProjectsComboBoxModel(projects);
            projectComboBox.setModel(projectsComboBoxModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        projectComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer projectId = Integer.parseInt(((ProjectsComboBoxModel)projectComboBox.getModel()).getSelected().getId());

                try(WorkersApiService workersApiService = new WorkersApiService()) {
                    List<Task> taskList = workersApiService.getTasks(projectId);
                    TasksComboBoxModel tasksComboBoxModel = new TasksComboBoxModel(taskList);
                    taskComboBox.setModel(tasksComboBoxModel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void onOK() {
        try(WorkersApiService workersApiService = new WorkersApiService()) {
            workersApiService.postCreateWorkReport(
                    Integer.parseInt(((ProjectsComboBoxModel)projectComboBox.getModel()).getSelected().getId()),
                    ((TasksComboBoxModel)taskComboBox.getModel()).getSelected().getId(),
                    Integer.parseInt((String)hoursComboBox.getSelectedItem()) * 60 + Integer.parseInt((String)minutesComboBox.getSelectedItem()),
                    textArea1.getText()
            );
            addReportHandler.reportAddHandler();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        dispose();
    }

    public void setAddReportHandler(AddReportHandler addReportHandler) {
        this.addReportHandler = addReportHandler;
    }

    private void onCancel() {
        dispose();
    }
}
