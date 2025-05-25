package view.component.panel;

import controller.ApiController;
import controller.SearchController;
import event.Event;
import event.EventListener;
import lib.ScrollablePanel;
import lib.StretchIcon;
import model.Movie;
import model.Multimedia;
import net.miginfocom.swing.MigLayout;
import view.MainFrame;
import view.component.MySearchTextField;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SearchPanel extends JPanel implements EventListener {

    private final MainFrame mainFrame;

    private MySearchTextField txfCentralSearch;

    private JPanel pnlResultList;
    private ScrollablePanel pnlInnerResultList;
    private JScrollPane scrollPaneResult;
    private JButton btnSearch;

    public SearchPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        createUI();
        createListeners();
    }

    public void updateState() {
        if (getComponent(0) instanceof DetailMultimediaPanel detailPanel) {
            detailPanel.updatePage();
        }
    }

    private void createUI() {
        setLayout(new MigLayout(
                "ins 0, fill",
                "[fill]",
                "[fill]"
        ));
        setBorder(new LineBorder(Color.BLACK, 1, false));

        pnlResultList = new JPanel(new MigLayout(
                "ins 20, fillx, aligny 50%, flowy",
                "[fill]",
                "[fill]20[fill]"
        ));
        pnlResultList.setBackground(new Color(224, 224, 224));

        add(pnlResultList);

        JPanel pnlSearch = new JPanel(new MigLayout(
                "",
                "[fill]",
                "[]"
        ));
        pnlSearch.setOpaque(false);

        pnlResultList.add(pnlSearch);

        pnlInnerResultList = new ScrollablePanel(new MigLayout(
                "ins 0, fill, flowy",
                "[fill]",
                "[fill, 100!]0[fill, 100!]"
        ));
        pnlInnerResultList.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        pnlInnerResultList.setBorder(LineBorder.createBlackLineBorder());

        scrollPaneResult = new JScrollPane(pnlInnerResultList);
        scrollPaneResult.getVerticalScrollBar().setUnitIncrement(20);

        txfCentralSearch = new MySearchTextField();
        txfCentralSearch.setBorder(BorderFactory.createCompoundBorder(
                txfCentralSearch.getBorder(),
                BorderFactory.createEmptyBorder(0, 20, 0, 0)
        ));

        btnSearch = new JButton("Search");

        pnlSearch.add(txfCentralSearch, "push, sg group1");
        pnlSearch.add(btnSearch, "sg group1");
    }

    private void createListeners() {
        btnSearch.addActionListener(_ -> {
            if (txfCentralSearch.getText().isEmpty())
                return;
            String searchString = txfCentralSearch.getText().strip();
            List<Multimedia> multiList = SearchController.searchMultimediaByKeyword(searchString);
            if (multiList.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame,"No se ha encontrado resultados",
                        "No hay resultados", JOptionPane.INFORMATION_MESSAGE);
            } else {
                addResultPanel(multiList);
            }
        });

        txfCentralSearch.addActionListener(_ -> btnSearch.doClick());
    }

    private void addResultPanel(List<Multimedia> multiList) {
        pnlInnerResultList.removeAll();
        try {
            for (Multimedia multimedia : multiList) {
                pnlInnerResultList.add(createListItem(multimedia));
            }

            // Se puede borrar. Está para esperar a que las imágenes carguen un poco
            Thread.sleep(500);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (getComponentZOrder(scrollPaneResult) == -1)
            pnlResultList.add(scrollPaneResult, "push");

        revalidate();
        repaint();
    }

    private JPanel createListItem(Multimedia multimedia) throws MalformedURLException {
        JPanel panel = new JPanel(new MigLayout(
                "fill",
                "[fill, 15%][fill, 70%][fill, 15%]",
                "[fill]"
        ));
        panel.setBorder(LineBorder.createGrayLineBorder());

        Color backgroundColor = multimedia instanceof Movie ?
                new Color(250, 219, 111) : new Color(132, 182, 244);
        panel.setBackground(backgroundColor);

        String baseURLForPosters = ApiController.getBaseURLForPosters(true);
        String posterUrlString = multimedia.getPosterUrl();
        JLabel lblPoster = new JLabel();
        if (posterUrlString == null) {
            lblPoster.setText("No Image");
        } else {
            URL posterUrl = URI.create(baseURLForPosters + posterUrlString).toURL();

            new ImageLoaderWorker(panel, lblPoster, posterUrl).execute();

            lblPoster.setText("Loading Image");
        }
        JLabel lblTitle = new JLabel(String.format("<html><p>%s</p></html>", multimedia.getTitle()));
        JLabel lblScore = new JLabel(multimedia.getScore(), SwingConstants.CENTER);

        panel.add(lblPoster);
        panel.add(lblTitle);
        panel.add(lblScore);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showDetailPanel(new DetailMultimediaPanel(SearchPanel.this, multimedia));
            }
        });

        return panel;
    }

    private void showDetailPanel(DetailMultimediaPanel detailPanel) {
        removeAll();
        add(detailPanel);

        revalidate();
        repaint();
    }

    private void removeDetailPanel() {
        removeAll();
        add(pnlResultList);
        txfCentralSearch.requestFocusInWindow();

        revalidate();
        repaint();
    }

    @Override
    public void onEvent(Event event) {
        Map<String, Object> data = event.data();
        switch (event.type()) {
            case SHOW_DETAIL_PANEL -> {
                DetailMultimediaPanel panel = (DetailMultimediaPanel) data.get("detailPanel");

                showDetailPanel(panel);
            }
            case HIDE_DETAIL_PANEL -> removeDetailPanel();
        }
    }

    private static class ImageLoaderWorker extends SwingWorker<StretchIcon, Void> {

        private final JPanel panel;
        private final JLabel lblPoster;

        private final URL posterUrl;

        public ImageLoaderWorker(JPanel panel, JLabel lblPoster, URL posterUrl) {
            this.panel = panel;
            this.lblPoster = lblPoster;
            this.posterUrl = posterUrl;
        }

        @Override
        protected StretchIcon doInBackground(){
            return new StretchIcon(posterUrl, true);
        }

        @Override
        protected void done() {
            try {
                lblPoster.setText(null);
                lblPoster.setIcon(get());
                panel.revalidate();
                panel.repaint();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
