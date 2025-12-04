package service;

import context.SessionContext;
import dto.MultimediaDetailDTO;
import dto.MultimediaSummaryDTO;
import dto.UserDTO;
import dto.request.MultimediaDetailRequest;
import dto.request.SearchMultimediaRequest;
import dto.response.MultimediaDetailResponse;
import dto.response.SearchMultimediaResponse;
import protocol.Message;
import protocol.MessageType;
import protocol.SocketCommunication;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

public class SearchService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final UserDTO user = SessionContext.getInstance().getUser();

    public List<MultimediaSummaryDTO> searchByName(String text) {
        SearchMultimediaRequest request = new SearchMultimediaRequest(user.getId(), text, user.getSessionToken());

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.API_SEARCH_MULTIMEDIA, request));
        SearchMultimediaResponse response = mapper.convertValue(serverMessage.content(), SearchMultimediaResponse.class);

        response.results()
                .forEach(dto -> dto.setPosterPath(response.baseImageUrl() + dto.getPosterPath()));
        return response.results();
    }

    public MultimediaDetailDTO getMultimediaDetail(MultimediaSummaryDTO multimedia) {
        MultimediaDetailRequest request = new MultimediaDetailRequest(
                user.getId(),
                multimedia.getApiId(),
                multimedia.getType(),
                user.getSessionToken());

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.API_DETAIL_MULTIMEDIA, request));
        MultimediaDetailResponse response = mapper.convertValue(serverMessage.content(), MultimediaDetailResponse.class);
        response.multimedia().setPosterPath(response.baseImageUrl() + response.multimedia().getPosterPath());

        return response.multimedia();
    }
}
