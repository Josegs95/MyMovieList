package json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.Movie;
import model.Multimedia;
import model.TvShow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static List<Multimedia> parseJSONFromAPI(JsonArray array) {
        List<Multimedia> multiList = new ArrayList<>();
        for (JsonElement element : array) {
            JsonObject multiAsJson = element.getAsJsonObject();
            Multimedia multi;
            String releaseDateKey, titleKey;

            //Id
            int id = multiAsJson.get("id").getAsInt();

            switch (multiAsJson.get("media_type").getAsString()) {
                case "movie" -> {
                    multi = new Movie(id);
                    titleKey = "title";
                    releaseDateKey = "release_date";
                }
                case "tv" -> {
                    multi = new TvShow(id);
                    titleKey = "name";
                    releaseDateKey = "first_air_date";
                }
                default -> {
                    continue;
                }
            }

            //Title
            multi.setTitle(multiAsJson.get(titleKey).getAsString());

            //Release Date
            if (!multiAsJson.get(releaseDateKey).isJsonNull()
                    && !multiAsJson.get(releaseDateKey).getAsString().isEmpty())
                multi.setReleaseDate(LocalDate.parse(multiAsJson.get(releaseDateKey).getAsString()));

            //Poster
            if (!multiAsJson.get("poster_path").isJsonNull())
                multi.setPosterUrl(multiAsJson.get("poster_path").getAsString());

            //Score
            if (Double.parseDouble(multiAsJson.get("vote_average").getAsString()) == 0.0)
                multi.setScore("No score");
            else
                multi.setScore(multiAsJson.get("vote_average").getAsString());

            //Popularity
            multi.setPopularity(Double.parseDouble(multiAsJson.get("popularity").getAsString()));

            multiList.add(multi);
        }

        return multiList;
    }
}
