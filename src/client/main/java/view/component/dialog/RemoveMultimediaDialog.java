package view.component.dialog;

import model.User;
import model.UserList;
import net.miginfocom.swing.MigLayout;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class RemoveMultimediaDialog extends JDialog {

    private final List<UserList> userLists;
    private final MainFrame mainFrame;
    private final User user;

    private JComboBox<String> cmbLists;
    private JButton btnCancel;
    private JButton btnRemove;

    private boolean cancelled;

    public RemoveMultimediaDialog (List<UserList> userLists) {
        super(MainFrame.getInstance(), true);

        this.userLists = userLists;

        mainFrame = MainFrame.getInstance();
        user = mainFrame.getUser();

        init();
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public UserList getSelectedList() {
        if (cancelled || cmbLists.getSelectedItem() == null) {
            return null;
        }

        String selectedListName = cmbLists.getSelectedItem().toString();
        return user.getLists().stream()
                .filter(userList -> userList.getListName().equals(selectedListName))
                .findFirst()
                .orElse(null);
    }

    private void init() {
        createUI();
        createListeners();
    }

    private void createUI() {
        setSize(300, 200);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getContentPane().setBackground(new Color(223, 223, 223));
        setTitle("Remove");

        setLayout(new MigLayout(
                "ins 10 20 10 20, flowy, fill, alignx center",
                "[fill]",
                "[]")
        );

        // List selector component

        JPanel pnlLists = new JPanel(new MigLayout(
                "fill, alignx center",
                "[fill, 40%]15[fill, 60%]",
                "[]"));
        pnlLists.setOpaque(false);

        JLabel lblLists = new JLabel("Lists:", SwingConstants.RIGHT);

        String[] listNames = userLists.stream()
                .map(UserList::getListName)
                .toArray(String[]::new);

        cmbLists = new JComboBox<>(listNames);
        cmbLists.setSelectedItem(userLists.getFirst());
        ((JLabel) cmbLists.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        pnlLists.add(lblLists);
        pnlLists.add(cmbLists);

        // Buttons

        JPanel pnlButtons = new JPanel(new MigLayout(
                "ins 0, fill",
                "[]30[]",
                "[]"
        ));
        pnlButtons.setOpaque(false);

        btnCancel = new JButton("Cancel");
        btnRemove = new JButton("Remove");

        pnlButtons.add(btnCancel, "sg 99, alignx right");
        pnlButtons.add(btnRemove, "sg 99, alignx left");

        add(pnlLists);
        add(pnlButtons);
    }

    private void createListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                quit();
            }
        });

        btnCancel.addActionListener(_ -> quit());

        btnRemove.addActionListener(_ -> {
            cancelled = false;
            RemoveMultimediaDialog.this.dispose();
        });
    }

    private void quit() {
        cancelled = true;
        dispose();
    }
}
