package controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class APIController {
    public static Map searchMovie(String movieName, String API_TOKEN) throws IOException, InterruptedException {
        URI uri = URI.create(String.format("https://api.themoviedb.org/3/search/multi?query=%s", movieName));
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
