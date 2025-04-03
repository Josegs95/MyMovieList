package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import json.Parser;
import model.Multimedia;
import thread.FetchDataFromAPI;
import view.component.panel.SearchPanel;

import java.io.IOException;
import java.util.List;

public class SearchController {

    public SearchController() {}

    public List<Multimedia> searchMultimediaByKeyword(String name) {
        try {
            JsonObject data = ApiController.searchMultimedia(name);
            JsonArray results = data.get("results").getAsJsonArray();
            List<Multimedia> multiList = Parser.parseJSONFromAPI(results);

            // Borrar
            System.out.println("Data: " + data);

            new Thread(new FetchDataFromAPI(multiList)).start();

            return multiList;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void backButtonFromDetailPanel(SearchPanel view) {
        view.deleteDetailPanel();
    }
}
