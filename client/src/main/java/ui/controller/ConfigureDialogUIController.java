package ui.controller;

import dto.MultimediaListItemDTO;
import dto.MultimediaSummaryDTO;
import dto.UserListDTO;
import model.entity.MultimediaStatus;
import service.UserListService;
import ui.util.ErrorHandler;
import ui.view.component.dialog.ConfigureMultimediaDialog;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConfigureDialogUIController {

    private final ConfigureMultimediaDialog view;
    private final MultimediaSummaryDTO summaryDTO;
    private final UserListService service;

    private final JSpinner spnEpisode;
    private final SpinnerNumberModel spinnerModel;
    private final JComboBox<MultimediaStatus> cmbStatus;

    private boolean internalUpdate;

    public ConfigureDialogUIController(ConfigureMultimediaDialog view, MultimediaSummaryDTO summaryDTO, UserListService service) {
        this.view = view;
        this.summaryDTO = summaryDTO;
        this.service = service;

        this.spnEpisode = view.getSpnEpisode();
        this.spinnerModel = (SpinnerNumberModel) spnEpisode.getModel();
        this.cmbStatus = view.getCmbStatus();

        initListeners();
    }

    private void initListeners() {
        view.getBtnAccept().addActionListener(_ -> onAcceptButton());
        view.getBtnCancel().addActionListener(_ -> onClose());
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });
        view.getCmbStatus().addItemListener(this::onStatusComboBox);
        view.getSpnEpisode().addChangeListener(_ -> onEpisodeSpinner());
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

    private void onAcceptButton() {
        UserListDTO userList = view.getSelectedList();
        try {
            MultimediaListItemDTO listItem = new MultimediaListItemDTO(
                    userList.id(),
                    view.getSelectedMultimediaStatus(),
                    view.getSelectedCurrentEpisode(),
                    summaryDTO);

            service.addItemToList(userList, listItem);
            view.dispose();
        } catch (Exception e) {
            ErrorHandler.showError(view, e);
        }
    }

    private void onClose() {
        view.cancelDialog();
    }
}
