package ru.nsu.fit.ejsvald.server.data;

public class MessageInfo {
    private final int roomID;
    private final String messages;

    public MessageInfo(int number, String messages) {
        this.messages = messages;
        this.roomID = number;
    }

    public int getNumber() {
        return roomID;
    }

    public String getMessages() {
        return messages;
    }
}
