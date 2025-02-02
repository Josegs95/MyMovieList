package view;

import controller.MainFrameController;
import net.miginfocom.swing.MigLayout;
import view.component.SearchFeaturePanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class MainFrameView {
    private MainFrameController controller;
    private JFrame frame;
    private JPanel pnlCentral;

    final private String APP_TITLE = "MyMovieList";

    private List<JPanel> centralPanelList = new ArrayList<>();

    public MainFrameView(){
        init();
    }

    private void init() {
        frame = new JFrame(APP_TITLE);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(new Color(192, 192, 192));
        frame.getContentPane().setLayout(new MigLayout(
                "fill",
                "[fill, grow 15]5[fill, grow 85]",
                "[fill]"
        ));

        JPanel pnlLateral = new JPanel(new MigLayout(
                "flowy",
                "[grow]",
                "[50]"
        ));
        pnlLateral.setBackground(new Color(224, 224,224));
        pnlLateral.setBorder(new LineBorder(Color.BLACK, 1, false));

        pnlCentral = SearchFeaturePanel.getInstance();

        //Lateral panel's components

        JButton btnLateralSearch = new MyLateralButton("Search");
        JButton btnLateralLists = new MyLateralButton("Lists");

        //Listeners

        btnLateralSearch.addActionListener(e -> {
            if (pnlCentral != SearchFeaturePanel.getInstance())
                changeCentralPanel(SearchFeaturePanel.getInstance());
        });

        btnLateralLists.addActionListener(e -> {
            changeCentralPanel(new JPanel());
        });

        //Adds

        pnlLateral.add(btnLateralSearch, "grow");
        pnlLateral.add(btnLateralLists, "grow");

        frame.add(pnlLateral);
        frame.add(pnlCentral);

        frame.addWindowListener(new MainWindowListener(frame));

        frame.setVisible(true);
    }

    public void setController (MainFrameController controller){
        this.controller = controller;
    }

    public void changeCentralPanel(JPanel panel){
        frame.remove(pnlCentral);
        pnlCentral = panel;
        frame.add(pnlCentral);

        frame.repaint();
    }

    private class MainWindowListener extends WindowAdapter{
        final private JFrame FRAME;

        public MainWindowListener(JFrame frame){
            this.FRAME = frame;
        }

        @Override
        public void windowClosing(WindowEvent e) {
            FRAME.dispose();
        }
    }

    private class MyLateralButton extends JButton{

        MyLateralButton(String text){
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(getFont().deriveFont(Font.BOLD, 18));
        }
    }
}
