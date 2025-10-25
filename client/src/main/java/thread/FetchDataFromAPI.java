package thread;

import controller.ApiController;
import model.Movie;
import model.Multimedia;
import model.MultimediaType;
import model.TvShow;
import tools.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
        JsonNode data = ApiController.searchMultimediaDetail(multimedia);
        System.out.println("Details: " + data.toString());

        MultimediaType mediaType = multimedia.getMultimediaType();

        if (fullInfo) {
            // Release Date
            String releaseDate = mediaType == MultimediaType.MOVIE ?
                    data.get("release_date").asString() : data.get("first_air_date").asString();
            multimedia.setReleaseDate(LocalDate.parse(releaseDate));

            // Score
            double score = data.get("vote_average").asDouble();
            multimedia.setScore(score == 0 ? "No score" : String.valueOf(score));
        }

        // Poster
        multimedia.setPosterUrl(data.get("poster_path").asString());

        //Synopsis
        multimedia.setSynopsis(data.get("overview").asString());

        //Genres
        multimedia.setGenreList(data.get("genres").valueStream()
                .map(JsonNode::asString)
                .toList());

        //Country
        multimedia.setCountry(data.get("origin_country").valueStream()
                .map(JsonNode::asString)
                .collect(Collectors.joining(", ")));

        switch (multimedia) {
            case Movie movie -> movie.setDuration(data.get("runtime").asString());
            case TvShow tvShow -> {
                boolean inProduction = data.get("in_production").asBoolean();
                String episodeRuntime = "Unknown";
                JsonNode episodeRuntimeList = data.get("episode_run_time");
                if (!episodeRuntimeList.isEmpty()) {
                    episodeRuntime = episodeRuntimeList.get(episodeRuntimeList.size() - 1)
                            .asString();
                }

                tvShow.setAiringStatus(inProduction ? "Ongoing" : "Finished");
                tvShow.setTotalEpisodes(data.get("number_of_episodes").asInt());
                tvShow.setTotalSeasons(data.get("number_of_seasons").asInt());
                tvShow.setEpisodeDuration(episodeRuntime);
            }
            default -> {}
        }
    }
}
