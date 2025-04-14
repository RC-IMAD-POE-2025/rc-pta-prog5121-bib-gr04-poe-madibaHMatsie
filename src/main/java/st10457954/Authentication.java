package st10457954;

/**
 * Manages user login by validating credentials and tracking login status.
 * @author Madiba
 */
public class Authentication {
    // Stores the user object for credential validation
    private final User user;

    // Indicates if the user is currently logged in
    private boolean isLoggedIn;

    /**
     * Initializes Authentication with a User object.
     * @param user The User containing registration details
     */
    public Authentication(User user) 
    {
        this.user = user;
        this.isLoggedIn = false; // User is not logged in initially
    }

    /**
     * Validates login credentials against the user's registered details.
     * @param username The username to check
     * @param password The password to check
     * @return true if credentials are valid, false otherwise
     */
    public boolean loginUser(String username, String password) 
    {
        // Verifies username and password match the user's data
        isLoggedIn = username != null && password != null &&
                     username.equals(user.getUsername()) &&
                     password.equals(user.getPassword());
        return isLoggedIn;
    }

    /**
     * Provides a message based on the current login status.
     * @return A welcome message if logged in, or an error message if not
     */
    public String returnLoginStatus() 
    {
        if (isLoggedIn) {
            return "Welcome back " + user.getFirstName() + " " + 
                   user.getLastName() + "!\nit is great to see you.";
        } else {
            return "Username or password incorrect, please try again!";
        }
    }
}