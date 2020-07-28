package ru.nsu.fit.ejsvald.server.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.fit.ejsvald.server.AnswerAndRequest;
import ru.nsu.fit.ejsvald.server.data.StringPair;
import ru.nsu.fit.ejsvald.server.utils.JsonParser;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.Iterator;

@RestController
public class DefaultController {
    private final Service service = new Service();
    private final AnswerAndRequest answerAndRequest = new AnswerAndRequest();

    @RequestMapping(value = "/getMessage", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "Accept=application/json")
    public String getMessages(@RequestBody String jsonData) {
        JsonObject jsonObj = JsonParser.parse(jsonData);
        int userID = jsonObj.getInt("userID");
        String password = jsonObj.getString("password");
        int roomID = jsonObj.getInt("roomID");
        return service.getMessages(userID, password, roomID);
    }

    @RequestMapping(value = "/addMessage", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "Accept=application/json")
    public boolean addMessage(@RequestBody String jsonData) {
        JsonObject jsonObj = JsonParser.parse(jsonData);
        int userID = jsonObj.getInt("userID");
        String password = jsonObj.getString("password");
        JsonArray userList = jsonObj.getJsonArray("recipientList");
        int roomID = jsonObj.getInt("roomID");
        String message = jsonObj.getString("messageBody");
        return service.addMessage(userID, password, userList, roomID, message);
    }

    @RequestMapping(value = "/signUp", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "Accept=application/json")
    public int signUp(@RequestBody String jsonData) {
        JsonObject jsonObj = JsonParser.parse(jsonData);
        String login = jsonObj.getString("login");
        String password = jsonObj.getString("password");
        return service.signUp(login, password);
    }

    @RequestMapping(value = "/generateRoomID", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "Accept=application/json")
    public int generateRoomID() {
        return service.generateRoomID();
    }

    @RequestMapping(value = "/quite", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "Accept=application/json")
    public boolean quite(@RequestBody String jsonData) {
        JsonObject jsonObj = JsonParser.parse(jsonData);
        int userID = jsonObj.getInt("userID");
        String password = jsonObj.getString("password");
        return service.quite(userID, password);
    }

    @RequestMapping(value = "/addRequest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "Accept=application/json")
    public int addRequest(@RequestBody String request) {
        JsonObject jsonObj = JsonParser.parse(request);
        return answerAndRequest.addRequest(jsonObj.getInt("userID"), jsonObj.getString("requestStructure"));
    }

    @RequestMapping(value = "/addAnswer", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "Accept=application/json")
    public void addAnswer(@RequestBody String answer) {
        JsonObject jsonObj = JsonParser.parse(answer);
        answerAndRequest.addAnswer(jsonObj.getString("answer"), jsonObj.getInt("idToPutAnswer"));
    }

    @RequestMapping(value = "/getAnswer", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "Accept=application/json")
    public String getAnswer(@RequestBody String requestId) {
        JsonObject jsonObject = JsonParser.parse(requestId);
        return answerAndRequest.getAnswer(jsonObject.getInt("requestId"));
    }

    @RequestMapping(value = "/getRequest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            headers = "Accept=application/json")
    public String getRequest(@RequestBody String userId) {
        JsonObject jsonObject = JsonParser.parse(userId);
        return answerAndRequest.getRequest(jsonObject.getInt("userID")); ///FIXME: что оно возвращает? (<_>)(пофиксшено? пары стрингов)
    }
}
