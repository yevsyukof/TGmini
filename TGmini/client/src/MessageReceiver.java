import Network.JsonParser;
import Network.Postman;
import data.CurrentSessionData;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class MessageReceiver implements Runnable {

    private CurrentSessionData userInfo;

    public MessageReceiver(CurrentSessionData userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public void run() {
        Postman server = new Postman("http://localhost:8080");
        server.setCurSesData(userInfo);
        while (true) {
            server.tryPutAnswer(userInfo.myUserID);//FIXME: возможно стоит это вызывать после обработки входящих сообщений
            if(userInfo.curRoomID != -1 ) {
                String incomingJsonMessage = server.getMessages(userInfo.myUserID, userInfo.password, userInfo.curRoomID);
                JsonArray jsonArray = JsonParser.parse(incomingJsonMessage).getJsonArray("array");
            /*for (JsonValue jsonValue : jsonArray) {
                JsonObject curJsonMessage = jsonValue.asJsonObject();
//                String userID = curJsonMessage.getString("userID");
//                String curMessageType = curJsonMessage.getString("messageType");
                String curMessage = curJsonMessage.getString("message"); // (name + ": " + message) or ...
                System.out.println(curMessage);
            }*/
                for (int i = 0; i < jsonArray.size(); i++) {
                    //System.out.println(jsonArray.getString(i));
                    JsonObject curJsonMessage = JsonParser.parse(jsonArray.getString(i));
                    //JsonObject curJsonMessage = jsonArray.getJsonObject(i);
//                String userID = curJsonMessage.getString("userID");
//                String curMessageType = curJsonMessage.getString("messageType");
                    String curMessage = curJsonMessage.getString("message"); // (name + ": " + message) or ...
                    System.out.println(curMessage);
                }
            }
        }
    }
}
