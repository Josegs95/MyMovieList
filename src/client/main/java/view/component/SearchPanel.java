package view.component;

import controller.APIController;
import controller.SearchController;
import lib.ScrollablePanel;
import lib.StretchIcon;
import model.Movie;
import model.Multimedia;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class SearchPanel extends JPanel {

    static private SearchPanel panel;
    private ScrollablePanel pnlResults;
    private JButton btnSearch;

    private SearchController controller;

    private SearchPanel() {
        super(new MigLayout(
                "ins 20, fillx, aligny 50%, flowy",
                "[fill]",
                "[fill]20[fill]"
        ));

        init();
    }

    private void init() {
        setBackground(new Color(224, 224, 224));
        setBorder(new LineBorder(Color.BLACK, 1, false));

        //Components

        JPanel pnlSearch = new JPanel(new MigLayout(
                "",
                "[fill]",
                "[]"
        ));
        pnlSearch.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, false));
        pnlResults = new ScrollablePanel(new MigLayout(
                "ins 0, fill, flowy",
                "[fill]",
                "[fill, 100!]0[fill, 100!]"
        ));
        pnlResults.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        pnlResults.setBorder(new LineBorder(Color.BLACK, 1, false));

        MySearchTextField txfCentralSearch = new MySearchTextField();
        txfCentralSearch.setBorder(BorderFactory.createCompoundBorder(
                txfCentralSearch.getBorder(),
                BorderFactory.createEmptyBorder(0, 20, 0, 0)
        ));

        btnSearch = new JButton("Search");

        //Listeners

        btnSearch.addActionListener(e -> {
            if (txfCentralSearch.getText().isEmpty())
                return;
            List<Multimedia> multiList = controller.searchMultimedia(txfCentralSearch.getText().strip());
            addResultPanel(multiList);
        });

        //Adds

        pnlSearch.add(txfCentralSearch, "push, sg group1");
        pnlSearch.add(btnSearch, "sg group1");

        add(pnlSearch);
        //add(scrollPane, "pushy");
        //add(pnlResults, "push");
    }

    public static SearchPanel getInstance() {
        if (panel == null)
            panel = new SearchPanel();

        return panel;
    }

    public void setController(SearchController controller) {
        this.controller = controller;
    }

    public JButton getDefaultButton() {
        return btnSearch;
    }

    private void addResultPanel(List<Multimedia> multiList) {
        String baseURLForPosters = APIController.getBaseURLForPosters();
        try {
            for (Multimedia multi : multiList) {
                JPanel panel = new JPanel(new MigLayout(
                        "fill",
                        "[fill, 15%][fill, 70%][fill, 15%]",
                        "[fill]"
                ));

                Color backgroundColor;
                if (multi instanceof Movie)
                    backgroundColor = new Color(250, 219, 111);
                else
                    backgroundColor = new Color(255, 204, 203);
                panel.setBackground(backgroundColor);

                JLabel lblPoster;
                if (multi.getPosterURL() == null)
                    lblPoster = new JLabel("No Image");
                else {

                    StretchIcon multiPoster = new StretchIcon(
                            URI.create(baseURLForPosters + multi.getPosterURL()).toURL(),
                            true);
                    lblPoster = new JLabel(multiPoster);
                }
                JLabel lblTitle = new JLabel("<html>" + multi.getTitle() + "</html>");
                JLabel lblScore = new JLabel(multi.getScore());
                lblScore.setHorizontalAlignment(SwingConstants.CENTER);

                panel.add(lblPoster);
                panel.add(lblTitle);
                panel.add(lblScore);
                pnlResults.add(panel);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JScrollPane scrollPane = new JScrollPane(pnlResults);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        if (getComponentCount() < 2)
            add(scrollPane, "push");

        revalidate();
        repaint();
    }
}
