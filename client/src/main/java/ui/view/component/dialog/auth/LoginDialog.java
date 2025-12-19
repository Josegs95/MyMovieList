package ui.view.component.dialog.auth;

import net.miginfocom.swing.MigLayout;
import ui.view.MainFrame;

import javax.swing.*;

public class LoginDialog extends AuthenticationDialog {

    private JButton btnLogin;
    private JButton btnRegister;
    private JButton btnCancel;

    private boolean successful = false;

    public LoginDialog(MainFrame mainFrame) {
        super(mainFrame, ModalityType.APPLICATION_MODAL);

        createUI();
        setTextFieldListeners();
    }

    private void createUI() {
        setTitle("Login");

        JPanel pnlButtons = new JPanel(new MigLayout(
                "fill",
                "[center]15[center]15[center]",
                "[]"
        ));
        pnlButtons.setOpaque(false);

        btnLogin = new JButton("Login");
        btnRegister = new JButton("Registro");
        btnCancel = new JButton("Cancelar");
        setDefaultButton(btnLogin);

        pnlButtons.add(btnCancel, "sg button");
        pnlButtons.add(btnRegister, "sg button");
        pnlButtons.add(btnLogin, "sg button");

        add(pnlButtons);
    }

    public void clearFields() {
        super.getTextFieldUsername().setText("");
        super.getTextFieldPassword().setText("");
    }

    public void onLoginSuccess() {
        successful = true;
        dispose();
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

    public boolean isSuccessful() {
        return successful;
    }

    public void setUsername(String username){
        super.setUsername(username);
    }
}
