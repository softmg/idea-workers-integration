package ru.softmg.workers.ui.form;

import ru.softmg.workers.http.WorkersApiService;
import ru.softmg.workers.model.User;
import ru.softmg.workers.ui.handler.LoginHandler;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

public class LoginDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPasswordField passwordField1;
    private JTextField textField1;
    private LoginHandler loginHandler;

    private WorkersApiService workersApiService;

    LoginDialog(WorkersApiService workersApiService) {
        this.workersApiService = workersApiService;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public void setLoginHandler(LoginHandler loginHandler) {
        this.loginHandler = loginHandler;
    }

    private void onOK() {
        try {
            workersApiService.postLogin(textField1.getText(), new String(passwordField1.getPassword()))
                    .thenAccept(o -> loginHandler.loggedInHandler((User)o));
        } catch (IOException e) {
            e.printStackTrace();
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
