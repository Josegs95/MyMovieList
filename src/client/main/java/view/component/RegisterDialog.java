package view.component;

import controller.AuthenticationController;
import exception.RegisterValidationException;
import model.ServerResponse;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class RegisterDialog extends AuthenticationDialog{

    final private JDialog PARENT;
    final private AuthenticationController CONTROLLER;

    protected RegisterDialog(JDialog parent, boolean modal, AuthenticationController controller) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);

        this.PARENT = parent;
        this.CONTROLLER = controller;
        init();
    }

    private void init(){
        setLayout(new MigLayout(
                "align 50% 50%, flowy",
                "[align center, fill]",
                "[][][][]30[]"
        ));
        setSize(400, 400);
        setLocationRelativeTo(PARENT);
        setTitle("Register");

        Map<String, JTextField> textFieldMap = getTextFieldMap();

        //Components

            //Password
        JPanel pnlPassword = new JPanel(new MigLayout(
                "fill, flowy",
                "[fill]",
                "[]0[]"
        ));
        pnlPassword.setOpaque(false);

        JLabel lblPassword = new JLabel("Repeat Password");

        JPasswordField psfPassword = new JPasswordField(null, 20);

        pnlPassword.add(lblPassword);
        pnlPassword.add(psfPassword);

            //Email
        JPanel pnlEmail = new JPanel(new MigLayout(
                "fill, flowy",
                "[fill]",
                "[]0[]"
        ));
        pnlEmail.setOpaque(false);

        JLabel lblEmail = new JLabel(
                "<html>Email <small>(optional)</small></html>"
        );

        JTextField txfEmail = new JTextField(null, 25);

        pnlEmail.add(lblEmail);
        pnlEmail.add(txfEmail);

        textFieldMap.put("Password2", psfPassword);
        textFieldMap.put("Email", txfEmail);

            //Buttons
        JPanel pnlButtons = new JPanel(new MigLayout(
                "fill",
                "[center]15[center]",
                "[]"
        ));
        pnlButtons.setOpaque(false);

        JButton btnRegister = new JButton("Register");
        JButton btnCancel = new JButton("Cancel");
        setDefaultButton(btnRegister);

        pnlButtons.add(btnCancel, "sg button");
        pnlButtons.add(btnRegister, "sg button");

        //Listeners

        btnCancel.addActionListener(_ -> RegisterDialog.this.dispose());
        btnRegister.addActionListener(_ -> {
            try{
                Map<String, Object> userData = new HashMap<>();
                for (Map.Entry<String, JTextField> entry : textFieldMap.entrySet()){
                    userData.put(entry.getKey().toLowerCase(), entry.getValue().getText());
                }
                ServerResponse serverResponse = CONTROLLER.registerUser(userData);

                if (serverResponse.getStatus() == 200) {
                    if (PARENT instanceof LoginDialog) {
                        ((LoginDialog) PARENT).setUsername(textFieldMap.get("Username").getText());
                    }
                    dispose();
                } else
                    JOptionPane.showMessageDialog(
                            RegisterDialog.this,
                            serverResponse.getMessageError(),
                            "Error al registrarse",
                            JOptionPane.ERROR_MESSAGE
                    );
            } catch (RegisterValidationException e) {
                JOptionPane.showMessageDialog(
                        RegisterDialog.this,
                        e.getMessage(),
                        "Error al registrarse",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        setTextFieldListeners(textFieldMap);

        //Adds

        add(pnlPassword);
        add(pnlEmail);
        add(pnlButtons);

        setVisible(true);
    }
}
