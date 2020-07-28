import Network.Postman;
import layout.Layout;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Postman server = new Postman("http://localhost:8080");
        Scanner sc = new Scanner(System.in);
        Layout signInLayout = new SignInLayout(server, sc);
        while(true) {
            signInLayout = signInLayout.start();
        }
    }
}
