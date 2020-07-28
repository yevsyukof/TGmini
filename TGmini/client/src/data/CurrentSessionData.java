package data;

import Network.JsonParser;
import Network.Postman;
import anatoly.crypto.library.RSA;

import java.util.*;

public class CurrentSessionData {
    Map<String, Integer> roomsIDs = new HashMap<>();//(roomName, roomID)
    public Map<Integer, LinkedList<Integer>> rooms = new HashMap<>();//(roomID, usersIdList)
    Map<Integer, String> roomsKeys = new HashMap<>();// (roomID, curKeyForRoom)
    Map<Long, Integer> myInvites = new HashMap<>();// (invite, roomID)

    public String login;
    public String password;
    public int myUserID;
    public int curRoomID = -1;
    private final Postman server;
    public static String publicKey = null;
    public RSA rsa = new RSA();

    public CurrentSessionData(Postman server) {
        this.server = server;
    }

    public int getRoomIdByName(String roomName) {
        return roomsIDs.get(roomName);
    }

    public void switchRoom(String roomName) {
        curRoomID = roomsIDs.get(roomName);
    }

    public String[] getRoomNames() {
        return roomsIDs.keySet().toArray(new String[0]);
    }

    public List<Integer> getUserListByRoom(int roomID) {
        return rooms.get(roomID);
    }

    public String getPublicKey() {
        if (publicKey == null) {
            publicKey = new StringBuilder(String.valueOf(rsa.hostRole().getPublicKey()[0]))
                    .append(" ")
                    .append(rsa.hostRole().getPublicKey()[1]).toString();
        }
        return publicKey;
    }

    public void enterExistRoom(int roomID, LinkedList<Integer> roomMembers) {
        curRoomID = roomID;
        rooms.put(roomID, roomMembers);
        rooms.get(roomID).add(myUserID);
    }

    public long generateNewInvite(int roomID) { //TODO: create_invite
        long inviteCode = new Random().nextInt(1936000000);
        myInvites.put(inviteCode, roomID);
        return inviteCode;
    }

    public int checkInviteCode(long inviteCode) {
        int toReturn = -1;
        if (myInvites.containsKey(inviteCode)) {
            toReturn = myInvites.get(inviteCode);
            myInvites.remove(inviteCode);
        }
        return toReturn;
    }

    public void addNewMember(int roomID, int newMemberID) { //TODO: иницировать смену ключа
        rooms.get(roomID).add(newMemberID);
        List<Integer> recipients = rooms.get(roomID);
        server.addMessage(myUserID, password, curRoomID,
                "add_new_room_member", JsonParser.buildArrayFromList(recipients), Integer.toString(newMemberID));
        List<Integer> newMember = new ArrayList<>();
        newMember.add(newMemberID);
        server.addMessage(myUserID, password, curRoomID,
                "addSucceed", JsonParser.buildArrayFromList(newMember), " ");
        /**отосласть изменение участникам чата и иницировать смену ключа*/
    }


    public void setNewKeyForRoom(String roomID, String newKey) {
        roomsKeys.put(Integer.valueOf(roomID), newKey);
    }

    public void excludeChatMember(String roomID, String userID) {
        rooms.get(roomID).remove(userID);
        /**отосласть изменение участникам чата и иницировать смену ключа*/
    }

    public void logInToChat(String inviteCode) {
        // TODO: отослать подтверждение приграсившему
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMyUserID(int myUserID) {
        this.myUserID = myUserID;
    }

    public void addRoom(int roomID, String newRoomName) {
        roomsIDs.put(newRoomName, roomID);
        rooms.put(roomID, new LinkedList<>());
        rooms.get(roomID).add(myUserID);
    }
}
