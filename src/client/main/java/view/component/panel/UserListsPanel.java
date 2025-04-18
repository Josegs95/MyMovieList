package view.component.panel;

import controller.UserListController;
import lib.ScrollablePanel;
import model.ServerResponse;
import model.UserList;
import net.miginfocom.swing.MigLayout;
import view.MainFrame;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.HashSet;

public class UserListsPanel extends JPanel {

    private final MainFrame mainFrame;

    private ScrollablePanel pnlMultimediaLists;

    public UserListsPanel(){
        this.mainFrame = MainFrame.getInstance();

        init();
        initUserList();
    }

    private void init() {
        setLayout(new MigLayout(
                "flowy, fill, ins 0",
                "[fill]",
                "[50!, fill]10[fill]"
        ));

        JPanel pnlButtons = new JPanel(new MigLayout(
                "fill, ins 0",
                "[align center]",
                "[align center]"
        ));
        pnlButtons.setBorder(LineBorder.createBlackLineBorder());

        JButton btnCreateList = new JButton("Create List");
        btnCreateList.setFocusPainted(false);

        pnlButtons.add(btnCreateList);

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

        //Listeners

        btnCreateList.addActionListener(_->{
            String listName = JOptionPane.showInputDialog(mainFrame,
                    "Write the list's name that you desire.");

            if (listName == null)
                return;

            ServerResponse serverResponse = UserListController.createUserList(mainFrame.getUser(), listName);
            if (serverResponse.getStatus() != 200) {
                String errorMessage = "Could not create the new list";
                if (serverResponse.getErrorCode() == 23) {
                    errorMessage = "You already have a list with than name";
                }
                JOptionPane.showMessageDialog(mainFrame, errorMessage,
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            UserList newList = new UserList(listName, new HashSet<>());
            MainFrame.getInstance().getUser().getLists().add(newList);
            createListItemPanel(newList);
        });

        //Adds

        add(pnlButtons);
        add(scrollPane);
    }

    private void initUserList(){
        mainFrame.getUser().getLists().forEach(this::createListItemPanel);
    }

    private void createListItemPanel(UserList userList){
        pnlMultimediaLists.add(new UserListCollapsablePanel(userList));
        UserListsPanel.this.revalidate();
        UserListsPanel.this.repaint();
    }
}
