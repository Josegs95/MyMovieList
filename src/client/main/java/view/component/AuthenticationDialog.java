package view.component;

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

    final private Window PARENT;

    private Map<String, JTextField> textFieldMap;
    private JButton btnDefault;

    AuthenticationDialog(Window owner, Dialog.ModalityType modalityType){
        super(owner, modalityType);

        PARENT = owner;
        init();
    }

    private void init() {
        setLayout(new MigLayout(
                "align 50% 50%, flowy",
                "[align center, fill]",
                "[][]30[]"
        ));
        setSize(400, 300);
        setLocationRelativeTo(PARENT);
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

        addWindowListener(new AuthenticationWindowListener(PARENT, this));

        //Adds

        add(pnlUsername);
        add(pnlPassword);
    }

    protected Map<String, JTextField> getTextFieldMap() {
        return textFieldMap;
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

        final private Window PARENT;
        final private Window INSTANCE;

        public AuthenticationWindowListener(Window parent, Window instance){
            this.PARENT = parent;
            this.INSTANCE = instance;
        }

        @Override
        public void windowClosing(WindowEvent e) {
            INSTANCE.dispose();
        }

        @Override
        public void windowClosed(WindowEvent e) {
            if (PARENT instanceof JFrame)
                PARENT.dispose();
        }
    }
}
