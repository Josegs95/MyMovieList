package ui.view.component.panel;

import dto.MovieSummaryDTO;
import dto.MultimediaSummaryDTO;
import lib.StretchIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class MultimediaItemPanel extends JPanel {

    private static final Color MOVIE_COLOR = new Color(250, 219, 111);
    private static final Color SERIES_COLOR = new Color(132, 182, 244);
    private static final Map<String, StretchIcon> IMAGE_CACHE =
            Collections.synchronizedMap(new HashMap<>());

    private final MultimediaSummaryDTO multimedia;
    private final Consumer<MultimediaSummaryDTO> consumer;

    public MultimediaItemPanel(MultimediaSummaryDTO multimedia, Consumer<MultimediaSummaryDTO> consumer) throws MalformedURLException {
        this.multimedia = multimedia;
        this.consumer = consumer;

        init();
    }

    private void init() {
        setLayout(new MigLayout(
                "fill",
                "[fill, 15%][fill, 70%][fill, 15%]",
                "[fill]"));
        setBorder(LineBorder.createGrayLineBorder());
        setBackground(multimedia instanceof MovieSummaryDTO ? MOVIE_COLOR : SERIES_COLOR);

        JLabel lblPoster = new JLabel();
        String posterUrlString = multimedia.getPosterPath();
        if (posterUrlString == null) {
            lblPoster.setText("No Image");
        }
        else if (IMAGE_CACHE.containsKey(posterUrlString)) {
            lblPoster.setIcon(IMAGE_CACHE.get(posterUrlString));
        }
        else {
            lblPoster.setText("Loading...");
            new ImageLoaderWorker(lblPoster, posterUrlString).execute();
        }
        JLabel lblTitle = new JLabel(String.format("<html><p>%s</p></html>", multimedia.getTitle()));
        JLabel lblScore = new JLabel(multimedia.getScore().toString(), SwingConstants.CENTER);

        add(lblPoster);
        add(lblTitle);
        add(lblScore);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                consumer.accept(multimedia);
            }
        });
    }

    private static class ImageLoaderWorker extends SwingWorker<StretchIcon, Void> {
        private final JLabel lblPoster;

        private final String posterUrlString;

        public ImageLoaderWorker(JLabel lblPoster, String posterUrlString) {
            this.lblPoster = lblPoster;
            this.posterUrlString = posterUrlString;
        }

        @Override
        protected StretchIcon doInBackground(){
            try {
                return new StretchIcon(URI.create(posterUrlString).toURL(), true);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void done() {
            try {
                StretchIcon icon = get();
                IMAGE_CACHE.put(posterUrlString, icon);

                lblPoster.setText(null);
                lblPoster.setIcon(icon);

                lblPoster.revalidate();
                lblPoster.repaint();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                lblPoster.setIcon(null);
                lblPoster.setText("Image Error");
            }
        }
    }
}
