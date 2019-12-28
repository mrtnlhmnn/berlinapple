package de.mrtnlhmnn.berlinapple;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptPassword {
    private static String password = "TODO";

    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        for (int i=0; i<10; i++) {
            System.out.println(passwordEncoder.encode(password));
        }
    }
}
