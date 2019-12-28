package de.mrtnlhmnn.berlinapple;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = "TODO";
        for (int i=0; i<10; i++) {
            System.out.println(passwordEncoder.encode(password));
        }
    }
}
