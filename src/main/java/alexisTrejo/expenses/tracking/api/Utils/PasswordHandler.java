package alexisTrejo.expenses.tracking.api.Utils;


import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHandler {

    // Create a BCryptPasswordEncoder instance
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public static String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }


    public static boolean validatePassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
