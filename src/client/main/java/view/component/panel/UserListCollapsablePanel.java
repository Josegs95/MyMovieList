package view.component.panel;

import lib.ScrollablePanel;
import lib.StretchIcon;
import model.*;
import net.miginfocom.swing.MigLayout;
import view.MainFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class UserListCollapsablePanel extends JPanel {

    private boolean isCollapsed = true;
    private final UserList multimediaList;

    public UserListCollapsablePanel(UserList userList){
        this.multimediaList = userList;

        init();
    }

    private void init() {
        setLayout(new MigLayout(
                "fill, flowy, gap 0 0, ins 0",
                "[fill]",
                "[fill]"
        ));

        //Components
        JButton btnExpand = new JButton(multimediaList.toString());
        btnExpand.setContentAreaFilled(false);
        btnExpand.setFocusable(false);
        btnExpand.setHorizontalAlignment(SwingConstants.LEFT);
        Border borderButton = BorderFactory.createCompoundBorder(LineBorder.createBlackLineBorder(),
                BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btnExpand.setBorder(borderButton);

        JPanel pnlContent = new ScrollablePanel(new MigLayout(
                "ins 0, fill, gap 0 0, flowy",
                "[fill]",
                "[100!, fill]"
        ));
        fillPanelWithMultimedia(pnlContent);

        //Listener
        btnExpand.addActionListener(_ ->{
            if (isCollapsed && !multimediaList.getMultimediaList().isEmpty()){
                UserListCollapsablePanel.this.add(pnlContent);
            } else {
                UserListCollapsablePanel.this.remove(pnlContent);
            }

            isCollapsed = !isCollapsed;
            UserListCollapsablePanel.this.revalidate();
            UserListCollapsablePanel.this.repaint();
        });

        add(btnExpand, "hmin 100");
    }

    private void fillPanelWithMultimedia(JPanel pnlContent){
        for (MultimediaAtList multimediaAtList : multimediaList.getMultimediaList()){
            MultimediaType multimediaType = multimediaAtList.getMultimedia().getMultimediaType();
            Multimedia multimedia = multimediaAtList.getMultimedia();

            // Set up components

            JPanel panel = new JPanel(new MigLayout(
                    "ins 0 5 0 0, fill, debug",
                    "[20%, fill][20%, fill][20%, fill][20%, fill][20%, fill]",
                    "[fill]"
            ));
            panel.setBackground(multimediaType == MultimediaType.MOVIE ? Color.CYAN : Color.PINK);
            panel.setBorder(LineBorder.createGrayLineBorder());

            JLabel lblTitle = new JLabel("<html><span align=center>"
                    + multimedia.getTitle() + "</span></html>");
            JLabel lblType = new JLabel(multimediaType.toString());
            JLabel lblStatus = new JLabel(multimediaAtList.getStatus().toString());
            String episodeString = "";
            if (multimediaType == MultimediaType.TV_SHOW){
                episodeString = String.join(
                        "/",
                        String.valueOf(multimediaAtList.getCurrentEpisode()),
                        String.valueOf(((TvShow) multimedia).getTotalEpisodes()));
            }
            JLabel lblCurrentEpisode = new JLabel(episodeString);

            lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
            lblType.setHorizontalAlignment(SwingConstants.CENTER);
            lblCurrentEpisode.setHorizontalAlignment(SwingConstants.CENTER);
            lblStatus.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel pnlButtons = new JPanel(new MigLayout(
                    "fill, flowy, gapy 6!, align 50% 50%",
                    "[35!, fill]",
                    "[35!, fill]"));
            pnlButtons.setOpaque(false);

            ImageIcon configIcon = getCustomImageIcon("images/config.png");
            ImageIcon deleteIcon = getCustomImageIcon("images/delete.png");
            JButton btnConfig = new JButton(configIcon);
            JButton btnDelete = new JButton(deleteIcon);

            btnDelete.setContentAreaFilled(false);
            btnConfig.setContentAreaFilled(false);
            btnConfig.setMargin(new Insets(2, 2, 2, 2));
            btnDelete.setMargin(new Insets(4, 4, 4, 4));
            btnConfig.setFocusPainted(false);
            btnDelete.setFocusPainted(false);

            pnlButtons.add(btnConfig, "sg 1");
            pnlButtons.add(btnDelete, "sg 1");

            // Listeners

            btnDelete.addActionListener((_ -> {
                String message = "¿Are you sure that you want to delete \"" +
                        multimedia.getTitle() + "\" from your list?";
                int response = JOptionPane.showConfirmDialog(
                        MainFrame.getInstance(),
                        message,
                        "Confirmation",
                        JOptionPane.OK_CANCEL_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    System.out.println("Dijo ok");
                } else if (response == JOptionPane.CANCEL_OPTION) {
                    System.out.println("Dijo cancel");
                } else if (response == JOptionPane.CLOSED_OPTION) {
                    System.out.println("Cerró");
                }
            }));

            // Adds

            panel.add(lblTitle);
            panel.add(lblType);
            panel.add(lblCurrentEpisode);
            panel.add(lblStatus);
            panel.add(pnlButtons);

            pnlContent.add(panel);
        }
    }

    private ImageIcon getCustomImageIcon(String iconName){
        try {
            InputStream input = getClass().getClassLoader()
                    .getResourceAsStream(iconName);
            if (input == null) {
                System.out.println("Could not load the icon with name: " + iconName);
                return null;
            }
            BufferedImage image = ImageIO.read(input);

            return new StretchIcon(image, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
