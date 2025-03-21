package view;

import controller.AuthenticationController;
import controller.SearchController;
import net.miginfocom.swing.MigLayout;
import view.component.LoginDialog;
import view.component.SearchPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrameView {
    private JFrame frame;
    private JPanel pnlCentral;

    final private String APP_TITLE = "MyMovieList";

    public MainFrameView() {
        initFrame();

        LoginDialog loginDialog = new LoginDialog(frame, true, new AuthenticationController());

        finishInit();
    }

    private void initFrame() {
        frame = new JFrame(APP_TITLE);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(new Color(192, 192, 192));

        frame.setVisible(true);
    }

    private void finishInit() {
        frame.getContentPane().setLayout(new MigLayout(
                "fill",
                "[fill, 22%]5[fill, 78%]",
                "[fill]"
        ));

        JPanel pnlLateral = new JPanel(new MigLayout(
                "flowy",
                "[grow]",
                "[50]"
        ));
        pnlLateral.setBackground(new Color(224, 224, 224));
        pnlLateral.setBorder(new LineBorder(Color.BLACK, 1, false));

        SearchPanel searchPanel = SearchPanel.getInstance();
        searchPanel.setController(new SearchController(searchPanel));
        pnlCentral = searchPanel;

        //Lateral panel's components

        JButton btnLateralSearch = new MyLateralButton("Search");
        JButton btnLateralLists = new MyLateralButton("Lists");

        //Listeners

        btnLateralSearch.addActionListener(_ -> {
            if (pnlCentral != SearchPanel.getInstance())
                changeCentralPanel(SearchPanel.getInstance());
        });

        btnLateralLists.addActionListener(_ -> {
            JPanel panel = new JPanel();
            panel.setBackground(new Color(224, 224, 224));
            panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false));

            changeCentralPanel(panel);
        });

        //Adds

        pnlLateral.add(btnLateralSearch, "grow");
        pnlLateral.add(btnLateralLists, "grow");

        frame.add(pnlLateral);
        frame.add(pnlCentral);

        frame.addWindowListener(new MainWindowListener(frame));

        frame.revalidate();
        frame.repaint();
    }

    public void changeCentralPanel(JPanel panel) {
        frame.getContentPane().remove(pnlCentral);
        pnlCentral = panel;
        frame.getContentPane().add(pnlCentral);

        frame.revalidate();
        frame.repaint();
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
