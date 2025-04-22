package view.component.panel;

import controller.UserListController;
import lib.ScrollablePanel;
import model.ServerResponse;
import model.User;
import model.UserList;
import net.miginfocom.swing.MigLayout;
import thread.FetchUserLists;
import view.MainFrame;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UserListsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final User user;

    private ScrollablePanel pnlMultimediaLists;

    public UserListsPanel(){
        this.mainFrame = MainFrame.getInstance();
        this.user = mainFrame.getUser();

        init();
        printUserLists();
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

            ServerResponse serverResponse = UserListController.createUserList(user, listName);
            if (serverResponse.getStatus() != 200) {
                processServerError(serverResponse);
            } else {
                SwingWorker<List<UserList>, Void> worker = new SwingWorker<>() {
                    @Override
                    protected List<UserList> doInBackground() {
                        return new FetchUserLists(user).getUpdatedUserLists();
                    }

                    @Override
                    protected void done() {
                        try {
                            user.setLists(get());
                            printUserLists();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                worker.execute();
            }
        });

        //Adds

        add(pnlButtons);
        add(scrollPane);
    }

    private void printUserLists() {
        pnlMultimediaLists.removeAll();
        user.getLists().forEach(list -> pnlMultimediaLists.add(new CollapsableUserListPanel(list)));
        updateUIStatus();
    }

    private void updateUIStatus() {
        revalidate();
        repaint();
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
