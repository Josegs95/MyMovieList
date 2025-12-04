package ui.view.component.dialog;

import context.SessionContext;
import dto.*;
import entity.MultimediaStatus;
import entity.MultimediaType;
import net.miginfocom.swing.MigLayout;
import ui.view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

public class ConfigureMultimediaDialog extends JDialog {

    private static final Color BACKGROUND_COLOR = new Color(223, 223, 223);

    private final MainFrame mainFrame;
    private final MultimediaListItemDTO listItemDTO;
    private final UserDTO user;

    private JComboBox<UserListDTO> cmbLists;
    private JComboBox<MultimediaStatus> cmbStatus;
    private JSpinner spnEpisode;
    private JButton btnCancel;
    private JButton btnAccept;

    private boolean cancelled = false;

    public ConfigureMultimediaDialog(MainFrame mainFrame, MultimediaSummaryDTO summaryDTO) {
        this(mainFrame, new MultimediaListItemDTO(null, MultimediaStatus.PLAN_TO_WATCH, 1, summaryDTO));
    }

    public ConfigureMultimediaDialog(MainFrame mainFrame, MultimediaListItemDTO listItemDTO) {
        super(mainFrame, true);

        this.mainFrame = mainFrame;
        this.listItemDTO = listItemDTO;
        this.user = SessionContext.getInstance().getUser();

        createUI();
    }

    private void createUI() {
        setSize(350, 250);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setTitle("Configuration");

        setLayout(new MigLayout(
                "ins 10 20 10 20, fill",
                "[fill]20[grow]",
                "[]")
        );

        // List selector component

        JLabel lblLists = new JLabel("Lists:",SwingConstants.RIGHT);

        List<UserListDTO> lists;
        if (listItemDTO.getListId() == null) {
            lists = getAllListsWithoutMultimedia();
        } else {
            lists = user.getLists().stream()
                    .filter(listDTO -> listDTO.id().equals(listItemDTO.getListId()))
                    .toList();
            cmbLists.setEnabled(false);
        }
        cmbLists = new JComboBox<>(lists.toArray(UserListDTO[]::new));
        cmbLists.setRenderer(new SimpleNameRenderer<>(UserListDTO::name));
        cmbLists.setSelectedItem(lists.getFirst());

        // Status selector component

        JLabel lblStatus = new JLabel("Status:", SwingConstants.RIGHT);

        MultimediaStatus[] statuses = MultimediaStatus.getMultimediaStatusValues(listItemDTO.getMultimedia().getType());
        cmbStatus = new JComboBox<>(statuses);
        cmbStatus.setRenderer(new SimpleNameRenderer<>(MultimediaStatus::name));
        cmbStatus.setSelectedItem(listItemDTO.getStatus());

        // Current episode selector component

        JLabel lblCurrentEpisode = new JLabel("Current episode:", SwingConstants.RIGHT);

        JPanel pnlSpinnerEpisode = new JPanel(new MigLayout(
                "ins 0, aligny center",
                "[][]",
                "[]")
        );
        pnlSpinnerEpisode.setOpaque(false);

        int totalEpisodes = 0;
        if (listItemDTO.getMultimedia() instanceof SeriesSummaryDTO series) {
            totalEpisodes = series.getTotalEpisodes();
        }
        spnEpisode = new JSpinner(new SpinnerNumberModel(0, 0, totalEpisodes, 1));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spnEpisode.getEditor();
        editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);
        spnEpisode.setValue(listItemDTO.getCurrentEpisode());

        JLabel lblTotalEpisodes = new JLabel("/ " + totalEpisodes);

        pnlSpinnerEpisode.add(spnEpisode);
        pnlSpinnerEpisode.add(lblTotalEpisodes);

        if (listItemDTO.getMultimedia().getType() == MultimediaType.MOVIE) {
            spnEpisode.setEnabled(false);
            lblCurrentEpisode.setForeground(Color.LIGHT_GRAY);
            lblTotalEpisodes.setText(null);
        }

        // Buttons

        JPanel pnlButtons = new JPanel(new MigLayout(
                "ins 0, fill",
                "[]30[]",
                "[]"
        ));
        pnlButtons.setOpaque(false);

        btnCancel = new JButton("Cancel");
        btnAccept = new JButton("Add");

        pnlButtons.add(btnCancel, "sg 99, alignx right");
        pnlButtons.add(btnAccept, "sg 99, alignx left");

        //Adds

        add(lblLists, "sg 1");
        add(cmbLists, "sg 2, wrap");
        add(lblStatus, "sg 1");
        add(cmbStatus, "sg 2, wrap");
        add(lblCurrentEpisode, "sg 1");
        add(pnlSpinnerEpisode, "sg 2, wrap");
        add(pnlButtons, "span 2");
    }

    private List<UserListDTO> getAllListsWithoutMultimedia() {
        return user.getLists().stream()
                .filter(listDTO -> listDTO.listItems().stream()
                        .map(MultimediaListItemDTO::getMultimedia)
                        .noneMatch(multimedia -> multimedia.equals(listItemDTO.getMultimedia())))
                .toList();
    }

    public UserListDTO getSelectedList() {
        if (isCancelled() || cmbLists.getSelectedItem() == null) {
            return null;
        }

        return (UserListDTO) (cmbLists.getSelectedItem());
    }

    public MultimediaStatus getSelectedMultimediaStatus() {
        if (cancelled) {
            return null;
        }

        return (MultimediaStatus) cmbStatus.getSelectedItem();
    }

    public Integer getSelectedCurrentEpisode() {
        if (cancelled || listItemDTO.getMultimedia().getType() == MultimediaType.MOVIE) {
            return null;
        }

        return (Integer) (spnEpisode.getModel().getValue());
    }

    public void cancelDialog() {
        cancelled = true;
        this.dispose();
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public JButton getBtnAccept() {
        return btnAccept;
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }

    public JSpinner getSpnEpisode() {
        return spnEpisode;
    }

    public JComboBox<MultimediaStatus> getCmbStatus() {
        return cmbStatus;
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
