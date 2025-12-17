package ui.controller;

import context.SessionContext;
import ui.event.SessionExpiredEvent;
import ui.util.ErrorHandler;
import ui.util.EventBus;
import ui.view.MainFrame;
import ui.view.component.dialog.auth.SessionDialog;
import ui.view.component.panel.SearchPanel;
import ui.view.component.panel.UserListPanel;

import javax.swing.*;

public class MainFrameUIController {

    private final MainFrame view;

    public MainFrameUIController(MainFrame view) {
        this.view = view;

        EventBus.subscribe(SessionExpiredEvent.class, this::onSessionExpiredEvent);

        initListeners();
    }

    private void onSessionExpiredEvent(SessionExpiredEvent sessionExpiredEvent) {
        String message = "Sesión expirada. Introduzca tu contraseña para refrescarla";
        JOptionPane.showMessageDialog(view, message, "Información", JOptionPane.INFORMATION_MESSAGE);

        try {
            SessionDialog sessionDialog = new SessionDialog(view);
            new SessionDialogController(sessionDialog, SessionContext.getInstance().getAuthService());
            sessionDialog.setVisible(true);

            if (sessionDialog.isSessionRefreshed()) {
                sessionExpiredEvent.action().execute();
                return;
            }

            view.dispose();
        } catch (Exception e) {
            ErrorHandler.showError(view, e);
        }
    }

    private void initListeners() {
        view.getBtnLateralSearch().addActionListener(_ -> onSearchLateralMenu());
        view.getBtnLateralLists().addActionListener(_ -> onListsLateralMenu());
    }

    private void onSearchLateralMenu() {
        view.changeCentralPanel(SearchPanel.class);
    }

    private void onListsLateralMenu() {
        view.changeCentralPanel(UserListPanel.class);
    }
}
