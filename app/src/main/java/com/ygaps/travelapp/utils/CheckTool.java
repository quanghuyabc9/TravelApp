package com.ygaps.travelapp.utils;

import java.util.regex.Pattern;

public class CheckTool {
    public static boolean isValidEmail(String email) {
<<<<<<< HEAD
        //return Pattern.matches("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$", email);
        return email.length() >= 4;
=======
//        return Pattern.matches("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$", email);
        return true;
>>>>>>> d273eaca17c1f7bd04203d47591829d698c07d4c
    }

    public static boolean isValidPassword(String password){
        return password.length() >= 4;
    }
}
