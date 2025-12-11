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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SearchService {

    private final Map<MultimediaSummaryDTO, MultimediaDetailDTO> API_CACHE = new ConcurrentHashMap<>();

    private final ObjectMapper mapper = new ObjectMapper();
    private final UserDTO user = SessionContext.getInstance().getUser();

    public List<MultimediaSummaryDTO> searchByName(String text) {
        SearchMultimediaRequest request = new SearchMultimediaRequest(user.getId(), text, user.getSessionToken());

        Message serverMessage = SocketCommunication.sendMessageToServer(new Message(MessageType.API_SEARCH_MULTIMEDIA, request));
        SearchMultimediaResponse response = mapper.convertValue(serverMessage.content(), SearchMultimediaResponse.class);
        List<MultimediaSummaryDTO> resultList = response.results();

        resultList.forEach(dto -> dto.setPosterPath(response.baseImageUrl() + dto.getPosterPath()));

        return resultList;
    }

    public MultimediaDetailDTO getMultimediaDetail(MultimediaSummaryDTO multimediaSummary) {
        if (API_CACHE.containsKey(multimediaSummary)) {
            return API_CACHE.get(multimediaSummary);
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

        return multimediaDetail;
    }
}
