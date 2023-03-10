package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import model.SerializationUtil;
import model.AdminUser;

/**
 * @author Daniel
 * This controller handles the User object related methods
 * Admins can create other admin accounts
 */

public class AdminController {
    /**
     * @return ArrayList<Object>
     */
    public static ArrayList<Object> readAdminAccountsFile() {
        ArrayList<Object> adminAccounts = new ArrayList<>();
        try {
			adminAccounts = SerializationUtil.deserialize("database/adminAccounts.ser");
            return adminAccounts;
		} catch (IOException | ClassNotFoundException e) {
			// e.printStackTrace();
		}
        return new ArrayList<Object>();
    }

    /**
     * Print all admin account details - email and password
     * For test cases
     */
    public static void readAdminAccountsFileAndPrint() {
        ArrayList<Object> adminAccounts = readAdminAccountsFile();
        for (int i = 0; i < adminAccounts.size(); i++) {
            AdminUser verifiedUser = (AdminUser) adminAccounts.get(i); 
            System.out.println("Email: " + verifiedUser.getEmail() + ", Password: " + verifiedUser.getPasswordHashed());
        }
    }

    /**
     * Helper function to verify if admin account is registered through email
     * We assume each email is tied to one admin account only - no duplicates
     * @param email
     * @return boolean
     */
    public static boolean isAdminAccountByEmail(String email) {
        ArrayList<Object> adminAccounts = readAdminAccountsFile();
        for (int i = 0; i < adminAccounts.size(); i++) {
            AdminUser verifiedUser = (AdminUser) adminAccounts.get(i);
            if (verifiedUser.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper function to verify if admin account is registered through password
     * We assume each password is tied to one admin account only - no duplicates
     * @param password
     * @return boolean
     */
    public static boolean isAdminAccountByPassword(String password) {
        ArrayList<Object> adminAccounts = readAdminAccountsFile();
        for (int i = 0; i < adminAccounts.size(); i++) {
            AdminUser verifiedUser = (AdminUser) adminAccounts.get(i);
            if (verifiedUser.validatePassword(password)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Uses AdminUser validatePassword to check if email and password inputted is registered as an admin account
     * @param email
     * @param password
     * @return boolean
     */
    public static boolean verify(String email, String password) {
        ArrayList<Object> adminAccounts = readAdminAccountsFile();
        for (int i = 0; i < adminAccounts.size(); i++) {
            AdminUser verifiedUser = (AdminUser) adminAccounts.get(i);
            if (verifiedUser.getEmail().equals(email) && verifiedUser.validatePassword(password)) {
                return true;
            }
        }
        return false;
    }

    // only for admin to login
    // allow the user to exit if they quit trying to login

     /**
      * Login sequence
      * @return boolean
      */
    public static boolean login() {
        System.out.println("=== Logging In ===");

        ArrayList<Object> adminAccounts = readAdminAccountsFile();
        File f = new File("database/adminAccounts.ser");
        if (f.exists()) {
            adminAccounts = readAdminAccountsFile();
        }
        else {
            System.out.println("No admin accounts registered! Please create an admin account!");
            createAdminAccount();
            return true;
        }

        boolean result = false;
        boolean exit = false;

        do {
            System.out.println("Please enter your email.");
            String email = InputController.getEmail();
            System.out.println("Please enter your password.");
            String password = InputController.getString();
            result = verify(email, password);

            if (!result) {
                System.out.println("Wrong email or password.");
                System.out.println("1. Enter again");
                System.out.println("2. Exit");
                System.out.print("Choice: ");
                if (InputController.getIntRange(1, 2) == 2) {
                    exit = true;
                }
            }
        } while (!result && !exit);

        if (result) {
            System.out.println("You have logged in successully.");
            return true;
        }
        return false;
    }

    /**
     * Admin account creation sequence
     */
    public static void createAdminAccount() {
        System.out.println("=== Creating new admin account ===");

        System.out.println("Please enter your new account's email.");
        String email = InputController.getEmail();
        System.out.println("Please enter your new account's password.");
        String password = InputController.getString();
        
        if (isAdminAccountByEmail(email)) {
            System.out.println("Admin account already exist!");
        }
        else {
            AdminUser newUser = new AdminUser(email, password);
            try {
                SerializationUtil.serialize(newUser, "database/adminAccounts.ser");
                System.out.println("New admin account registered!");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Admin account registering unsuccessful!");
            }
        }  
    }

    /**
     * Admin account deletetion sequence
     */
    public static void deleteAdminAccount() {
        System.out.println("=== Deleting admin account ===");

        System.out.println("Please enter the email of admin account to delete.");
        String email = InputController.getEmail();

        if (!isAdminAccountByEmail(email)) {
            System.out.println("Admin account does not exist!");
        }
        else {
            ArrayList<Object> adminAccounts = readAdminAccountsFile();
            for(int i = 0; i < adminAccounts.size(); i++) {
                AdminUser verifiedUser = (AdminUser) adminAccounts.get(i);
                if (verifiedUser.getEmail().equals(email)) {
                    System.out.println("Admin account " + email + " successfully deleted.");
                    adminAccounts.remove(i);
                    break;
                }
            }

            File dfile = new File("database/adminAccounts.ser");
            try {
                SerializationUtil.deleteFile(dfile);
            } catch (IOException e) {
                // e.printStackTrace();
            }
            
            for(int i = 0; i < adminAccounts.size(); i++) {
                AdminUser verifiedUser = (AdminUser) adminAccounts.get(i);
                try {
                    SerializationUtil.serialize(verifiedUser, "database/adminAccounts.ser");
                } catch (IOException e) {
                    // e.printStackTrace();
                }
    	    }
        }  
    }

    /**
     * Change password sequence
     */
    public static void changePassword() {
        ArrayList<Object> adminAccounts = readAdminAccountsFile();
        System.out.println("=== Changing account password ===");

        

        boolean exit = false;
        while (!exit) {
            System.out.println("Please enter your current password.");
            String oldPassword = InputController.getString();
            System.out.println("Please enter your new password.");
            String newPassword = InputController.getString();
            
            if (oldPassword.equals(newPassword)) {
                System.out.println("Same password! Would you like to try again? (y/n)");
                boolean booleanChoice = InputController.getBoolean();
                if (!booleanChoice) {
                    exit = true;
                }
            }
            else if (!isAdminAccountByPassword(oldPassword)) {
                System.out.println("Wrong current password! Would you like to try again? (y/n)");
                boolean booleanChoice = InputController.getBoolean();
                if (!booleanChoice) {
                    exit = true;
                }
            }
            else {
                for (int i = 0; i < adminAccounts.size(); i++) {
                    AdminUser currentUser = (AdminUser) adminAccounts.get(i);
                    if (currentUser.validatePassword(oldPassword)) {
                        exit = true;
                        System.out.println("Account password changed successfully");
                        currentUser.updatePassword(oldPassword, newPassword);
                    }
                }
    
                File dfile = new File("database/adminAccounts.ser");
                try {
                    SerializationUtil.deleteFile(dfile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                for(int i = 0; i < adminAccounts.size(); i++) {
                    AdminUser verifiedUser = (AdminUser) adminAccounts.get(i);
                    try {
                        SerializationUtil.serialize(verifiedUser, "database/adminAccounts.ser");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        

    }
}
