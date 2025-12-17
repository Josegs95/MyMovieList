package ui.controller;

import context.SessionContext;
import exception.SessionExpiredException;
import model.dto.MultimediaDetailDTO;
import model.dto.MultimediaListItemDTO;
import service.SearchService;
import service.UserListService;
import ui.event.DetailPanelOnListsEvent;
import ui.event.SessionExpiredEvent;
import ui.util.ErrorHandler;
import ui.util.EventBus;
import ui.view.MainFrame;
import ui.view.component.dialog.ConfigureMultimediaDialog;
import ui.view.component.panel.CollapsableListPanel;
import util.PendingAction;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ListItemUIController {

    private final MainFrame mainFrame;
    private final CollapsableListPanel.ListItemPanel view;
    private final UserListService listService;
    private final SearchService searchService;

    public ListItemUIController(MainFrame mainFrame, CollapsableListPanel.ListItemPanel view) {
        this.mainFrame = mainFrame;
        this.view = view;
        this.listService = SessionContext.getInstance().getUserListService();
        this.searchService = SessionContext.getInstance().getSearchService();

        initListeners();
    }

    private void initListeners() {
        view.getBtnConfig().addActionListener(_ -> onBtnConfig());
        view.getBtnDelete().addActionListener(_ -> onBtnDelete());
        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onPanelClicked();
            }
        });
    }

    private void onBtnConfig() {
        PendingAction action = () -> {
            MultimediaListItemDTO item = view.getMultimediaItem();
            ConfigureMultimediaDialog dialog = new ConfigureMultimediaDialog(mainFrame, item);
            new ConfigureDialogUIController(dialog, listService::modifyItemList);
            dialog.setVisible(true);
        };
        try {
            action.execute();
        } catch (SessionExpiredException e) {
            EventBus.publish(new SessionExpiredEvent(action));
        } catch (Exception e) {
            ErrorHandler.showError(mainFrame, e);
        }
    }

    private void onBtnDelete() {
        if(!view.showDeleteDialog()) return;

        try {
            MultimediaListItemDTO item = view.getMultimediaItem();
            listService.deleteItemFromList(view.getUserList(), item);
        } catch (Exception e) {
            ErrorHandler.showError(mainFrame, e);
        }
    }

    private void onPanelClicked() {
        try {
            MultimediaListItemDTO listItem = view.getMultimediaItem();
            MultimediaDetailDTO detailDTO = searchService.getMultimediaDetail(listItem.getMultimedia());
            EventBus.publish(new DetailPanelOnListsEvent(detailDTO, listItem.getMultimedia(), view.getUserList()));
        } catch (Exception e){
            ErrorHandler.showError(mainFrame, e);
        }
    }
}
