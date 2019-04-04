package ru.softmg.workers.ui.form;

import ru.softmg.workers.model.Report;

import javax.swing.*;
import java.awt.event.*;

public class ReportViewDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonCancel;
    private JTextField dateTextField;
    private JTextField projectTextField;
    private JTextField taskTextField;
    private JTextField spentTextField;
    private JTextArea commentTextArea;

    public ReportViewDialog(Report report) {
        setContentPane(contentPane);
        setModal(true);

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        dateTextField.setText(report.getUpdatedAt());
        projectTextField.setText(report.getProjectName());
        taskTextField.setText(report.getJiraKey() + " " + report.getTaskName());
        spentTextField.setText((report.getSpentTime() / 60) + "h " + (report.getSpentTime() % 60) + "m ");
        commentTextArea.setText(report.getComment());

        dateTextField.setEnabled(false);
        projectTextField.setEnabled(false);
        taskTextField.setEnabled(false);
        spentTextField.setEnabled(false);
        commentTextArea.setEnabled(false);
    }

    private void onCancel() {
        dispose();
    }
}
