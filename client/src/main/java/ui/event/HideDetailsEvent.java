package ui.event;

import ui.view.component.panel.DetailPanel;

public record HideDetailsEvent(DetailPanel detailPanel) implements Event {
}
