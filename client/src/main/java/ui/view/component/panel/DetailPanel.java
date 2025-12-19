package ui.view.component.panel;

import context.SessionContext;
import lib.ScrollablePanel;
import lib.StretchIcon;
import model.dto.*;
import net.miginfocom.swing.MigLayout;
import ui.view.MainFrame;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

public class DetailPanel extends JPanel {

    private static final Color MOVIE_COLOR = new Color(250, 219, 111);
    private static final Color SERIES_COLOR = new Color(132, 182, 244);

    private final MainFrame mainFrame;
    private final UserDTO user;
    private final MultimediaDetailDTO detailDTO;
    private final MultimediaSummaryDTO summaryDTO;

    private JButton btnBack;
    private JButton btnAddToList;
    private JButton btnRemoveFromList;

    public DetailPanel(MainFrame mainFrame, MultimediaDetailDTO detailDTO, MultimediaSummaryDTO summaryDTO) {
        this.mainFrame = mainFrame;
        this.detailDTO = detailDTO;
        this.summaryDTO = summaryDTO;
        this.user = SessionContext.getInstance().getUser();

        createUI();
        checkButtonAvailability();
    }

    private void createUI() {
        setLayout(new MigLayout(
                "fill, ins 0",
                "[fill, 40%][fill, 60%]",
                "[fill]")
        );

        boolean isAMovie = detailDTO instanceof MovieDetailDTO;
        setBackground(isAMovie ? MOVIE_COLOR : SERIES_COLOR);

        //Poster
        JPanel pnlLateral = new JPanel(new MigLayout(
                "flowy, fill",
                "[fill]",
                "[fill, 60%]50[fill, 40%]"
        ));
        pnlLateral.setOpaque(false);

        StretchIcon iconPoster;
        try {
            iconPoster = new StretchIcon(
                    URI.create(detailDTO.getPosterPath()).toURL(),
                    true);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        JLabel lblPoster = new JLabel(iconPoster);
        lblPoster.setBorder(LineBorder.createBlackLineBorder());

        pnlLateral.add(lblPoster);

        //Panel Details

        JPanel pnlDetails = new JPanel(new MigLayout(
                "ins 5, fill",
                "[|||]",
                "[fill, 20%|fill, 10%|fill, 10%|fill, 40%|fill, 10%|fill, 10%]"
        ));
        pnlDetails.setOpaque(false);

        //Title & Score

        LocalDate releaseDate = detailDTO.getReleaseDate();

        String titleText = detailDTO.getTitle();
        if (releaseDate != null) {
            titleText = String.format("%s (%d)", titleText, releaseDate.getYear());
        }

        String title = String.format("<html><p style=\"text-align: center;\">%s</p></html>", titleText);
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        Font titleFont = lblTitle.getFont().deriveFont(Font.BOLD, 22);
        lblTitle.setFont(titleFont);
        lblTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        JLabel lblScore = new JLabel(detailDTO.getScore().toString(), SwingConstants.CENTER);
        lblScore.setFont(titleFont);

        //Release date & Country

        String releaseDateString = releaseDate != null ? releaseDate.toString() : "Desconocida";
        JLabel lblReleaseDate = new JLabel(String.format("Fecha de lanzamiento: %s", releaseDateString));

        JLabel lblCountry = new JLabel(String.format("País: %s", detailDTO.getCountry()), SwingConstants.CENTER);

        // Duration & Status

        String duration = isAMovie ? ((MovieDetailDTO) detailDTO).getDuration() : ((SeriesDetailDTO) detailDTO).getEpisodeDuration();
        JLabel lblDuration = new JLabel(String.format("Duración: %s", duration));

        String status = isAMovie ? "" : ((SeriesDetailDTO) detailDTO).getAiringStatus();
        JLabel lblStatus = new JLabel(String.format("Estado: %s", status), SwingConstants.CENTER);

        // Synopsis

        ScrollablePanel pnlSynopsis = new ScrollablePanel(new MigLayout(
                "fill",
                "[fill]",
                "[fill]"
        ));
        pnlSynopsis.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        pnlSynopsis.setOpaque(false);

        String synopsis = String.format("<html><p style=\"text-align: center\">Sinopsis: %s</p></html>", detailDTO.getOverview());
        JLabel lblSynopsis = new JLabel(synopsis, SwingConstants.CENTER);

        pnlSynopsis.add(lblSynopsis);

        JScrollPane scrollSynopsis = new JScrollPane(pnlSynopsis);
        scrollSynopsis.getViewport().setOpaque(false);
        scrollSynopsis.setOpaque(false);
        scrollSynopsis.setBorder(null);

        //Genre List

        String genres = String.join(", ", detailDTO.getGenreList());
        JLabel lblGenres = new JLabel(String.format("Géneros: %s", genres), SwingConstants.CENTER);

        //Episode & Season count

        JLabel lblEpisodeCount = null, lblSeasonCount = null;
        if (detailDTO instanceof SeriesDetailDTO serie) {
            int totalEpisodes = serie.getTotalEpisodes();
            int totalSeasons = serie.getTotalSeasons();
            ((SeriesSummaryDTO) summaryDTO).setTotalEpisodes(serie.getTotalEpisodes());
            lblEpisodeCount = new JLabel(String.format("# Episodios: %d", totalEpisodes), SwingConstants.CENTER);
            lblSeasonCount = new JLabel(String.format("# Temporadas: %d", totalSeasons), SwingConstants.CENTER);
        }

        //Buttons

        JPanel pnlButton = new JPanel(new MigLayout(
                "flowy, align 50% 50%",
                "[60%!, fill]",
                "[]10[]10[]"
        ));
        pnlButton.setOpaque(false);
        pnlButton.setBorder(LineBorder.createBlackLineBorder());

        btnBack = new JButton("Atrás");
        btnAddToList = new JButton("Añadir");
        btnRemoveFromList = new JButton("Borrar");

        btnBack.setFocusPainted(false);
        btnAddToList.setFocusPainted(false);
        btnRemoveFromList.setFocusPainted(false);

        pnlButton.add(btnBack, "sg buttons");
        pnlButton.add(btnAddToList, "sg buttons");
        pnlButton.add(btnRemoveFromList, "sg buttons");

        pnlLateral.add(pnlButton);

        //Adds

        pnlDetails.add(lblTitle, "growx 100, push");
        pnlDetails.add(lblScore, "alignx center, wrap");
        pnlDetails.add(lblReleaseDate, "growx 50");
        pnlDetails.add(lblCountry, "growx 50, wrap");
        pnlDetails.add(lblDuration, isAMovie ? "growx 50, wrap" : "growx 50");
        if (!isAMovie)
            pnlDetails.add(lblStatus, "growx 50, wrap");
        pnlDetails.add(scrollSynopsis, "spanx 4, growx 100, wrap");
        pnlDetails.add(lblGenres, "spanx 4, growx 100, wrap");
        if (!isAMovie) {
            pnlDetails.add(lblEpisodeCount, "growx 50");
            pnlDetails.add(lblSeasonCount, "growx 50, wrap");
        }

        add(pnlLateral);
        add(pnlDetails);
    }

    public void checkButtonAvailability() {
        List<UserListDTO> lists = user.getLists();
        if (!lists.isEmpty()) {
            btnAddToList.setEnabled(false);
            btnRemoveFromList.setEnabled(false);
        }

        long listsContainingItemCount = lists.stream()
                .filter(userListDTO -> userListDTO.getListItems().stream()
                        .anyMatch(item -> item.getMultimedia().equals(summaryDTO)))
                .count();
        int userListsCount = lists.size();

        boolean multimediaAlreadyExistsInAllLists = listsContainingItemCount == userListsCount;
        boolean multimediaAlreadyExistsInAnyList = listsContainingItemCount > 0;

        btnAddToList.setEnabled(!multimediaAlreadyExistsInAllLists);
        btnRemoveFromList.setEnabled(multimediaAlreadyExistsInAnyList);
    }

    public boolean showRemoveDialog(MultimediaSummaryDTO summaryDTO, UserListDTO userListDTO) {
        String message = String.format("¿Estás seguro/a de que quieres borrar \"%s\" de la lista \"%s\"?",
                summaryDTO.getTitle(),
                userListDTO.getName());
        int result = JOptionPane.showConfirmDialog(mainFrame, message, "Confirmación", JOptionPane.YES_NO_OPTION);

        return result == JOptionPane.YES_OPTION;
    }

    public void showAddedToListSuccessDialog(MultimediaSummaryDTO summaryDTO, UserListDTO userListDTO) {
        String message = String.format(
                "\"%s\" añadido a la lista \"%s\" exitosamente.",
                summaryDTO.getTitle(),
                userListDTO.getName());
        JOptionPane.showMessageDialog(mainFrame, message,"Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public MultimediaSummaryDTO getSummaryDTO() {
        return summaryDTO;
    }

    public JButton getBtnBack() {
        return btnBack;
    }

    public JButton getBtnAddToList() {
        return btnAddToList;
    }

    public JButton getBtnRemoveFromList() {
        return btnRemoveFromList;
    }
}
