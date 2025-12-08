package ui.controller;

import model.dto.MultimediaListItemDTO;
import model.dto.MultimediaSummaryDTO;
import model.dto.UserListDTO;
import service.UserListService;
import ui.util.ErrorHandler;
import ui.view.component.dialog.ConfigureMultimediaDialog;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConfigureDialogUIController {

    private final ConfigureMultimediaDialog view;
    private final MultimediaSummaryDTO summaryDTO;
    private final UserListService service;

    public ConfigureDialogUIController(ConfigureMultimediaDialog view, MultimediaSummaryDTO summaryDTO, UserListService service) {
        this.view = view;
        this.summaryDTO = summaryDTO;
        this.service = service;

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
    }

    private void onAcceptButton() {
        UserListDTO userList = view.getSelectedList();
        try {
            MultimediaListItemDTO listItem = new MultimediaListItemDTO(
                    userList.getId(),
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
