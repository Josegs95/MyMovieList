package ui.view.component.panel;

import lib.StretchIcon;
import model.dto.MultimediaListItemDTO;
import model.dto.MultimediaSummaryDTO;
import model.dto.SeriesSummaryDTO;
import model.dto.UserListDTO;
import model.entity.MultimediaType;
import net.miginfocom.swing.MigLayout;
import ui.controller.ListItemUIController;
import ui.view.MainFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CollapsableListPanel extends JPanel {

    private final MainFrame mainFrame;
    private final UserListDTO userList;

    private JButton btnRename;
    private JButton btnDelete;
    private JPanel panelItems;
    private JPanel pnlUserList;
    private JLabel lblListName;

    private boolean expanded = false;
    private final Map<MultimediaListItemDTO, ListItemPanel> multimediaDict = new HashMap<>();

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

    public void updateListNameLabel() {
        int nItems = userList.getListItems().size();
        String listName = String.format("%s (%d %s)",
                userList.getName(),
                nItems,
                nItems == 1 ? "item" : "items");
        lblListName.setText(listName);
    }

    private void createItemPanels() {
        userList.getListItems().forEach(this::addMultimediaListItem);
        updateListNameLabel();

        revalidate();
        repaint();
    }

    public void addMultimediaListItem(MultimediaListItemDTO item) {
        ListItemPanel panel = new ListItemPanel(item);
        new ListItemUIController(mainFrame, panel);
        panelItems.add(panel);

        multimediaDict.put(item, panel);
    }

    public void modifyListItem(MultimediaListItemDTO item) {
        ListItemPanel oldPanel = multimediaDict.get(item);
        if (oldPanel == null) return;

        Container parent = oldPanel.getParent();
        if (parent == null) return;

        int position = parent.getComponentZOrder(oldPanel);
        ListItemPanel panel = new ListItemPanel(item);
        new ListItemUIController(mainFrame, panel);

        parent.remove(oldPanel);
        parent.add(panel, position);

        multimediaDict.put(item, panel);

        parent.revalidate();
        parent.repaint();
    }

    public void removeMultimediaItem(MultimediaListItemDTO item) {
        ListItemPanel panel = multimediaDict.remove(item);
        panelItems.remove(panel);

        updateListNameLabel();

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
                if (!expanded && !userList.getListItems().isEmpty()){
                    CollapsableListPanel.this.add(panelItems);
                } else {
                    CollapsableListPanel.this.remove(panelItems);
                }

                expanded = !expanded;
                CollapsableListPanel.this.revalidate();
                CollapsableListPanel.this.repaint();
            }
        });
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

    public String showRenameListDialog() {
        String message = String.format("Elige un nuevo nombre para la lista \"%s\":", userList.getName());
        JTextField textField = new JTextField(userList.getName());
        textField.requestFocus();
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                SwingUtilities.invokeLater(textField::selectAll);
            }
        });

        int response = JOptionPane.showConfirmDialog(
                mainFrame,
                new Object[] {message, textField},
                "Renombrar lista",
                JOptionPane.OK_CANCEL_OPTION);
        return response == JOptionPane.YES_OPTION ? textField.getText() : null;
    }

    public boolean showDeleteListDialog() {
        String message = String.format("¿Estás seguro/a que quieres borrar la lista \"%s\"?", userList.getName());
        int dialogResponse = JOptionPane.showConfirmDialog(mainFrame, message, "Confirmation", JOptionPane.OK_CANCEL_OPTION);

        return dialogResponse == JOptionPane.YES_OPTION;
    }

    public UserListDTO getUserList() {
        return userList;
    }

    public JButton getBtnRename() {
        return btnRename;
    }

    public JButton getBtnDelete() {
        return btnDelete;
    }

    public class ListItemPanel extends JPanel{

        private static final Color MOVIE_COLOR = new Color(250, 219, 111);
        private static final Color SERIE_COLOR = new Color(132, 182, 244);

        private final MultimediaListItemDTO multimediaItem;

        private JButton btnConfig;
        private JButton btnDelete;

        public ListItemPanel(MultimediaListItemDTO multimediaItem) {
            this.multimediaItem = multimediaItem;

            createUI();
            createListeners();
        }

        private void createUI() {
            MultimediaSummaryDTO multimediaDTO = multimediaItem.getMultimedia();
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
            JLabel lblStatus = new JLabel(multimediaItem.getStatus().toString(), SwingConstants.CENTER);

            // Episode
            String episodeString = "";
            if (multimediaDTO instanceof SeriesSummaryDTO serie){
                episodeString = multimediaItem.getCurrentEpisode() + "/" + serie.getTotalEpisodes();
            }
            JLabel lblCurrentEpisode = new JLabel(episodeString, SwingConstants.CENTER);

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

        public boolean showDeleteDialog() {
            String message = String.format("¿Estás seguro/a de eliminar \"%s\" de la lista \"%s\"?",
                    multimediaItem.getMultimedia().getTitle(),
                    userList.getName());
            int response = JOptionPane.showConfirmDialog(mainFrame, message, "Eliminar elemento", JOptionPane.YES_NO_OPTION);

            return response == JOptionPane.YES_OPTION;
        }

        public MultimediaListItemDTO getMultimediaItem() {
            return multimediaItem;
        }

        public UserListDTO getUserList() {
            return userList;
        }

        public JButton getBtnConfig() {
            return btnConfig;
        }

        public JButton getBtnDelete() {
            return btnDelete;
        }
    }
}
