package com.ptitB22CN539.LaptopShop.Utils;

import jakarta.annotation.Nullable;

import java.util.Random;

public class GeneratedRandomCode {
    public static String generateRandomCode(@Nullable Integer length) {
        if (length == null) {
            length = 6;
        }
        Random random = new Random();
        Long randomNumber = random.nextLong(1L, (long) Math.pow(10, length));
        return String.format("%0" + length + "d", randomNumber);
    }
}
