package layout;

import Network.JsonParser;
import Network.Postman;
import anatoly.crypto.library.AES;
import anatoly.crypto.library.MD5;
import anatoly.crypto.library.RSA;
import data.CurrentSessionData;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Scanner;

public class RoomListLayout implements Layout {
    private final CurrentSessionData roomInfo;
    private final Scanner in;
    private final Postman server;

    public RoomListLayout(Scanner in, Postman server, CurrentSessionData roomInfo) {
        this.in = in;
        this.server = server;
        this.roomInfo = roomInfo;
    }

    @Override
    public Layout start() {
        while (true) {
            String[] roomsList = roomInfo.getRoomNames();
            System.out.println("Rooms list:");
            for (int i = 0; i < roomsList.length; i++) {
                System.out.println(i + ". " + roomsList[i]);
            }
            System.out.println("Enter /move_to <room number> to move in the Room");
            System.out.println("Enter /join_to <userID> <secretNumber> to join the Room");
            System.out.println("Enter /create_room <roomName> to create the Room");
            String[] answer = in.nextLine().split(" ");
            if ("/join_to".equals(answer[0])  || "/j".equals(answer[0])) {
                if (answer.length != 3) {
                    System.out.println("invalid count of args");
                    continue;
                }
                JsonObject pubKeyRequest = Json.createObjectBuilder()
                        .add("userID", roomInfo.myUserID) //FIXME: мы получателя в blockedRequestForAns засовываем
                        .add("request", "get_public_key") //FIXME: тут был type
//                        .add("request", "get_public_key")
                        .build();
                String[] InviterPublicKey = server.blockedRequestForAns(Integer.parseInt(answer[1]),
                        pubKeyRequest.toString()).split(" ");
                long[] publicKey = {Long.parseLong(InviterPublicKey[0]), Long.parseLong(InviterPublicKey[1])};
                RSA rsa = new RSA();
                long inviteCiphered = rsa.senderRole(publicKey).encryptMessage(Long.parseLong(answer[2]));
                JsonObject roomListRequest = Json.createObjectBuilder()
                        .add("request", "accept_invite")
                        .add("userID", roomInfo.myUserID)
                        .add("encryptedInvite", String.valueOf(inviteCiphered))
                        .build();
                String EncodeInBase64Str = server.blockedRequestForAns(Integer.parseInt(answer[1]),
                                                                        roomListRequest.toString());
                for (byte a: EncodeInBase64Str.getBytes()){
                    System.out.print(a + " ");
                }
                System.out.println();
                System.out.println("передаваемый код");
                String newRoomInfo = new String(Base64.getDecoder()
                                                .decode(EncodeInBase64Str.getBytes()));
                AES aes = new AES(new MD5().getMD5Hash(answer[2]));
                newRoomInfo = aes.decryptMessage(newRoomInfo);////FIXME
                JsonObject newDataJson = JsonParser.parse(newRoomInfo);
                JsonArray JsonRoomMembers = newDataJson.getJsonArray("usersList");
                int existRoomID = newDataJson.getInt("roomID");
                roomInfo.enterExistRoom(existRoomID,
                        (LinkedList<Integer>) JsonParser.buildIntListFromJsonArray(JsonRoomMembers));
                return new RoomLayout(roomInfo, server, in);
            }
            if ("/move_to".equals(answer[0]) || "/m".equals(answer[0])) {
                if (answer.length != 2) {
                    System.out.println("invalid count of args");
                    continue;
                }
                int roomNumber;
                try {
                    roomNumber = Integer.parseInt(answer[1]);
                } catch (NumberFormatException e) {
                    System.out.println("invalid format of room number. Please, enter number of [0;" + (roomsList.length - 1) + "]");
                    continue;
                }
                if (roomNumber >= roomsList.length) {
                    System.out.println("invalid count of args");
                    continue;
                }
                roomInfo.curRoomID = roomInfo.getRoomIdByName(roomsList[roomNumber]);
                return new RoomLayout(roomInfo, server, in);
            }
            if ("/create_room".equals(answer[0])  || "/c".equals(answer[0])){
                if (answer.length != 2) {
                    System.out.println("invalid count of args");
                    continue;
                }
                roomInfo.addRoom(server.generateRoomID(), answer[1]);
            }
        }
    }
}
