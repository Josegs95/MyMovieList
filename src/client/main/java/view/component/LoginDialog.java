package view.component;

import controller.AuthenticationController;
import model.ServerResponse;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class LoginDialog extends AuthenticationDialog {

    final private AuthenticationController CONTROLLER;

    public LoginDialog(JFrame parent, boolean modal, AuthenticationController controller) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);

        this.CONTROLLER = controller;
        init();
    }

    private void init() {
        //Components

            //Buttons
        JPanel pnlButtons = new JPanel(new MigLayout(
                "fill",
                "[center]15[center]15[center]",
                "[]"
        ));
        pnlButtons.setOpaque(false);

        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Register");
        JButton btnCancel = new JButton("Cancel");
        setDefaultButton(btnLogin);

        pnlButtons.add(btnCancel, "sg button");
        pnlButtons.add(btnRegister, "sg button");
        pnlButtons.add(btnLogin, "sg button");

        //Listeners

        Map<String, JTextField> textFieldMap = getTextFieldMap();
        btnLogin.addActionListener(_ ->{
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", textFieldMap.get("Username").getText());
            userData.put("password", textFieldMap.get("Password").getText());
            ServerResponse serverResponse = CONTROLLER.loginUser(userData);

            if (serverResponse.getStatus() != 200)
                JOptionPane.showMessageDialog(
                        LoginDialog.this,
                        serverResponse.getMessageError(),
                        "Error al registrarse",
                        JOptionPane.ERROR_MESSAGE);


            if ((boolean) serverResponse.getData().get("login"))
                JOptionPane.showMessageDialog(
                        LoginDialog.this,
                        "Usuario logueado correctamente",
                        "Login",
                        JOptionPane.INFORMATION_MESSAGE);
            else
                JOptionPane.showMessageDialog(
                        LoginDialog.this,
                        "Credenciales de usuario incorrectas. Reviselas e intentelo de nuevo",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);

        });
        btnRegister.addActionListener(_ -> {
            for (JTextField textField : textFieldMap.values())
                textField.setText("");
            new RegisterDialog(LoginDialog.this, true, CONTROLLER);
        });
        btnCancel.addActionListener(_ -> LoginDialog.this.dispose());

        setTextFieldListeners(textFieldMap);

        //Adds

        add(pnlButtons);

        setVisible(true);
    }

    protected void setUsername(String username){
        getTextFieldMap().get("Username").setText(username);
    }
}
