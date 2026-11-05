package lk.ijse.vishmibakehouse.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public static void main(String[] args) {
        System.out.println("admin123: " + hashPassword("admin123"));
        System.out.println("manager123: " + hashPassword("manager123"));
        System.out.println("prod123: " + hashPassword("prod123"));
        System.out.println("sales123: " + hashPassword("sales123"));
        System.out.println("pack123: " + hashPassword("pack123"));
        System.out.println("del123: " + hashPassword("del123"));
    }
}
