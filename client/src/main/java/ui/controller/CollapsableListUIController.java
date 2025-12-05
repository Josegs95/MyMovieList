package ui.controller;

import service.UserListService;
import ui.util.ErrorHandler;
import ui.view.component.panel.CollapsableListPanel;

public class CollapsableListUIController {

    private final CollapsableListPanel view;
    private final UserListService service;

    public CollapsableListUIController(CollapsableListPanel view, UserListService service) {
        this.view = view;
        this.service = service;

        initListeners();
    }

    private void initListeners() {
        view.getBtnRename().addActionListener(_ -> onRenameButton());
        view.getBtnDelete().addActionListener(_ -> onDeleteButton());
    }

    private void onRenameButton() {
        String newListName = view.showRenameListDialog();
        if (newListName.isEmpty() || newListName.equals(view.getUserList().getName())) {
            return;
        }

        try {
            service.renameList(view.getUserList(), newListName);
            view.updateListNameLabel();
        } catch(Exception e) {
            ErrorHandler.showError(view, e);
        }
    }

    private void onDeleteButton() {

    }
}
