package config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ApiService;
import tools.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ApiConfiguration {

    private static String API_TOKEN;
    private static String LANGUAGE;

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiConfiguration.class);

    // Keys: base_url, secure_base_url, backdrop_sizes, logo_sizes, poster_sizes,
    //       profile_sizes, still_sizes
    private static final Map<String, JsonNode> IMAGES_URLS = new HashMap<>();
    private static String iconSize;
    private static String posterSize;
    private static boolean built = false;

    private ApiConfiguration(){}

    private static void initApiConfiguration() {
        API_TOKEN = System.getProperty("API_READ_ACCESS_TOKEN", "");
        LANGUAGE = System.getProperty("LANGUAGE", "en-GB");
        String urlString = BASE_URL + "configuration";

        ApiService apiService = new ApiService(null);
        JsonNode data = apiService.getApiInfo(urlString);
        if (data == null) {
            LOGGER.error("Unknown error while fetching API info in {}", urlString);
            return;
        }

        JsonNode imagesData = data.get("images");
        if (imagesData == null) {
            return;
        }

        imagesData.forEachEntry(IMAGES_URLS::put);
        List<String> posterSizes = IMAGES_URLS.get("poster_sizes").valueStream()
                .map(JsonNode::asString)
                .toList();
        String baseImageUrl = Optional.of(IMAGES_URLS.get("secure_base_url").stringValue())
                .orElse(IMAGES_URLS.get("base_url").stringValue());
        iconSize = baseImageUrl + (posterSizes.contains("w185") ? "w185" : "original");
        posterSize = baseImageUrl + (posterSizes.contains("w780") ? "w780" : "original");
    }

    public static void load() {
        if (!built) {
            initApiConfiguration();
            built = true;
        }
    }

    public static String getApiToken() {
        return API_TOKEN;
    }

    public static String getLanguage() {
        return LANGUAGE;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getIconSize() {
        return iconSize;
    }

    public static String getPosterSize() {
        return posterSize;
    }
}
