package thread;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import controller.ApiController;
import model.Movie;
import model.Multimedia;
import model.TvShow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FetchDataFromAPI implements Runnable {

    final private List<Multimedia> multimediaList;

    public FetchDataFromAPI(List<Multimedia> multimediaList) {
        this.multimediaList = multimediaList;
    }

    @Override
    public void run() {
        try (ExecutorService executor = Executors.newCachedThreadPool()) {
            for (Multimedia multimedia : multimediaList) {
                executor.execute(new FetchMultiData(multimedia));
            }
        }

        System.out.println("Threads done!");
    }

    private static class FetchMultiData implements Runnable {

        private final Multimedia multimedia;

        public FetchMultiData(Multimedia multimedia) {
            this.multimedia = multimedia;
        }

        @Override
        public void run() {
            JsonObject data;
            try {
                data = ApiController.searchMultimediaDetail(multimedia);
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
                case TvShow tvShow -> {
                    boolean inProduction = data.get("in_production").getAsBoolean();
                    String episodeRuntime = "Unknown";
                    JsonArray episodeRuntimeList = data.get("episode_run_time").getAsJsonArray();
                    if (!episodeRuntimeList.isEmpty()) {
                        episodeRuntime = episodeRuntimeList.get(episodeRuntimeList.size() - 1)
                                .getAsString();
                    }

                    tvShow.setStatus(inProduction ? "Ongoing" : "Finished");
                    tvShow.setTotalEpisodes(data.get("number_of_episodes").getAsInt());
                    tvShow.setTotalSeasons(data.get("number_of_seasons").getAsInt());
                    tvShow.setEpisodeDuration(episodeRuntime);
                }
                default -> {}
            }
        }
    }
}
