package view;

import controller.MainFrameController;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrameView {
    private MainFrameController controller;

    final private String APP_TITLE = "MyMovieList";

    public MainFrameView(){
        init();
    }

    private void init() {
        JFrame frame = new JFrame(APP_TITLE);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new MainWindowListener(frame));

        frame.setVisible(true);
    }

    public void setController (MainFrameController controller){
        this.controller = controller;
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
}
