package controller;

import model.Movie;
import model.Multimedia;
import model.TvShow;
import service.ApiService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiController {
    private static final String API_TOKEN;
    private static final String LANGUAGE;

    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // Keys: base_url, secure_base_url, backdrop_sizes, logo_sizes, poster_sizes,
    //       profile_sizes, still_sizes
    private static Map<String, JsonNode> API_CONFIGURATION;

    static {
        API_TOKEN = System.getProperty("API_READ_ACCESS_TOKEN", "");
        LANGUAGE = System.getProperty("LANGUAGE", "en-GB");
    }

    public static void setUpConfigurationDetails() {
        if (API_CONFIGURATION != null)
            return;

        URI uri = URI.create("https://api.themoviedb.org/3/configuration");
        JsonNode data = makeGetRequest(uri);
        if (data == null)
            return;

        API_CONFIGURATION = new HashMap<>();
        JsonNode imagesData = data.get("images");
        imagesData.forEachEntry((key, value) -> API_CONFIGURATION.put(key, value));
    }

    public static List<Multimedia> searchMultimedia(String multiName) {
        multiName = multiName.replace(" ", "%20");
        String urlString = BASE_URL + "search/multi?query=" + multiName;
        urlString += "&language=" + LANGUAGE;

        JsonNode apiResponse = makeGetRequest(URI.create(urlString));

        if (apiResponse == null) {
            return null;
        }

        return ApiService.searchMultimedia(apiResponse);
    }

    public static JsonNode searchMultimediaDetail(Multimedia multimedia) {
        String urlString = BASE_URL;
        urlString += switch (multimedia) {
            case Movie _ -> "movie/";
            case TvShow _ -> "tv/";
            default -> "";
        };
        urlString += multimedia.getId();
        urlString += "&language=" + LANGUAGE;

        return makeGetRequest(URI.create(urlString));
    }

    public static String getBaseURLForPosters(boolean fullSize) {
        if (API_CONFIGURATION == null)
            return null;

        String baseURL = API_CONFIGURATION.get("secure_base_url").asString();
        List<JsonNode> sizeList = new ArrayList<>(API_CONFIGURATION.get("poster_sizes").values());
        String posterSize;
        if (fullSize)
            posterSize = "original";
        else if (sizeList.size() > 1)
            posterSize = sizeList.get(1).asString();
        else
            posterSize = sizeList.get(0).asString();

        return baseURL + posterSize;
    }

    private static JsonNode makeGetRequest(URI uri) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + API_TOKEN)
                .GET()
                .build();
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Se ha producido un error desconocido");
                System.out.println("API: " + response.body());
                return null;
            }

            return MAPPER.readTree(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
