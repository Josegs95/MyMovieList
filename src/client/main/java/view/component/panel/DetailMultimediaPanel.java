package view.component.panel;

import controller.ApiController;
import controller.SearchController;
import controller.UserListController;
import lib.ScrollablePanel;
import lib.StretchIcon;
import model.*;
import net.miginfocom.swing.MigLayout;
import view.MainFrame;
import view.component.dialog.ConfigureMultimediaDialog;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;

public class DetailMultimediaPanel extends JPanel {

    private final SearchPanel searchPanel;
    private final Multimedia multimedia;
    private final SearchController controller;
    private String baseUrlPoster;

    public DetailMultimediaPanel(SearchPanel parent, Multimedia multimedia,
                                 SearchController controller) {
        super(new MigLayout(
                "fill, ins 0",
                "[fill, 40%][fill, 60%]",
                "[fill]"));

        this.searchPanel = parent;
        this.multimedia = multimedia;
        this.controller = controller;

        init();
    }

    private void init() {
        setBackground(Color.PINK);
        baseUrlPoster = ApiController.getBaseURLForPosters(true);

        String releaseDateString = multimedia.getReleaseDate() != null ?
                multimedia.getReleaseDate().toString() :
                null;
        boolean isAMovie = multimedia instanceof Movie;

        if (isAMovie)
            setBackground(new Color(250, 219, 111));
        else
            setBackground(new Color(132, 182, 244));

        //Components

        //Poster
        JPanel pnlPoster = new JPanel(new MigLayout(
                "flowy, fill",
                "[fill]",
                "[fill, 60%]50[fill, 40%]"
        ));
        pnlPoster.setOpaque(false);

        StretchIcon iconPoster;
        try {
            iconPoster = new StretchIcon(
                    URI.create(baseUrlPoster + multimedia.getPosterUrl()).toURL(),
                    true);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        JLabel lblPoster = new JLabel(iconPoster);
        lblPoster.setBorder(LineBorder.createBlackLineBorder());

        pnlPoster.add(lblPoster);

        //Panel Details

        JPanel pnlDetails = new JPanel(new MigLayout(
                "ins 5, fill",
                "[|||]",
                "[fill, 20%|fill, 10%|fill, 10%|fill, 40%|fill, 10%|fill, 10%]"
        ));
        pnlDetails.setOpaque(false);

        //Title & Score

        String titleText = "<html><p align=\"center\">" + multimedia.getTitle();
        titleText += releaseDateString != null ?
                " (" + multimedia.getReleaseDate().getYear() + ")</p></html>" :
                "</p></html>";
        JLabel lblTitle = new JLabel(titleText);
        Font titleFont = lblTitle.getFont().deriveFont(Font.BOLD, 22);
        lblTitle.setFont(titleFont);
        lblTitle.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, Color.BLACK));

        JLabel lblScore = new JLabel(multimedia.getScore());
        lblScore.setFont(titleFont);
        lblScore.setHorizontalAlignment(SwingConstants.CENTER);

        //Release date & Country

        JLabel lblReleaseDate = new JLabel("Release Date: " +
                (releaseDateString == null ?
                        "Unknown" : releaseDateString));

        JLabel lblCountry = new JLabel("Country: " + multimedia.getCountry());

        //Duration & Status

        JLabel lblDuration = new JLabel("Duration: " + (isAMovie ?
                ((Movie) multimedia).getDuration() : ((TvShow) multimedia).getEpisodeDuration()));

        JLabel lblStatus = null;
        if (!isAMovie)
            lblStatus = new JLabel("Status: " + ((TvShow) multimedia).getAiringStatus());

        //Synopsis

        ScrollablePanel pnlSynopsis = new ScrollablePanel(new MigLayout(
                "fill",
                "[fill]",
                "[fill]"
        ));
        pnlSynopsis.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        pnlSynopsis.setOpaque(false);

        String synopsisString = multimedia.getSynopsis();
        if (synopsisString.isEmpty())
            synopsisString = "-no synopsis found-";
        JLabel lblSynopsis = new JLabel("<html><p align='center'>Synopsis: " +
                synopsisString +
                "</p></html>");

        pnlSynopsis.add(lblSynopsis);

        JScrollPane scrollSynopsis = new JScrollPane(pnlSynopsis);
        scrollSynopsis.getViewport().setOpaque(false);
        scrollSynopsis.setOpaque(false);
        scrollSynopsis.setBorder(null);

        //Genre List

        String genres = String.join(", ", multimedia.getGenreList());
        JLabel lblGenres = new JLabel("Genres: " + genres);
        lblGenres.setHorizontalAlignment(SwingConstants.CENTER);

        //Episode & Season count

        JLabel lblEpisodeCount = null, lblSeasonCount = null;
        if (!isAMovie) {
            int totalEpisodes = ((TvShow) multimedia).getTotalEpisodes();
            int totalSeasons = ((TvShow) multimedia).getTotalSeasons();
            lblEpisodeCount = new JLabel("# Episodes: " + totalEpisodes);
            lblSeasonCount = new JLabel("# Seasons: " + totalSeasons);

            lblEpisodeCount.setHorizontalAlignment(SwingConstants.CENTER);
            lblEpisodeCount.setHorizontalAlignment(SwingConstants.CENTER);
        }

        //Buttons

        JPanel pnlButton = new JPanel(new MigLayout(
                "flowy, align 50% 50%",
                "[60%!, fill]",
                "[]10[]10[]"
        ));
        pnlButton.setOpaque(false);
        pnlButton.setBorder(LineBorder.createBlackLineBorder());

        JButton btnBack = new JButton("Back");
        JButton btnAddToList = new JButton("Add");
        JButton btnRemoveFromList = new JButton("Remove");

        btnBack.setFocusPainted(false);
        btnAddToList.setFocusPainted(false);
        btnRemoveFromList.setFocusPainted(false);

        btnRemoveFromList.setEnabled(false);

        pnlButton.add(btnBack, "sg buttons");
        pnlButton.add(btnAddToList, "sg buttons");
        pnlButton.add(btnRemoveFromList, "sg buttons");

        pnlPoster.add(pnlButton);

        //Listeners

        btnBack.addActionListener(_ -> controller.backButtonFromDetailPanel(searchPanel));
        btnAddToList.addActionListener(_ -> {
            MainFrame mainFrame = MainFrame.getInstance();
            User user = mainFrame.getUser();

            if (user.getLists().isEmpty()) {
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "You have not created any list yet to add this multimedia.",
                        "Add multimedia to a list",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            ConfigureMultimediaDialog dialog =
                    new ConfigureMultimediaDialog(mainFrame, multimedia,
                            mainFrame.getUser().getLists());
            dialog.setVisible(true);

            if (!dialog.isCanceled()) {
                UserList selectedList = dialog.getSelectedList();
                MultimediaAtList multimediaAtList = new MultimediaAtList(
                        multimedia,
                        dialog.getSelectedMultimediaStatus(),
                        dialog.getSelectedCurrentEpisode());

                ServerResponse response = UserListController.addMultimediaToList(
                        user,
                        selectedList.getListName(),
                        multimediaAtList);
                if (response.getStatus() != 200) {
                    JOptionPane.showMessageDialog(
                            mainFrame,
                            "Couldn't add the multimedia to the list",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                selectedList.getMultimediaList().add(multimediaAtList);
            }
        });

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

        add(pnlPoster);
        add(pnlDetails);

        revalidate();
        repaint();
    }

    public Multimedia getMultimedia() {
        return multimedia;
    }
}
