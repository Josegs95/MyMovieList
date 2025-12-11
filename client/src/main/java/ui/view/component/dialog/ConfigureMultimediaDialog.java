package ui.view.component.dialog;

import context.SessionContext;
import model.dto.*;
import model.entity.MultimediaStatus;
import model.entity.MultimediaType;
import net.miginfocom.swing.MigLayout;
import ui.view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.function.Function;

public class ConfigureMultimediaDialog extends JDialog {

    private static final Color BACKGROUND_COLOR = new Color(223, 223, 223);

    private final MainFrame mainFrame;
    private final MultimediaListItemDTO listItem;
    private final UserDTO user;

    private JComboBox<UserListDTO> cmbLists;
    private JComboBox<MultimediaStatus> cmbStatus;
    private JSpinner spnEpisode;
    private SpinnerNumberModel spinnerModel;
    private JTextField spinnerTextField;
    private JButton btnCancel;
    private JButton btnAccept;

    private boolean cancelled = false;
    private boolean internalUpdate = false;

    public ConfigureMultimediaDialog(MainFrame mainFrame, MultimediaSummaryDTO summaryDTO) {
        this(mainFrame, new MultimediaListItemDTO(null, MultimediaStatus.PLAN_TO_WATCH, 1, summaryDTO));
    }

    public ConfigureMultimediaDialog(MainFrame mainFrame, MultimediaListItemDTO listItem) {
        super(mainFrame, true);

        this.mainFrame = mainFrame;
        this.listItem = listItem;
        this.user = SessionContext.getInstance().getUser();

        createUI();
        createListeners();
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
        if (listItem.getListId() == null) {
            lists = getAllListsWithoutMultimedia();
            cmbLists = new JComboBox<>(lists.toArray(UserListDTO[]::new));
        } else {
            UserListDTO userList = user.getLists().stream()
                    .filter(listDTO -> listDTO.getId().equals(listItem.getListId()))
                    .findFirst().orElseThrow();
            cmbLists = new JComboBox<>(new UserListDTO[] {userList});
            cmbLists.setEnabled(false);
        }

        cmbLists.setRenderer(new SimpleNameRenderer<>(UserListDTO::getName));

        // Status selector component

        JLabel lblStatus = new JLabel("Status:", SwingConstants.RIGHT);

        MultimediaStatus[] statuses = MultimediaStatus.getMultimediaStatusValues(listItem.getMultimedia().getType());
        cmbStatus = new JComboBox<>(statuses);
        cmbStatus.setRenderer(new SimpleNameRenderer<>(MultimediaStatus::name));
        cmbStatus.setSelectedItem(listItem.getStatus());

        // Current episode selector component

        JLabel lblCurrentEpisode = new JLabel("Current episode:", SwingConstants.RIGHT);

        JPanel pnlSpinnerEpisode = new JPanel(new MigLayout(
                "ins 0, aligny center",
                "[][]",
                "[]")
        );
        pnlSpinnerEpisode.setOpaque(false);

        int totalEpisodes = 0;
        if (listItem.getMultimedia() instanceof SeriesSummaryDTO series) {
            totalEpisodes = series.getTotalEpisodes();
        }
        spnEpisode = new JSpinner(new SpinnerNumberModel(0, 0, totalEpisodes, 1));
        spinnerModel = (SpinnerNumberModel) spnEpisode.getModel();
        JSpinner.NumberEditor spinnerEditor = (JSpinner.NumberEditor) spnEpisode.getEditor();
        spinnerTextField = spinnerEditor.getTextField();

        spinnerTextField.setHorizontalAlignment(SwingConstants.CENTER);
        spnEpisode.setValue(listItem.getCurrentEpisode());
        spinnerEditor.getFormat().setGroupingUsed(false);

        JLabel lblTotalEpisodes = new JLabel("/ " + totalEpisodes);

        pnlSpinnerEpisode.add(spnEpisode);
        pnlSpinnerEpisode.add(lblTotalEpisodes);

        if (listItem.getMultimedia().getType() == MultimediaType.MOVIE) {
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
        btnAccept = new JButton("Accept");

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

    private void createListeners() {
        spnEpisode.addChangeListener(_ -> onEpisodeSpinner());
        cmbStatus.addItemListener(this::onStatusComboBox);
        spinnerTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                SwingUtilities.invokeLater(spinnerTextField::selectAll);
            }
        });
    }

    private void onEpisodeSpinner() {
        if (internalUpdate) {
            return;
        }

        internalUpdate = true;

        try {
            int currentValue = spinnerModel.getNumber().intValue();
            if (currentValue == 0) {
                cmbStatus.setSelectedItem(MultimediaStatus.PLAN_TO_WATCH);
            } else if (currentValue == (Integer) spinnerModel.getMaximum()) {
                cmbStatus.setSelectedItem(MultimediaStatus.FINISHED);
            }
        } finally {
            internalUpdate = false;
        }
    }

    private void onStatusComboBox(ItemEvent event) {
        if (event.getStateChange() != ItemEvent.SELECTED || internalUpdate) {
            return;
        }

        internalUpdate = true;

        try {
            MultimediaStatus selectedStatus = (MultimediaStatus) event.getItem();
            switch (selectedStatus) {
                case PLAN_TO_WATCH -> spnEpisode.setValue(0);
                case FINISHED -> spnEpisode.setValue(spinnerModel.getMaximum());
            }
        } finally {
            internalUpdate = false;
        }
    }

    public boolean hasMultimediaChanges() {
        return listItem.getListId() == null
                || !listItem.getListId().equals(getSelectedList().getId())
                || !listItem.getCurrentEpisode().equals(getSelectedCurrentEpisode())
                || listItem.getStatus() != getSelectedMultimediaStatus();
    }

    public void cancelDialog() {
        cancelled = true;
        this.dispose();
    }

    private List<UserListDTO> getAllListsWithoutMultimedia() {
        return user.getLists().stream()
                .filter(listDTO -> listDTO.getListItems().stream()
                        .map(MultimediaListItemDTO::getMultimedia)
                        .noneMatch(multimedia -> multimedia.equals(listItem.getMultimedia())))
                .toList();
    }

    public MultimediaListItemDTO getListItemResult() {
        return new MultimediaListItemDTO(
                getSelectedList().getId(),
                getSelectedMultimediaStatus(),
                getSelectedCurrentEpisode(),
                listItem.getMultimedia());
    }

    public UserListDTO getSelectedList() {
        if (isCancelled()) return null;
        if (listItem.getListId() != null) {
            user.getLists().stream()
                    .filter(list -> list.getId().equals(listItem.getListId()))
                    .findFirst().orElseThrow();
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
        if (cancelled || listItem.getMultimedia().getType() == MultimediaType.MOVIE) {
            return null;
        }

        return (Integer) (spnEpisode.getModel().getValue());
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
