package lk.ijse.vishmibakehouse.util;

import lk.ijse.vishmibakehouse.dto.UserAccountDTO;

public class SecurityContext {
    private static UserAccountDTO currentUser;

    public static void setCurrentUser(UserAccountDTO user) {
        currentUser = user;
    }

    public static UserAccountDTO getCurrentUser() {
        return currentUser;
    }

    public static boolean isAuthenticated() {
        return currentUser != null;
    }

    public static void clear() {
        currentUser = null;
    }

    public static boolean isOwnerOrManager() {
        if (currentUser == null) return false;
        String role = currentUser.getAccessRole();
        return "OWNER".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role);
    }

    public static boolean canAccess(String section) {
        if (currentUser == null) return false;
        if (isOwnerOrManager()) return true;

        String role = currentUser.getAccessRole().toUpperCase();
        return switch (section.toLowerCase()) {
            case "dashboard" -> true;
            case "products", "ingredients", "purchases" -> role.equals("PRODUCTION");
            case "production", "usage", "leftovers" -> role.equals("PRODUCTION");
            case "outlet-sales", "special-orders" -> role.equals("SALES");
            case "packing-dispatch" -> role.equals("PACKING");
            case "deliveries" -> role.equals("DELIVERY");
            case "employees", "reports" -> false; // Strictly Owner / Manager only
            default -> false;
        };
    }
}
