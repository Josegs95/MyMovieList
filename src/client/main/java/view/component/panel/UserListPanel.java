package view.component.panel;

import controller.UserListController;
import event.Event;
import event.EventListener;
import lib.ScrollablePanel;
import model.MultimediaListItem;
import model.ServerResponse;
import model.User;
import model.UserList;
import net.miginfocom.swing.MigLayout;
import view.MainFrame;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class UserListPanel extends JPanel implements EventListener {

    private final MainFrame mainFrame;
    private final User user;

    private JPanel pnlContainer;
    private ScrollablePanel pnlMultimediaLists;
    private JButton btnCreateList;

    private final Map<UserList, CollapsableUserListPanel> userListViewDict;

    public UserListPanel(MainFrame mainFrame){
        this.mainFrame = mainFrame;
        this.user = mainFrame.getUser();

        userListViewDict = new HashMap<>();

        createUI();
        createListeners();
    }

    public void updateState() {
        if (pnlContainer.getComponent(0) instanceof DetailMultimediaPanel detailPanel) {
            detailPanel.updatePage();
        }
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

        pnlMultimediaLists = new ScrollablePanel(new MigLayout(
                "fillx, flowy, ins 0",
                "[fill]",
                "[100::null, fill]0[100::null, fill]"
        ));
        pnlMultimediaLists.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        pnlMultimediaLists.setScrollableHeight(ScrollablePanel.ScrollableSizeHint.STRETCH);
        pnlMultimediaLists.setBorder(LineBorder.createBlackLineBorder());

        JScrollPane scrollPane = new JScrollPane(pnlMultimediaLists);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        pnlContainer.add(scrollPane);
    }

    private void createListeners() {
        btnCreateList.addActionListener(_->{
            String listName = JOptionPane.showInputDialog(mainFrame,
                    "Write the list's name that you desire.");

            if (listName == null)
                return;

            ServerResponse serverResponse = UserListController.createUserList(user, listName);
            if (serverResponse.getStatus() != 200) {
                processServerError(serverResponse);
            } else {
                // Add list to model
                UserList userList = new UserList(listName, new HashSet<>());
                user.getLists().add(userList);

                // Add list to the UI
                CollapsableUserListPanel panel = new CollapsableUserListPanel(this, userList);
                pnlMultimediaLists.add(panel);
                userListViewDict.put(userList, panel);

                revalidate();
                repaint();
            }
        });
    }

    private void processServerError(ServerResponse response) {
        String errorMessage = "Could not create the new list";
        if (response.getErrorCode() == 23) {
            errorMessage = "You already have a list with than name";
        }
        JOptionPane.showMessageDialog(mainFrame, errorMessage,
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void addMultimediaToList(UserList userList, MultimediaListItem item) {
        // Add MultimediaListItem to model
        userList.getMultimediaList().add(item);

        // Add MultimediaListItem to the UI
        CollapsableUserListPanel panel = userListViewDict.get(userList);
        panel.addMultimediaListItem(item);
        panel.updateListName();

        revalidate();
        repaint();
    }

    private void removeMultimediaFromList(UserList userList, MultimediaListItem item) {
        // Remove MultimediaListItem from model
        userList.removeMultimediaListItem(item);

        // Remove MultimediaListItem from the UI
        CollapsableUserListPanel panel = userListViewDict.get(userList);
        panel.removeMultimediaItem(item);

        revalidate();
        repaint();
    }

    private void showDetailPanel(DetailMultimediaPanel panel) {
        remove(0);
        add(panel);

        revalidate();
        repaint();
    }

    private void removeDetailPanel() {
        remove(0);
        add(pnlContainer);

        revalidate();
        repaint();
    }

    private void createUserListItems(List<UserList> userLists) {
        pnlMultimediaLists.removeAll();

        for (UserList userList : userLists) {
            CollapsableUserListPanel panel = new CollapsableUserListPanel(this, userList);
            userListViewDict.put(userList, panel);
            pnlMultimediaLists.add(panel);
        }

        revalidate();
        repaint();
    }

    private void deleteUserList(UserList userList) {
        // Delete userList from model
        user.getLists().remove(userList);

        // Delete userList from UI
        pnlMultimediaLists.remove(userListViewDict.get(userList));

        revalidate();
        repaint();
    }

    @Override @SuppressWarnings("unchecked")
    public void onEvent(Event event) {
        Map<String, Object> data = event.data();

        switch (event.type()) {
            case ADD_MULTIMEDIA -> {
                UserList userList = (UserList) data.get("userList");
                MultimediaListItem multimediaListItem = (MultimediaListItem) data.get("multimediaListItem");

                addMultimediaToList(userList, multimediaListItem);
            }
            case REMOVE_MULTIMEDIA -> {
                UserList userList = (UserList) data.get("userList");
                MultimediaListItem multimediaListItem = (MultimediaListItem) data.get("multimediaListItem");

                removeMultimediaFromList(userList, multimediaListItem);
            }
            case SHOW_DETAIL_PANEL -> {
                DetailMultimediaPanel panel = (DetailMultimediaPanel) data.get("detailPanel");

                showDetailPanel(panel);
            }
            case HIDE_DETAIL_PANEL -> removeDetailPanel();
            case CREATE_USER_LIST_ITEMS -> {
                List<UserList> userLists = (List<UserList>) data.get("userLists");

                createUserListItems(userLists);
            }
            case DELETE_USER_LIST -> {
                UserList userList = (UserList) data.get("userList");

                deleteUserList(userList);
            }
        }
    }
}
