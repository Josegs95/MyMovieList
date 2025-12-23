package ui.event;

import util.PendingAction;

public record SessionExpiredEvent(PendingAction action) implements Event {}
