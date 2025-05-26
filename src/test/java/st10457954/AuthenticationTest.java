package st10457954;

import st10457954.Authentication;
import st10457954.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest 
{

    /** The User instance used to store test user data. */
    private User user;

    /** The Authentication instance under test, linked to the User instance. */
    private Authentication login;

    // Part 1 Test data
    private static final String VALID_USERNAME = "kyl_1";
    private static final String VALID_PASSWORD = "Ch&&sec@ke99!";
    private static final String VALID_CELL_PHONE_NUMBER = "+27838968976";
    private static final String VALID_FIRST_NAME = "Kyle";
    private static final String VALID_LAST_NAME = "Smith";
    private static final String INVALID_USERNAME = "kyle!!!!!!!";
    private static final String INVALID_PASSWORD = "password";

    // Expected messages
    private static final String SUCCESS_LOGIN_MESSAGE = "Welcome back " + VALID_FIRST_NAME + " " + VALID_LAST_NAME + "!\nit is great to see you.";
    private static final String FAILED_LOGIN_MESSAGE = "Username or password incorrect, please try again!";

    @BeforeEach
    public void setUp() {
        user = new User();
        user.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE_NUMBER, VALID_FIRST_NAME, VALID_LAST_NAME);
        login = new Authentication(user);
    }

    @Test
    void testSuccessfulLogin() {
        boolean result = login.loginUser(VALID_USERNAME, VALID_PASSWORD);
        assertTrue(result);
        assertEquals(SUCCESS_LOGIN_MESSAGE, login.returnLoginStatus());
    }

    @Test
    void testInvalidUsername() {
        boolean result = login.loginUser(INVALID_USERNAME, VALID_PASSWORD);
        assertFalse(result);
        assertEquals(FAILED_LOGIN_MESSAGE, login.returnLoginStatus());
    }

    @Test
    void testInvalidPassword() {
        boolean result = login.loginUser(VALID_USERNAME, INVALID_PASSWORD);
        assertFalse(result);
        assertEquals(FAILED_LOGIN_MESSAGE, login.returnLoginStatus());
    }

    @Test
    void testBothInvalidCredentials() {
        boolean result = login.loginUser(INVALID_USERNAME, INVALID_PASSWORD);
        assertFalse(result);
        assertEquals(FAILED_LOGIN_MESSAGE, login.returnLoginStatus());
    }

    @Test
    void testNullUsername() {
        boolean result = login.loginUser(null, VALID_PASSWORD);
        assertFalse(result);
        assertEquals(FAILED_LOGIN_MESSAGE, login.returnLoginStatus());
    }

    @Test
    void testNullPassword() {
        boolean result = login.loginUser(VALID_USERNAME, null);
        assertFalse(result);
        assertEquals(FAILED_LOGIN_MESSAGE, login.returnLoginStatus());
    }

    @Test
    void testBothNullCredentials() {
        boolean result = login.loginUser(null, null);
        assertFalse(result);
        assertEquals(FAILED_LOGIN_MESSAGE, login.returnLoginStatus());
    }

    

    @Test
    void testCaseSensitiveUsername() {
        boolean result = login.loginUser(VALID_USERNAME.toUpperCase(), VALID_PASSWORD);
        assertFalse(result);
        assertEquals(FAILED_LOGIN_MESSAGE, login.returnLoginStatus());
    }

    @Test
    void testCaseSensitivePassword() {
        boolean result = login.loginUser(VALID_USERNAME, VALID_PASSWORD.toLowerCase());
        assertFalse(result);
        assertEquals(FAILED_LOGIN_MESSAGE, login.returnLoginStatus());
    }

    @Test
    void testMultipleLoginAttempts() {
        // First attempt: invalid
        boolean result1 = login.loginUser(INVALID_USERNAME, INVALID_PASSWORD);
        assertFalse(result1);
        assertEquals(FAILED_LOGIN_MESSAGE, login.returnLoginStatus());

        // Second attempt: valid
        boolean result2 = login.loginUser(VALID_USERNAME, VALID_PASSWORD);
        assertTrue(result2);
        assertEquals(SUCCESS_LOGIN_MESSAGE, login.returnLoginStatus());
    }
}
