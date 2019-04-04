package ru.softmg.workers.ui.form;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import ru.softmg.workers.http.WorkersApiService;
import ru.softmg.workers.model.Project;
import ru.softmg.workers.model.Report;
import ru.softmg.workers.model.ReportsPage;
import ru.softmg.workers.model.Task;
import ru.softmg.workers.repository.CurrentUserComponent;
import ru.softmg.workers.repository.UserDailyReportsComponent;
import ru.softmg.workers.ui.handler.TickHandler;
import ru.softmg.workers.ui.model.ProjectsComboBoxModel;
import ru.softmg.workers.ui.model.ReportsTableModel;
import ru.softmg.workers.ui.model.TasksComboBoxModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class WorkersMainToolWindow {
    private JPanel workersMainToolWindowContent;
    private JButton loginButton;
    private JPanel notLoggedInPanel;
    private JPanel reportsPanel;
    private JTable reportsTable;
    private JPanel tableContentPanel;
    private JButton refreshButton;
    private JButton addButton;
    private JLabel reportMainLabel;
    private JButton publishButton;
    private JComboBox projectComboBox;
    private JComboBox taskComboBox;
    private JButton startButton;
    private JLabel timerLabel;
    private JButton saveButton;

    Thread timerThread = null;

    private WorkersApiService workersApiService = new WorkersApiService();

    private void refresh() {
        workersApiService.postGetReports().thenAccept(o -> {
            ReportsPage reportsPage = (ReportsPage)o;
            if(reportsPage != null) {
                ReportsTableModel reportsTableModel = (ReportsTableModel)reportsTable.getModel();
                List<Report> filteredReports = reportsPage.getData().stream()
                        .filter(report -> {
                            LocalDateTime localDateTime = LocalDateTime.parse(report.getUpdatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                            return localDateTime.getDayOfMonth() == LocalDateTime.now().getDayOfMonth() &&
                                    localDateTime.getMonth() == LocalDateTime.now().getMonth() &&
                                    localDateTime.getYear() == LocalDateTime.now().getYear();
                        }).collect(Collectors.toList());
                reportsTableModel.setReports(filteredReports);
                reportsTable.updateUI();
            }
        });
    }

    public WorkersMainToolWindow() {
        CurrentUserComponent currentUserComponent = ApplicationManager.getApplication().getComponent(CurrentUserComponent.class);
        reportsPanel.setVisible(false);
        reportMainLabel.setText("Report for today (" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ")");

        loginButton.addActionListener(e -> {
            LoginDialog loginDialog = new LoginDialog(workersApiService);
            loginDialog.setLoginHandler(user -> {
                currentUserComponent.loadState(new CurrentUserComponent.State(user));

                notLoggedInPanel.setVisible(false);
                reportsPanel.setVisible(true);

                refresh();
            });
            loginDialog.pack();
            loginDialog.setLocationRelativeTo(workersMainToolWindowContent);
            loginDialog.setTitle("Log in to Workers");
            loginDialog.setResizable(false);
            loginDialog.setModal(true);
            loginDialog.setVisible(true);
        });

        UserDailyReportsComponent userDailyReportsComponent = ApplicationManager.getApplication().getComponent(UserDailyReportsComponent.class);
        TableModel tableModel = new ReportsTableModel(userDailyReportsComponent.getState().getReports());
        reportsTable = new JBTable(tableModel);
        tableContentPanel.setLayout(new BorderLayout());
        JBScrollPane tableContainer = new JBScrollPane(reportsTable);
        tableContentPanel.add(tableContainer, BorderLayout.CENTER);

        addButton.addActionListener(e -> {
            AddReportDialog addReportDialog = new AddReportDialog(workersApiService);
            addReportDialog.pack();
            addReportDialog.setLocationRelativeTo(workersMainToolWindowContent);
            addReportDialog.setTitle("Add report");
            addReportDialog.setModal(true);
            addReportDialog.setAddReportHandler(this::refresh);
            addReportDialog.setVisible(true);
        });

        reportsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getClickCount() == 2 && reportsTable.getSelectedRow() != -1) {
                    Report report = ((ReportsTableModel)reportsTable.getModel()).getRowAt(reportsTable.getSelectedRow());

                    ReportViewDialog reportViewDialog = new ReportViewDialog(report);
                    reportViewDialog.pack();
                    reportViewDialog.setLocationRelativeTo(workersMainToolWindowContent);
                    reportViewDialog.setTitle("Report data");
                    reportViewDialog.setResizable(true);
                    reportViewDialog.setMinimumSize(new Dimension(500, 300));
                    reportViewDialog.setModal(true);
                    reportViewDialog.setVisible(true);
                }
            }
        });

        refreshButton.addActionListener(e -> refresh());
        publishButton.addActionListener(e -> workersApiService.postCreateDailyReport());

        workersApiService.getProjectList().thenAccept(o -> {
            List<Project> projects = (List<Project>)o;
            if(projects != null) {
                ProjectsComboBoxModel projectsComboBoxModel = new ProjectsComboBoxModel(projects);
                projectComboBox.setModel(projectsComboBoxModel);
            }
        });

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

        startButton.addActionListener(e -> {
            Project selectedProject = ((ProjectsComboBoxModel)projectComboBox.getModel()).getSelected();
            Task selectedTask = ((TasksComboBoxModel)taskComboBox.getModel()).getSelected();
            if(selectedProject != null && selectedTask != null) {
                if(startButton.getText().equals("Start")) {
                    timerThread = new Thread(new TimerService(tick -> timerLabel.setText(getTimer(tick))));
                    timerThread.start();
                } else {
                    if(timerThread.isAlive())
                        timerThread.interrupt();
                    startButton.setText("Start");
                }
            }
        });
    }

    private String getTimer(Integer timer) {
        String hours = String.format("%02d", timer / 60);
        String minutes = String.format("%02d", timer % 60);
        return hours + ":" + minutes;
    }

    public JPanel getContent() {
        return workersMainToolWindowContent;
    }

    private static class TimerService implements Runnable {
        private static Integer currentTimer = 0;
        private TickHandler tickHandler = null;

        public TimerService(TickHandler tickHandler) {
            this.tickHandler = tickHandler;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
                currentTimer++;
                tickHandler.tick(currentTimer);
            }
        }
    }
}
