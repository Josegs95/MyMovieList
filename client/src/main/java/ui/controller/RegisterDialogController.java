package ui.controller;

import context.SessionContext;
import service.AuthService;
import ui.event.RegisterUserEvent;
import ui.util.ErrorHandler;
import ui.util.EventBus;
import ui.util.Subscription;
import ui.view.component.dialog.auth.RegisterDialog;

import java.util.Arrays;

public class RegisterDialogController {

    private final RegisterDialog view;
    private final AuthService authService;

    private final Subscription subscription;

    public RegisterDialogController(RegisterDialog view) {
        this.view = view;
        this.authService = SessionContext.getInstance().getAuthService();

        subscription = EventBus.subscribe(RegisterUserEvent.class, this::onRegisterUser);

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
        char[] password = view.getPassword();
        String email = view.getEmail();

        try {
            authService.register(username, new String(password).strip(), email.isEmpty() ? null : email);
        } catch (Exception e) {
            ErrorHandler.showError(view, e);
        } finally {
            Arrays.fill(password, '0');
        }
    }

    private void onCancel() {
        subscription.unsubscribe();
        view.dispose();
    }

    private void onRegisterUser(RegisterUserEvent registerUserEvent) {
        view.setRegisteredUser(registerUserEvent.username());
    }
}
