package ui.view.component.dialog.auth;

import model.entity.User;
import net.miginfocom.swing.MigLayout;
import ui.view.MainFrame;

import javax.swing.*;

public class LoginDialog extends AuthenticationDialog {

    private User user;
    private boolean loginSuccess = false;

    private JButton btnLogin;
    private JButton btnRegister;
    private JButton btnCancel;

    public LoginDialog(MainFrame mainFrame) {
        super(mainFrame, ModalityType.APPLICATION_MODAL);

        init();
    }

    private void init() {
        JPanel pnlButtons = new JPanel(new MigLayout(
                "fill",
                "[center]15[center]15[center]",
                "[]"
        ));
        pnlButtons.setOpaque(false);

        btnLogin = new JButton("Login");
        btnRegister = new JButton("Register");
        btnCancel = new JButton("Cancel");
        setDefaultButton(btnLogin);

        pnlButtons.add(btnCancel, "sg button");
        pnlButtons.add(btnRegister, "sg button");
        pnlButtons.add(btnLogin, "sg button");

        add(pnlButtons);

        setTextFieldListeners();
    }

    public void clearFields() {
        super.getTextFieldUsername().setText("");
        super.getTextFieldPassword().setText("");
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JButton getBtnRegister() {
        return btnRegister;
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }

    public String getUsername() {
        return super.getUsername();
    }

    public void setUsername(String username){
        super.setUsername(username);
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

    public void setLoginSuccess(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }

    public User getLoggedUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
