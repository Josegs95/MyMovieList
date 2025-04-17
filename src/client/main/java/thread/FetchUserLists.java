package thread;

import controller.UserListController;
import model.*;
import view.MainFrame;

import java.util.*;

public class FetchUserLists implements Runnable{

    private final MainFrame mainFrame;

    public FetchUserLists(){
        this.mainFrame = MainFrame.getInstance();
    }

    @Override @SuppressWarnings("unchecked")
    public void run(){
        ServerResponse response = UserListController.fetchUserList(mainFrame.getUser());
        List<UserList> userLists = new ArrayList<>();

        List<Map<String, Object>> userListsSerialized =
                (List<Map<String, Object>>) (response.getData().get("lists"));
        for(Map<String, Object> listData : userListsSerialized) {
            String listName = listData.get("listName").toString();
            Set<MultimediaAtList> multimediaItemsList = new HashSet<>();

            List<Map<String, Object>> multimediaItemsListSerialized =
                    (List<Map<String, Object>>) listData.get("multimediaItems");
            for(Map<String, Object> multimediaData : multimediaItemsListSerialized) {
                String title = multimediaData.get("title").toString();
                int apiId = (int) (multimediaData.get("apiId"));
                MultimediaType type =
                        MultimediaType.valueOf(multimediaData.get("type").toString());
                MultimediaStatus status =
                        MultimediaStatus.valueOf(multimediaData.get("status").toString());
                int currentEpisode = (int) (multimediaData.get("currentEpisode"));
                int totalEpisodes = (int) (multimediaData.get("totalEpisodes"));

                Multimedia multimedia;
                if (type == MultimediaType.MOVIE) {
                    multimedia = new Movie(apiId);
                } else if (type == MultimediaType.TV_SHOW) {
                    multimedia = new TvShow(apiId);
                    ((TvShow) multimedia).setTotalEpisodes(totalEpisodes);
                } else {
                    System.err.println("Unknown multimedia type" + type);
                    continue;
                }
                multimedia.setTitle(title);

                MultimediaAtList multimediaAtList =
                        new MultimediaAtList(multimedia, status, currentEpisode);
                multimediaItemsList.add(multimediaAtList);
            }

            userLists.add(new UserList(listName, multimediaItemsList));
        }

        mainFrame.getUser().setLists(userLists);
    }
}
