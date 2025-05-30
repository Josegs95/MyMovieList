package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import json.Parser;
import model.Multimedia;
import thread.FetchDataFromAPI;

import java.io.IOException;
import java.util.List;

public class SearchController {

    private SearchController() {}

    public static List<Multimedia> searchMultimediaByKeyword(String name) {
        try {
            JsonObject data = ApiController.searchMultimedia(name);
            JsonArray results = data.get("results").getAsJsonArray();
            List<Multimedia> multiList = Parser.parseJSONFromAPI(results);

            FetchDataFromAPI.fetchData(multiList, false);

            return multiList;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
