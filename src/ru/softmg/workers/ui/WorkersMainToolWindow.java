package ru.softmg.workers.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import ru.softmg.workers.http.WorkersApiService;
import ru.softmg.workers.model.Report;
import ru.softmg.workers.model.ReportsPage;
import ru.softmg.workers.repository.CurrentUserComponent;
import ru.softmg.workers.repository.UserDailyReportsComponent;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

    WorkersMainToolWindow() {
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
    }

    JPanel getContent() {
        return workersMainToolWindowContent;
    }
}
