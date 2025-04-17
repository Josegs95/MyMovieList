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
    private final Multimedia multimedia;
    private final List<UserList> userLists;

    private JComboBox<String> cmbLists;
    private JComboBox<MultimediaStatus> cmbStatus;
    private JSpinner spnEpisode;

    private boolean isCanceled = false;

    public ConfigureMultimediaDialog(MainFrame mainFrame, Multimedia multimedia,
                                     List<UserList> userLists) {
        super(mainFrame, true);

        this.mainFrame = mainFrame;
        this.multimedia = multimedia;
        this.userLists = userLists;
        init();
    }

    private void init() {
        setSize(300, 250);
        setLocationRelativeTo(mainFrame);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getContentPane().setBackground(new Color(223, 223, 223));
        setTitle("Configuration");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                isCanceled = true;
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

        cmbLists = new JComboBox<>(userLists.stream()
                .map(UserList::getListName)
                .toArray(String[]::new)
        );
        cmbLists.setSelectedItem(userLists.getFirst());
        ((JLabel) cmbLists.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // Status selector component

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setHorizontalAlignment(SwingConstants.RIGHT);

        MultimediaStatus[] statuses = MultimediaStatus.getMultimediaStatusValues(multimedia.getMultimediaType());
        cmbStatus = new JComboBox<>(statuses);
        ((JLabel) cmbStatus.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // Current episode selector component

        JLabel lblCurrentEpisode = new JLabel("Current episode:");
        lblCurrentEpisode.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel pnlSpinnerEpisode = new JPanel(new MigLayout(
                "ins 0, aligny center",
                "[][]",
                "[]")
        );
        pnlSpinnerEpisode.setOpaque(false);

        int totalEpisodes = 0;
        if (multimedia.getMultimediaType() == MultimediaType.TV_SHOW) {
            totalEpisodes = ((TvShow) multimedia).getTotalEpisodes();
        }
        spnEpisode = new JSpinner(
                new SpinnerNumberModel(0, 0, totalEpisodes, 1));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spnEpisode.getEditor();
        editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTotalEpisodes = new JLabel("/ " + totalEpisodes);

        pnlSpinnerEpisode.add(spnEpisode);
        pnlSpinnerEpisode.add(lblTotalEpisodes);

        if (multimedia.getMultimediaType() == MultimediaType.MOVIE) {
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

        JButton btnCancel = new JButton("Cancel");
        JButton btnAccept = new JButton("Add");

        pnlButtons.add(btnCancel, "sg 99, alignx right");
        pnlButtons.add(btnAccept, "sg 99, alignx left");

        //Listeners
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
            isCanceled = true;
            ConfigureMultimediaDialog.this.dispose();
        });

        btnAccept.addActionListener(_ -> ConfigureMultimediaDialog.this.dispose());

        //Adds

        add(lblLists, "sg 1");
        add(cmbLists, "sg 2, wrap");
        add(lblStatus, "sg 1");
        add(cmbStatus, "sg 2, wrap");
        add(lblCurrentEpisode, "sg 1");
        add(pnlSpinnerEpisode, "sg 2, wrap");
        add(pnlButtons, "span 2");
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public UserList getSelectedList() {
        if (isCanceled || cmbLists.getSelectedItem() == null) {
            return null;
        }

        String selectedListName = cmbLists.getSelectedItem().toString();
        return userLists.stream()
                .filter(userList -> userList.getListName().equals(selectedListName))
                .findFirst()
                .orElse(null);
    }

    public MultimediaStatus getSelectedMultimediaStatus() {
        if (!isCanceled) {
            return (MultimediaStatus) cmbStatus.getSelectedItem();
        }

        return null;
    }

    public int getSelectedCurrentEpisode() {
        if (!isCanceled) {
            return (int) (spnEpisode.getModel().getValue());
        }

        return -1;
    }
}
