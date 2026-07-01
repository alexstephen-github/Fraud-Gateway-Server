package com.paygateway.fraud.service;

import java.security.SecureRandom;

public final class IdGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private IdGenerator() {
    }

    public static String withPrefix(String prefix) {
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < 14; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
