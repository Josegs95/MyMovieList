package thread;

import com.sun.tools.javac.Main;
import controller.UserListController;
import model.*;
import view.MainFrame;

import java.util.*;

public class FetchUserLists implements Runnable{

    private final User user;

    public FetchUserLists(User user){
        this.user = user;
    }

    @Override
    public void run(){
        List<UserList> userLists = getUpdatedUserLists();

        MainFrame.getInstance().setUserLists(userLists);
    }

    @SuppressWarnings("unchecked")
    public List<UserList> getUpdatedUserLists() {
        ServerResponse response = UserListController.fetchUserList(user);

        List<UserList> userLists = new ArrayList<>();
        List<Map<String, Object>> userListsSerialized =
                (List<Map<String, Object>>) (response.getData().get("lists"));
        for(Map<String, Object> listData : userListsSerialized) {
            String listName = listData.get("listName").toString();
            Set<MultimediaListItem> multimediaItemsList = new HashSet<>();

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

                MultimediaListItem multimediaListItem =
                        new MultimediaListItem(multimedia, status, currentEpisode);
                multimediaItemsList.add(multimediaListItem);
            }

            userLists.add(new UserList(listName, multimediaItemsList));
        }

        return userLists;
    }
}
