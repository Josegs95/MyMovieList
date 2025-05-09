package view.component.panel;

import controller.ApiController;
import controller.SearchController;
import controller.UserListController;
import lib.ScrollablePanel;
import lib.StretchIcon;
import model.*;
import net.miginfocom.swing.MigLayout;
import thread.FetchDataFromAPI;
import thread.FetchUserLists;
import view.MainFrame;
import view.component.dialog.ConfigureMultimediaDialog;
import view.component.dialog.RemoveMultimediaDialog;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DetailMultimediaPanel extends JPanel {

    private final SearchPanel searchPanel;
    private final Multimedia multimedia;

    private final MainFrame mainFrame;
    private final User user;

    private JButton btnBack;
    private JButton btnAddToList;
    private JButton btnRemoveFromList;

    private List<UserList> userListsWithMultimedia;

    public DetailMultimediaPanel(SearchPanel parent, Multimedia multimedia) {
        this.searchPanel = parent;
        this.multimedia = multimedia;

        this.mainFrame = MainFrame.getInstance();
        this.user = mainFrame.getUser();

        init();
    }

    private void init() {
        createUI();
        addListenersToComponents();
        updatePage();
    }

    public Multimedia getMultimedia() {
        return multimedia;
    }

    public void updatePage() {
        userListsWithMultimedia = user.getUserListsWhichContainsMultimedia(multimedia);

        btnRemoveFromList.setEnabled(!userListsWithMultimedia.isEmpty());
        btnAddToList.setEnabled(user.getLists().size() > userListsWithMultimedia.size());

        revalidate();
        repaint();
    }

    private void createUI() {
        setLayout(new MigLayout(
                "fill, ins 0",
                "[fill, 40%][fill, 60%]",
                "[fill]")
        );

        boolean isAMovie = multimedia instanceof Movie;
        setBackground(isAMovie ? new Color(250, 219, 111) : new Color(132, 182, 244));

        //Poster
        JPanel pnlLateral = new JPanel(new MigLayout(
                "flowy, fill",
                "[fill]",
                "[fill, 60%]50[fill, 40%]"
        ));
        pnlLateral.setOpaque(false);

        String baseUrlPoster = ApiController.getBaseURLForPosters(true);
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

        pnlLateral.add(lblPoster);

        //Panel Details

        JPanel pnlDetails = new JPanel(new MigLayout(
                "ins 5, fill",
                "[|||]",
                "[fill, 20%|fill, 10%|fill, 10%|fill, 40%|fill, 10%|fill, 10%]"
        ));
        pnlDetails.setOpaque(false);

        //Title & Score

        LocalDate releaseDate = multimedia.getReleaseDate();

        String titleText = multimedia.getTitle();
        if (releaseDate != null) {
            titleText = String.format("%s (%d)", titleText, releaseDate.getYear());
        }

        String title = String.format("<html><p style=\"text-align: center;\">%s</p></html>", titleText);
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        Font titleFont = lblTitle.getFont().deriveFont(Font.BOLD, 22);
        lblTitle.setFont(titleFont);
        lblTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        JLabel lblScore = new JLabel(multimedia.getScore(), SwingConstants.CENTER);
        lblScore.setFont(titleFont);

        //Release date & Country

        String releaseDateString = releaseDate != null ? releaseDate.toString() : "Unknown";
        JLabel lblReleaseDate = new JLabel(String.format("Release Date: %s", releaseDateString));

        JLabel lblCountry = new JLabel(String.format("Country: %s", multimedia.getCountry()), SwingConstants.CENTER);

        //Duration & Status

        String duration = isAMovie ? ((Movie) multimedia).getDuration() : ((TvShow) multimedia).getEpisodeDuration();
        JLabel lblDuration = new JLabel(String.format("Duration: %s", duration));

        String status = isAMovie ? "" : ((TvShow) multimedia).getAiringStatus();
        JLabel lblStatus = new JLabel(String.format("Status: %s", status), SwingConstants.CENTER);

        //Synopsis

        ScrollablePanel pnlSynopsis = new ScrollablePanel(new MigLayout(
                "fill",
                "[fill]",
                "[fill]"
        ));
        pnlSynopsis.setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        pnlSynopsis.setOpaque(false);

        String synopsis = String.format("<html><p style=\"text-align: center\">Synopsis: %s</p></html>",
                multimedia.getSynopsis());
        JLabel lblSynopsis = new JLabel(synopsis, SwingConstants.CENTER);

        pnlSynopsis.add(lblSynopsis);

        JScrollPane scrollSynopsis = new JScrollPane(pnlSynopsis);
        scrollSynopsis.getViewport().setOpaque(false);
        scrollSynopsis.setOpaque(false);
        scrollSynopsis.setBorder(null);

        //Genre List

        String genres = String.join(", ", multimedia.getGenreList());
        JLabel lblGenres = new JLabel(String.format("Genres: %s", genres), SwingConstants.CENTER);

        //Episode & Season count

        JLabel lblEpisodeCount = null, lblSeasonCount = null;
        if (!isAMovie) {
            int totalEpisodes = ((TvShow) multimedia).getTotalEpisodes();
            int totalSeasons = ((TvShow) multimedia).getTotalSeasons();
            lblEpisodeCount = new JLabel(String.format("# Episodes: %d", totalEpisodes), SwingConstants.CENTER);
            lblSeasonCount = new JLabel(String.format("# Seasons: %d", totalSeasons), SwingConstants.CENTER);
        }

        //Buttons

        JPanel pnlButton = new JPanel(new MigLayout(
                "flowy, align 50% 50%",
                "[60%!, fill]",
                "[]10[]10[]"
        ));
        pnlButton.setOpaque(false);
        pnlButton.setBorder(LineBorder.createBlackLineBorder());

        btnBack = new JButton("Back");
        btnAddToList = new JButton("Add");
        btnRemoveFromList = new JButton("Remove");

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

    private void addListenersToComponents() {
        btnBack.addActionListener(_ -> SearchController.backButtonFromDetailPanel(searchPanel));
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

            ConfigureMultimediaDialog dialog = new ConfigureMultimediaDialog(mainFrame, multimedia, user);
            dialog.setVisible(true);

            if (dialog.isCancelled()) {
                return;
            }

            UserList selectedList = dialog.getSelectedList();
            MultimediaStatus selectedStatus = dialog.getSelectedMultimediaStatus();
            int selectedCurrentEpisode = dialog.getSelectedCurrentEpisode();
            MultimediaListItem multimediaListItem = new MultimediaListItem(multimedia, selectedStatus, selectedCurrentEpisode);

            // Send the information to the server

            ServerResponse response = UserListController.addMultimediaToList(user, selectedList, multimediaListItem);
            if (response.getStatus() != 200) {
                processServerError(response, "Couldn't add the multimedia to the list");
            } else {
                String message = String.format("\"%s\" has been added to \"%s\" list successfully.",
                        multimedia.getTitle(), selectedList.getListName());
                JOptionPane.showMessageDialog(mainFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                new FetchListsWorker().execute();
                FetchDataFromAPI.fetchData(multimedia);
            }
        });
        btnRemoveFromList.addActionListener(_ -> {
            RemoveMultimediaDialog dialog = new RemoveMultimediaDialog(userListsWithMultimedia);
            dialog.setVisible(true);

            if (dialog.isCancelled()) {
                return;
            }

            UserList selectedList = dialog.getSelectedList();
            ServerResponse response = UserListController.deleteMultimediaFromList(user, selectedList, multimedia);
            if (response.getStatus() != 200) {
                processServerError(response, "Couldn't remove the multimedia from the list");
                return;
            }

            String message = String.format("'%s' successfully removed from '%s' list",
                    multimedia.getTitle(), selectedList.getListName());
            JOptionPane.showMessageDialog(mainFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
            new FetchListsWorker().execute();
        });
    }

    private void processServerError(ServerResponse response, String defaultMessage) {
        String message = response.getErrorMessage() != null ?
                response.getErrorMessage() : defaultMessage;

        JOptionPane.showMessageDialog(mainFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private class FetchListsWorker extends SwingWorker<List<UserList>, Void> {
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
    }
}
