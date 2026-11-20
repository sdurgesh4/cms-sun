package com.inturn.suncomputer;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateBcryptHash {
    public static void main(String[] args) {
        // Replace with your desired new password
        String plainPassword = "admin123";

        // Default strength is 10 (rounds)
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(plainPassword);

        System.out.println("Plain Password: " + plainPassword);
        System.out.println("BCrypt Hash:    " + hashedPassword);
    }
}