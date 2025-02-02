package view.component;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class SearchFeaturePanel extends JPanel {

    static private SearchFeaturePanel panel;

    private SearchFeaturePanel(){
        super(new MigLayout(
                "ins 20",
                "[fill][fill]",
                "[]"
        ));
        init();
    }

    private void init() {
        setBackground(new Color(224, 224,224));
        setBorder(new LineBorder(Color.BLACK, 1, false));

        //Components

        MySearchTextField txfCentralSearch = new MySearchTextField();
        txfCentralSearch.setBorder(BorderFactory.createCompoundBorder(
                txfCentralSearch.getBorder(),
                BorderFactory.createEmptyBorder(0, 20, 0, 0)
        ));

        JButton btnCentralSearch = new JButton("Search");

        //Listeners

        //Adds

        add(txfCentralSearch, "push, sg group1");
        add(btnCentralSearch, "sg group1");

    }

    public static SearchFeaturePanel getInstance(){
        if (panel == null)
            panel = new SearchFeaturePanel();

        return panel;
    }
}
