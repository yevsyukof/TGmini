package ru.nsu.fit.ejsvald.server;

import ru.nsu.fit.ejsvald.server.data.MessageInfo;

import java.util.ArrayList;
import java.util.List;

public class MessagesList {

    private ArrayList<StringBuilder> messagesForUsers;

    public MessagesList() {
        messagesForUsers = new ArrayList<>();
    }

    public void addMessage(int ID, String name, String message) {
        messagesForUsers.get(ID)
                .append(ID) ///добавляем ID для распределния сообщений пользователь/ чат + пользователь
                .append("&")
                .append(name)
                .append(": ")
                .append(message)
                .append(System.lineSeparator());///согласен(раньше я все это конкатенировал)
    }

//    public void addInfo(String info) { //что оно делает?
//        messagesForUsers.add(info);
//    }

    public MessageInfo getMessagesForID(int ID) { ///getFrom
        MessageInfo toReturn = new MessageInfo(messagesForUsers.get(ID).length(), messagesForUsers.get(ID).toString());
        messagesForUsers.get(ID).setLength(0); ///хз что делает ПРЕД строка
        return toReturn;
    }


}
