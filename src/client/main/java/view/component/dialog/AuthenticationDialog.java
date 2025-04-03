package view.component.dialog;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public abstract class AuthenticationDialog extends JDialog{

    private final Window parentWindow;

    private Map<String, JTextField> textFieldMap;
    private JButton btnDefault;
    private boolean loginSuccess = false;

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
        getContentPane().setBackground(new Color(223, 223, 223));
        setTitle("Login");

        //Components

        textFieldMap = new HashMap<>();

        //Username
        JPanel pnlUsername = new JPanel(new MigLayout(
                "fill, flowy",
                "[fill]",
                "[]0[]"
        ));
        pnlUsername.setOpaque(false);

        JLabel lblUsername = new JLabel("Username");

        JTextField txfUsername = new JTextField(null, 20);

        pnlUsername.add(lblUsername);
        pnlUsername.add(txfUsername);

        //Password
        JPanel pnlPassword = new JPanel(new MigLayout(
                "fill, flowy",
                "[fill]",
                "[]0[]"
        ));
        pnlPassword.setOpaque(false);

        JLabel lblPassword = new JLabel("Password");

        JPasswordField psfPassword = new JPasswordField(null, 20);

        pnlPassword.add(lblPassword);
        pnlPassword.add(psfPassword);

        textFieldMap.put("Username", txfUsername);
        textFieldMap.put("Password", psfPassword);

        //Listeners

        addWindowListener(new AuthenticationWindowListener(parentWindow, this));

        //Adds

        add(pnlUsername);
        add(pnlPassword);
    }

    protected Map<String, JTextField> getTextFieldMap() {
        return textFieldMap;
    }

    protected void setLoginSuccess(){
        loginSuccess = true;
    }

    protected boolean isLoginSuccess(){
        return loginSuccess;
    }

    protected void setDefaultButton(JButton button){
        btnDefault = button;
    }

    protected void setTextFieldListeners(Map<String, JTextField> textFieldMap){
        for (JTextField textField : textFieldMap.values()){
            textField.addActionListener(_ -> btnDefault.doClick());
            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    textField.selectAll();
                }
            });
        }
    }


    private static class AuthenticationWindowListener extends WindowAdapter {

        private final Window parent;
        private final Window instance;

        public AuthenticationWindowListener(Window parent, Window instance){
            this.parent = parent;
            this.instance = instance;
        }

        @Override
        public void windowClosing(WindowEvent e) {
            instance.dispose();
        }

        @Override
        public void windowClosed(WindowEvent e) {
            if (parent instanceof JFrame){
                if (instance instanceof LoginDialog && !((LoginDialog) instance).isLoginSuccess())
                    parent.dispose();
            }

        }
    }
}
