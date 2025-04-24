package view.component.panel;

import controller.ApiController;
import controller.SearchController;
import lib.ScrollablePanel;
import lib.StretchIcon;
import model.Movie;
import model.Multimedia;
import net.miginfocom.swing.MigLayout;
import view.component.MySearchTextField;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

public class SearchPanel extends JPanel {

    private static SearchPanel instance;

    private JPanel pnlResultList;

    private ScrollablePanel pnlInnerResultList;
    private JScrollPane scrollPaneResult;
    private MySearchTextField txfCentralSearch;

    private SearchPanel() {
        super(new MigLayout(
                "ins 0, fill",
                "[fill]",
                "[fill]"
        ));

        init();
    }

    private void init() {
        setBorder(new LineBorder(Color.BLACK, 1, false));

        //Components

        pnlResultList = new JPanel(new MigLayout(
                "ins 20, fillx, aligny 50%, flowy",
                "[fill]",
                "[fill]20[fill]"
        ));
        pnlResultList.setBackground(new Color(224, 224, 224));

        JPanel pnlSearch = new JPanel(new MigLayout(
                "",
                "[fill]",
                "[]"
        ));
        pnlSearch.setOpaque(false);
        pnlInnerResultList = new ScrollablePanel(new MigLayout(
                "ins 0, fill, flowy",
                "[fill]",
                "[fill, 100!]0[fill, 100!]"
        ));
        pnlInnerResultList.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        pnlInnerResultList.setBorder(new LineBorder(Color.BLACK, 1, false));

        scrollPaneResult = new JScrollPane(pnlInnerResultList);
        scrollPaneResult.getVerticalScrollBar().setUnitIncrement(20);

        txfCentralSearch = new MySearchTextField();
        txfCentralSearch.setBorder(BorderFactory.createCompoundBorder(
                txfCentralSearch.getBorder(),
                BorderFactory.createEmptyBorder(0, 20, 0, 0)
        ));

        JButton btnSearch = new JButton("Search");

        //Listeners

        btnSearch.addActionListener(_ -> {
            if (txfCentralSearch.getText().isEmpty())
                return;
            String searchString = txfCentralSearch.getText().strip();
            List<Multimedia> multiList = SearchController.searchMultimediaByKeyword(searchString);
            if (multiList.isEmpty())
                JOptionPane.showMessageDialog(SearchPanel.this.getParent(),
                        "No se ha encontrado resultados",
                        "No hay resultados",
                        JOptionPane.INFORMATION_MESSAGE);
            else
                addResultPanel(multiList);
        });

        txfCentralSearch.addActionListener(_ -> btnSearch.doClick());

        //Adds

        pnlSearch.add(txfCentralSearch, "push, sg group1");
        pnlSearch.add(btnSearch, "sg group1");

        pnlResultList.add(pnlSearch);

        add(pnlResultList);
    }

    public static SearchPanel getInstance() {
        if (instance == null)
            instance = new SearchPanel();

        return instance;
    }

    public void deleteDetailPanel() {
        remove(0);
        add(pnlResultList);
        txfCentralSearch.requestFocusInWindow();

        revalidate();
        repaint();
    }

    public void updateDetailPanel() {
        if (getComponent(0) instanceof DetailMultimediaPanel) {
            ((DetailMultimediaPanel) getComponent(0)).updatePage();
        }
    }

    private void addResultPanel(List<Multimedia> multiList) {
        String baseURLForPosters = ApiController.getBaseURLForPosters(false);
        if (!multiList.isEmpty())
            pnlInnerResultList.removeAll();
        try {
            for (Multimedia multi : multiList) {
                JPanel panel = createListItem(multi, baseURLForPosters);
                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println("Clickó en el panel");
                        showDetailPanel(new DetailMultimediaPanel(
                                SearchPanel.this, multi));
                    }
                });
                pnlInnerResultList.add(panel);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (getComponentZOrder(scrollPaneResult) == -1)
            pnlResultList.add(scrollPaneResult, "push");

        revalidate();
        repaint();
    }

    private void showDetailPanel(DetailMultimediaPanel panel) {
        remove(pnlResultList);
        add(panel);

        revalidate();
        repaint();
    }

    private JPanel createListItem(Multimedia multi, String baseURLForPosters)
            throws MalformedURLException {
        JPanel panel = new JPanel(new MigLayout(
                "fill",
                "[fill, 15%][fill, 70%][fill, 15%]",
                "[fill]"
        ));

        Color backgroundColor;
        if (multi instanceof Movie)
            backgroundColor = new Color(250, 219, 111);
        else
            backgroundColor = new Color(132, 182, 244);
        panel.setBackground(backgroundColor);

        JLabel lblPoster;
        if (multi.getPosterUrl() == null)
            lblPoster = new JLabel("No Image");
        else {
            StretchIcon multiPoster = new StretchIcon(
                    URI.create(baseURLForPosters + multi.getPosterUrl()).toURL(),
                    true);
            lblPoster = new JLabel(multiPoster);
        }
        JLabel lblTitle = new JLabel("<html>" + multi.getTitle() + "</html>");
        JLabel lblScore = new JLabel(multi.getScore());
        lblScore.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(lblPoster);
        panel.add(lblTitle);
        panel.add(lblScore);

        return panel;
    }

}
