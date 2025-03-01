package thread;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import controller.APIController;
import model.Movie;
import model.Multimedia;
import model.TVShow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FetchDataFromAPI implements Runnable {

    final private List<Multimedia> MULTIMEDIA_LIST;

    public FetchDataFromAPI(List<Multimedia> multimediaList) {
        this.MULTIMEDIA_LIST = multimediaList;
    }

    @Override
    public void run() {
        try (ExecutorService executor = Executors.newCachedThreadPool()) {
            for (Multimedia multimedia : MULTIMEDIA_LIST) {
                executor.execute(new FetchMultiData(multimedia));
            }
        }

        System.out.println("Threads done!");
    }

    private class FetchMultiData implements Runnable {

        final private Multimedia multimedia;

        public FetchMultiData(Multimedia multimedia) {
            this.multimedia = multimedia;
        }

        @Override
        public void run() {
            JsonObject data;
            try {
                data = APIController.searchMultimediaDetail(multimedia);
                System.out.println("Details: " + data);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            //Synopsis
            multimedia.setSynopsis(data.get("overview").getAsString());

            //Genres
            List<String> genreList = new ArrayList<>();
            for (JsonElement element : data.get("genres").getAsJsonArray()) {
                JsonObject object = element.getAsJsonObject();
                if (!object.isEmpty())
                    genreList.add(element.getAsJsonObject().get("name").getAsString());
            }
            multimedia.setGenreList(genreList);

            //Country
            List<String> countryList = new ArrayList<>();
            for (JsonElement element : data.get("origin_country").getAsJsonArray()) {
                countryList.add(element.getAsString());
            }
            multimedia.setCountry(String.join(", ", countryList));

            switch (multimedia) {
                case Movie movie -> movie.setDuration(data.get("runtime").getAsString());
                case TVShow tvShow -> {
                    tvShow.setEpisodeCount(data.get("number_of_episodes").getAsInt());
                    tvShow.setSeasonCount(data.get("number_of_seasons").getAsInt());
                    tvShow.setStatus(data.get("in_production").getAsBoolean() ? "Ongoing" : "Finished");
                    JsonArray episodesRuntime = data.get("episode_run_time").getAsJsonArray();
                    if (!episodesRuntime.isEmpty())
                        tvShow.setEpisodeDuration(episodesRuntime.get(episodesRuntime.size() - 1).getAsString());
                }
                default -> {
                }
            }
        }
    }
}
