package ui.controller;

import model.dto.MultimediaDetailDTO;
import model.dto.MultimediaSummaryDTO;
import service.SearchService;
import ui.event.HideDetailsEvent;
import ui.util.ErrorHandler;
import ui.util.EventBus;
import ui.view.component.panel.SearchPanel;

import java.util.List;

public class SearchUIController {

    private final SearchPanel view;
    private final SearchService service;

    public SearchUIController(SearchPanel view, SearchService service) {
        this.view = view;
        this.service = service;

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

        try {
            List<MultimediaSummaryDTO> resultList = service.searchByName(text);
            if (resultList == null || resultList.isEmpty()) {
                view.showNoResultsDialog();
                return;
            }

            view.addResultPanel(resultList, this::onItemClicked);
        } catch (Exception e) {
            ErrorHandler.showError(view, e);
        }
    }

    private void onItemClicked(MultimediaSummaryDTO summaryDTO) {
        try {
            MultimediaDetailDTO detailDTO = service.getMultimediaDetail(summaryDTO);
            view.showDetailPanel(detailDTO, summaryDTO);
        } catch(Exception e) {
            ErrorHandler.showError(view, e);
        }
    }

    private void onHideDetailPanelEvent(HideDetailsEvent event) {
        view.showResultPanel(event.detailPanel());
    }
}
