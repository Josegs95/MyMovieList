package controller;

import com.google.gson.JsonElement;
import view.component.SearchPanel;

import java.io.IOException;
import java.util.Map;

public class SearchController {
    final private SearchPanel VIEW;

    public SearchController(SearchPanel view){
        this.VIEW = view;
    }

    public void searchMultimedia(String name){
        try {
            Map<String, JsonElement> data = APIController.searchMultimedia(name);
            for (Map.Entry<String, JsonElement> entry : data.entrySet())
                System.out.println(entry.getKey() + ", " + entry.getValue());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
