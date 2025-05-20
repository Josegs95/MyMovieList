package view;

import model.User;
import net.miginfocom.swing.MigLayout;
import thread.FetchUserLists;
import view.component.dialog.auth.LoginDialog;
import view.component.panel.SearchPanel;
import view.component.panel.UserListsPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame{

    private static MainFrame instance;
    private User user;

    private JPanel pnlCentral;
    private JButton btnLateralSearch;
    private JButton btnLateralLists;
    private static SearchPanel searchPanel;
    private static UserListsPanel userListsPanel;

    private static final String APP_TITLE = "MyMovieList";

    private MainFrame(boolean withLogin) {
        initFrame();

        if (withLogin) {
            LoginDialog dialog = new LoginDialog(this, true);
            if (dialog.isLoginSuccess()) {
                user = dialog.getLoggedUser();
                new Thread(new FetchUserLists(user)).start();
            }
        }

        finishInit();
        createListeners();
    }

    public static synchronized MainFrame getInstance(){
        if (instance == null) {
            instance = new MainFrame(true);
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

    public void removeDetailPanel() {
        if (pnlCentral == searchPanel) {
            searchPanel.removeDetailPanel();
        } else if (pnlCentral == userListsPanel) {
            userListsPanel.removeDetailPanel();
        }
    }

    public void updateCentralPanelUI() {
        if (pnlCentral == searchPanel) {
            searchPanel.updateState();
        } else if (pnlCentral == userListsPanel) {
            userListsPanel.updateUIStatus();
        }
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
        searchPanel = SearchPanel.getInstance();
        userListsPanel = new UserListsPanel(this);

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
            if (pnlCentral != userListsPanel) {
                changeCentralPanel(userListsPanel);
                userListsPanel.updateUIStatus();
            }
        });

        addWindowListener(new MainWindowListener(this));
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
