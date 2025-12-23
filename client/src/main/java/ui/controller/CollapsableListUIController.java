package ui.controller;

import context.SessionContext;
import exception.SessionExpiredException;
import service.UserListService;
import ui.event.ModifyListItemEvent;
import ui.event.RenameListEvent;
import ui.event.SessionExpiredEvent;
import ui.util.CompositeSubscription;
import ui.util.ErrorHandler;
import ui.util.EventBus;
import ui.view.MainFrame;
import ui.view.component.panel.CollapsableListPanel;
import util.PendingAction;

public class CollapsableListUIController {

    private final MainFrame mainFrame;
    private final CollapsableListPanel view;
    private final UserListService service;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    public CollapsableListUIController(MainFrame mainFrame, CollapsableListPanel view) {
        this.mainFrame = mainFrame;
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
        String listName = view.showRenameListDialog();
        if (listName == null) return;

        String newListName = listName.strip();
        if (newListName.isEmpty() || newListName.equals(view.getUserList().getName())) return;

        PendingAction action = () -> service.renameList(view.getUserList(), newListName);
        try {
            action.execute();
        } catch (SessionExpiredException e) {
            EventBus.publish(new SessionExpiredEvent(action));
        } catch (Exception e) {
            ErrorHandler.showError(mainFrame, e);
        }
    }

    private void onDeleteButton() {
        if (!view.showDeleteListDialog()) {
            return;
        }

        PendingAction action = () -> service.deleteList(view.getUserList());
        try {
            action.execute();
        } catch (SessionExpiredException e) {
            EventBus.publish(new SessionExpiredEvent(action));
        } catch (Exception e) {
            ErrorHandler.showError(mainFrame, e);
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
