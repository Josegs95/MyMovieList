package ui.controller;

import dto.UserListDTO;
import service.UserListService;
import ui.event.GetListsEvent;
import ui.event.ListItemAddedEvent;
import ui.event.ListItemDeletedEvent;
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

        EventBus.subscribe(GetListsEvent.class, this::onAllListsGotten);
        EventBus.subscribe(ListItemAddedEvent.class, this::onListItemAdded);
        EventBus.subscribe(ListItemDeletedEvent.class, this::onListItemDeleted);

        initListeners();
        getUserData();
    }

    private void initListeners() {
        view.getBtnCreateList().addActionListener(_ -> onCreateList());
    }

    private void getUserData() {
        CompletableFuture
                .supplyAsync(userListService::getAllListsWithItems)
                .thenAccept(lists -> EventBus.publish(new GetListsEvent(lists)))
                .exceptionally(e -> {
                    ErrorHandler.showError(view, (Exception) e);
                    return null;
                });
    }

    private void onCreateList() {
        String listName = view.showListNameDialog();
        if (listName == null) {
            return;
        }

        try {
            UserListDTO userList = userListService.createList(listName);
            view.createUserList(userList);
        } catch (Exception e) {
            ErrorHandler.showError(view, e);
        }
    }

    private void onAllListsGotten(GetListsEvent event) {
        SwingUtilities.invokeLater(() -> event.lists().forEach(view::createUserList));
    }

    private void onListItemAdded(ListItemAddedEvent event) {
        view.addMultimediaToList(event.userListDTO(), event.listItemDTO());
    }

    private void onListItemDeleted(ListItemDeletedEvent event) {
        view.removeMultimediaFromList(event.userListDTO(), event.deletedItem());
    }
}
