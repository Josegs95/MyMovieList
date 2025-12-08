package ui.view.component.dialog;

import context.SessionContext;
import model.dto.MultimediaListItemDTO;
import model.dto.MultimediaSummaryDTO;
import model.dto.UserDTO;
import model.dto.UserListDTO;
import net.miginfocom.swing.MigLayout;
import ui.view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class RemoveMultimediaDialog extends JDialog {

    private static final Color BACKGROUND_COLOR = new Color(223, 223, 223);

    private final MainFrame mainFrame;
    private final MultimediaSummaryDTO multimedia;

    private JComboBox<UserListDTO> cmbLists;
    private JButton btnCancel;
    private JButton btnRemove;

    private boolean cancelled;

    public RemoveMultimediaDialog (MainFrame mainFrame, MultimediaSummaryDTO multimedia) {
        super(mainFrame, true);

        this.multimedia = multimedia;
        this.mainFrame = mainFrame;

        createUI();
    }

    private void createUI() {
        setSize(350, 200);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
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

        cmbLists = new JComboBox<>(getAllListContainingMultimedia());
        cmbLists.setRenderer(new SimpleNameRenderer<>(UserListDTO::getName));

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

    private UserListDTO[] getAllListContainingMultimedia() {
        UserDTO user = SessionContext.getInstance().getUser();
        return user.getLists().stream()
                .filter(userList -> userList.getListItems().stream()
                        .map(MultimediaListItemDTO::getMultimedia)
                        .anyMatch(item -> item.equals(multimedia)))
                .toArray(UserListDTO[]::new);
    }

    public UserListDTO getSelectedList() {
        if (cancelled || cmbLists.getSelectedItem() == null) {
            return null;
        }

        return (UserListDTO) cmbLists.getSelectedItem();
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }

    public JButton getBtnRemove() {
        return btnRemove;
    }

    public MultimediaSummaryDTO getMultimedia() {
        return multimedia;
    }

    private static class SimpleNameRenderer<T> extends DefaultListCellRenderer {

        private final Function<T, String> nameExtractor;

        private SimpleNameRenderer(Function<T, String> nameExtractor) {
            this.nameExtractor = nameExtractor;

            setHorizontalAlignment(CENTER);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            return super.getListCellRendererComponent(
                    list,
                    value != null ? nameExtractor.apply((T) value) : "",
                    index,
                    isSelected,
                    cellHasFocus);
        }
    }
}
