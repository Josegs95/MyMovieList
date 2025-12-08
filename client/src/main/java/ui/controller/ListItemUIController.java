package ui.controller;

import model.dto.MultimediaListItemDTO;
import service.UserListService;
import ui.util.ErrorHandler;
import ui.view.component.panel.CollapsableListPanel;

public class ListItemUIController {

    private final CollapsableListPanel.ListItemPanel view;
    private final UserListService service;

    public ListItemUIController(CollapsableListPanel.ListItemPanel view, UserListService service) {
        this.view = view;
        this.service = service;

        initListeners();
    }

    private void initListeners() {
        view.getBtnConfig().addActionListener(_ -> onBtnConfig());
        view.getBtnDelete().addActionListener(_ -> onBtnDelete());
    }

    private void onBtnConfig() {

    }

    private void onBtnDelete() {
        if(!view.showDeleteDialog()) {
            return;
        }

        try {
            MultimediaListItemDTO item = view.getMultimediaItem();
            service.deleteItemFromList(view.getUserList(), item);
        } catch (Exception e) {
            ErrorHandler.showError(view, e);
        }
    }
}
