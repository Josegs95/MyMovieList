package ui.view.component.dialog.auth;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public abstract class AuthenticationDialog extends JDialog{

    private static final Color BACKGROUND_COLOR = new Color(223, 223, 223);

    private final Window parentWindow;
    private JButton btnDefault;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    AuthenticationDialog(Window owner, Dialog.ModalityType modalityType){
        super(owner, modalityType);

        parentWindow = owner;
        init();
    }

    private void init() {
        setLayout(new MigLayout(
                "align 50% 50%, flowy",
                "[align center, fill]",
                "[][]30[]"
        ));
        setSize(400, 300);
        setLocationRelativeTo(parentWindow);
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Components

        // Username field
        JPanel pnlUsername = new JPanel(new MigLayout(
                "fill, flowy",
                "[fill]",
                "[]0[]"
        ));
        pnlUsername.setOpaque(false);

        JLabel lblUsername = new JLabel("Usuario:");
        txtUsername = new JTextField(null, 20);

        pnlUsername.add(lblUsername);
        pnlUsername.add(txtUsername);

        // Password field
        JPanel pnlPassword = new JPanel(new MigLayout(
                "fill, flowy",
                "[fill]",
                "[]0[]"
        ));
        pnlPassword.setOpaque(false);

        JLabel lblPassword = new JLabel("Contraseña:");
        txtPassword = new JPasswordField(null, 20);

        pnlPassword.add(lblPassword);
        pnlPassword.add(txtPassword);

        add(pnlUsername);
        add(pnlPassword);
    }

    protected void setTextFieldListeners(){
        getTextFieldUsername().addActionListener(_ -> getBtnDefault().doClick());
        getTextFieldUsername().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                getTextFieldUsername().selectAll();
            }
        });
        getTextFieldPassword().addActionListener(_ -> getBtnDefault().doClick());
        getTextFieldPassword().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                getTextFieldPassword().selectAll();
            }
        });
    }

    public String getUsername() {
        return txtUsername.getText().strip();
    }

    public void setUsername(String username) {
        txtUsername.setText(username);
    }

    public String getPassword() {
        return new String(txtPassword.getPassword()).strip();
    }

    public JButton getBtnDefault() {
        return btnDefault;
    }

    public void setDefaultButton(JButton button){
        btnDefault = button;
    }

    public JTextField getTextFieldUsername() {
        return txtUsername;
    }

    public JPasswordField getTextFieldPassword() {
        return txtPassword;
    }
}
