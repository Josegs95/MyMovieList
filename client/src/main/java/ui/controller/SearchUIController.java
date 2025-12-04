package ui.controller;

import dto.MultimediaDetailDTO;
import dto.MultimediaSummaryDTO;
import service.SearchService;
import ui.event.HideDetailsEvent;
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

        EventBus.subscribe(HideDetailsEvent.class, view::showResultPanel);
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
            List<MultimediaSummaryDTO> elementList = service.searchByName(text);

            if (elementList == null || elementList.isEmpty()) {
                JOptionPane.showMessageDialog(view,"No se ha encontrado resultados",
                        "No hay resultados", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            view.addResultPanel(elementList, this::onItemClicked);
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
}
