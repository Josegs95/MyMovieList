package view.component.panel;

import controller.UserListController;
import lib.ScrollablePanel;
import lib.StretchIcon;
import model.*;
import net.miginfocom.swing.MigLayout;
import view.MainFrame;
import view.component.dialog.ConfigureMultimediaDialog;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class CollapsableUserListPanel extends JPanel {

    private final UserList userList;
    private final UserListsPanel userListsPanel;

    private ScrollablePanel pnlContent;
    private JLabel lblListName;

    private boolean collapsed = true;

    public CollapsableUserListPanel(UserListsPanel userListsPanel, UserList userList){
        this.userListsPanel = userListsPanel;
        this.userList = userList;

        init();
    }

    private void init() {
        MainFrame mainFrame = MainFrame.getInstance();
        User user = mainFrame.getUser();

        setLayout(new MigLayout(
                "fill, flowy, gap 0 0, ins 0",
                "[fill]",
                "[fill, 100!][fill]"
        ));

        //Components

        JPanel pnlListExpand = new JPanel(new MigLayout(
                "fill, ins 0 25 0 0",
                "[fill, 70%]0[fill, grow]",
                "[fill]"));
        pnlListExpand.setBorder(LineBorder.createBlackLineBorder());

        lblListName = new JLabel(userList.getFullListName());
        Font labelFont = lblListName.getFont();
        lblListName.setFont(labelFont.deriveFont(14f));

        JPanel pnlButtons = new JPanel(new MigLayout(
                "fill, ins 0, aligny center",
                "push[fill]10[fill]push",
                "[fill]"));

        JButton btnRename = getJButtonWithIcon("images/rename.png");
        JButton btnDelete = getJButtonWithIcon("images/delete.png");

        pnlButtons.add(btnRename, "w 35!, h 35!");
        pnlButtons.add(btnDelete, "w 35!, h 35!");

        pnlListExpand.add(lblListName);
        pnlListExpand.add(pnlButtons);


        pnlContent = new ScrollablePanel(new MigLayout(
                "ins 0, fill, gap 0 0, flowy",
                "[fill]",
                "[fill, 100!]"
        ));
        updateUserListUI();

        //Listener

        pnlListExpand.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (collapsed && !userList.getMultimediaList().isEmpty()){
                    CollapsableUserListPanel.this.add(pnlContent);
                } else {
                    CollapsableUserListPanel.this.remove(pnlContent);
                }

                collapsed = !collapsed;
                CollapsableUserListPanel.this.revalidate();
                CollapsableUserListPanel.this.repaint();
            }
        });
        btnRename.addActionListener(_ ->{
            String newListName = JOptionPane.showInputDialog(
                    mainFrame,
                    "¿How would you like to name the list?",
                    "Rename list",
                    JOptionPane.QUESTION_MESSAGE);

            if (newListName == null) {
                return;
            }

            ServerResponse serverResponse =
                    UserListController.renameUserList(user, userList.getListName(), newListName);

            if (serverResponse.getStatus() != 200) {
                processErrorMessage(serverResponse, String.format("Couldn't rename the list to \"%s\"", newListName));
            } else {
                userList.setListName(newListName);
                updateUserListUI();
            }
        });
        btnDelete.addActionListener(_ ->{
            String listName = userList.getListName();
            int dialogResponse = JOptionPane.showConfirmDialog(
                    mainFrame,
                    String.format("¿Are you sure that you want to delete the list \"%s\"?", listName),
                    "Confirmation",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (dialogResponse != JOptionPane.YES_OPTION) {
                return;
            }

            ServerResponse response = UserListController.deleteUserList(user, listName);
            if (response.getStatus() != 200) {
                processErrorMessage(response, String.format("Couldn't delete the list \"%s\"", listName));
            } else {
                user.getLists().remove(userList);
                mainFrame.updateCentralPanelUI();
            }
        });

        // Adds

        add(pnlListExpand);
    }

    private void updateUserListUI(){
        lblListName.setText(userList.getFullListName());
        pnlContent.removeAll();

        for (MultimediaListItem multimediaListItem : userList.getMultimediaList()){
            pnlContent.add(new MultimediaItemPanel(multimediaListItem));
        }

        revalidate();
        repaint();
    }

    private void removeMultimediaItemFromList (MultimediaListItem multimediaListItem) {
        userList.getMultimediaList().remove(multimediaListItem);

        if (userList.getMultimediaList().isEmpty()) {
            remove(pnlContent);
            collapsed = true;
        }

        updateUserListUI();
    }

    private JButton getJButtonWithIcon(String iconPath) {
        ImageIcon icon = getCustomImageIcon(iconPath);
        JButton button = new JButton(icon);

        button.setContentAreaFilled(false);
        button.setMargin(new Insets(2, 2, 2, 2));
        button.setFocusPainted(false);

        return button;
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

    private void processErrorMessage(ServerResponse serverResponse, String defaultMessage) {
        if (serverResponse.getStatus() == 200) {
            return;
        }

        if (!serverResponse.getErrorMessage().isEmpty()) {
            defaultMessage = serverResponse.getErrorMessage();
        }

        JOptionPane.showMessageDialog(MainFrame.getInstance(), defaultMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private class MultimediaItemPanel extends JPanel{

        private MultimediaListItem multimediaListItem;
        private final Multimedia multimedia;
        private final MultimediaType multimediaType;

        private JButton btnConfig;
        private JButton btnDelete;
        private JLabel lblStatus;
        private JLabel lblCurrentEpisode;

        public MultimediaItemPanel(MultimediaListItem multimediaListItem) {
            this.multimediaListItem = multimediaListItem;
            this.multimedia = multimediaListItem.getMultimedia();
            this.multimediaType = multimedia.getMultimediaType();

            createUI();
            createListeners();
        }

        private void createUI() {
            setLayout(new MigLayout(
                    "ins 0 25 0 0, fill",
                    "[20%, fill][20%, fill][20%, fill][20%, fill][20%, fill]",
                    "[fill]"
            ));

            setBackground(multimediaType == MultimediaType.MOVIE ? new Color(250, 219, 111) : new Color(132, 182, 244));
            setBorder(LineBorder.createGrayLineBorder());

            JLabel lblTitle = new JLabel("<html><p style=\"text-align: center;\">"
                    + multimedia.getTitle() + "</p></html>", SwingConstants.CENTER);
            JLabel lblType = new JLabel(multimediaType.toString(), SwingConstants.CENTER);
            lblStatus = new JLabel(multimediaListItem.getStatus().toString(), SwingConstants.CENTER);
            String episodeString = "";
            if (multimediaType == MultimediaType.TV_SHOW){
                episodeString = String.join(
                        "/",
                        String.valueOf(multimediaListItem.getCurrentEpisode()),
                        String.valueOf(((TvShow) multimedia).getTotalEpisodes()));
            }
            lblCurrentEpisode = new JLabel(episodeString, SwingConstants.CENTER);

            JPanel pnlButtons = new JPanel(new MigLayout(
                    "fill, flowy, gapy 6!, align 50% 50%",
                    "[35!, fill]",
                    "[35!, fill]"));
            pnlButtons.setOpaque(false);

            btnConfig = getJButtonWithIcon("images/config.png");
            btnDelete = getJButtonWithIcon("images/delete.png");

            pnlButtons.add(btnConfig, "sg 1");
            pnlButtons.add(btnDelete, "sg 1");

            // Adds

            add(lblTitle);
            add(lblType);
            add(lblCurrentEpisode);
            add(lblStatus);
            add(pnlButtons);
        }

        private void createListeners() {
            btnDelete.addActionListener((_ -> {
                String message = "¿Are you sure that you want to delete \"" + multimedia.getTitle() +
                        "\" from your list?";
                int dialogResponse = JOptionPane.showConfirmDialog(
                        MainFrame.getInstance(),
                        message,
                        "Confirmation",
                        JOptionPane.OK_CANCEL_OPTION);
                if (dialogResponse != JOptionPane.YES_OPTION) {
                    return;
                }

                ServerResponse serverResponse = UserListController.deleteMultimediaFromList(
                        MainFrame.getInstance().getUser(), userList, multimedia);

                if (serverResponse.getStatus() == 200) {
                    JOptionPane.showMessageDialog(
                            MainFrame.getInstance(),
                            "\"" + multimedia.getTitle() + "\" has successfully removed from the list.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    removeMultimediaItemFromList(multimediaListItem);
                } else {
                    String defaultErrorMessage = "Couldn't remove \"" + multimedia.getTitle() + "\" from the list.";
                    processErrorMessage(serverResponse, defaultErrorMessage);
                }

            }));
            btnConfig.addActionListener(_ -> {
                MainFrame mainFrame = MainFrame.getInstance();
                ConfigureMultimediaDialog dialog = new ConfigureMultimediaDialog(mainFrame, multimediaListItem, userList);
                dialog.setVisible(true);

                if (dialog.isCancelled()) {
                    return;
                }

                MultimediaStatus selectedStatus = dialog.getSelectedMultimediaStatus();
                int selectedCurrentEpisode = dialog.getSelectedCurrentEpisode();
                MultimediaListItem multimediaListItem = new MultimediaListItem(multimedia,
                        selectedStatus, selectedCurrentEpisode);

                // Send the information to the server

                ServerResponse response = UserListController.modifyMultimediaAttributes(mainFrame.getUser(),
                        userList, multimediaListItem);
                if (response.getStatus() != 200) {
                    processErrorMessage(response, "Couldn't modify the multimedia.");
                } else {
                    String message = String.format("\"%s\" has been modified successfully.",
                            multimedia.getTitle());
                    JOptionPane.showMessageDialog(mainFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);

                    MultimediaStatus status = MultimediaStatus.valueOf(response.getData().get("status").toString());
                    int currentEpisode = (int) (response.getData().get("currentEpisode"));

                    this.multimediaListItem = new MultimediaListItem(multimedia, status, currentEpisode);
                    updateMultimediaAttributes();
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    userListsPanel.showDetailPanel(new DetailMultimediaPanel(multimedia));
                }
            });
        }

        private void updateMultimediaAttributes() {
            lblStatus.setText(multimediaListItem.getStatus().toString());
            if (multimediaListItem.getMultimedia().getMultimediaType() == MultimediaType.TV_SHOW){
                String episodeString = String.join(
                        "/",
                        String.valueOf(multimediaListItem.getCurrentEpisode()),
                        String.valueOf(((TvShow) multimediaListItem.getMultimedia()).getTotalEpisodes()));

                lblCurrentEpisode.setText(episodeString);
            }

            revalidate();
            repaint();

        }
    }
}
