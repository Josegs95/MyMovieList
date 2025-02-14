package view.component;

import controller.SearchController;
import model.Multimedia;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DetailMultimediaPanel extends JPanel {

    final private Multimedia MULTIMEDIA;
    final private SearchController CONTROLLER;

    public DetailMultimediaPanel(Multimedia multimedia, SearchController controller) {
        super(new MigLayout(
                "",
                "",
                ""));

        this.MULTIMEDIA = multimedia;
        this.CONTROLLER = controller;

        init();
    }

    private void init() {
        setBackground(Color.ORANGE);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1 -> System.out.println(MULTIMEDIA);
                    case MouseEvent.BUTTON3 -> CONTROLLER.backButtonFromDetailPanel();
                    default -> {
                    }
                }
            }
        });
    }

    public Multimedia getMultimedia() {
        return MULTIMEDIA;
    }
}
