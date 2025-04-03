package view.component.dialog;

import controller.AuthenticationController;
import model.ServerResponse;
import model.User;
import net.miginfocom.swing.MigLayout;
import view.MainFrame;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class LoginDialog extends AuthenticationDialog {

    private final MainFrame mainFrame;

    public LoginDialog(MainFrame mainFrame, boolean modal) {
        super(mainFrame, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);

        this.mainFrame = mainFrame;
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
            ServerResponse serverResponse = AuthenticationController.loginUser(userData);

            if (serverResponse.getStatus() != 200)
                JOptionPane.showMessageDialog(
                        LoginDialog.this,
                        serverResponse.getErrorMessage(),
                        "Error al registrarse",
                        JOptionPane.ERROR_MESSAGE);

            // Borrar
            System.out.println(serverResponse.getData());

            if ((boolean) serverResponse.getData().get("login")) {
                String username = userData.get("username").toString();
                Integer token = (Integer) (serverResponse.getData().get("token"));
                User user = new User(username, token);
                mainFrame.setUser(user);
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

    protected void setUsername(String username){
        getTextFieldMap().get("Username").setText(username);
    }
}
