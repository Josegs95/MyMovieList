package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import file.ApplicationProperty;
import model.Movie;
import model.Multimedia;
import model.TVShow;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIController {
    final private static String API_TOKEN;
    final private static String LANGUAGE;

    final private static String BASE_URL = "https://api.themoviedb.org/3/";

    // Keys: base_url, secure_base_url, backdrop_sizes, logo_sizes, poster_sizes, profile_sizes, still_sizes
    private static Map<String, JsonElement> API_CONFIGURATION;

    static {
        Map<String, String> properties = ApplicationProperty.getProperties();

        API_TOKEN = properties.getOrDefault("API_READ_ACCESS_TOKEN", "");
        LANGUAGE = properties.getOrDefault("LANGUAGE", "en-GB");
    }

    public static JsonObject searchMultimedia(String multiName) throws IOException, InterruptedException {
        String urlString = BASE_URL + "search/multi?query=" + multiName.replace(" ", "%20");
        urlString += "&language=" + LANGUAGE;

        return makeGetRequest(URI.create(urlString));
    }

    public static JsonObject searchMultimediaDetail(Multimedia multimedia) throws IOException, InterruptedException {
        String urlString = BASE_URL;
        urlString += switch (multimedia) {
            case Movie _ -> "movie/";
            case TVShow _ -> "tv/";
            default -> "";
        };
        urlString += multimedia.getId();
        urlString += "&language=" + LANGUAGE;

        return makeGetRequest(URI.create(urlString));
    }

    public static void setUpConfigurationDetails() throws IOException, InterruptedException {
        if (API_CONFIGURATION != null)
            return;

        URI uri = URI.create("https://api.themoviedb.org/3/configuration");
        JsonObject data = makeGetRequest(uri);
        if (data == null)
            return;

        API_CONFIGURATION = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : data.get("images").getAsJsonObject().entrySet()) {
            if (entry.getValue() instanceof JsonArray array)
                API_CONFIGURATION.put(entry.getKey(), array);
            else if (entry.getValue() instanceof JsonElement value) {
                API_CONFIGURATION.put(entry.getKey(), value);
            }
        }
    }

    public static String getBaseURLForPosters(boolean fullSize) {
        if (API_CONFIGURATION == null)
            return null;

        String baseURL = API_CONFIGURATION.get("secure_base_url").getAsString();
        JsonArray sizeArray = API_CONFIGURATION.get("poster_sizes").getAsJsonArray();
        List<String> sizeList = sizeArray.asList().stream()
                .map(JsonElement::getAsString)
                .toList();
        String posterSize;
        if (fullSize)
            posterSize = "original";
        else if (sizeList.size() > 1)
            posterSize = sizeList.get(1);
        else
            posterSize = sizeList.get(0);

        return baseURL + posterSize;
    }

    private static JsonObject makeGetRequest(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + API_TOKEN)
                .GET()
                .build();
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Se ha producido un error desconocido");
                System.out.println("API: " + response.body());
                return null;
            }

            return JsonParser.parseString(response.body()).getAsJsonObject();
        }
    }
}
