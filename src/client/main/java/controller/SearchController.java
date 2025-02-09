package controller;

import com.google.gson.JsonObject;
import json.Parser;
import model.Multimedia;
import view.component.SearchPanel;

import java.io.IOException;
import java.util.List;

public class SearchController {
    final private SearchPanel VIEW;

    public SearchController(SearchPanel view) {
        this.VIEW = view;
    }

    public List<Multimedia> searchMultimedia(String name) {
        try {
            JsonObject data = APIController.searchMultimedia(name);
            System.out.println("Data: " + data);
            List<Multimedia> multiList = Parser.parseJSONFromAPI(data.get("results").getAsJsonArray());

            multiList = multiList.stream()
                    .sorted((a, b) -> (int) ((a.getPopularity() - b.getPopularity()) * -1))
                    .toList();

            for (Multimedia multi : multiList)
                System.out.println(multi);

            return multiList;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
