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

public class FetchDataFromAPI {

    private FetchDataFromAPI(){}

    public static void fetchData(List<Multimedia> multimediaList) {
        try (ExecutorService executor = Executors.newCachedThreadPool()) {
            for (Multimedia multimedia : multimediaList) {
                executor.execute(() -> setMultimediaDataFromApi(multimedia));
            }
        }

        System.out.println("Threads done!");
    }

    public static void fetchData(Multimedia multimedia) {
        new Thread(() -> setMultimediaDataFromApi(multimedia)).start();
    }

    private static void setMultimediaDataFromApi(Multimedia multimedia) {
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

                tvShow.setAiringStatus(inProduction ? "Ongoing" : "Finished");
                tvShow.setTotalEpisodes(data.get("number_of_episodes").getAsInt());
                tvShow.setTotalSeasons(data.get("number_of_seasons").getAsInt());
                tvShow.setEpisodeDuration(episodeRuntime);
            }
            default -> {}
        }
    }
}
