import Network.Postman;
import data.CurrentSessionData;
import layout.Layout;
import layout.RoomListLayout;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

public class SignInLayout implements Layout {

    CurrentSessionData userInfo;
    private final Postman server;
    private final Scanner in;
    public SignInLayout(Postman server,Scanner in) {
        this.server = server;
        this.in = in;
        userInfo = new CurrentSessionData(server);
        server.setCurSesData(userInfo);
    }

    private int signUp() {
        System.out.print("Enter your login: ");
        userInfo.login = in.nextLine();
        userInfo.password = generatePassword();
        return server.signUp(userInfo.login, userInfo.password);
    }

    private String generatePassword() {
        byte[] b = new byte[20];
        new Random().nextBytes(b);
        return new String(b, StandardCharsets.US_ASCII);
    }

    @Override
    public Layout start() {
        while (true) {
            int generatedId = signUp();
            if (generatedId != -1 && generatedId != -2) {
                userInfo.setMyUserID(generatedId);
                new Thread(new MessageReceiver(userInfo)).start();
                return new RoomListLayout(in, server, userInfo);
            }
            if (generatedId == -2) System.out.println("This login is already exist. Please, enter another login");
            if (generatedId == -1) System.out.println("Server error. Please try again");
        }
    }
}
