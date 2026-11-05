package lk.ijse.vishmibakehouse;

import lk.ijse.vishmibakehouse.bo.BOFactory;
import lk.ijse.vishmibakehouse.bo.BOTypes;
import lk.ijse.vishmibakehouse.bo.custom.AuthBO;
import lk.ijse.vishmibakehouse.dto.UserAccountDTO;
import lk.ijse.vishmibakehouse.util.ValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SystemCoreTest {

    @Test
    @DisplayName("Test BCrypt Authentication for Seed Accounts")
    public void testAuthentication() throws SQLException {
        AuthBO authBO = BOFactory.getInstance().getBO(BOTypes.AUTH);

        Optional<UserAccountDTO> admin = authBO.authenticate("admin", "admin123");
        assertTrue(admin.isPresent(), "Admin credentials should authenticate successfully");
        assertEquals("OWNER", admin.get().getAccessRole());

        Optional<UserAccountDTO> sales = authBO.authenticate("sales", "sales123");
        assertTrue(sales.isPresent(), "Sales credentials should authenticate successfully");
        assertEquals("SALES", sales.get().getAccessRole());

        Optional<UserAccountDTO> badPass = authBO.authenticate("admin", "wrong_password");
        assertFalse(badPass.isPresent(), "Wrong password must be rejected");

        Optional<UserAccountDTO> badUser = authBO.authenticate("non_existent_user", "12345");
        assertFalse(badUser.isPresent(), "Non-existent user must be rejected");
    }

    @Test
    @DisplayName("Test Validation Utility Rules")
    public void testValidationRules() {
        assertTrue(ValidationUtil.isValidPhone("0771234567"));
        assertTrue(ValidationUtil.isValidPhone("0112854321"));
        assertFalse(ValidationUtil.isValidPhone("12345"));

        assertTrue(ValidationUtil.isValidName("White Bread"));
        assertTrue(ValidationUtil.isValidName("Naveesha Niyumal"));
        assertFalse(ValidationUtil.isValidName(""));

        assertTrue(ValidationUtil.isPositiveDecimal("150.50"));
        assertFalse(ValidationUtil.isPositiveDecimal("-10.00"));
        assertFalse(ValidationUtil.isPositiveDecimal("0"));
        assertFalse(ValidationUtil.isPositiveDecimal("abc"));

        assertTrue(ValidationUtil.isPositiveInteger("25"));
        assertFalse(ValidationUtil.isPositiveInteger("-5"));
        assertFalse(ValidationUtil.isPositiveInteger("0"));
    }
}
