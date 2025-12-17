package ui.controller;

import context.SessionContext;
import exception.SessionExpiredException;
import model.dto.MultimediaListItemDTO;
import model.dto.MultimediaSummaryDTO;
import model.dto.UserListDTO;
import service.UserListService;
import ui.event.SessionExpiredEvent;
import ui.util.ErrorHandler;
import ui.util.EventBus;
import ui.view.component.dialog.RemoveMultimediaDialog;
import util.PendingAction;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class RemoveDialogUIController {

    private final RemoveMultimediaDialog view;
    private final UserListService service;

    public RemoveDialogUIController(RemoveMultimediaDialog view) {
        this.view = view;
        this.service = SessionContext.getInstance().getUserListService();

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
        PendingAction action = () -> {
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
        };

        try {
            action.execute();
        } catch (SessionExpiredException e) {
            EventBus.publish(new SessionExpiredEvent(action));
        } catch (Exception e) {
            ErrorHandler.showError(view, e);
        }
    }

    private void onCancel() {
        view.setCancelled(true);
        view.dispose();
    }
}
