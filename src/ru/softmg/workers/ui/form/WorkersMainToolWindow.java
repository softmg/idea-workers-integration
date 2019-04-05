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
import ru.softmg.workers.ui.ColorTableRenderer;
import ru.softmg.workers.ui.handler.TickHandler;
import ru.softmg.workers.ui.model.ProjectsComboBoxModel;
import ru.softmg.workers.ui.model.ReportsTableModel;
import ru.softmg.workers.ui.model.TasksComboBoxModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
    Integer currentTick = 0;

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
        reportsTable.setDefaultRenderer(String.class, new ColorTableRenderer());
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
            if(projectComboBox.getSelectedItem() != null) {
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
            }
        });

        startButton.addActionListener(e -> {
            Project selectedProject = ((ProjectsComboBoxModel)projectComboBox.getModel()).getSelected();
            Task selectedTask = ((TasksComboBoxModel)taskComboBox.getModel()).getSelected();
            if(selectedProject != null && selectedTask != null) {
                if(startButton.getText().equals("Start")) {
                    timerThread = new Thread(new TimerService(this::timerTick));
                    timerThread.start();
                    startButton.setText("Stop");
                    projectComboBox.setEnabled(false);
                    taskComboBox.setEnabled(false);
                } else {
                    if(timerThread.isAlive())
                        timerThread.interrupt();
                    startButton.setText("Start");
                }
            }
        });

        saveButton.addActionListener(e -> {
            Project selectedProject = ((ProjectsComboBoxModel)projectComboBox.getModel()).getSelected();
            Task selectedTask = ((TasksComboBoxModel)taskComboBox.getModel()).getSelected();
            ((ReportsTableModel)reportsTable.getModel()).getReports().add(
                    new Report(
                            null,
                            null,
                            Integer.parseInt(selectedProject.getId()),
                            selectedTask.getId(),
                            selectedTask.getName(),
                            "",
                            currentTick,
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            selectedProject.getName(),
                            null,
                            selectedTask.getName().split(":")[0],
                            "",
                            true
                            ));
            projectComboBox.setEnabled(true);
            projectComboBox.setSelectedItem(null);
            taskComboBox.setEnabled(true);
            taskComboBox.setSelectedItem(null);
            reportsTable.updateUI();

            timerThread = new Thread(new TimerService(0, this::timerTick));
        });
    }

    private void timerTick(Integer tick) {
        this.currentTick = tick;
        timerLabel.setText(getTimer(tick));
    }

    private String getTimer(Integer timer) {
        String hours = String.format("%02d", timer / 3600);
        String minutes = String.format("%02d", (timer % 3600) / 60);
        String seconds = String.format("%02d", (timer % 3600) % 60);
        return hours + ":" + minutes + ":" + seconds;
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

        public TimerService(Integer timer, TickHandler tickHandler) {
            currentTimer = timer;
            this.tickHandler = tickHandler;
            tickHandler.tick(currentTimer);
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
