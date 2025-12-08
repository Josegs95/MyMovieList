package ui.controller;

import service.UserListService;
import ui.event.RenameListEvent;
import ui.util.ErrorHandler;
import ui.util.EventBus;
import ui.util.Subscription;
import ui.view.component.panel.CollapsableListPanel;

public class CollapsableListUIController {

    private final CollapsableListPanel view;
    private final UserListService service;

    private final Subscription subscription;

    public CollapsableListUIController(CollapsableListPanel view, UserListService service) {
        this.view = view;
        this.service = service;

        subscription = EventBus.subscribe(RenameListEvent.class, this::onRenameListEvent);

        initListeners();
    }

    private void initListeners() {
        view.getBtnRename().addActionListener(_ -> onRenameButton());
        view.getBtnDelete().addActionListener(_ -> onDeleteButton());
    }

    private void onRenameButton() {
        String newListName = view.showRenameListDialog();
        if (newListName == null) return;

        newListName = newListName.strip();
        if (newListName.isEmpty() || newListName.equals(view.getUserList().getName())) return;

        try {
            service.renameList(view.getUserList(), newListName);
        } catch(Exception e) {
            ErrorHandler.showError(view, e);
        }
    }

    private void onDeleteButton() {
        if (!view.showDeleteListDialog()) {
            return;
        }

        try {
            service.deleteList(view.getUserList());
        } catch (Exception e) {
            ErrorHandler.showError(view, e);
        }
    }

    private void onRenameListEvent(RenameListEvent event) {
        view.updateListNameLabel();
    }

    public void dispose() {
        subscription.unsubscribe();
    }
}
