package Controller;

import DB.DBConnection;
import Model.SignUpBean;
import Model.UserBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // Method to check if the email is already in use
    public boolean isEmailUsed(String email) {
        String sql = "SELECT 1 FROM tbl_user WHERE email = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            return true; // Assume email is used in case of error to prevent duplicate entry
        }
    }

    // Updated signupUser method to return status strings
    public String SignUpUser(SignUpBean user) {
        if (isEmailUsed(user.getEmail())) {
            return "emailUsed";
        }

        String sql = "INSERT INTO tbl_user (name, username, password, email, city, address, postal_code) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getCity());
            ps.setString(6, user.getAddress());
            ps.setString(7, user.getPostalCode());
            int result = ps.executeUpdate();
            return result > 0 ? "success" : "failure";
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            return "failure";
        }
    }

    // Method to get user by username
    public UserBean getUserByUsername(String username) {
        String sql = "SELECT * FROM tbl_user WHERE username = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UserBean user = new UserBean();
                user.setName(rs.getString("name"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
        }
        return null;
    }

    // Method to update user details
    public boolean updateUser(UserBean user) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE tbl_user SET ");
        boolean first = true;

        if (user.getName() != null) {
            sqlBuilder.append("name = ?");
            first = false;
        }
        if (user.getEmail() != null) {
            if (!first) sqlBuilder.append(", ");
            sqlBuilder.append("email = ?");
            first = false;
        }
        if (user.getPassword() != null) {
            if (!first) sqlBuilder.append(", ");
            sqlBuilder.append("password = ?");
        }

        sqlBuilder.append(" WHERE username = ?");

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {
            int index = 1;

            if (user.getName() != null) {
                ps.setString(index++, user.getName());
            }
            if (user.getEmail() != null) {
                ps.setString(index++, user.getEmail());
            }
            if (user.getPassword() != null) {
                ps.setString(index++, user.getPassword());
            }
            ps.setString(index, user.getUsername());

            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            return false;
        }
    }

    // Method to delete user by username
    public boolean deleteUser(String username) {
        String sql = "DELETE FROM tbl_user WHERE username = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            return false;
        }
    }
}