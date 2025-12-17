package service;

import config.ApiConfiguration;
import config.HibernateUtil;
import filter.TMDBFilter;
import model.dto.MovieDetailDTO;
import model.dto.MultimediaDetailDTO;
import model.dto.MultimediaSummaryDTO;
import model.dto.SeriesDetailDTO;
import model.entity.MultimediaType;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protocol.dto.response.ApiSearchResponse;
import protocol.dto.response.MultimediaDetailResponse;
import protocol.dto.response.SearchMultimediaResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ApiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiService.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final AuthService authService;

    public ApiService(AuthService authService) {
        this.authService = authService;
    }

    public SearchMultimediaResponse searchAllByName(Long userId, String searchText, String sessionToken) {
        checkUserAuthentication(userId, sessionToken);

        String text = searchText.replace(" ", "%20");
        String urlString = ApiConfiguration.getBaseUrl()
                + "search/multi?query=" + text
                + "&language=" + ApiConfiguration.getLanguage();

        List<MultimediaSummaryDTO> elementList = new ArrayList<>();
        try (ExecutorService executor = Executors.newCachedThreadPool()) {
            List<Future<String>> futures = executor.invokeAll(List.of(
                    () -> makeGetRequest(urlString + "&page=1"),
                    () -> makeGetRequest(urlString + "&page=2"),
                    () -> makeGetRequest(urlString + "&page=3")));
            for (Future<String> future : futures) {
                ApiSearchResponse response = MAPPER.readValue(future.get(), ApiSearchResponse.class);
                elementList.addAll(response.results());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        elementList = TMDBFilter.sortResults(elementList, searchText);

        return new SearchMultimediaResponse(ApiConfiguration.getIconSize(), elementList);
    }

    public MultimediaDetailResponse getMultimediaDetails(Long userId, String apiId, MultimediaType type, String sessionToken) {
        checkUserAuthentication(userId, sessionToken);

        String typeEndpoint = type == MultimediaType.MOVIE ? "movie/" : "tv/";
        String urlString = ApiConfiguration.getBaseUrl()
                + typeEndpoint
                + apiId
                + "?language=" + ApiConfiguration.getLanguage();

        MultimediaDetailDTO dto;
        if (type == MultimediaType.MOVIE) {
            dto = MAPPER.readValue(makeGetRequest(urlString), MovieDetailDTO.class);
        } else {
            dto = MAPPER.readValue(makeGetRequest(urlString), SeriesDetailDTO.class);
        }

        return new MultimediaDetailResponse(ApiConfiguration.getPosterSize(), dto);
    }

    public JsonNode getApiInfo(String urlString) {
        return MAPPER.readTree(makeGetRequest(urlString));
    }

    private void checkUserAuthentication(Long idUser, String sessionToken) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                authService.validateSession(session, idUser, sessionToken);

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    private String makeGetRequest(String urlString) {
        URI uri = URI.create(urlString);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("accept", "application/json")
                .header("Authorization", "Bearer " + ApiConfiguration.getApiToken())
                .GET()
                .build();
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                LOGGER.error("Unknown error");
                LOGGER.info("API message: {}", response.body());
                return null;
            }

            LOGGER.info("API message: {}", response.body());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
