package ui.view.component.panel;

import dto.MultimediaListItemDTO;
import dto.UserListDTO;
import event.Event;
import event.EventListener;
import lib.ScrollablePanel;
import net.miginfocom.swing.MigLayout;
import ui.view.MainFrame;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.HashMap;
import java.util.Map;

public class UserListPanel extends JPanel implements EventListener {

    private final MainFrame mainFrame;

    private JPanel pnlContainer;
    private ScrollablePanel pnlLists;
    private JButton btnCreateList;

    private final Map<Long, CollapsableListPanel> listPanelMap = new HashMap<>();

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

    // * BORRAR *
    public void updateState() {
        if (pnlContainer.getComponent(0) instanceof DetailPanel detailPanel) {
            detailPanel.updatePage();
        }
    }
    //

    public String showListNameDialog() {
        return JOptionPane.showInputDialog(mainFrame,"Write the list's name that you desire.");
    }

    public void createUserList(UserListDTO userList) {
        CollapsableListPanel panel = new CollapsableListPanel(mainFrame, userList);
        listPanelMap.put(userList.id(), panel);
        pnlLists.add(panel);

        revalidate();
        repaint();
    }

    public void addMultimediaToList(UserListDTO userListDTO, MultimediaListItemDTO addedItem) {
        CollapsableListPanel listPanel = listPanelMap.get(userListDTO.id());
        listPanel.addMultimediaListItem(addedItem);
        listPanel.updateListNameItems();

        revalidate();
        repaint();
    }

    public void removeMultimediaFromList(UserListDTO userListDTO, MultimediaListItemDTO deletedItem) {
        listPanelMap.get(userListDTO.id()).removeMultimediaItem(deletedItem);

        revalidate();
        repaint();
    }

//    private void showDetailPanel(DetailPanel panel) {
//        remove(0);
//        add(panel);
//
//        revalidate();
//        repaint();
//    }
//
//    private void removeDetailPanel() {
//        remove(0);
//        add(pnlContainer);
//
//        revalidate();
//        repaint();
//    }
//
//    private void deleteUserList(UserList userList) {
//        // Delete userList from model
////        user.getMultimediaLists().remove(userList);
//
//        // Delete userList from UI
//        pnlLists.remove(listPanelMap.get(userList));
//
//        revalidate();
//        repaint();
//    }

    public JButton getBtnCreateList() {
        return btnCreateList;
    }

    public Map<Long, CollapsableListPanel> getListPanelMap() {
        return listPanelMap;
    }

    // * BORRAR *
    @Override @SuppressWarnings("unchecked")
    public void onEvent(Event event) {
//        Map<String, Object> data = event.data();
//
//        switch (event.type()) {
//            case ADD_MULTIMEDIA -> {
//                UserList userList = (UserList) data.get("userList");
//                MultimediaListItem multimediaListItem = (MultimediaListItem) data.get("multimediaListItem");
//
//                addMultimediaToList(userList, multimediaListItem);
//            }
//            case REMOVE_MULTIMEDIA -> {
//                UserList userList = (UserList) data.get("userList");
//                MultimediaListItem multimediaListItem = (MultimediaListItem) data.get("multimediaListItem");
//
//                removeMultimediaFromList(userList, multimediaListItem);
//            }
//            case SHOW_DETAIL_PANEL -> {
//                DetailPanel panel = (DetailPanel) data.get("detailPanel");
//
//                showDetailPanel(panel);
//            }
//            case HIDE_DETAIL_PANEL -> removeDetailPanel();
//            case CREATE_USER_LIST_ITEMS -> {
//                List<UserListDTO> userLists = (List<UserListDTO>) data.get("userLists");
//
//                createUserListItems(userLists);
//            }
//            case DELETE_USER_LIST -> {
//                UserList userList = (UserList) data.get("userList");
//
//                deleteUserList(userList);
//            }
//        }
    }
}
