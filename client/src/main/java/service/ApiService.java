package service;

import model.Movie;
import model.Multimedia;
import model.TvShow;
import thread.FetchDataFromAPI;
import tools.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ApiService {

    public static List<Multimedia> searchMultimedia(JsonNode data) {
        JsonNode results = data.get("results");
        List<Multimedia> multimediaList = parseApiData(results);

        FetchDataFromAPI.fetchData(multimediaList, false);

        return multimediaList;
    }

    private static List<Multimedia> parseApiData(JsonNode results) {
        List<Multimedia> multimediaList = new ArrayList<>();

        for(JsonNode node : results) {
            // Id
            int id = node.get("id").asInt();

            // Multimedia type
            String mediaType = node.get("media_type").asString();

            // Title
            String title = mediaType.equals("movie") ?
                    node.get("title").asString() : node.get("name").asString();

            // Poster
            String posterURL = node.get("poster_path").asString();

            // Release Date
            String releaseDate = mediaType.equals("movie") ?
                    node.get("release_date").asString() : node.get("first_air_date").asString();

            // Score
            String score = node.get("vote_average").asString();

            // Popularity
            Double popularity = node.get("popularity").asDouble();

            if (mediaType.equals("movie")) {
                multimediaList.add(new Movie(
                        id,
                        title,
                        posterURL,
                        LocalDate.parse(releaseDate),
                        score,
                        popularity));
            } else {
                multimediaList.add(new TvShow(
                        id,
                        title,
                        posterURL,
                        LocalDate.parse(releaseDate),
                        score,
                        popularity));
            }
        }

        return multimediaList;
    }
}
