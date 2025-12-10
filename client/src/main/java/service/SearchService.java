package service;

import context.SessionContext;
import model.dto.MultimediaDetailDTO;
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SearchService {

    private static final Map<MultimediaSummaryDTO, MultimediaDetailDTO> API_CACHE = new ConcurrentHashMap<>();

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
        if (API_CACHE.containsKey(multimediaSummary)) {
            EventBus.publish(new DetailApiEvent(API_CACHE.get(multimediaSummary), multimediaSummary));
            return;
        }

        MultimediaDetailRequest request = new MultimediaDetailRequest(
                user.getId(),
                multimediaSummary.getApiId(),
                multimediaSummary.getType(),
                user.getSessionToken());

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.API_DETAIL_MULTIMEDIA, request));
        MultimediaDetailResponse response = mapper.convertValue(serverMessage.content(), MultimediaDetailResponse.class);
        MultimediaDetailDTO multimediaDetail = response.multimedia();
        multimediaDetail.setPosterPath(response.baseImageUrl() + response.multimedia().getPosterPath());
        API_CACHE.put(multimediaSummary, multimediaDetail);

        EventBus.publish(new DetailApiEvent(multimediaDetail, multimediaSummary));
    }
}
