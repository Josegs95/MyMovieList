package controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import file.ApplicationProperty;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class APIController {
    private static String API_TOKEN;

    static {
        API_TOKEN = ApplicationProperty.getProperties().get("API_READ_ACCESS_TOKEN");
    }

    public static Map<String, JsonElement> searchMultimedia(String multiName) throws IOException, InterruptedException {
        URI uri = URI.create(String.format("https://api.themoviedb.org/3/search/multi?query=%s", multiName));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + API_TOKEN)
                .GET()
                .build();
        HttpResponse response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200){
            System.out.println("Se ha producido un error desconocido");
            System.out.println("API: " + response.body());
        }
        JsonObject data = JsonParser.parseString(response.body().toString()).getAsJsonObject();
        return data.asMap();
    }
}
