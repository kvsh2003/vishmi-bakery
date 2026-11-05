package lk.ijse.vishmibakehouse.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(?:0|94|\\+94)?(7[0-9]{8}|11[0-9]{7}|[0-9]{9,10})$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s.'-]{2,100}$");
    private static final Pattern VEHICLE_NO_PATTERN = Pattern.compile("^(?:[A-Z]{2,3}\\s?[A-Z]{1,2}\\s?-\\s?[0-9]{4}|[0-9]{2,3}\\s?-\\s?[0-9]{4}|[A-Z]{2}\\s?[0-9]{4})$");

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return true; // Optional field in some forms
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        return NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isValidVehicleNo(String regNo) {
        if (regNo == null || regNo.trim().isEmpty()) return false;
        return VEHICLE_NO_PATTERN.matcher(regNo.trim().toUpperCase()).matches();
    }

    public static boolean isPositiveDecimal(String val) {
        if (val == null || val.trim().isEmpty()) return false;
        try {
            BigDecimal bd = new BigDecimal(val.trim());
            return bd.compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNonNegativeDecimal(String val) {
        if (val == null || val.trim().isEmpty()) return false;
        try {
            BigDecimal bd = new BigDecimal(val.trim());
            return bd.compareTo(BigDecimal.ZERO) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveInteger(String val) {
        if (val == null || val.trim().isEmpty()) return false;
        try {
            int num = Integer.parseInt(val.trim());
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
