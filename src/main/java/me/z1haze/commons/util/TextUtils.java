package me.z1haze.commons.util;

public class TextUtils {
    public static String capitalizeFully(String text) {
        String[] words = text.split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
        }
        return String.join(" ", words);
    }
}
