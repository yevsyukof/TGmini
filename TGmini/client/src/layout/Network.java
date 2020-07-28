//import com.fasterxml.jackson.databind.ObjectMapper;
//import data.MessageInfo;
//
//import javax.json.Json;
//import javax.json.JsonObject;
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class Network {
//    private final String urlString;
//    private int ok;
//    private int notOk;
//    public Network(String urlString) {
//        this.urlString = urlString;
//    }
//
//    public String sendToBase(String urlString, String method) {
//        return sendToBase(urlString,method,null);
//    }
//
//    public String sendToBase(String urlString, String method, String body) {
//        URL url;
//        HttpURLConnection connection = null;
//        while (true) {
//            try {
//                url = new URL(urlString);
//                connection = (HttpURLConnection) url.openConnection();
//                if (!method.equals("GET") && body != null && !body.equals("")) {
//                    connection.setDoOutput(true);
//                }
//                connection.setRequestMethod(method);
//                connection.setDoInput(true);
//                connection.setRequestProperty("Content-Type", "application/json");
//                connection.setRequestProperty("Accept", "application/json");
//
//                if (connection.getDoOutput()) {
//                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
//                    if (body != null){
//                        wr.write(body);
//                    }
//                    wr.flush();
//                }
//                if (connection.getInputStream() != null) {
//                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                    String data = br.readLine();//FIXME: read all lines
//                    ok++;
//                    return data;
//                } else {
//                    ok++;
//                    return null;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.out.println("NOT OK!");
//                notOk++;
//                System.out.println("ok: " + ok + " notOk: " +notOk);
//            } finally {
//                if (connection != null) {
//                    connection.disconnect();
//                }
//                try {
//                    Thread.sleep(300);
//                } catch (InterruptedException e) {
//                    System.out.println("TIMER ERROR!");
//                    //e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public MessageInfo getMessage(int number, String login, String password) {
//        ObjectMapper mapper = new ObjectMapper();
//        JsonObject jsonobj = Json.createObjectBuilder()
//                .add("number", number)
//                .add("login", login)
//                .add("password", password)
//                .build();
//
//        String jsonString = sendToBase(urlString + "/getMessage", "POST", jsonobj.toString());
//        MessageInfo toReturn = null;
//        if (jsonString == null) {
//            return null;
//        }
//        try {
//            toReturn = mapper.readValue(new StringReader(jsonString), MessageInfo.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return toReturn;
//    }
//
//    public void addMessage(String login, String password, String message) {
//        if (message.equals("")) {
//            return;
//        }
//        JsonObject jsonObj = Json.createObjectBuilder()
//                .add("login", login)
//                .add("password", password)
//                .add("message", message)
//                .build();
//        sendToBase(urlString + "/addMessage", "POST",jsonObj.toString());
//    }
//
//    public boolean signIn(String login, String password) {
//        JsonObject jsonObj = Json.createObjectBuilder()
//                .add("login", login)
//                .add("password", password)
//                .build();
//        String result = sendToBase(urlString + "/signIn", "POST",jsonObj.toString());
//        return result.equals("true");
//    }
//
//    public boolean enterInRoom(String login, String userPassword, int roomID) {
//        JsonObject jsonObj = Json.createObjectBuilder()
//                .add("login", login)
//                .add("password", userPassword)
//                .add("roomName", roomID)
//                .build();
//        String result = sendToBase(urlString + "/enterInRoom", "POST", jsonObj.toString());
//        return result.equals("true");
//    }
//}
