package Network;

import anatoly.crypto.library.AES;
import anatoly.crypto.library.MD5;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.CurrentSessionData;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;


public class Postman {

    private final String urlString;
    private int ok;
    private int notOk;

    private CurrentSessionData curSesData;

    public Postman(String urlString) {
        this.urlString = urlString;
    }

    public String sendToBase(String urlString, String method) {
        return sendToBase(urlString, method, null);
    }

    public String sendToBase(String urlString, String method, String body) {
        URL url;
        HttpURLConnection connection = null;
        while (true) {
            try {
                url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                if (!method.equals("GET") && body != null && !body.equals("")) {
                    connection.setDoOutput(true);
                }
                connection.setRequestMethod(method);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                if (connection.getDoOutput()) {
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    if (body != null) {
                        wr.write(body);
                    }
                    wr.flush();
                }
                if (connection.getInputStream() != null) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String data = br.readLine();//FIXME: read all lines
                    ok++;
                    return data;
                } else {
                    ok++;
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("NOT OK!");
                notOk++;
                System.out.println("ok: " + ok + " notOk: " + notOk);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    System.out.println("TIMER ERROR!");
                    //e.printStackTrace();
                }
            }
        }
    }

    public void setCurSesData(CurrentSessionData userInfo) {
        curSesData = userInfo;
    }

    //TODO///////////////////////////////////////////////////////////////////////////////////////////////
    public String blockedRequestForAns(int responseUserId, String requestStructure) {
        JsonObject requestForNewMess = Json.createObjectBuilder()
                .add("userID", responseUserId)
                .add("requestStructure", requestStructure)
                .build();
        String requestId = sendToBase(urlString + "/addRequest", "POST", requestForNewMess.toString());
        System.out.println("requestId:" + requestId);
        String answer = sendToBase(urlString + "/getAnswer", "POST",
                Json.createObjectBuilder().add("requestId", Integer.parseInt(requestId)).build().toString());
        while (Objects.isNull(answer)) {//FIXME:не уверен что тут вернется, так как это json
            answer = sendToBase(urlString + "/getAnswer", "POST",
                    Json.createObjectBuilder().add("requestId", Integer.parseInt(requestId)).build().toString());
        }
        return answer;
    }

    public void tryPutAnswer(int myId) {
        JsonObject requestForNewMess = Json.createObjectBuilder()
                .add("userID", myId)
                .build();
        String request = sendToBase(urlString + "/getRequest", "POST", requestForNewMess.toString());
        if (Objects.nonNull(request)) {
            System.out.println("e:/getRequest" + request);
            String answer = requestInterpreter(request);
            System.out.println("e:/addAnswer" + answer);
            sendToBase(urlString + "/addAnswer", "POST", answer);
        }
    }

    public String getMessages(int userID, String password, int curRoomID) {
        ObjectMapper mapper = new ObjectMapper();
        JsonObject requestForNewMess = Json.createObjectBuilder()
                .add("userID", userID)
                .add("password", password)
                .add("roomID", curRoomID)
                .build();
        return sendToBase(urlString + "/getMessage", "POST", requestForNewMess.toString());
    }

    public void addMessage(int userID, String password, int roomID,
                           String messageType, JsonArray recipientList, String message) {
        if (message.equals("")) {
            return;
        }
        JsonObject messageBody = Json.createObjectBuilder()
                .add("messageType", messageType)
                .add("userID", userID)
                .add("message", message)
                .build();
        JsonObject jsonObj = Json.createObjectBuilder()
                .add("userID", userID)
                .add("password", password)
                .add("roomID", roomID)
                .add("recipientList", recipientList)
                .add("messageBody", messageBody.toString())
                .build();
        sendToBase(urlString + "/addMessage", "POST", jsonObj.toString());
    }


    public void logOut(String login, String passwordHash) {
        JsonObject jsonObj = Json.createObjectBuilder()
                .add("login", login)
                .add("passwordHash", passwordHash)
                .build();
        sendToBase(urlString + "/logOut", "POST", jsonObj.toString());
    }

    public int signUp(String login, String password) {
        JsonObject jsonObj = Json.createObjectBuilder()
                .add("login", login)
                .add("password", password)
                .build();
        return Integer.parseInt(sendToBase(urlString + "/signUp", "POST", jsonObj.toString()));
    }

    public int generateRoomID() {
        String result = sendToBase(urlString + "/generateRoomID", "GET");
        return Integer.parseInt(result);
    }

    public boolean isLoginExist(String login) {
        String result = sendToBase(urlString + "/isLoginExist", "post", login);
        return Boolean.parseBoolean(result);
    }

    public String requestInterpreter(String request) {
        JsonObject parser = JsonParser.parse(request);
        JsonObject requestAnswer = JsonParser.parse(parser.getString("request"));
        switch (requestAnswer.getString("request")) {
            case ("get_public_key"): {
                int idToPutAnswer = Integer.parseInt(parser.getString("idToPutAnswer"));
                requestAnswer = Json.createObjectBuilder()
                        .add("idToPutAnswer", idToPutAnswer)
                        .add("answer", curSesData.getPublicKey()).build();
                return requestAnswer.toString();
                //FIXME: при получении такого сообщения и его обработки, мы должны сидеть в режиме ответа
            }
            case ("accept_invite"): { //нас просят принять инвайт
                int senderID = requestAnswer.getInt("userID");
                String encryptedInvite = requestAnswer.getString("encryptedInvite");
                long decryptedInviteCode = curSesData.rsa.hostRole().decryptMessage(Long.parseLong(encryptedInvite));
                int idToPutAnswer = Integer.parseInt(parser.getString("idToPutAnswer"));
                int checkInvite = curSesData.checkInviteCode(decryptedInviteCode);
                if (checkInvite != -1) { //FIXME: ломает ситему при неправильном инвайт-коде
                    JsonObject jsonRoomList = Json.createObjectBuilder()
                            .add("roomID", checkInvite)
                            .add("usersList", JsonParser.buildArrayFromList(curSesData.rooms.get(checkInvite)))
                            .build();
                    AES aes = new AES(new MD5().getMD5Hash(String.valueOf(decryptedInviteCode)));
                    AES aes1 = new AES(new MD5().getMD5Hash(String.valueOf(decryptedInviteCode)));

                    String encryptedMessage = aes.encryptMessage(jsonRoomList.toString());
                    System.out.println("исходное сообщение");
                    for (byte a : jsonRoomList.toString().getBytes()) {
                        System.out.print(a + " ");
                    }
                    System.out.println();
                    System.out.println("расшифрованное-зашифрованное сообщение");
                    for (byte a : aes1.decryptMessage(encryptedMessage).getBytes()) {
                        System.out.print(a + " ");
                    }
                    System.out.println();

                    requestAnswer = Json.createObjectBuilder()
                            .add("answer", jsonRoomList.toString())
                            .add("idToPutAnswer", idToPutAnswer)
                            .build();
                    curSesData.addNewMember(checkInvite, senderID);
                    //TODO: разослать другим участникам чата информацию о добавлении нового участника
                    return requestAnswer.toString();
                }
                throw new Error("checkInvite == -1");
            }
            default:
                throw new Error("invalid request == " + requestAnswer.getString("request"));
//            case ("enterIn_request"): { //TODO: изначално мы должны были запросить у пригласителя его открытый ключ
//                //расшифровываем своим закрытым ключом
//                int checkInviteResult = curSes.checkInviteCode(Integer.parseInt(curMessage));
//                if (checkInviteResult != -1) {
//                    curSes.addNewMember(curSes.curRoomID, Integer.parseInt(curMessage));
//                }
//                break;
//            }
//            case ("add_you_in_room"): {
//                boolean createRoom = false;
//                String newRoomID = null;
//                for (String elem : curMessage.split(" ")) {
//                    if (!createRoom) {
//                        createRoom = true;
//                        newRoomID = elem;
//                        curSes.enterInExistRoom(newRoomID, sc.nextLine()); //FIXME
//                    } else {
//                        curSes.addNewMember(newRoomID, elem); ///ну или что то типо того
//                    }
//                }
//                break;
//            }
//            case ("rebuild_key_and_return"): {
//                //FIXME
//                break;
//            }
//            case ("accept_new_room_key"): {
//                if (curSes.curRoom.contains(userID)) {
//                    curSes.setNewKeyForRoom(curSes.curRoomID, curMessage);
//                }
//                break;
//            }
//            case ("room_member_log_out"): {
//                curSes.curRoom.remove(userID);
//                //TODO: инициализировать смену ключей
//                break;
//            }
//            case ("add_new_room_member"): {
//                if (curSes.curRoom.contains(userID)) {
//                    curSes.curRoom.add(curMessage);
//                }
//                break;
//            }
//            default:
//                System.out.println(curMessage);
//                break;
        }
    }
}
