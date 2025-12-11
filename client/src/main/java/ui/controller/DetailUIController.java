package ui.controller;

import model.dto.MultimediaListItemDTO;
import model.dto.MultimediaSummaryDTO;
import model.dto.UserListDTO;
import service.UserListService;
import ui.event.*;
import ui.util.CompositeSubscription;
import ui.util.ErrorHandler;
import ui.util.EventBus;
import ui.view.MainFrame;
import ui.view.component.dialog.ConfigureMultimediaDialog;
import ui.view.component.dialog.RemoveMultimediaDialog;
import ui.view.component.panel.DetailPanel;

import javax.swing.*;

public class DetailUIController {

    private final MainFrame mainFrame;
    private final DetailPanel view;
    private final UserListService service;
    private final UserListDTO userList;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    public DetailUIController(MainFrame mainFrame, DetailPanel view, UserListService service, UserListDTO userList) {
        this.mainFrame = mainFrame;
        this.view = view;
        this.service = service;
        this.userList = userList;

        subscriptions.add(EventBus.subscribe(CreateListEvent.class, this::onCreateListEvent));
        subscriptions.add(EventBus.subscribe(GetListsEvent.class, this::onGetListsEvent));
        subscriptions.add(EventBus.subscribe(RenameListEvent.class, this::onRenameListEvent));
        subscriptions.add(EventBus.subscribe(DeleteListEvent.class, this::onDeleteListEvent));
        subscriptions.add(EventBus.subscribe(AddListItemEvent.class, this::onListItemCreated));
        subscriptions.add(EventBus.subscribe(DeleteListItemEvent.class, this::onListItemDeleted));
        subscriptions.add(EventBus.subscribe(HideDetailsEvent.class, this::onHideDetailPanel));

        initListeners();
    }

    private void initListeners() {
        view.getBtnBack().addActionListener(_ -> onBackButton());
        view.getBtnAddToList().addActionListener(_ -> onAddToListButton());
        view.getBtnRemoveFromList().addActionListener(_ -> onRemoveButton());
    }

    private void onAddToListButton() {
        MultimediaSummaryDTO multimedia = view.getSummaryDTO();

        ConfigureMultimediaDialog dialog = new ConfigureMultimediaDialog(mainFrame, multimedia);
        new ConfigureDialogUIController(dialog, service::addItemToList);
        dialog.setVisible(true);
    }

    private void onRemoveButton() {
        MultimediaSummaryDTO multimedia = view.getSummaryDTO();

        if (userList == null) {
            RemoveMultimediaDialog dialog = new RemoveMultimediaDialog(mainFrame, multimedia);
            new RemoveDialogUIController(dialog, new UserListService());
            dialog.setVisible(true);
            return;
        }

        boolean confirmRemove = view.showRemoveDialog(multimedia, userList);
        if(confirmRemove) {
            try {
                MultimediaListItemDTO listItem = userList.getListItems().stream()
                        .filter(item -> item.getMultimedia().equals(multimedia))
                        .findFirst().orElseThrow();
                service.deleteItemFromList(userList, listItem);
            } catch (Exception e) {
                ErrorHandler.showError(mainFrame, e);
            }
        }
    }

    private void onBackButton() {
        EventBus.publish(new HideDetailsEvent(view));
    }

    private void onCreateListEvent(CreateListEvent createListEvent) {
        view.checkButtonAvailability();
    }

    private void onGetListsEvent(GetListsEvent event) {
        view.checkButtonAvailability();
    }

    private void onRenameListEvent(RenameListEvent event) {
        view.checkButtonAvailability();
    }

    private void onDeleteListEvent(DeleteListEvent event) {
        view.checkButtonAvailability();
    }

    private void onListItemCreated(AddListItemEvent event) {
        String message = String.format(
                "\"%s\" añadido a la lista \"%s\" exitosamente.",
                event.listItemDTO().getMultimedia().getTitle(),
                event.userListDTO().getName());
        JOptionPane.showMessageDialog(view, message,"Información", JOptionPane.INFORMATION_MESSAGE);
        view.checkButtonAvailability();
    }

    private void onListItemDeleted(DeleteListItemEvent event) {
        view.checkButtonAvailability();
    }

    private void onHideDetailPanel(HideDetailsEvent event) {
        subscriptions.unsubscribe();
    }
}
