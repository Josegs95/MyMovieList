package ui.controller;

import model.dto.MultimediaListItemDTO;
import service.UserListService;
import ui.util.ErrorHandler;
import ui.view.MainFrame;
import ui.view.component.dialog.ConfigureMultimediaDialog;
import ui.view.component.panel.CollapsableListPanel;

public class ListItemUIController {

    private final MainFrame mainFrame;
    private final CollapsableListPanel.ListItemPanel view;
    private final UserListService service;

    public ListItemUIController(MainFrame mainFrame, CollapsableListPanel.ListItemPanel view, UserListService service) {
        this.mainFrame = mainFrame;
        this.view = view;
        this.service = service;

        initListeners();
    }

    private void initListeners() {
        view.getBtnConfig().addActionListener(_ -> onBtnConfig());
        view.getBtnDelete().addActionListener(_ -> onBtnDelete());
    }

    private void onBtnConfig() {
        try {
            MultimediaListItemDTO item = view.getMultimediaItem();
            ConfigureMultimediaDialog dialog = new ConfigureMultimediaDialog(mainFrame, item);
            new ConfigureDialogUIController(dialog, service::modifyItemList);
            dialog.setVisible(true);

        } catch (Exception e) {
            ErrorHandler.showError(mainFrame, e);
        }
    }

    private void onBtnDelete() {
        if(!view.showDeleteDialog()) {
            return;
        }

        try {
            MultimediaListItemDTO item = view.getMultimediaItem();
            service.deleteItemFromList(view.getUserList(), item);
        } catch (Exception e) {
            ErrorHandler.showError(mainFrame, e);
        }
    }
}
