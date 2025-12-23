package ui.view.component.dialog.auth;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;

public class RegisterDialog extends AuthenticationDialog{

    private final LoginDialog loginDialog;
    private JPasswordField txtRepeatPassword;
    private JTextField txtEmail;
    private JButton btnRegister;
    private JButton btnCancel;

    public RegisterDialog(LoginDialog loginDialog) {
        super(loginDialog, ModalityType.APPLICATION_MODAL);

        this.loginDialog = loginDialog;

        createUI();
        setTextFieldListeners();
    }

    private void createUI(){
        setLayout(new MigLayout(
                "align 50% 50%, flowy",
                "[align center, fill]",
                "[][][][]30[]"
        ));
        setSize(400, 400);
        setTitle("Registro");

        // Components

        // Repeat password field
        JPanel pnlPassword = new JPanel(new MigLayout(
                "fill, flowy",
                "[fill]",
                "[]0[]"
        ));
        pnlPassword.setOpaque(false);

        JLabel lblPassword = new JLabel("Repetir contraseña:");
        txtRepeatPassword = new JPasswordField(null, 20);

        pnlPassword.add(lblPassword);
        pnlPassword.add(txtRepeatPassword);

        // Email field
        JPanel pnlEmail = new JPanel(new MigLayout(
                "fill, flowy",
                "[fill]",
                "[]0[]"
        ));
        pnlEmail.setOpaque(false);

        JLabel lblEmail = new JLabel("<html>Email <small>(optional)</small>:</html>");
        txtEmail = new JTextField(null, 25);

        pnlEmail.add(lblEmail);
        pnlEmail.add(txtEmail);

        // Buttons
        JPanel pnlButtons = new JPanel(new MigLayout(
                "fill",
                "[center]15[center]",
                "[]"
        ));
        pnlButtons.setOpaque(false);

        btnRegister = new JButton("Registrar");
        btnCancel = new JButton("Cancelar");
        setDefaultButton(btnRegister);

        pnlButtons.add(btnCancel, "sg button");
        pnlButtons.add(btnRegister, "sg button");

        add(pnlPassword);
        add(pnlEmail);
        add(pnlButtons);
    }

    public boolean checkRegisterFields() {
        if (!checkUsernameField()) {
            return false;
        }
        if (!checkPasswordFields()) {
            return false;
        }

        return checkEmailField();
    }

    private boolean checkUsernameField() {
        String username = this.getUsername();
        if (username == null || username.isEmpty()) {
            JOptionPane.showMessageDialog(
                    loginDialog,
                    "El campo usuario es obligatorio",
                    "Error en el registro",
                    JOptionPane.ERROR_MESSAGE
            );

            return false;
        }
        return true;
    }

    private boolean checkPasswordFields() {
        char[] password = getPassword();
        char[] repeatedPassword = getRepeatedPassword();

        if (password.length == 0 || repeatedPassword.length == 0) {
            JOptionPane.showMessageDialog(
                    loginDialog,
                    "Los campos contraseña son obligatorios",
                    "Error en el registro",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        if (!Arrays.equals(password, repeatedPassword)) {
            JOptionPane.showMessageDialog(
                    loginDialog,
                    "Las contraseñas no coinciden entre sí",
                    "Error en el registro",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        return true;
    }

    private boolean checkEmailField() {
        String email = getEmail();

        if (email == null || email.isEmpty()) {
            return true;
        }

        String emailPattern = "^\\w+([.\\-_]?\\w+)*@\\w+(\\w+)?\\.\\w{2,3}$";
        if (!email.matches(emailPattern)) {
            JOptionPane.showMessageDialog(
                    loginDialog,
                    "El email no es válido",
                    "Error en el registro",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        return true;
    }

    public void setRegisteredUser(String username) {
        this.setUsername(username);
        this.loginDialog.getTextFieldPassword().requestFocus();
        this.dispose();
    }

    @Override
    protected void setTextFieldListeners() {
        super.setTextFieldListeners();
        getTxtRepeatPassword().addActionListener(_ -> getBtnRegister().doClick());
        getTxtRepeatPassword().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                getTxtRepeatPassword().selectAll();
            }
        });
        getTxtEmail().addActionListener(_ -> getBtnRegister().doClick());
        getTxtEmail().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                getTxtEmail().selectAll();
            }
        });
    }

    public void setUsername(String username) {
        loginDialog.setUsername(username);
    }

    public String getEmail() {
        return getTxtEmail().getText().strip();
    }

    public char[] getRepeatedPassword() {
        return getTxtRepeatPassword().getPassword();
    }

    public JPasswordField getTxtRepeatPassword() {
        return txtRepeatPassword;
    }

    public JTextField getTxtEmail() {
        return txtEmail;
    }

    public JButton getBtnRegister() {
        return btnRegister;
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }
}
