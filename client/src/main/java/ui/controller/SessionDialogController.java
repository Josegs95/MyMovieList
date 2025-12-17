package ui.controller;

import service.AuthService;
import ui.util.ErrorHandler;
import ui.view.component.dialog.auth.SessionDialog;

public class SessionDialogController {

    private final SessionDialog view;
    private final AuthService authService;

    public SessionDialogController(SessionDialog view, AuthService authService) {
        this.view = view;
        this.authService = authService;

        initControllers();
    }

    private void initControllers() {
        view.getBtnAccept().addActionListener(_ -> onBtnAccept());
        view.getBtnCancel().addActionListener(_ -> onBtnCancel());
    }

    private void onBtnAccept() {
        String username = view.getUsername();
        String password = view.getPassword();

        try {
            authService.login(username, password);

            view.setSessionRefreshed(true);
            view.dispose();
        } catch (Exception e) {
            ErrorHandler.showError(view, e);
        }
    }

    private void onBtnCancel() {
        view.dispose();
    }
}
