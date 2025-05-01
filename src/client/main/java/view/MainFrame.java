package view;

import model.User;
import net.miginfocom.swing.MigLayout;
import thread.FetchUserLists;
import view.component.dialog.LoginDialog;
import view.component.panel.SearchPanel;
import view.component.panel.UserListsPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame{

    private JPanel pnlCentral;
    private User user;

    private static final String APP_TITLE = "MyMovieList";
    private static MainFrame instance;

    private static SearchPanel searchPanel;
    private static UserListsPanel userListsPanel;

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
    }

    public static MainFrame getInstance(){
        if (instance == null) {
            instance = new MainFrame(true);
            userListsPanel = new UserListsPanel();
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

    public void updateCentralPanelUI() {
        if (pnlCentral == searchPanel) {
            searchPanel.updateDetailPanel();
        } else if (pnlCentral == userListsPanel) {
            userListsPanel.printUserLists();
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

        //Initialize lateral panel

        JPanel pnlLateral = new JPanel(new MigLayout(
                "flowy, ins 0, fill",
                "[fill]",
                "[fill, 25%]10[fill, 75%]"
        ));
        pnlLateral.setOpaque(false);

        //Set central panel to "search mode"

        searchPanel = SearchPanel.getInstance();
        pnlCentral = searchPanel;

        //User panel initialization

        JPanel pnlUser = new JPanel(new MigLayout(
                "fill",
                "[fill]",
                "[fill]"
        ));
        pnlUser.setBackground(new Color(224, 224, 224));
        pnlUser.setBorder(LineBorder.createBlackLineBorder());

        //User panel components

        JLabel lblUsername = new JLabel();
        String welcomeMessage = "<html><p align=center>Welcome, [username]!</p></html>";
        if (user != null && !user.getUsername().isEmpty()) {
            welcomeMessage = welcomeMessage.replace("[username]", user.getUsername());
        }

        lblUsername.setText(welcomeMessage);
        lblUsername.setFont(lblUsername.getFont().deriveFont(Font.ITALIC, 18));
        lblUsername.setHorizontalAlignment(SwingConstants.CENTER);

        pnlUser.add(lblUsername);

        //Lateral menu components

        JPanel pnlMenuLateral = new JPanel(new MigLayout(
                "flowy, ins 0",
                "[grow]",
                "[50]0[50]"
        ));
        pnlMenuLateral.setBackground(new Color(224, 224, 224));
        pnlMenuLateral.setBorder(LineBorder.createBlackLineBorder());

        JButton btnLateralSearch = new MyLateralButton("Search");
        JButton btnLateralLists = new MyLateralButton("Lists");

        pnlMenuLateral.add(btnLateralSearch, "grow");
        pnlMenuLateral.add(btnLateralLists, "grow");

        //Listeners

        btnLateralSearch.addActionListener(_ -> {
            if (pnlCentral != searchPanel) {
                changeCentralPanel(searchPanel);
                searchPanel.updateDetailPanel();
            }

        });

        btnLateralLists.addActionListener(_ -> {
            if (pnlCentral != userListsPanel) {
                changeCentralPanel(userListsPanel);
                userListsPanel.printUserLists();
            }
        });

        addWindowListener(new MainWindowListener(this));

        //Adds

        pnlLateral.add(pnlUser);
        pnlLateral.add(pnlMenuLateral);

        add(pnlLateral);
        add(pnlCentral);

        // Logic

        revalidate();
        repaint();
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
