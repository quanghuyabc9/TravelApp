package com.ygaps.travelapp.utils;

import java.util.regex.Pattern;

public class CheckTool {
    public static boolean isValidEmail(String email) {
        //return Pattern.matches("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$", email);
        return email.length() >= 4;
    }

    public static boolean isValidPassword(String password){
        return password.length() >= 4;
    }
}
