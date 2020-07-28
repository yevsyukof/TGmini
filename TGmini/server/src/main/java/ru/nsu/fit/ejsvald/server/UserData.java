package ru.nsu.fit.ejsvald.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class UserData {
    private final int id;
    private final String login;
    private final String passwordHash;
    private final HashMap<Integer, List<String>> roomList;
    public UserData(int id, String login, String passwordHash) {
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
        this.roomList = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public HashMap<Integer, List<String>> getRoomList() {
        return roomList;
    }
    public List<String> getMessageList(int roomId) {
        if (!roomList.containsKey(roomId)) {
            roomList.put(roomId,new LinkedList<>());
        }
        return roomList.get(roomId);
    }
}
