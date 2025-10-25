package thread;

import controller.UserListController;
import controller.ViewController;
import event.Event;
import event.EventType;
import model.*;

import java.util.*;

public class FetchUserLists implements Runnable{

    private final User user;

    public FetchUserLists(User user){
        this.user = user;
    }

    @Override
    public void run(){
        List<UserList> userLists = getUpdatedUserLists();

        user.setLists(userLists);

        Event event = new Event(EventType.CREATE_USER_LIST_ITEMS, Map.of("userLists", userLists));
        ViewController.getInstance().notifyView("userListPanel", event);

        List<Multimedia> multimediaList = userLists.stream()
                .flatMap(list -> list.getMultimediaList().stream().map(MultimediaListItem::getMultimedia))
                .toList();
        FetchDataFromAPI.fetchData(multimediaList, true);
    }

    @SuppressWarnings("unchecked")
    public List<UserList> getUpdatedUserLists() {
        Message response = UserListController.fetchUserList(user);

        List<UserList> userLists = new ArrayList<>();
        List<Map<String, Object>> userListsSerialized =
                (List<Map<String, Object>>) (response.content().get("lists"));
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
