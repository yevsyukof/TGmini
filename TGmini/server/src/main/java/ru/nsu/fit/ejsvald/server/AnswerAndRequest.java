package ru.nsu.fit.ejsvald.server;

import ru.nsu.fit.ejsvald.server.data.StringPair;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AnswerAndRequest {
    HashMap<Integer, String> answers = new HashMap<>();
    HashMap<Integer, Queue<StringPair>> requests = new HashMap<>();

    int freeKey = 0;
    public String getAnswer(int requestID){
        return answers.get(requestID);
    }

    public void addAnswer(String message, int requestID){
        answers.put(requestID, message);
    }

    public int addRequest(int userID, String curRequest) {
        if (!requests.containsKey(userID)){
            requests.put(userID, new LinkedList<>());
        }
        requests.get(userID).add(new StringPair(curRequest, freeKey));
        return freeKey++;
    }


//    public StringPair getRequest(int userID) {
//        if (requests.containsKey(userID)){
//            return requests.get(userID).poll();
//        }
//        return null;
//    }

    public String getRequest(int userID) {
        if (requests.containsKey(userID)){
            StringPair curRequest = requests.get(userID).poll();
            if (curRequest == null){
                return null;
            }
            JsonObject jsonCurRequest = Json.createObjectBuilder()
                    .add("idToPutAnswer", String.valueOf(curRequest.idToPutAnswer))
                    .add("request", curRequest.request)
                    .build();
            return jsonCurRequest.toString();
        }
        return null;
    }
}
