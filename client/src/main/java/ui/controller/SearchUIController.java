package ui.controller;

import context.SessionContext;
import exception.SessionExpiredException;
import model.dto.MultimediaDetailDTO;
import model.dto.MultimediaSummaryDTO;
import service.SearchService;
import ui.event.HideDetailsEvent;
import ui.event.SessionExpiredEvent;
import ui.util.ErrorHandler;
import ui.util.EventBus;
import ui.view.MainFrame;
import ui.view.component.panel.SearchPanel;
import util.PendingAction;

import java.util.List;

public class SearchUIController {

    private final MainFrame mainFrame;
    private final SearchPanel view;
    private final SearchService service;

    public SearchUIController(MainFrame mainFrame, SearchPanel view) {
        this.mainFrame = mainFrame;
        this.view = view;
        this.service = SessionContext.getInstance().getSearchService();

        EventBus.subscribe(HideDetailsEvent.class, this::onHideDetailPanelEvent);

        initListeners();
    }

    private void initListeners() {
        view.getBtnSearch().addActionListener(_ -> onSearch());
        view.getTxtSearch().addActionListener(_ -> onSearch());
    }

    private void onSearch() {
        String text = view.getTxtSearch().getText().strip();
        if (text.isEmpty()) {
            return;
        }

        PendingAction action = () -> {
            List<MultimediaSummaryDTO> resultList = service.searchByName(text);
            if (resultList == null || resultList.isEmpty()) {
                view.showNoResultsDialog();
                return;
            }

            view.addResultPanel(resultList, this::onItemClicked);
        };

        try {
            action.execute();
        } catch (SessionExpiredException e) {
            EventBus.publish(new SessionExpiredEvent(action));
        } catch (Exception e) {
            ErrorHandler.showError(mainFrame, e);
        }
    }

    private void onItemClicked(MultimediaSummaryDTO summaryDTO) {
        PendingAction action = () -> {
            MultimediaDetailDTO detailDTO = service.getMultimediaDetail(summaryDTO);
            view.showDetailPanel(detailDTO, summaryDTO);
        };

        try {
            action.execute();
        } catch (SessionExpiredException e) {
            EventBus.publish(new SessionExpiredEvent(action));
        } catch (Exception e) {
            ErrorHandler.showError(mainFrame, e);
        }
    }

    private void onHideDetailPanelEvent(HideDetailsEvent event) {
        view.showResultPanel(event.detailPanel());
    }
}
