package ui.controller;

import ui.event.ListItemAddedEvent;
import ui.util.EventBus;
import ui.view.component.panel.CollapsableListPanel;

public class CollapsableListUIController {

    private final CollapsableListPanel view;

    public CollapsableListUIController(CollapsableListPanel view) {
        this.view = view;

        initListeners();
    }

    private void initListeners() {
    }


}
