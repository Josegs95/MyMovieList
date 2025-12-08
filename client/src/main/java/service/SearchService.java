package service;

import context.SessionContext;
import model.dto.MultimediaSummaryDTO;
import model.dto.UserDTO;
import protocol.Message;
import protocol.MessageType;
import protocol.SocketCommunication;
import protocol.dto.request.MultimediaDetailRequest;
import protocol.dto.request.SearchMultimediaRequest;
import protocol.dto.response.MultimediaDetailResponse;
import protocol.dto.response.SearchMultimediaResponse;
import tools.jackson.databind.ObjectMapper;
import ui.event.DetailApiEvent;
import ui.event.SearchApiEvent;
import ui.util.EventBus;

public class SearchService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final UserDTO user = SessionContext.getInstance().getUser();

    public void searchByName(String text) {
        SearchMultimediaRequest request = new SearchMultimediaRequest(user.getId(), text, user.getSessionToken());

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.API_SEARCH_MULTIMEDIA, request));
        SearchMultimediaResponse response = mapper.convertValue(serverMessage.content(), SearchMultimediaResponse.class);

        response.results()
                .forEach(dto -> dto.setPosterPath(response.baseImageUrl() + dto.getPosterPath()));

        EventBus.publish(new SearchApiEvent(response.results()));
    }

    public void getMultimediaDetail(MultimediaSummaryDTO multimediaSummary) {
        MultimediaDetailRequest request = new MultimediaDetailRequest(
                user.getId(),
                multimediaSummary.getApiId(),
                multimediaSummary.getType(),
                user.getSessionToken());

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.API_DETAIL_MULTIMEDIA, request));
        MultimediaDetailResponse response = mapper.convertValue(serverMessage.content(), MultimediaDetailResponse.class);
        response.multimedia().setPosterPath(response.baseImageUrl() + response.multimedia().getPosterPath());

        EventBus.publish(new DetailApiEvent(response.multimedia(), multimediaSummary));
    }
}
