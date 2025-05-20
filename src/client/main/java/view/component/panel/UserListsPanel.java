package view.component.panel;

import controller.UserListController;
import lib.ScrollablePanel;
import model.ServerResponse;
import model.User;
import model.UserList;
import net.miginfocom.swing.MigLayout;
import view.MainFrame;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.HashSet;

public class UserListsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final User user;

    private JPanel pnlContainer;
    private ScrollablePanel pnlMultimediaLists;
    private JButton btnCreateList;

    private boolean detailedView;

    public UserListsPanel(MainFrame mainFrame){
        this.mainFrame = mainFrame;
        this.user = mainFrame.getUser();

        this.detailedView = false;

        createUI();
        updateUIStatus();
        createListeners();
    }

    public void updateUIStatus() {
        if (detailedView) {
            ((DetailMultimediaPanel) getComponent(0)).updatePage();
        } else {
            pnlMultimediaLists.removeAll();
            user.getLists()
                    .forEach(list -> pnlMultimediaLists.add(new CollapsableUserListPanel(this, list)));
        }

        revalidate();
        repaint();
    }

    public void showDetailPanel(DetailMultimediaPanel panel) {
        remove(0);
        add(panel);
        detailedView = true;

        revalidate();
        repaint();
    }

    public void removeDetailPanel() {
        remove(0);
        add(pnlContainer);
        detailedView = false;

        revalidate();
        repaint();
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
                UserList userList = new UserList(listName, new HashSet<>());
                user.getLists().add(userList);
                pnlMultimediaLists.add(new CollapsableUserListPanel(this, userList));

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
}
