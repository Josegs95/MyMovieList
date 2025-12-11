package ui.controller;

import context.SessionContext;
import service.UserListService;
import ui.event.ModifyListItemEvent;
import ui.event.RenameListEvent;
import ui.util.CompositeSubscription;
import ui.util.ErrorHandler;
import ui.util.EventBus;
import ui.view.component.panel.CollapsableListPanel;

public class CollapsableListUIController {

    private final CollapsableListPanel view;
    private final UserListService service;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    public CollapsableListUIController(CollapsableListPanel view) {
        this.view = view;
        this.service = SessionContext.getInstance().getUserListService();

        subscriptions.add(EventBus.subscribe(RenameListEvent.class, this::onRenameListEvent));
        subscriptions.add(EventBus.subscribe(ModifyListItemEvent.class, this::onModifyListItemEvent));

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

    private void onModifyListItemEvent(ModifyListItemEvent modifyListItemEvent) {
        view.modifyListItem(modifyListItemEvent.modifiedListItem());
    }

    public void dispose() {
        subscriptions.unsubscribe();
    }
}
