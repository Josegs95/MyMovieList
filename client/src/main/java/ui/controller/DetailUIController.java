package ui.controller;

import dto.MultimediaSummaryDTO;
import service.UserListService;
import ui.event.HideDetailsEvent;
import ui.event.ListItemAddedEvent;
import ui.event.ListItemDeletedEvent;
import ui.util.CompositeSubscription;
import ui.util.EventBus;
import ui.view.MainFrame;
import ui.view.component.dialog.ConfigureMultimediaDialog;
import ui.view.component.dialog.RemoveMultimediaDialog;
import ui.view.component.panel.DetailPanel;

import javax.swing.*;

public class DetailUIController {

    private final MainFrame mainFrame;
    private final DetailPanel view;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    public DetailUIController(MainFrame mainFrame, DetailPanel view) {
        this.mainFrame = mainFrame;
        this.view = view;

        subscriptions.add(EventBus.subscribe(ListItemAddedEvent.class, this::onListItemCreated));
        subscriptions.add(EventBus.subscribe(ListItemDeletedEvent.class, this::onListItemDeleted));
        subscriptions.add(EventBus.subscribe(HideDetailsEvent.class, this::onHideDetailPanel));

        initListeners();
    }

    private void initListeners() {
        view.getBtnBack().addActionListener(_ -> onBackButton());
        view.getBtnAddToList().addActionListener(_ -> onAddToListButton());
        view.getBtnRemoveFromList().addActionListener(_ -> onRemoveButton());
    }

    private void onAddToListButton() {
        MultimediaSummaryDTO multimedia = view.getSummaryDTO();

        ConfigureMultimediaDialog dialog = new ConfigureMultimediaDialog(mainFrame, multimedia);
        new ConfigureDialogUIController(dialog, multimedia, new UserListService());
        dialog.setVisible(true);
    }

    private void onRemoveButton() {
        MultimediaSummaryDTO multimedia = view.getSummaryDTO();

        RemoveMultimediaDialog dialog = new RemoveMultimediaDialog(mainFrame, multimedia);
        new RemoveDialogUIController(dialog, new UserListService());
        dialog.setVisible(true);
    }

    private void onBackButton() {
        EventBus.publish(new HideDetailsEvent());
    }

    private void onListItemCreated(ListItemAddedEvent event) {
        System.out.println("Entra y se muestra el mensaje");
        String message = String.format(
                "\"%s\" añadido a la lista \"%s\" exitosamente.",
                event.listItemDTO().getMultimedia().getTitle(),
                event.userListDTO().name());
        JOptionPane.showMessageDialog(view, message,"Información", JOptionPane.INFORMATION_MESSAGE);
        view.checkButtonAvailability();
    }

    private void onListItemDeleted(ListItemDeletedEvent event) {
        view.checkButtonAvailability();
    }

    private void onHideDetailPanel(HideDetailsEvent event) {
        subscriptions.unsubscribe();
    }
}
