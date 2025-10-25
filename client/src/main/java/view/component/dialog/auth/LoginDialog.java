package view.component.dialog.auth;

import controller.AuthenticationController;
import model.Message;
import model.User;
import net.miginfocom.swing.MigLayout;
import view.MainFrame;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class LoginDialog extends AuthenticationDialog {

    private User user;

    public LoginDialog(MainFrame mainFrame, boolean modal) {
        super(mainFrame, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);

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
            userData.put("username", textFieldMap.get("Username").getText().strip());
            userData.put("password", textFieldMap.get("Password").getText().strip());
            Message serverMessage = AuthenticationController.loginUser(userData);

//            if (serverMessage.status() != 200)
//                JOptionPane.showMessageDialog(
//                        LoginDialog.this,
//                        serverMessage.getErrorMessage(),
//                        "Error al registrarse",
//                        JOptionPane.ERROR_MESSAGE);

            if ((boolean) serverMessage.content().get("login")) {
                String username = userData.get("username").toString();
                Integer token = (Integer) (serverMessage.content().get("token"));

                user = new User(username, token);
                setLoginSuccess();
                dispose();
            } else
                JOptionPane.showMessageDialog(
                        LoginDialog.this,
                        "Credenciales de usuario incorrectas. Reviselas e intentelo de nuevo",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);

        });
        btnRegister.addActionListener(_ -> {
            for (JTextField textField : textFieldMap.values())
                textField.setText("");
            new RegisterDialog(LoginDialog.this, true);
        });
        btnCancel.addActionListener(_ -> LoginDialog.this.dispose());

        setTextFieldListeners(textFieldMap);

        //Adds

        add(pnlButtons);

        setVisible(true);
    }

    public User getLoggedUser() {
        return user;
    }

    protected void setUsername(String username){
        getTextFieldMap().get("Username").setText(username);
    }
}
