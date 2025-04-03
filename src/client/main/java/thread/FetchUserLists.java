package thread;

import controller.UserListController;
import json.JSONMessageProtocol;
import model.*;
import view.MainFrame;

import java.util.*;

public class FetchUserLists implements Runnable{

    private final MainFrame mainFrame;

    public FetchUserLists(MainFrame parent){
        this.mainFrame = parent;
    }

    @Override @SuppressWarnings("unchecked")
    public void run(){
        ServerResponse response = UserListController.fetchUserList(mainFrame.getUser());
        Map<String, Object> messageData = JSONMessageProtocol
                .createMapFromJSONString(response.getDataAsJsonString());

        Map<String, Object> serializedData = (Map<String, Object>) (messageData.get("data"));
        List<Object> serializedUserLists = (List<Object>) (serializedData.get("lists"));
        List<UserList> userLists = new ArrayList<>();
        for (Object o : serializedUserLists){
            Map<String, Object> serializedUserList = (Map<String, Object>) o;

            String listName = serializedUserList.get("listName").toString();

            List<Object> serializedMultimediaList = (List<Object>)
                    (serializedUserList.get("multimediaList"));
            Set<MultimediaAtList> multimediaList = new HashSet<>();
            for (Object ob : serializedMultimediaList){
                Map<String, Object> serializedMultimediaInfo = (Map<String, Object>) ob;
                Map<String, Object> serializedMultimedia = (Map<String, Object>)
                        (serializedMultimediaInfo.get("multimedia"));

                MultimediaStatus multimediaStatus = MultimediaStatus.
                        valueOf(serializedMultimediaInfo.get("status").toString());
                int currentEpisode = (int) (serializedMultimediaInfo.get("current_episode"));
                int apiID = (int) (serializedMultimedia.get("ID"));
                String title = serializedMultimedia.get("title").toString();
                MultimediaType multimediaType = MultimediaType.valueOf(
                        serializedMultimediaInfo.get("multimediaType").toString());

                Multimedia multimedia;
                if (multimediaType == MultimediaType.MOVIE)
                    multimedia = new Movie(apiID);
                else {
                    multimedia = new TvShow(apiID);
                    int totalEpisodes = (int) (serializedMultimedia.get("episodeCount"));
                    int totalSeasons = (int) (serializedMultimedia.get("seasonCount"));

                    ((TvShow) multimedia).setTotalEpisodes(totalEpisodes);
                    ((TvShow) multimedia).setTotalSeasons(totalSeasons);
                }
                multimedia.setTitle(title);

                MultimediaAtList multimediaAtList =
                        new MultimediaAtList(multimedia, multimediaStatus,
                                currentEpisode, multimediaType);
                multimediaList.add(multimediaAtList);
            }

            UserList userList = new UserList(listName, multimediaList);
            userLists.add(userList);
        }

        mainFrame.getUser().setLists(userLists);
    }
}
