package ui.util;

import ui.event.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("rawtypes")
public class EventBus {

    private static final Map<Class<? extends Event>, List<Consumer>> SUBSCRIBERS = new HashMap<>();

    private EventBus(){}

    public static <T extends Event> Subscription subscribe(Class<T> eventType, Consumer<T> listener) {
        List<Consumer> listeners = SUBSCRIBERS.computeIfAbsent(eventType, _ -> new ArrayList<>());
        listeners.add(listener);

        return () -> {
            List<Consumer> currentListeners = SUBSCRIBERS.get(eventType);
            if (currentListeners != null) {
                currentListeners.remove(listener);
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static void publish(Event event) {
        Class<?> eventType = event.getClass();
        List<Consumer> listeners = SUBSCRIBERS.get(eventType);

        if (listeners == null || listeners.isEmpty()) {
            return;
        }

        List<Consumer> copyOfListeners = new ArrayList<>(listeners);
        copyOfListeners.forEach(consumer -> consumer.accept(event));
    }
}
