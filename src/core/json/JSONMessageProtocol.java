package json;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONMessageProtocol {

    private static final Gson GSON;

    static {
        GSON = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter()).create();
    }


    public static String createJSONFromMap(Map<String, Object> map){
        return new Gson().toJsonTree(map).getAsJsonObject().toString();
    }

    public static Map<String, Object> createMapFromJSONString(String jsonString){
        JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
        Map<String, Object> resultMap = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.asMap().entrySet()){
            resultMap.put(entry.getKey(), parseJSONToObject(entry.getValue()));
        }

        return resultMap;
    }

    public static JsonElement serializeObject(Object object){
        return GSON.toJsonTree(object);
    }

    private static List<Object> createListFromJSONArray(JsonArray jsonArray){
        List<Object> resultList = new ArrayList<>();

        for(JsonElement element : jsonArray){
            resultList.add(parseJSONToObject(element));
        }

        return resultList;
    }

    private static Object parseJSONToObject(JsonElement element){
        switch (element){
            case JsonObject jsonObject -> {
                return createMapFromJSONString(jsonObject.toString());
            }
            case JsonArray jsonArray -> {
                return createListFromJSONArray(jsonArray);
            }
            case JsonPrimitive primitive -> {
                if (primitive.isString())
                    return primitive.getAsString();
                else if (primitive.isNumber())
                    return primitive.getAsInt();
                else
                    return primitive.getAsBoolean();
            }
            default -> {
                System.out.println("Valor no soportado: " + element);
                return null;
            }
        }
    }

    private static class LocalDateTypeAdapter implements JsonSerializer<LocalDate>,
            JsonDeserializer<LocalDate> {

        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Override
        public JsonElement serialize(final LocalDate date, final Type typeOfSrc,
                                     final JsonSerializationContext context) {
            return new JsonPrimitive(date.format(formatter));
        }

        @Override
        public LocalDate deserialize(final JsonElement json, final Type typeOfT,
                                     final JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDate.parse(json.getAsString(), formatter);
        }
    }
}
