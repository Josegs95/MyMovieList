package ui.view.component.panel;

import dto.MultimediaDetailDTO;
import dto.MultimediaSummaryDTO;
import event.Event;
import event.EventListener;
import lib.ScrollablePanel;
import net.miginfocom.swing.MigLayout;
import ui.controller.DetailUIController;
import ui.event.HideDetailsEvent;
import ui.view.MainFrame;
import ui.view.component.MySearchTextField;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class SearchPanel extends JPanel implements EventListener {

    private final MainFrame mainFrame;

    private MySearchTextField txtSearch;

    private final CardLayout cards = new CardLayout();
    private JPanel pnlGeneral;
    private ScrollablePanel pnlResults;
    private JScrollPane scrollPaneResult;
    private JButton btnSearch;

    public SearchPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        createUI();
    }

    private void createUI() {
        setLayout(cards);
        setBorder(new LineBorder(Color.BLACK, 1, false));

        pnlGeneral = new JPanel(new MigLayout(
                "ins 20, fillx, aligny 50%, flowy",
                "[fill]",
                "[fill]20[fill]"
        ));
        pnlGeneral.setBackground(new Color(224, 224, 224));

        add(pnlGeneral, "RESULTS");

        JPanel pnlSearch = new JPanel(new MigLayout(
                "",
                "[fill]",
                "[]"
        ));
        pnlSearch.setOpaque(false);

        pnlGeneral.add(pnlSearch);

        pnlResults = new ScrollablePanel(new MigLayout(
                "ins 0, fill, flowy",
                "[fill]",
                "[fill, 100!]0[fill, 100!]"
        ));
        pnlResults.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        pnlResults.setBorder(LineBorder.createBlackLineBorder());

        scrollPaneResult = new JScrollPane(pnlResults);
        scrollPaneResult.getVerticalScrollBar().setUnitIncrement(20);

        txtSearch = new MySearchTextField();
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                txtSearch.getBorder(),
                BorderFactory.createEmptyBorder(0, 20, 0, 0)
        ));

        btnSearch = new JButton("Search");

        pnlSearch.add(txtSearch, "push, sg group1");
        pnlSearch.add(btnSearch, "sg group1");
    }

    public void addResultPanel(List<MultimediaSummaryDTO> multiList, Consumer<MultimediaSummaryDTO> consumer) {
        pnlResults.removeAll();
        try {
            for (MultimediaSummaryDTO multimedia : multiList) {
                pnlResults.add(new MultimediaItemPanel(multimedia, consumer));
            }

            // Se puede borrar. Está para esperar a que las imágenes carguen un poco
//            Thread.sleep(500);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (getComponentZOrder(scrollPaneResult) == -1) {
            pnlGeneral.add(scrollPaneResult, "push");
        }

        revalidate();
        repaint();
    }

    public void showResultPanel(HideDetailsEvent event) {
        cards.show(this, "RESULTS");
        txtSearch.requestFocusInWindow();
    }

    public void showDetailPanel(MultimediaDetailDTO detailDTO, MultimediaSummaryDTO summaryDTO) {
        DetailPanel detailPanel = new DetailPanel(mainFrame, detailDTO, summaryDTO);
        new DetailUIController(mainFrame, detailPanel);
        add(detailPanel, "DETAILS");
        cards.show(this, "DETAILS");
    }

    public MySearchTextField getTxtSearch() {
        return txtSearch;
    }

    public JButton getBtnSearch() {
        return btnSearch;
    }

    // * BORRAR *
    @Override
    public void onEvent(Event event) {
//        Map<String, Object> data = event.data();
//        switch (event.type()) {
//            case SHOW_DETAIL_PANEL -> {
//                DetailMultimediaPanel panel = (DetailMultimediaPanel) data.get("detailPanel");
//
//                showDetailPanel(panel);
//            }
//            case HIDE_DETAIL_PANEL -> showDetailPanel();
//        }
    }
    //


}
