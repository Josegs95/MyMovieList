package view.component;

import controller.SearchController;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class SearchPanel extends JPanel {

    static private SearchPanel panel;

    private SearchController controller;

    private SearchPanel() {
        super(new MigLayout(
                "ins 20",
                "[fill][fill]",
                "[]"
        ));

        init();
    }

    private void init() {
        setBackground(new Color(224, 224, 224));
        setBorder(new LineBorder(Color.BLACK, 1, false));

        //Components

        MySearchTextField txfCentralSearch = new MySearchTextField();
        txfCentralSearch.setBorder(BorderFactory.createCompoundBorder(
                txfCentralSearch.getBorder(),
                BorderFactory.createEmptyBorder(0, 20, 0, 0)
        ));

        JButton btnCentralSearch = new JButton("Search");

        //Listeners

        btnCentralSearch.addActionListener(e -> {
            String userSearch = txfCentralSearch.getText();
            controller.searchMultimedia(userSearch);
        });

        //Adds

        add(txfCentralSearch, "push, sg group1");
        add(btnCentralSearch, "sg group1");

    }

    public static SearchPanel getInstance() {
        if (panel == null)
            panel = new SearchPanel();

        return panel;
    }

    public void setController(SearchController controller) {
        this.controller = controller;
    }
}
