package view.component.dialog;

import model.*;
import net.miginfocom.swing.MigLayout;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class ConfigureMultimediaDialog extends JDialog {

    private final MainFrame mainFrame;
    private final MultimediaListItem multimediaListItem;
    private final Multimedia multimedia;
    private final MultimediaType multimediaType;
    private final User user;
    private final UserList multimediaList;

    private JComboBox<UserList> cmbLists;
    private JComboBox<MultimediaStatus> cmbStatus;
    private JSpinner spnEpisode;
    private JButton btnCancel;
    private JButton btnAccept;

    private boolean cancelled = false;

    public ConfigureMultimediaDialog(MainFrame mainFrame, MultimediaListItem multimediaListItem) {
        this(mainFrame, multimediaListItem,null);
    }

    public ConfigureMultimediaDialog(MainFrame mainFrame, MultimediaListItem multimediaListItem, UserList userList) {
        super(mainFrame, true);

        this.mainFrame = mainFrame;
        this.multimediaListItem = multimediaListItem;
        this.multimedia = multimediaListItem.getMultimedia();
        this.multimediaType = multimedia.getMultimediaType();
        this.user = mainFrame.getUser();
        this.multimediaList = userList;

        createUI();
        createListenersForComponents();
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public UserList getMultimediaList() {
        if (cancelled || cmbLists.getSelectedItem() == null) {
            return null;
        }

        return (UserList) (cmbLists.getSelectedItem());
    }

    public MultimediaStatus getSelectedMultimediaStatus() {
        if (!cancelled) {
            return (MultimediaStatus) cmbStatus.getSelectedItem();
        }

        return null;
    }

    public int getSelectedCurrentEpisode() {
        if (!cancelled) {
            return (int) (spnEpisode.getModel().getValue());
        }

        return -1;
    }

    private void createUI() {
        setSize(300, 250);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getContentPane().setBackground(new Color(223, 223, 223));
        setTitle("Configuration");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancelled = true;
                ConfigureMultimediaDialog.this.dispose();
            }
        });

        setLayout(new MigLayout(
                "ins 10 20 10 20, fill",
                "[fill]20[grow]",
                "[]")
        );

        // List selector component

        JLabel lblLists = new JLabel("Lists:");
        lblLists.setHorizontalAlignment(SwingConstants.RIGHT);


        if (multimediaList == null) {
            List<UserList> lists = user.getLists();

            cmbLists = new JComboBox<>(lists.toArray(UserList[]::new));
            cmbLists.setSelectedItem(lists.getFirst());
        } else {
            cmbLists = new JComboBox<>(new UserList[] {multimediaList});
            cmbLists.setSelectedItem(multimediaList);
            cmbLists.setEnabled(false);
        }

        ((JLabel) cmbLists.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // Status selector component

        JLabel lblStatus = new JLabel("Status:", SwingConstants.RIGHT);

        MultimediaStatus[] statuses = MultimediaStatus.getMultimediaStatusValues(multimediaType);
        cmbStatus = new JComboBox<>(statuses);
        ((JLabel) cmbStatus.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        cmbStatus.setSelectedItem(multimediaListItem.getStatus());

        // Current episode selector component

        JLabel lblCurrentEpisode = new JLabel("Current episode:", SwingConstants.RIGHT);

        JPanel pnlSpinnerEpisode = new JPanel(new MigLayout(
                "ins 0, aligny center",
                "[][]",
                "[]")
        );
        pnlSpinnerEpisode.setOpaque(false);

        int totalEpisodes = 0;
        if (multimediaType == MultimediaType.TV_SHOW) {
            totalEpisodes = ((TvShow) multimedia).getTotalEpisodes();
        }
        spnEpisode = new JSpinner(
                new SpinnerNumberModel(0, 0, totalEpisodes, 1));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spnEpisode.getEditor();
        editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);
        spnEpisode.setValue(multimediaListItem.getCurrentEpisode());

        JLabel lblTotalEpisodes = new JLabel("/ " + totalEpisodes);

        pnlSpinnerEpisode.add(spnEpisode);
        pnlSpinnerEpisode.add(lblTotalEpisodes);

        if (multimediaType == MultimediaType.MOVIE) {
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

    private void createListenersForComponents() {
        cmbStatus.addActionListener(_ -> {
            if (cmbStatus.getSelectedItem() == null) {
                return;
            }

            MultimediaStatus selectedStatus = (MultimediaStatus) cmbStatus.getSelectedItem();
            switch (selectedStatus) {
                case PLAN_TO_WATCH -> spnEpisode.setValue(0);
                case FINISHED -> spnEpisode.setValue(((SpinnerNumberModel) spnEpisode.getModel()).getMaximum());
            }
        });

        spnEpisode.addChangeListener(_ -> {
            SpinnerNumberModel spinnerModel = (SpinnerNumberModel) spnEpisode.getModel();
            int currentValue = (int) spinnerModel.getValue();
            if (currentValue == 0) {
                cmbStatus.setSelectedItem(MultimediaStatus.PLAN_TO_WATCH);
            } else if (currentValue == (Integer) spinnerModel.getMaximum()) {
                cmbStatus.setSelectedItem(MultimediaStatus.FINISHED);
            }
        });

        btnCancel.addActionListener(_ -> {
            cancelled = true;
            ConfigureMultimediaDialog.this.dispose();
        });

        btnAccept.addActionListener(_ -> ConfigureMultimediaDialog.this.dispose());
    }
}
