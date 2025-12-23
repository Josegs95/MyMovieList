package ui.controller;

import service.AuthService;
import ui.util.ErrorHandler;
import ui.view.component.dialog.auth.SessionDialog;

import java.util.Arrays;

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
        char[] password = view.getPassword();

        try {
            authService.login(username, new String(password).strip());

            view.setSessionRefreshed(true);
            view.dispose();
        } catch (Exception e) {
            ErrorHandler.showError(view, e);
        } finally {
            Arrays.fill(password, '0');
        }
    }

    private void onBtnCancel() {
        view.dispose();
    }
}
