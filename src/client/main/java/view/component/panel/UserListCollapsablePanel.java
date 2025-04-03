package view.component.panel;

import lib.ScrollablePanel;
import model.*;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;

public class UserListCollapsablePanel extends JPanel {

    private boolean isCollapsed = true;
    private final UserList multimediaList;

    public UserListCollapsablePanel(UserList userList){
        this.multimediaList = userList;

        init();

        // BORRAR
        multimediaList.getMultimediaList().stream()
                .map(MultimediaAtList::getMultimediaType)
                .forEach(System.out::println);
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

        ScrollablePanel pnlContent = new ScrollablePanel(new MigLayout(
                "ins 0, fill, gap 0 0, flowy",
                "[fill]",
                "[100!, fill]"
        ));
        pnlContent.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        fillPanelWithMultimedia(pnlContent);

        JScrollPane scrollPane = new JScrollPane(pnlContent);

        //Listener
        btnExpand.addActionListener(_ ->{
            if (isCollapsed && !multimediaList.getMultimediaList().isEmpty()){
                UserListCollapsablePanel.this.add(scrollPane);
            } else {
                UserListCollapsablePanel.this.remove(scrollPane);
            }

            isCollapsed = !isCollapsed;
            UserListCollapsablePanel.this.revalidate();
            UserListCollapsablePanel.this.repaint();
        });

        add(btnExpand, "hmin 100");
    }

    private void fillPanelWithMultimedia(JPanel pnlContent){
        for (MultimediaAtList multimediaAtList : multimediaList.getMultimediaList()){
            MultimediaType multimediaType = multimediaAtList.getMultimediaType();
            Multimedia multimedia = multimediaAtList.getMultimedia();
            JPanel panel = new JPanel(new MigLayout(
                    "ins 0, fill, debug",
                    "[20%, fill][20%, fill][20%, fill][20%, fill][20%, fill]",
                    "[fill]"
            ));
            panel.setBackground(multimediaType == MultimediaType.MOVIE ? Color.CYAN : Color.PINK);

            JLabel lblTitle = new JLabel(multimedia.getTitle());
            lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
            JLabel lblType = new JLabel(multimediaType.toString());
            lblType.setHorizontalAlignment(SwingConstants.CENTER);
            String episodeString = "";
            if (multimediaType == MultimediaType.TV_SHOW){
                episodeString = String.join(
                        "/",
                        String.valueOf(multimediaAtList.getCurrentEpisode()),
                        String.valueOf(((TvShow) multimedia).getTotalEpisodes()));
            }
            JLabel lblCurrentEpisode = new JLabel(episodeString);
            lblCurrentEpisode.setHorizontalAlignment(SwingConstants.CENTER);
            JLabel lblStatus = new JLabel(multimediaAtList.getStatus().toString());
            lblStatus.setHorizontalAlignment(SwingConstants.CENTER);

            panel.add(lblTitle);
            panel.add(lblType);
            panel.add(lblCurrentEpisode);
            panel.add(lblStatus);

            pnlContent.add(panel);
        }
    }
}
