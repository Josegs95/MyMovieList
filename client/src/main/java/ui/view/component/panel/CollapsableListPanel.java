package ui.view.component.panel;

import dto.MultimediaListItemDTO;
import dto.MultimediaSummaryDTO;
import dto.SeriesSummaryDTO;
import dto.UserListDTO;
import lib.StretchIcon;
import model.entity.MultimediaType;
import net.miginfocom.swing.MigLayout;
import ui.view.MainFrame;

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

public class CollapsableListPanel extends JPanel {

    private final UserListDTO userList;
    private final MainFrame mainFrame;

    private JButton btnRename;
    private JButton btnDelete;
    private JPanel panelItems;
    private JPanel pnlUserList;
    private JLabel lblListName;

    private boolean expanded = false;
    private final Map<MultimediaListItemDTO, MultimediaItemPanel> multimediaDict = new HashMap<>();

    public CollapsableListPanel(MainFrame mainFrame, UserListDTO userList){
        this.mainFrame = mainFrame;
        this.userList = userList;

        createUI();
        createListeners();
        createItemPanels();
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


        lblListName = new JLabel();
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

        panelItems = new JPanel(new MigLayout(
                "ins 0, fill, gap 0 0, flowy",
                "[fill]",
                "[fill, 100!]"
        ));

        add(pnlUserList);
    }

    public void updateListNameItems() {
        int nItems = userList.listItems().size();
        String listName = String.format("%s (%d %s)",
                userList.name(),
                nItems,
                nItems == 1 ? "item" : "items");
        lblListName.setText(listName);
    }

    private void createItemPanels() {
        userList.listItems().forEach(this::addMultimediaListItem);
        updateListNameItems();

        revalidate();
        repaint();
    }

    public void addMultimediaListItem(MultimediaListItemDTO item) {
        MultimediaItemPanel panel = new MultimediaItemPanel(item);
        panelItems.add(panel);

        multimediaDict.put(item, panel);
    }

    public void removeMultimediaItem(MultimediaListItemDTO item) {
        MultimediaItemPanel panel = multimediaDict.remove(item);
        panelItems.remove(panel);

        updateListNameItems();

        // If there are not more multimedia items in the list, the list wrap itself.
        if (panelItems.getComponentCount() == 0) {
            remove(panelItems);
            expanded = false;
        }
    }

    private void createListeners() {
        pnlUserList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!expanded && !userList.listItems().isEmpty()){
                    CollapsableListPanel.this.add(panelItems);
                } else {
                    CollapsableListPanel.this.remove(panelItems);
                }

                expanded = !expanded;
                CollapsableListPanel.this.revalidate();
                CollapsableListPanel.this.repaint();
            }
        });
//        btnRename.addActionListener(_ ->{
//            String newListName = JOptionPane.showInputDialog(
//                    mainFrame,
//                    "¿How would you like to name the list?",
//                    "Rename list",
//                    JOptionPane.QUESTION_MESSAGE);
//
//            if (newListName == null) {
//                return;
//            }
//
//            Message serverMessage = UserListController.renameUserList(user, userList.name(), newListName);
//
//            if (serverMessage.status() != 200) {
//                processErrorMessage(serverMessage, String.format("Couldn't rename the list to \"%s\"", newListName));
//            } else {
////                userList.setName(newListName);
//                updateListName();
//            }
//        });
//        btnDelete.addActionListener(_ ->{
//            String listName = userList.name();
//            int dialogResponse = JOptionPane.showConfirmDialog(
//                    mainFrame,
//                    String.format("¿Are you sure that you want to delete the list \"%s\"?", listName),
//                    "Confirmation",
//                    JOptionPane.OK_CANCEL_OPTION
//            );
//
//            if (dialogResponse != JOptionPane.YES_OPTION) {
//                return;
//            }
//
//            Message response = UserListController.deleteUserList(user, listName);
//            if (response.status() != 200) {
//                processErrorMessage(response, String.format("Couldn't delete the list \"%s\"", listName));
//            } else {
//                Event event = new Event(EventType.DELETE_USER_LIST, Map.of("userList", userList));
//                ViewController.getInstance().notifyView("userListPanel", event);
//            }
//        });
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

    public UserListDTO getUserList() {
        return userList;
    }

    public JPanel getPanelItems() {
        return panelItems;
    }

    public JButton getBtnDelete() {
        return btnDelete;
    }

    public JButton getBtnRename() {
        return btnRename;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    private class MultimediaItemPanel extends JPanel{

        private static final Color MOVIE_COLOR = new Color(250, 219, 111);
        private static final Color SERIE_COLOR = new Color(132, 182, 244);

        private final MultimediaListItemDTO listItemDTO;

        private JButton btnConfig;
        private JButton btnDelete;
        private JLabel lblStatus;
        private JLabel lblCurrentEpisode;

        public MultimediaItemPanel(MultimediaListItemDTO listItemDTO) {
            this.listItemDTO = listItemDTO;

            createUI();
            createListeners();
        }

        private void createUI() {
            MultimediaSummaryDTO multimediaDTO = listItemDTO.getMultimedia();
            MultimediaType multimediaType = multimediaDTO.getType();

            setLayout(new MigLayout(
                    "ins 0 25 0 0, fill",
                    "[20%, fill][20%, fill][20%, fill][20%, fill][20%, fill]",
                    "[fill]"
            ));

            setBackground(multimediaType == MultimediaType.MOVIE ? MOVIE_COLOR : SERIE_COLOR);
            setBorder(LineBorder.createGrayLineBorder());

            // Title
            JLabel lblTitle = new JLabel("<html><p style=\"text-align: center;\">"
                    + multimediaDTO.getTitle() + "</p></html>", SwingConstants.CENTER);

            // Type
            JLabel lblType = new JLabel(multimediaType.toString(), SwingConstants.CENTER);

            // Status
            lblStatus = new JLabel(listItemDTO.getStatus().toString(), SwingConstants.CENTER);

            // Episode
            String episodeString = "";
            if (multimediaDTO instanceof SeriesSummaryDTO serie){
                episodeString = listItemDTO.getCurrentEpisode() + "/" + serie.getTotalEpisodes();
            }
            lblCurrentEpisode = new JLabel(episodeString, SwingConstants.CENTER);

            // Buttons
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
//            btnDelete.addActionListener((_ -> {
//                String message = String.format("¿Are you sure that you want to delete \"%s\" from your list?",
//                        multimedia.getTitle());
//                int dialogResponse = JOptionPane.showConfirmDialog(mainFrame, message,"Confirmation",
//                        JOptionPane.OK_CANCEL_OPTION);
//                if (dialogResponse != JOptionPane.YES_OPTION) {
//                    return;
//                }
//
//                Message serverMessage = UserListController.deleteMultimediaFromList(user, userList, multimedia);
//
//                if (serverMessage.status() != 200) {
//                    String defaultErrorMessage = "Couldn't remove \"" + multimedia.getTitle() + "\" from the list.";
//                    processErrorMessage(serverMessage, defaultErrorMessage);
//                } else {
//                    message = String.format("\"%s\" has been modified successfully.", multimedia.getTitle());
//                    JOptionPane.showMessageDialog(mainFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
//
//                    Event event = new Event(EventType.REMOVE_MULTIMEDIA,
//                            Map.of("userList", userList, "multimediaListItem", multimediaListItem));
//                    ViewController.getInstance().notifyView("userListPanel", event);
//                }
//
//            }));
//            btnConfig.addActionListener(_ -> {
//                ConfigureMultimediaDialog dialog = new ConfigureMultimediaDialog(mainFrame, multimediaListItem);
//                dialog.setVisible(true);
//
//                if (dialog.isCancelled()) {
//                    return;
//                }
//
//                MultimediaStatus selectedStatus = dialog.getSelectedMultimediaStatus();
//                int selectedCurrentEpisode = dialog.getSelectedCurrentEpisode();
//                MultimediaListItem multimediaListItem = new MultimediaListItem(multimedia, userList,
//                        selectedStatus, selectedCurrentEpisode);
//
//                // Send the information to the server
//
//                Message response =
//                        UserListController.modifyMultimediaAttributes(user, userList, multimediaListItem);
//                if (response.status() != 200) {
//                    processErrorMessage(response, "Couldn't modify the multimedia.");
//                } else {
//                    String message = String.format("\"%s\" has been modified successfully.", multimedia.getTitle());
//                    JOptionPane.showMessageDialog(mainFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
//
//                    MultimediaStatus status = MultimediaStatus.valueOf(response.getContentAsMap().get("status").toString());
//                    int currentEpisode = (int) (response.getContentAsMap().get("currentEpisode"));
//
//                    this.multimediaListItem = new MultimediaListItem(multimedia, userList, status, currentEpisode);
//                    updateMultimediaAttributes();
//                }
//            });
//            addMouseListener(new MouseAdapter() {
//                @Override
//                public void mouseClicked(MouseEvent e) {
//                    DetailMultimediaPanel detailMultimediaPanel = new DetailMultimediaPanel(mainFrame, userListPanel, multimedia);
//                    Event event = new Event(EventType.SHOW_DETAIL_PANEL,
//                            Map.of("detailPanel", detailMultimediaPanel));
//                    ViewController.getInstance().notifyView("userListPanel", event);
//                }
//            });
        }

        public JButton getBtnConfig() {
            return btnConfig;
        }

        public JButton getBtnDelete() {
            return btnDelete;
        }
    }
}
