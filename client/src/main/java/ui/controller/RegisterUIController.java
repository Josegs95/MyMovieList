package ui.controller;

import context.SessionContext;
import service.AuthService;
import ui.event.RegisterUserEvent;
import ui.util.ErrorHandler;
import ui.util.EventBus;
import ui.util.Subscription;
import ui.view.component.dialog.auth.RegisterDialog;

public class RegisterUIController {

    private final RegisterDialog view;
    private final AuthService authService;

    private final Subscription subscription;

    public RegisterUIController(RegisterDialog view) {
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
        String password = view.getPassword();
        String email = view.getEmail();

        try {
            authService.register(username, password, email.isEmpty() ? null : email);

        } catch (Exception e) {
            ErrorHandler.showError(view, e);
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
