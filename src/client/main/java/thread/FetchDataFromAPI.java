package thread;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import controller.ApiController;
import model.Movie;
import model.Multimedia;
import model.MultimediaType;
import model.TvShow;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FetchDataFromAPI {

    private FetchDataFromAPI(){}

    public static void fetchData(List<Multimedia> multimediaList, boolean fullInfo) {
        try (ExecutorService executor = Executors.newCachedThreadPool()) {
            for (Multimedia multimedia : multimediaList) {
                executor.execute(() -> setMultimediaDataFromApi(multimedia, fullInfo));
            }
        }

        System.out.println("Threads done!");
    }

    private static void setMultimediaDataFromApi(Multimedia multimedia, boolean fullInfo) {
        JsonObject data;
        try {
            data = ApiController.searchMultimediaDetail(multimedia);
            System.out.println("Details: " + data);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        MultimediaType type = multimedia.getMultimediaType();

        if (fullInfo) {
            // Release Date
            JsonElement jsonReleaseDate = data.get(type == MultimediaType.MOVIE ? "release_date" : "first_air_date");
            if (!jsonReleaseDate.isJsonNull()) {
                multimedia.setReleaseDate(LocalDate.parse(jsonReleaseDate.getAsString()));
            }

            // Score
            double score = data.get("vote_average").getAsDouble();
            multimedia.setScore(score == 0 ? "No score" : String.valueOf(score));
        }

        // Poster
        JsonElement jsonPoster = data.get("poster_path");
        if (!jsonPoster.isJsonNull()) {
            multimedia.setPosterUrl(jsonPoster.getAsString());
        }

        //Synopsis
        JsonElement jsonSynopsis = data.get("overview");
        if (!jsonSynopsis.isJsonNull()) {
            multimedia.setSynopsis(jsonSynopsis.getAsString());
        }

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
