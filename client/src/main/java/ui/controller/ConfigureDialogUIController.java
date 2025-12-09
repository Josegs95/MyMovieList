package ui.controller;

import model.dto.MultimediaListItemDTO;
import ui.util.ErrorHandler;
import ui.view.component.dialog.ConfigureMultimediaDialog;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

public class ConfigureDialogUIController {

    private final ConfigureMultimediaDialog view;
    private final Consumer<MultimediaListItemDTO> onAcceptConsumer;

    public ConfigureDialogUIController(ConfigureMultimediaDialog view, Consumer<MultimediaListItemDTO> onAcceptConsumer) {
        this.view = view;
        this.onAcceptConsumer = onAcceptConsumer;

        initListeners();
    }

    private void initListeners() {
        view.getBtnAccept().addActionListener(_ -> onAcceptButton());
        view.getBtnCancel().addActionListener(_ -> onClose());
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });
    }

    private void onAcceptButton() {
        if (!view.hasMultimediaChanges()) {
            view.dispose();
            return;
        }

        try {
            MultimediaListItemDTO listItem = view.getListItemResult();
            onAcceptConsumer.accept(listItem);

            view.dispose();
        } catch (Exception e) {
            ErrorHandler.showError(view, e);
        }
    }

    private void onClose() {
        view.cancelDialog();
    }
}
