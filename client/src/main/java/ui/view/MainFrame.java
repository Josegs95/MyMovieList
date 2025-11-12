package ui.view;

import controller.ViewController;
import model.entity.User;
import net.miginfocom.swing.MigLayout;
import service.AuthService;
import thread.FetchUserLists;
import ui.controller.LoginUIController;
import ui.view.component.dialog.auth.LoginDialog;
import ui.view.component.panel.SearchPanel;
import ui.view.component.panel.UserListPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame{

    private static volatile MainFrame instance;
    private User user;

    private JPanel pnlCentral;
    private JButton btnLateralSearch;
    private JButton btnLateralLists;
    private SearchPanel searchPanel;
    private UserListPanel userListPanel;

    private static final String APP_TITLE = "MyMovieList";
    private static final boolean LOGIN = true;

    private MainFrame() {
        initFrame();

        if (LOGIN) {
            doLogin();
        }

        finishInit();
        createListeners();
    }

    public static MainFrame getInstance(){
        if (instance == null) {
            synchronized (MainFrame.class) {
                if (instance == null) {
                    instance = new MainFrame();
                }
            }
        }

        return instance;
    }

    public void changeCentralPanel(JPanel panel) {
        getContentPane().remove(pnlCentral);
        pnlCentral = panel;
        getContentPane().add(pnlCentral);

        revalidate();
        repaint();
    }

    public void setUser(User user){
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    private void initFrame() {
        setTitle(APP_TITLE);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(192, 192, 192));

        setVisible(true);
    }

    private void finishInit() {
        getContentPane().setLayout(new MigLayout(
                "fill",
                "[fill, 22%]5[fill, 78%]",
                "[fill]"
        ));
        searchPanel = new SearchPanel(this);
        userListPanel = new UserListPanel(this);

        ViewController.getInstance().registerView("searchPanel", searchPanel);
        ViewController.getInstance().registerView("userListPanel", userListPanel);

        // Set central panel to "search mode"

        pnlCentral = searchPanel;

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
        add(pnlCentral);

        // Logic

        revalidate();
        repaint();
    }

    private void createListeners() {
        btnLateralSearch.addActionListener(_ -> {
            if (pnlCentral != searchPanel) {
                changeCentralPanel(searchPanel);
                searchPanel.updateState();
            }
        });

        btnLateralLists.addActionListener(_ -> {
            if (pnlCentral != userListPanel) {
                changeCentralPanel(userListPanel);
                userListPanel.updateState();
            }
        });

        addWindowListener(new MainWindowListener(this));
    }

    private void doLogin() {
        LoginDialog loginDialog = new LoginDialog(this);
        new LoginUIController(loginDialog, new AuthService());
        loginDialog.setVisible(true);

        if (loginDialog.isLoginSuccess()) {
            this.user = loginDialog.getLoggedUser();
//            new Thread(new FetchUserLists(user)).start();
        }
    }

    private static class MainWindowListener extends WindowAdapter {
        final private JFrame FRAME;

        public MainWindowListener(JFrame frame) {
            this.FRAME = frame;
        }

        @Override
        public void windowClosing(WindowEvent e) {
            FRAME.dispose();
        }
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
