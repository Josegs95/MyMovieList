package json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.Movie;
import model.Multimedia;
import model.TVShow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static List<Multimedia> parseJSONFromAPI(JsonArray array) {
        List<Multimedia> multiList = new ArrayList<>();
        for (JsonElement element : array) {
            JsonObject multiAsJson = element.getAsJsonObject();
            Multimedia multi;
            switch (multiAsJson.get("media_type").getAsString()) {
                case "movie" -> {
                    multi = new Movie();
                    multi.setTitle(multiAsJson.get("title").getAsString());
                    if (!multiAsJson.get("release_date").isJsonNull() && !multiAsJson.get("release_date").getAsString().isEmpty())
                        multi.setReleaseDate(LocalDate.parse(multiAsJson.get("release_date").getAsString()));
                }
                case "tv" -> {
                    multi = new TVShow();
                    multi.setTitle(multiAsJson.get("name").getAsString());
                    if (!multiAsJson.get("first_air_date").isJsonNull() && !multiAsJson.get("first_air_date").getAsString().isEmpty())
                        multi.setReleaseDate(LocalDate.parse(multiAsJson.get("first_air_date").getAsString()));
                }
                default -> {
                    continue;
                }
            }

            if (!multiAsJson.get("poster_path").isJsonNull())
                multi.setPosterURL(multiAsJson.get("poster_path").getAsString());
            if (Double.parseDouble(multiAsJson.get("vote_average").getAsString()) == 0.0)
                multi.setScore("No score");
            else
                multi.setScore(multiAsJson.get("vote_average").getAsString());
            multi.setPopularity(Double.parseDouble(multiAsJson.get("popularity").getAsString()));

            multiList.add(multi);
        }

        return multiList;
    }
}
