package Network;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

public class JsonParser {

    public static JsonObject parse(String jsonData) {
        return Json.createReader(new StringReader(jsonData)).readObject();
    }

    public static <T> JsonArray buildArrayFromList(List<T> userList) {
        return Json.createArrayBuilder(userList).build();
    }

    public static List<Integer> buildIntListFromJsonArray(JsonArray jsonArray) {
        LinkedList<Integer> toReturn = new LinkedList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            toReturn.add(i,jsonArray.getInt(i));
        }
        return toReturn;
    }
}