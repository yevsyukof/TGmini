package layout;

import Network.JsonParser;
import Network.Postman;
import data.CurrentSessionData;

import java.util.Scanner;

public class RoomLayout implements Layout {

    private final Postman server;
    private final Scanner in;
    private final CurrentSessionData userInfo;
    public RoomLayout(CurrentSessionData userInfo, Postman server, Scanner in) {
        this.userInfo = userInfo;
        this.server = server;
        this.in = in;
    }


//    public int createRoom() {
//        ///TODO:нужна обработка корректности ввода
//        Scanner in = new Scanner(System.in);
//        System.out.print("Enter the room name: ");
//        String roomNameCreate = in.nextLine();
//        System.out.print("Enter the room name: ");
//        String roomPasswordCreate = in.nextLine();
//        return server.createRoom(login, userPassword, roomNameCreate, roomPasswordCreate);
//    }
//
//    public boolean enterInRoom() {
//        Scanner in = new Scanner(System.in);
//        System.out.print("Enter roomID: ");
//        int roomIDEnter = Integer.parseInt(in.nextLine());
//        String answer;
//        String passwordEnter = null;
//        boolean hasEntered = true;
//        boolean isIDExist = true; //TODO: проверка наличия логина
//        if (isIDExist) {
////            System.out.print("Enter room password to enter: ");
////            passwordEnter = in.nextLine();
//            if (server.enterInRoom(login, userPassword, roomID)) {
//                curRoomID = roomIDEnter;
//                curRoomPassword = passwordEnter;
//                return true;
//            }
//        }
//        while (!hasEntered) {
//            System.out.println("failed. Do you want enter again? (y/n)");
//            answer = in.nextLine();
//            if (answer.charAt(0) == 'n' || answer.charAt(0) == 'N') {
//                return false;
//            }
//            System.out.print("Enter roomID: ");
//            roomIDEnter = Integer.parseInt(in.nextLine());
////            System.out.print("Enter room password: ");
//            hasEntered = server.enterInRoom(login, userPassword, roomID);
//        }
//        curRoomID = roomIDEnter;
//        curRoomPassword = passwordEnter;
//        return true;
//    }

    @Override
    public Layout start() {
        System.out.println("You are in room. Now you can type message, press Enter to send in chat.");
        System.out.println("To change the room type \"/roomlist\"");
        System.out.println("To generate invite key type \"/gen_invite\"");
        while (true) {
            String input = in.nextLine();
            if(input.length()>0 && input.charAt(0) == '/') {
                if("/roomlist".equals(input) || "/r".equals(input)) {
                    userInfo.curRoomID=-1;
                    return new RoomListLayout(in, server, userInfo);
                } else if("/gen_invite".equals(input) || "/g".equals(input)) {
                    System.out.println("invite: " + userInfo.myUserID + " " + userInfo.generateNewInvite(userInfo.curRoomID));
                } else {
                    System.out.println("invalid command");
                }
                continue;
            }
            server.addMessage(userInfo.myUserID, userInfo.password, userInfo.curRoomID,  "mess",
                    JsonParser.buildArrayFromList(userInfo.getUserListByRoom(userInfo.curRoomID)), input);
        }
    }
}
