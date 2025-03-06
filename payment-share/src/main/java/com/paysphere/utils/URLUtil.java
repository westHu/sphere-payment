package com.paysphere.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLUtil {

    /**
     * URL 正则表达式
     */
    public static boolean isValidURL(String url) {

        String regex = "^(http|https):// ([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

}
