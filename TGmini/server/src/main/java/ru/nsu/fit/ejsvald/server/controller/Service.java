package ru.nsu.fit.ejsvald.server.controller;

import ru.nsu.fit.ejsvald.server.ServerDatabase;
import ru.nsu.fit.ejsvald.server.UserData;

import javax.json.*;
import java.util.LinkedList;
import java.util.List;

public class Service {
    private final ServerDatabase usersDB = new ServerDatabase();
    private int idRoom = 0;
    private int idUser = 0;

    public String getMessages(int id, String password, int roomId) {
        UserData userData = usersDB.userData.get(id);
        boolean hasLoggedIn = usersDB.authentication(id, password);
        synchronized (userData.getRoomList()) {
            if (hasLoggedIn) {
                JsonArrayBuilder jsonMessages = Json.createArrayBuilder();
                List<String> messageList = usersDB.userData.get(id).getMessageList(roomId);
                for (String message : messageList) {
                    jsonMessages.add(message);
                }
                messageList.clear();
                JsonObject objToSend = Json.createObjectBuilder()
                        .add("array", jsonMessages.build())
                        .build();
                return objToSend.toString();
            }

        }
        return "authentication error";
    }

    public boolean addMessage(int id, String password, JsonArray userList, int roomID, String message) {
        if (!usersDB.authentication(id, password)) {
            return false;
        }
        synchronized (usersDB.userData.get(id).getMessageList(roomID)) {
            for (int i = 0; i < userList.size(); i++) {
                UserData userData = usersDB.userData.get(userList.getInt(i));
                if (!userData.getRoomList().containsKey(roomID)) {
                    userData.getRoomList().put(roomID, new LinkedList<>());
                }
                userData.getRoomList().get(roomID).add(message);
            }
        }
        return true;
    }

    public int signUp(String login, String password) {
        int id = generateUserID();
        if (usersDB.isLoginExist(login)) return -2;
        if (usersDB.signUp(id, login, password)) {
            return id;
        }
        return -1;
    }

    public boolean quite(int id, String password) {
        boolean hasLoggedIn = usersDB.authentication(id, password);
        if (hasLoggedIn) {
            return usersDB.quite(id);
        }
        return false;
    }

    public int generateRoomID() {
        return idRoom++;
    }

    private int generateUserID() {
        //FIXME:если переполнение, то обнулять
        return idUser++;
    }
}
