package ui.view;

import context.SessionContext;
import controller.ViewController;
import dto.UserDTO;
import net.miginfocom.swing.MigLayout;
import service.AuthService;
import service.SearchService;
import service.UserListService;
import ui.controller.LoginUIController;
import ui.controller.SearchUIController;
import ui.controller.UserListUIController;
import ui.view.component.dialog.auth.LoginDialog;
import ui.view.component.panel.SearchPanel;
import ui.view.component.panel.UserListPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MainFrame extends JFrame{

    private static final Color BACKGROUND_COLOR = new Color(192, 192, 192);

    private JPanel centralPanel;
    private JButton btnLateralSearch;
    private JButton btnLateralLists;
    private SearchPanel searchPanel;
    private UserListPanel userListPanel;

    private static final String APP_TITLE = "MyMovieList";
    private static final boolean LOGIN = true;

    public MainFrame() {
        initFrame();

        if (LOGIN) {
            if (!doLogin()) {
                dispose();
                throw new RuntimeException("The client closed the application");
            }
        }

        finishInit();
    }

    private void initFrame() {
        setTitle(APP_TITLE);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setVisible(true);
    }

    public void finishInit() {
        UserDTO user = SessionContext.getInstance().getUser();

        getContentPane().setLayout(new MigLayout(
                "fill",
                "[fill, 22%]5[fill, 78%]",
                "[fill]"
        ));
        searchPanel = new SearchPanel(this);
        userListPanel = new UserListPanel(this);
        new SearchUIController(searchPanel, new SearchService());
        new UserListUIController(userListPanel, new UserListService());

        // * BORRAR *
        ViewController.getInstance().registerView("searchPanel", searchPanel);
        ViewController.getInstance().registerView("userListPanel", userListPanel);
        //

        // Set central panel to "search mode"

        centralPanel = searchPanel;

        // Create "lateral panel"

        JPanel pnlLateral = new JPanel(new MigLayout(
                "flowy, ins 0, fill",
                "[fill]",
                "[fill, 25%]10[fill, 75%]"
        ));
        pnlLateral.setOpaque(false);

        // Create "user panel" and its components

        JPanel pnlUser = new JPanel(new MigLayout(
                "fill",
                "[fill]",
                "[fill]"
        ));
        pnlUser.setBackground(new Color(224, 224, 224));
        pnlUser.setBorder(LineBorder.createBlackLineBorder());

        String userName = "guest user";
        if (user != null && !user.getUsername().isEmpty()) {
            userName = user.getUsername();
        }
        JLabel lblUsername = new JLabel(String.format("<html><p text-align= center>Welcome, %s!</p></html>", userName),
                SwingConstants.CENTER);

        lblUsername.setFont(lblUsername.getFont().deriveFont(Font.ITALIC, 18));

        pnlUser.add(lblUsername);

        //Create "lateral menu" and its components

        JPanel pnlMenuLateral = new JPanel(new MigLayout(
                "flowy, ins 0",
                "[grow]",
                "[50]0[50]"
        ));
        pnlMenuLateral.setBackground(new Color(224, 224, 224));
        pnlMenuLateral.setBorder(LineBorder.createBlackLineBorder());

        btnLateralSearch = new MyLateralButton("Search");
        btnLateralLists = new MyLateralButton("Lists");

        pnlMenuLateral.add(btnLateralSearch, "grow");
        pnlMenuLateral.add(btnLateralLists, "grow");

        //Adds

        pnlLateral.add(pnlUser);
        pnlLateral.add(pnlMenuLateral);

        add(pnlLateral);
        add(centralPanel);

        // Logic

        revalidate();
        repaint();
    }

    private boolean doLogin() {
        LoginDialog loginDialog = new LoginDialog(this);
        new LoginUIController(loginDialog, new AuthService());
        loginDialog.setVisible(true);

        return loginDialog.isSuccessful();
    }

    public <T extends JPanel> void changeCentralPanel(Class<T> panelType) {
        // Check if the central panel is already the requested one.
        if (centralPanel.getClass().equals(panelType)) {
            return;
        }

        getContentPane().remove(centralPanel);

        if (panelType.equals(SearchPanel.class)) {
            centralPanel = searchPanel;
        } else if (panelType.equals(UserListPanel.class)) {
            centralPanel = userListPanel;
        }
        getContentPane().add(centralPanel);
        revalidate();
        repaint();
    }

    public JButton getBtnLateralSearch() {
        return btnLateralSearch;
    }

    public JButton getBtnLateralLists() {
        return btnLateralLists;
    }

    private static class MyLateralButton extends JButton {

        MyLateralButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(getFont().deriveFont(Font.BOLD, 18));
        }
    }
}
