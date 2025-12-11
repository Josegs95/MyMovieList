package ui.controller;

import context.SessionContext;
import service.AuthService;
import ui.util.ErrorHandler;
import ui.view.component.dialog.auth.LoginDialog;
import ui.view.component.dialog.auth.RegisterDialog;

public class LoginUIController {

    private final LoginDialog view;
    private final AuthService authService;

    public LoginUIController(LoginDialog view) {
        this.view = view;
        this.authService = SessionContext.getInstance().getAuthService();

        initListeners();
    }

    private void initListeners() {
        view.getBtnLogin().addActionListener(_ -> onLogin());
        view.getBtnRegister().addActionListener(_ -> onRegister());
        view.getBtnCancel().addActionListener(_ -> onCancel());
    }

    private void onLogin() {
        String username = view.getUsername();
        String password = view.getPassword();

        try {
            authService.login(username, password);
            view.onLoginSuccess();
        } catch (Exception e) {
            ErrorHandler.showError(view, e);
        }
    }

    private void onRegister() {
        view.clearFields();
        RegisterDialog registerDialog = new RegisterDialog(view);
        new RegisterUIController(registerDialog);
        registerDialog.setVisible(true);
    }

    private void onCancel() {
        view.dispose();
    }
}
