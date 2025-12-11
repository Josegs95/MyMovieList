package ui.controller;

import service.UserListService;
import ui.event.*;
import ui.util.ErrorHandler;
import ui.util.EventBus;
import ui.view.component.panel.UserListPanel;

import javax.swing.*;
import java.util.concurrent.CompletableFuture;

public class UserListUIController {

    private final UserListPanel view;
    private final UserListService userListService;

    public UserListUIController(UserListPanel view, UserListService userListService) {
        this.view = view;
        this.userListService = userListService;

        EventBus.subscribe(CreateListEvent.class, this::onCreateListEvent);
        EventBus.subscribe(DeleteListEvent.class, this::onDeleteListEvent);
        EventBus.subscribe(GetListsEvent.class, this::onGetListsEvent);
        EventBus.subscribe(AddListItemEvent.class, this::onAddListItemEvent);
        EventBus.subscribe(DeleteListItemEvent.class, this::onDeleteListItemEvent);
        EventBus.subscribe(DetailPanelOnListsEvent.class, this::onDetailPanelOnListsEvent);
        EventBus.subscribe(HideDetailsEvent.class, this::onHideDetailsEvent);

        initListeners();
        getUserData();
    }

    private void initListeners() {
        view.getBtnCreateList().addActionListener(_ -> onCreateList());
    }

    private void getUserData() {
        CompletableFuture
                .runAsync(userListService::getAllListsWithItems)
                .exceptionally(e -> {
                    ErrorHandler.showError(view, (Exception) e);
                    return null;
                });
    }

    private void onCreateList() {
        String listName = view.showListNameDialog();
        if (listName == null) return;

        listName = listName.strip();
        if (listName.isEmpty()) return;

        try {
            userListService.createList(listName);
        } catch (Exception e) {
            ErrorHandler.showError(view, e);
        }
    }

    private void onCreateListEvent(CreateListEvent event) {
        view.createUserList(event.userList());
    }

    private void onDeleteListEvent(DeleteListEvent event) {
        view.deleteUserList(event.userList());
    }

    private void onGetListsEvent(GetListsEvent event) {
        SwingUtilities.invokeLater(() -> event.lists().forEach(view::createUserList));
    }

    private void onAddListItemEvent(AddListItemEvent event) {
        view.addMultimediaToList(event.userListDTO(), event.listItemDTO());
    }

    private void onDeleteListItemEvent(DeleteListItemEvent event) {
        view.removeMultimediaFromList(event.userListDTO(), event.deletedItem());
    }

    private void onDetailPanelOnListsEvent(DetailPanelOnListsEvent event) {
        view.showDetailPanel(event.multimediaDetail(), event.multimediaSummary(), event.userList());
    }

    private void onHideDetailsEvent(HideDetailsEvent event) {
        view.removeDetailPanel(event.detailPanel());
    }
}
