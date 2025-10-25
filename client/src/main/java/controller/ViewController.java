package controller;

import event.Event;
import event.EventListener;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ViewController {

    private static ViewController instance;
    private final Map<String, JPanel> viewDict;

    private ViewController() {
        viewDict = new HashMap<>();
    }

    public static synchronized ViewController getInstance() {
        if (instance == null) {
            instance = new ViewController();
        }

        return instance;
    }

    public void registerView(String name, JPanel panel) {
        viewDict.put(name, panel);
    }

    public void notifyView(String viewName, Event event) {
        JPanel view = viewDict.get(viewName);
        if (view instanceof EventListener listener) {
            listener.onEvent(event);
        }
    }
}
