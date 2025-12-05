package ui.controller;

import dto.MultimediaListItemDTO;
import dto.MultimediaSummaryDTO;
import dto.UserListDTO;
import service.UserListService;
import ui.util.ErrorHandler;
import ui.view.component.dialog.RemoveMultimediaDialog;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class RemoveDialogUIController {

    private final RemoveMultimediaDialog view;
    private final UserListService service;

    public RemoveDialogUIController(RemoveMultimediaDialog view, UserListService service) {
        this.view = view;
        this.service = service;

        initListeners();
    }

    private void initListeners() {
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        view.getBtnCancel().addActionListener(_ -> onCancel());
        view.getBtnRemove().addActionListener(_ -> onRemoveButton());
    }

    private void onRemoveButton() {
        try {
            UserListDTO list = view.getSelectedList();
            MultimediaSummaryDTO multimedia = view.getMultimedia();
            MultimediaListItemDTO listItemDTO = list.getListItems().stream()
                    .filter(multi -> multi.getMultimedia().equals(multimedia))
                    .findFirst().orElseThrow();

            service.deleteItemFromList(list, listItemDTO);

            String message = String.format("Se ha eliminado \"%s\" de la lista \"%s\" exitosamente",
                    multimedia.getTitle(),
                    list.getName());
            JOptionPane.showMessageDialog(view, message, "Información", JOptionPane.INFORMATION_MESSAGE);

            view.dispose();
        } catch (Exception e) {
            ErrorHandler.showError(view, e);
        }
    }

    private void onCancel() {
        view.setCancelled(true);
        view.dispose();
    }
}
