package ui.controller;

import ui.view.MainFrame;
import ui.view.component.panel.SearchPanel;
import ui.view.component.panel.UserListPanel;

public class MainFrameUIController {

    private final MainFrame view;

    public MainFrameUIController(MainFrame view) {
        this.view = view;

        initListeners();
    }

    private void initListeners() {
        view.getBtnLateralSearch().addActionListener(_ -> onSearchLateralMenu());
        view.getBtnLateralLists().addActionListener(_ -> onListsLateralMenu());
    }

    private void onSearchLateralMenu() {
        view.changeCentralPanel(SearchPanel.class);
    }

    private void onListsLateralMenu() {
        view.changeCentralPanel(UserListPanel.class);
    }

//    private void onUserAuthenticated(UserAuthenticatedEvent event) {
//        view.setUser(event.user());
//        view.finishInit();
//        initListeners();
////        new Thread(new FetchUserLists(user)).start();
//    }
}
