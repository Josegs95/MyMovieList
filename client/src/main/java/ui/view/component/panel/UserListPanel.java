package ui.view.component.panel;

import lib.ScrollablePanel;
import model.dto.MultimediaDetailDTO;
import model.dto.MultimediaListItemDTO;
import model.dto.MultimediaSummaryDTO;
import model.dto.UserListDTO;
import net.miginfocom.swing.MigLayout;
import ui.controller.CollapsableListUIController;
import ui.controller.DetailUIController;
import ui.view.MainFrame;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.HashMap;
import java.util.Map;

public class UserListPanel extends JPanel {

    private final MainFrame mainFrame;

    private JPanel pnlContainer;
    private DetailPanel detailPanel;
    private ScrollablePanel pnlLists;
    private JButton btnCreateList;

    private final Map<Long, CollapsableListPanel> listPanelMap = new HashMap<>();
    private final Map<CollapsableListPanel, CollapsableListUIController> listControllerMap = new HashMap<>();

    public UserListPanel(MainFrame mainFrame){
        this.mainFrame = mainFrame;

        createUI();
    }

    private void createUI() {
        setLayout(new MigLayout(
                "fill, ins 0",
                "[fill]",
                "[fill]"));
        setBorder(LineBorder.createBlackLineBorder());

        pnlContainer = new JPanel(new MigLayout(
                "flowy, fill, ins 0",
                "[fill]",
                "[50!, fill]10[fill]"));

        add(pnlContainer);

        JPanel pnlButtons = new JPanel(new MigLayout(
                "fill, ins 0",
                "[align center]",
                "[align center]"
        ));
        pnlButtons.setBorder(LineBorder.createBlackLineBorder());

        btnCreateList = new JButton("Create List");
        btnCreateList.setFocusPainted(false);

        pnlButtons.add(btnCreateList);
        pnlContainer.add(pnlButtons);

        pnlLists = new ScrollablePanel(new MigLayout(
                "fillx, flowy, ins 0",
                "[fill]",
                "[100::null, fill]0[100::null, fill]"
        ));
        pnlLists.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        pnlLists.setScrollableHeight(ScrollablePanel.ScrollableSizeHint.STRETCH);
        pnlLists.setBorder(LineBorder.createBlackLineBorder());

        JScrollPane scrollPane = new JScrollPane(pnlLists);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        pnlContainer.add(scrollPane);
    }

    public String showListNameDialog() {
        return JOptionPane.showInputDialog(mainFrame,"Escribe el nombre de la lista:");
    }

    public void createUserList(UserListDTO userList) {
        CollapsableListPanel listPanel = new CollapsableListPanel(mainFrame, userList);
        CollapsableListUIController controller = new CollapsableListUIController(listPanel);

        listPanelMap.put(userList.getId(), listPanel);
        listControllerMap.put(listPanel, controller);
        pnlLists.add(listPanel);

        revalidate();
        repaint();
    }

    public void addMultimediaToList(UserListDTO userListDTO, MultimediaListItemDTO addedItem) {
        CollapsableListPanel listPanel = listPanelMap.get(userListDTO.getId());
        listPanel.addMultimediaListItem(addedItem);
        listPanel.updateListNameLabel();

        revalidate();
        repaint();
    }

    public void removeMultimediaFromList(UserListDTO userListDTO, MultimediaListItemDTO deletedItem) {
        listPanelMap.get(userListDTO.getId()).removeMultimediaItem(deletedItem);

        revalidate();
        repaint();
    }

    public void showDetailPanel(MultimediaDetailDTO detailDTO, MultimediaSummaryDTO summaryDTO, UserListDTO userListDTO) {
        detailPanel = new DetailPanel(mainFrame, detailDTO, summaryDTO);
        new DetailUIController(mainFrame, detailPanel, userListDTO);

        remove(pnlContainer);
        add(detailPanel);

        revalidate();
        repaint();
    }

    public void removeDetailPanel(DetailPanel closingDetailPanel) {
        if (closingDetailPanel.equals(detailPanel)) {
            remove(closingDetailPanel);
            add(pnlContainer);

            revalidate();
            repaint();
        }
    }

    public void deleteUserList(UserListDTO userListDTO) {
        CollapsableListPanel listPanel = listPanelMap.remove(userListDTO.getId());
        CollapsableListUIController controller = listControllerMap.remove(listPanel);

        pnlLists.remove(listPanel);
        controller.dispose();

        revalidate();
        repaint();
    }

    public JButton getBtnCreateList() {
        return btnCreateList;
    }
}
