package st10457954;

import javax.swing.JFrame;

/**
 *
 * @author Madiba
 */
public class MadibaConnect {
    public static void main(String[] args)
    {
        // Main logic instances
        User pitse = new User();
        Authentication securityCheck = new Authentication(pitse);
        
        // Initialize the GUI constructed by the User & Auth validators 
        RegistrationInterface registrationInterface = new RegistrationInterface(pitse, securityCheck);
        registrationInterface.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        registrationInterface.setLocationRelativeTo(null);
        registrationInterface.setVisible(true);
    }
    
}
