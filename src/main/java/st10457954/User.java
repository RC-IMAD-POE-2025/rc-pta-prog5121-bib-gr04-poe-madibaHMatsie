package st10457954;

import java.util.regex.Pattern;

/**
 * Handles user registration and validation of personal details.
 * @author Madiba
 */
public class User {
   
    private String 
            username, 
            password, 
            cellphone, 
            firstName, 
            lastName;

    /**
     * Registers a user by validating and storing their details.
     * @param username The username to validate
     * @param password The password to validate
     * @param cellphone The cellphone number to validate
     * @param firstName The first name to validate
     * @param lastName The last name to validate
     * @return Feedback messages indicating validation results and registration outcome
     */
    public String registerUser(String username, String password, String cellphone, String firstName, String lastName) {
        String messages = ""; // Collects validation messages
        boolean validated = true; // Tracks if all validations pass

        // Validates username format
        if (checkUserName(username)) {
            messages += "Username successfully captured\n";
        } else {
            messages += "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.\n";
            validated = false;
        }

        // Validates password complexity
        if (checkPasswordComplexity(password)) {
            messages += "Password successfully captured\n";
        } else {
            messages += "Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.\n";
            validated = false;
        }

        // Validates cellphone number format
        if (checkCellPhoneNumber(cellphone)) {
            messages += "Cellphone number successfully captured\n";
        } else {
            messages += "Cellphone number is incorrectly formatted or does not contain an international code, please correct the number and try again.\n";
            validated = false;
        }

        // Validates first name
        if (checkName(firstName)) {
            messages += "First name successfully captured\n";
        } else {
            messages += "First name is invalid.\n";
            validated = false;
        }

        // Validates last name
        if (checkName(lastName)) {
            messages += "Last name successfully captured\n";
        } else {
            messages += "Last name is invalid.\n";
            validated = false;
        }

        // Stores details if all validations pass
        if (validated) 
        {
            this.username = username;
            this.password = password;
            this.cellphone = cellphone;
            this.firstName = firstName;
            this.lastName = lastName;
            messages += "\nREGISTRATION SUCCESS.";
        } else 
        {
            messages += "\nREGISTRATION FAILURE.";
        }

        return messages;
    }

    /**
     * Retrieves the username.
     * @return The stored username
     */
    public String getUsername() 
    {
        return username;
    }

    /**
     * Retrieves the password.
     * @return The stored password
     */
    public String getPassword() 
    {
        return password;
    }

    /**
     * Retrieves the cellphone number.
     * @return The stored cellphone number
     */
    public String getCellphone() 
    {
        return cellphone;
    }

    /**
     * Retrieves the first name.
     * @return The stored first name
     */
    public String getFirstName() 
    {
        return firstName;
    }

    /**
     * Retrieves the last name.
     * @return The stored last name
     */
    public String getLastName() 
    {
        return lastName;
    }

    /**
     * Checks if the username contains an underscore and is no longer than 5 characters.
     * @param username The username to validate
     * @return true if valid, false otherwise
     */
    private boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    /**
     * Checks if the password meets complexity requirements (≥ 8 characters, capital letter, number, special character).
     * @param password The password to validate
     * @return true if valid, false otherwise
     */
    private boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) return false;
        return password.matches(".*[A-Z].*") && // Capital letter
               password.matches(".*[0-9].*") && // Number
               password.matches(".*[!@#$%^&*()].*"); // Special char
    }

    /**
     * Checks if the cellphone number starts with +27 and has 9 digits.
     * @param cellphone The cellphone number to validate
     * @return true if valid, false otherwise
     */
    private boolean checkCellPhoneNumber(String cellphone) {
        return cellphone != null && Pattern.matches("^\\+27[0-9]{9}$", cellphone);
    }

    /**
     * Checks if the name contains only letters and is non-empty.
     * @param name The name to validate
     * @return true if valid, false otherwise
     */
    private boolean checkName(String name) {
        return name != null && !name.trim().isEmpty() && name.matches("^[a-zA-Z]+$");
    }
}