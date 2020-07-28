package ru.nsu.fit.ejsvald.server.utils;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;

public class JsonParser {
    public static JsonObject parse(String jsonData) {
       return Json.createReader(new StringReader(jsonData)).readObject();
    }
}
