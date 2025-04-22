package view.component.panel;

import controller.ApiController;
import controller.SearchController;
import controller.UserListController;
import lib.ScrollablePanel;
import lib.StretchIcon;
import model.*;
import net.miginfocom.swing.MigLayout;
import thread.FetchUserLists;
import view.MainFrame;
import view.component.dialog.ConfigureMultimediaDialog;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DetailMultimediaPanel extends JPanel {

    private final SearchPanel searchPanel;
    private final Multimedia multimedia;
    private final SearchController controller;

    private String baseUrlPoster;
    private MainFrame mainFrame;
    private User user;

    private JButton btnAddToList;
    private JButton btnRemoveFromList;

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
        mainFrame = MainFrame.getInstance();
        user = mainFrame.getUser();

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
        btnAddToList = new JButton("Add");
        btnRemoveFromList = new JButton("Remove");

        btnBack.setFocusPainted(false);
        btnAddToList.setFocusPainted(false);
        btnRemoveFromList.setFocusPainted(false);

        pnlButton.add(btnBack, "sg buttons");
        pnlButton.add(btnAddToList, "sg buttons");
        pnlButton.add(btnRemoveFromList, "sg buttons");

        pnlPoster.add(pnlButton);

        //Listeners

        btnBack.addActionListener(_ -> controller.backButtonFromDetailPanel(searchPanel));
        btnAddToList.addActionListener(_ -> {
            // Error if the user has no lists

            if (user.getLists().isEmpty()) {
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "You have not created any list yet to add this multimedia.",
                        "Add multimedia to a list",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Show dialog to configure the multimedia

            ConfigureMultimediaDialog dialog =
                    new ConfigureMultimediaDialog(mainFrame, multimedia,
                            user.getLists());
            dialog.setVisible(true);

            if (dialog.isCanceled()) {
                return;
            }

            UserList selectedList = dialog.getSelectedList();
            MultimediaStatus selectedStatus = dialog.getSelectedMultimediaStatus();
            int selectedCurrentEpisode = dialog.getSelectedCurrentEpisode();
            MultimediaListItem multimediaListItem = new MultimediaListItem(multimedia, selectedStatus, selectedCurrentEpisode);

            // Send the information to the server

            ServerResponse response = UserListController.addMultimediaToList(user, selectedList, multimediaListItem);
            if (response.getStatus() != 200) {
                JOptionPane.showMessageDialog(
                        mainFrame,
                        "Couldn't add the multimedia to the list",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                new SwingWorker<List<UserList>, Void>() {
                    @Override
                    protected List<UserList> doInBackground() {
                        return new FetchUserLists(user).getUpdatedUserLists();
                    }

                    @Override
                    protected void done() {
                        try {
                            user.setLists(get());
                            updatePage();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }.execute();
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

        // Status

        System.out.println("Any: " + user.hasMultimediaInAnyList(multimedia));
        System.out.println("All: " + user.hasMultimediaInAllList(multimedia));

        updatePage();
    }

    public Multimedia getMultimedia() {
        return multimedia;
    }

    private void updatePage() {
        btnRemoveFromList.setEnabled(user.hasMultimediaInAnyList(multimedia));
        btnAddToList.setEnabled(!user.hasMultimediaInAllList(multimedia));

        revalidate();
        repaint();
    }
}
