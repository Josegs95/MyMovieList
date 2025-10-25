package view.component.panel;

import controller.UserListController;
import controller.ViewController;
import event.Event;
import event.EventType;
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
import java.util.HashMap;
import java.util.Map;

public class CollapsableUserListPanel extends JPanel {

    private final UserList userList;
    private final UserListPanel userListPanel;
    private final MainFrame mainFrame;
    private final User user;

    private JPanel pnlMultimediaItems;
    private JLabel lblListName;
    private JPanel pnlUserList;
    private JButton btnRename;
    private JButton btnDelete;

    private boolean expanded = false;
    private final Map<MultimediaListItem, MultimediaItemPanel> multimediaDict;

    public CollapsableUserListPanel(UserListPanel userListPanel, UserList userList){
        this.userListPanel = userListPanel;
        this.userList = userList;
        this.mainFrame = MainFrame.getInstance();
        this.user = mainFrame.getUser();

        multimediaDict = new HashMap<>();

        createUI();
        createListeners();
        createItemPanels();
    }

    public void addMultimediaListItem(MultimediaListItem item) {
        MultimediaItemPanel pnlItem = new MultimediaItemPanel(item);
        multimediaDict.put(item, pnlItem);
        pnlMultimediaItems.add(pnlItem);
    }

    public void removeMultimediaItem(MultimediaListItem mli) {
        MultimediaItemPanel panel = multimediaDict.get(mli);
        pnlMultimediaItems.remove(panel);

        // If there are not more multimedia items in the list, the list wrap itself.
        if (pnlMultimediaItems.getComponentCount() == 0) {
            remove(pnlMultimediaItems);
            expanded = false;
        }

        updateListName();
    }

    public void updateListName() {
        lblListName.setText(userList.getFullListName());

        revalidate();
        repaint();
    }

    private void createUI() {
        setLayout(new MigLayout(
                "fill, flowy, gap 0 0, ins 0",
                "[fill]",
                "[fill, 100!][fill]"
        ));

        //Components

        pnlUserList = new JPanel(new MigLayout(
                "fill, ins 0 25 0 0",
                "[fill, 70%]0[fill, grow]",
                "[fill]"));
        pnlUserList.setBorder(LineBorder.createBlackLineBorder());

        lblListName = new JLabel(userList.getFullListName());
        Font labelFont = lblListName.getFont();
        lblListName.setFont(labelFont.deriveFont(14f));

        JPanel pnlButtons = new JPanel(new MigLayout(
                "fill, ins 0, aligny center",
                "push[fill]10[fill]push",
                "[fill]"));

        btnRename = getJButtonWithIcon("images/rename.png");
        btnDelete = getJButtonWithIcon("images/delete.png");

        pnlButtons.add(btnRename, "w 35!, h 35!");
        pnlButtons.add(btnDelete, "w 35!, h 35!");

        pnlUserList.add(lblListName);
        pnlUserList.add(pnlButtons);

        pnlMultimediaItems = new JPanel(new MigLayout(
                "ins 0, fill, gap 0 0, flowy",
                "[fill]",
                "[fill, 100!]"
        ));

        add(pnlUserList);
    }

    private void createListeners() {
        pnlUserList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!expanded && !userList.getMultimediaList().isEmpty()){
                    CollapsableUserListPanel.this.add(pnlMultimediaItems);
                } else {
                    CollapsableUserListPanel.this.remove(pnlMultimediaItems);
                }

                expanded = !expanded;
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

            Message serverMessage = UserListController.renameUserList(user, userList.getListName(), newListName);

            if (serverMessage.status() != 200) {
                processErrorMessage(serverMessage, String.format("Couldn't rename the list to \"%s\"", newListName));
            } else {
                userList.setListName(newListName);
                updateListName();
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

            Message response = UserListController.deleteUserList(user, listName);
            if (response.status() != 200) {
                processErrorMessage(response, String.format("Couldn't delete the list \"%s\"", listName));
            } else {
                Event event = new Event(EventType.DELETE_USER_LIST, Map.of("userList", userList));
                ViewController.getInstance().notifyView("userListPanel", event);
            }
        });
    }

    private void createItemPanels() {
        for (MultimediaListItem multimediaListItem : userList.getMultimediaList()){
            addMultimediaListItem(multimediaListItem);
        }

        revalidate();
        repaint();
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

    private void processErrorMessage(Message message, String defaultMessage) {
        if (message.status() == 200) {
            return;
        }

//        if (!message.getErrorMessage().isEmpty()) {
//            defaultMessage = message.getErrorMessage();
//        }

        JOptionPane.showMessageDialog(mainFrame, defaultMessage, "Error", JOptionPane.ERROR_MESSAGE);
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
                String message = String.format("¿Are you sure that you want to delete \"%s\" from your list?",
                        multimedia.getTitle());
                int dialogResponse = JOptionPane.showConfirmDialog(mainFrame, message,"Confirmation",
                        JOptionPane.OK_CANCEL_OPTION);
                if (dialogResponse != JOptionPane.YES_OPTION) {
                    return;
                }

                Message serverMessage = UserListController.deleteMultimediaFromList(user, userList, multimedia);

                if (serverMessage.status() != 200) {
                    String defaultErrorMessage = "Couldn't remove \"" + multimedia.getTitle() + "\" from the list.";
                    processErrorMessage(serverMessage, defaultErrorMessage);
                } else {
                    message = String.format("\"%s\" has been modified successfully.", multimedia.getTitle());
                    JOptionPane.showMessageDialog(mainFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);

                    Event event = new Event(EventType.REMOVE_MULTIMEDIA,
                            Map.of("userList", userList, "multimediaListItem", multimediaListItem));
                    ViewController.getInstance().notifyView("userListPanel", event);
                }

            }));
            btnConfig.addActionListener(_ -> {
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

                Message response =
                        UserListController.modifyMultimediaAttributes(user, userList, multimediaListItem);
                if (response.status() != 200) {
                    processErrorMessage(response, "Couldn't modify the multimedia.");
                } else {
                    String message = String.format("\"%s\" has been modified successfully.", multimedia.getTitle());
                    JOptionPane.showMessageDialog(mainFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);

                    MultimediaStatus status = MultimediaStatus.valueOf(response.content().get("status").toString());
                    int currentEpisode = (int) (response.content().get("currentEpisode"));

                    this.multimediaListItem = new MultimediaListItem(multimedia, status, currentEpisode);
                    updateMultimediaAttributes();
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    DetailMultimediaPanel detailMultimediaPanel = new DetailMultimediaPanel(userListPanel, multimedia);
                    Event event = new Event(EventType.SHOW_DETAIL_PANEL,
                            Map.of("detailPanel", detailMultimediaPanel));
                    ViewController.getInstance().notifyView("userListPanel", event);
                }
            });
        }

        private void updateMultimediaAttributes() {
            lblStatus.setText(multimediaListItem.getStatus().toString());
            if (multimedia instanceof TvShow tvShow){
                String episodeString = multimediaListItem.getCurrentEpisode() + "/" + tvShow.getTotalEpisodes();
                lblCurrentEpisode.setText(episodeString);
            }

            revalidate();
            repaint();

        }
    }
}
