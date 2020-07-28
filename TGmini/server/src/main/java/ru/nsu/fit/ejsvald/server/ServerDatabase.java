package ru.nsu.fit.ejsvald.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServerDatabase {

    public List<UserData> userData;

    public ServerDatabase() {
        this.userData = new ArrayList<>();
    }

    //private Data_clients DB = new Data_clients();

    public boolean isLoginExist(String login) {
        return false;//TODO
    }

    //    public boolean authentication(int id, String password) {
//        return true;//TODO
//    }
//    public int isLoginEnterCorrect(String login) {
//        if (login.length() > 20)
//            return 1;       // The login is too long. Please enter another login.
//        else if (login.length() == 0)
//            return 2;       // The login is too short. Please enter another login.
//        else if (!login.matches("^[a-zA-Z0-9_]+"))
//            return 3;       // Invalid characters entered. Please enter another login.
//        else
//            try {
//                if (!DB.isLoginExist(login))
//                    return 4;       // The user with this login is already exist. Please enter another login.
//            } catch (SQLException exc) {
//                System.out.println("SYSTEM_ERROR_1");
//            }
//        return 0;
//    }

    public boolean signUp(int id, String login, String password) {
//        int correction = isLoginEnterCorrect(login);
//        if (correction == 0) {
//            try {
//                DB.addNewUser(id, login, password);
//            } catch (SQLException exc) {
//                System.out.println("SYSTEM_ERROR_2");
//            }
//        }
//        return correction;
        userData.add(id,new UserData(id,login,password));
        return true;
    }

    public boolean authentication(int id, String password) {
        return true;
    }

    public boolean quite(int userID) {
        return true;
    }

}

