package ui.controller;

import model.dto.MultimediaSummaryDTO;
import service.SearchService;
import ui.event.DetailApiEvent;
import ui.event.HideDetailsEvent;
import ui.event.SearchApiEvent;
import ui.util.ErrorHandler;
import ui.util.EventBus;
import ui.view.component.panel.SearchPanel;

import javax.swing.*;
import java.util.List;

public class SearchUIController {

    private final SearchPanel view;
    private final SearchService service;

    public SearchUIController(SearchPanel view, SearchService service) {
        this.view = view;
        this.service = service;

        EventBus.subscribe(HideDetailsEvent.class, this::onHideDetailPanelEvent);
        EventBus.subscribe(SearchApiEvent.class, this::onSearchApiEvent);
        EventBus.subscribe(DetailApiEvent.class, this::onDetailApiEvent);

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
            service.searchByName(text);
        } catch (Exception e) {
            ErrorHandler.showError(view, e);
        }

    }

    private void onItemClicked(MultimediaSummaryDTO summaryDTO) {
        try {
            service.getMultimediaDetail(summaryDTO);
        } catch(Exception e) {
            ErrorHandler.showError(view, e);
        }
    }

    private void onHideDetailPanelEvent(HideDetailsEvent event) {
        view.showResultPanel();
    }

    private void onSearchApiEvent(SearchApiEvent event) {
        List<MultimediaSummaryDTO> elementList = event.multimediaList();
        if (elementList == null || elementList.isEmpty()) {
            JOptionPane.showMessageDialog(view,"No se ha encontrado resultados",
                    "No hay resultados", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        view.addResultPanel(elementList, this::onItemClicked);
    }

    private void onDetailApiEvent(DetailApiEvent event) {
        view.showDetailPanel(event.multimediaDetail(), event.multimediaSummary());
    }
}
