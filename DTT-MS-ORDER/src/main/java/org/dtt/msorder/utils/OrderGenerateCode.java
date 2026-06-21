package org.dtt.msorder.utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class OrderGenerateCode {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String generate(UUID userId) {
        String userPart = userId.toString()
                .substring(0, 4)
                .toUpperCase();
        String randomPart = randomLetters();
        int randomNumber = ThreadLocalRandom.current()
                .nextInt(100, 999);
        return "#ORDER-" + userPart + "-" + randomNumber + "-" + randomPart;
    }

    private static String randomLetters() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(CHARS.charAt(ThreadLocalRandom.current().nextInt(CHARS.length())));
        }
        return sb.toString();
    }

}
