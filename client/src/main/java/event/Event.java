package event;

import java.util.Map;

public record Event(EventType type, Map<String, Object> data) {}
