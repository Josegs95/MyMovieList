package ui.view.component.dialog.auth;

import context.SessionContext;
import model.dto.UserDTO;
import net.miginfocom.swing.MigLayout;
import ui.view.MainFrame;

import javax.swing.*;

public class SessionDialog extends AuthenticationDialog{

    private final UserDTO userDTO = SessionContext.getInstance().getUser();

    private JButton btnAccept;
    private JButton btnCancel;

    private boolean sessionRefreshed = false;

    public SessionDialog(MainFrame mainFrame) {
        super(mainFrame, ModalityType.APPLICATION_MODAL);

        createUI();
        setTextFieldListeners();
    }

    private void createUI() {
        setTitle("Refrescar sesión");

        JTextField txtUsername = getTextFieldUsername();
        txtUsername.setText(userDTO.getUsername());
        txtUsername.setEnabled(false);

        JPanel pnlButtons = new JPanel(new MigLayout(
                "fill",
                "[center]15[center]",
                "[]"
        ));
        pnlButtons.setOpaque(false);

        btnAccept = new JButton("Aceptar");
        btnCancel = new JButton("Cancelar");
        setDefaultButton(btnAccept);

        pnlButtons.add(btnCancel, "sg button");
        pnlButtons.add(btnAccept, "sg button");

        add(pnlButtons);
    }

    public JButton getBtnAccept() {
        return btnAccept;
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }

    public boolean isSessionRefreshed() {
        return sessionRefreshed;
    }

    public void setSessionRefreshed(boolean sessionRefreshed) {
        this.sessionRefreshed = sessionRefreshed;
    }
}
