package view.component;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginDialog extends JDialog {

    final private JFrame PARENT;

    public LoginDialog(JFrame parent, boolean modal) {
        super(parent, modal);

        this.PARENT = parent;

        init();
    }

    private void init() {
        setLayout(new MigLayout(
                "align 50% 50%, flowy",
                "[align center, fill]",
                "[][]30[]"
        ));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(PARENT);
        setResizable(false);
        getContentPane().setBackground(new Color(223, 223, 223));
        setTitle("Login");

        //Components

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

            //Buttons
        JPanel pnlButtons = new JPanel(new MigLayout(
                "fill",
                "[]15[]15[]",
                "[]"
        ));
        pnlButtons.setOpaque(false);

        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Register");
        JButton btnCancel = new JButton("Cancel");

        pnlButtons.add(btnCancel, "sg button");
        pnlButtons.add(btnRegister, "sg button");
        pnlButtons.add(btnLogin, "sg button");


        //Listeners

        addWindowListener(new LoginWindowListener(PARENT, this));

        txfUsername.addActionListener(_ -> btnLogin.doClick());
        txfUsername.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txfUsername.selectAll();
            }
        });
        psfPassword.addActionListener(_ -> btnLogin.doClick());
        psfPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                psfPassword.selectAll();
            }
        });

        btnCancel.addActionListener(_ -> LoginDialog.this.dispose());

        //Adds

        add(pnlUsername);
        add(pnlPassword);
        add(pnlButtons);

        setVisible(true);
    }

    public class LoginWindowListener extends WindowAdapter{

        final private JFrame PARENT;
        final private JDialog INSTANCE;

        public LoginWindowListener(JFrame parent, JDialog instance){
            this.PARENT = parent;
            this.INSTANCE = instance;
        }

        @Override
        public void windowClosing(WindowEvent e) {
            INSTANCE.dispose();
        }

        @Override
        public void windowClosed(WindowEvent e) {
            PARENT.dispose();
        }
    }
}
