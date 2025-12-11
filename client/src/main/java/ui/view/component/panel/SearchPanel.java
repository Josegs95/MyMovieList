package ui.view.component.panel;

import lib.ScrollablePanel;
import model.dto.MultimediaDetailDTO;
import model.dto.MultimediaSummaryDTO;
import net.miginfocom.swing.MigLayout;
import ui.controller.DetailUIController;
import ui.view.MainFrame;
import ui.view.component.MySearchTextField;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class SearchPanel extends JPanel{

    private final MainFrame mainFrame;

    private MySearchTextField txtSearch;

    private final CardLayout cards = new CardLayout();
    private JPanel pnlGeneral;
    private ScrollablePanel pnlResults;
    private JScrollPane scrollPaneResult;
    private DetailPanel detailPanel;
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
        for (MultimediaSummaryDTO multimedia : multiList) {
            pnlResults.add(new SearchItemPanel(multimedia, consumer));
        }

        // Opcional. Es para esperar a que las imágenes de los elementos carguen.
        // Thread.sleep(500);

        if (getComponentZOrder(scrollPaneResult) == -1) {
            pnlGeneral.add(scrollPaneResult, "push");
        }

        revalidate();
        repaint();
    }

    public void showResultPanel(DetailPanel closingDetailPanel) {
        if (closingDetailPanel.equals(detailPanel)) {
            detailPanel = null;
            cards.show(this, "RESULTS");
            txtSearch.requestFocusInWindow();
        }
    }

    public void showDetailPanel(MultimediaDetailDTO detailDTO, MultimediaSummaryDTO summaryDTO) {
        detailPanel = new DetailPanel(mainFrame, detailDTO, summaryDTO);
        new DetailUIController(mainFrame, detailPanel, null);

        add(detailPanel, "DETAILS");
        cards.show(this, "DETAILS");
    }

    public void showNoResultsDialog() {
        JOptionPane.showMessageDialog(mainFrame,"No se ha encontrado resultados","Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public MySearchTextField getTxtSearch() {
        return txtSearch;
    }

    public JButton getBtnSearch() {
        return btnSearch;
    }
}
