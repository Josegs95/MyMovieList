package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import file.ApplicationProperty;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class APIController {
    final private static String API_TOKEN;

    // Keys: base_url, secure_base_url, backdrop_sizes, logo_sizes, poster_sizes, profile_sizes, still_sizes
    private static Map<String, JsonElement> API_CONFIGURATION;

    static {
        if (ApplicationProperty.getProperties() != null)
            API_TOKEN = ApplicationProperty.getProperties().get("API_READ_ACCESS_TOKEN");
        else
            API_TOKEN = "";
    }

    public static JsonObject searchMultimedia(String multiName) throws IOException, InterruptedException {
        URI uri = URI.create(String.format("https://api.themoviedb.org/3/search/multi?query=%s",
                multiName.replace(' ', '+')));
        return makeGetRequest(uri);
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

    public static String getBaseURLForPosters() {
        if (API_CONFIGURATION == null)
            return null;

        String baseURL = API_CONFIGURATION.get("secure_base_url").getAsString();
        JsonArray sizeArray = API_CONFIGURATION.get("poster_sizes").getAsJsonArray();
        String posterSize = sizeArray.get(0).getAsString();

        return baseURL + posterSize;
    }

    private static JsonObject makeGetRequest(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + API_TOKEN)
                .GET()
                .build();
        try (HttpClient client = HttpClient.newHttpClient()){
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
