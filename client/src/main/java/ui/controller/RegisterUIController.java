package ui.controller;

import service.AuthService;
import ui.util.ErrorHandler;
import ui.view.component.dialog.auth.RegisterDialog;

public class RegisterUIController {

    private final RegisterDialog view;
    private final AuthService authService;

    public RegisterUIController(RegisterDialog view, AuthService authService) {
        this.view = view;
        this.authService = authService;

        initListeners();
    }

    private void initListeners() {
        view.getBtnRegister().addActionListener(_ -> onRegister());
        view.getBtnCancel().addActionListener(_ -> onCancel());
    }

    private void onRegister() {
        if (!view.checkRegisterFields()) {
            return;
        }

        String username = view.getUsername();
        String password = view.getPassword();
        String email = view.getEmail();

        try {
            authService.register(username, password, email.isEmpty() ? null : email);
            view.setUsername(username);
            view.dispose();
        } catch (Exception e) {
            ErrorHandler.showError(view, e);
        }
    }

    private void onCancel() {
        view.dispose();
    }
}
