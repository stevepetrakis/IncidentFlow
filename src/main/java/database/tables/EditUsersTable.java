/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.tables;

import mainClasses.User;
import com.google.gson.Gson;
import mainClasses.User;
import database.DB_Connection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import mainClasses.Participant;
import java.util.List;
import java.util.Arrays;
import java.sql.PreparedStatement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
/**
 *
 * @author Mike
 */
public class EditUsersTable {
 
    public void addUserFromJSON(String json) throws ClassNotFoundException{
         User user=jsonToUser(json);
         addNewUser(user);
    }
    
    public User jsonToUser(String json){
         Gson gson = new Gson();
        User user = gson.fromJson(json, User.class);
        return user;
    }
    
    public String userToJSON(User user){
         Gson gson = new Gson();

        String json = gson.toJson(user, User.class);
        return json;
    }
    
    public boolean userExists(String username) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String query = "SELECT COUNT(*) AS count FROM users WHERE username='" + username + "'";
        ResultSet rs = stmt.executeQuery(query);
        boolean exists = false;
        if(rs.next()){
            exists = rs.getInt("count") > 0;
        }
        rs.close();
        stmt.close();
        con.close();
        return exists;
    }
    
    public void updateUser(String username,String key,String value) throws SQLException, ClassNotFoundException{
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String update="UPDATE users SET "+key+"='"+value+"' WHERE username = '"+username+"'";
        stmt.executeUpdate(update);
        stmt.close();
        con.close();
    }

    public void deleteUser(String username) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String deleteQuery = "DELETE FROM users WHERE username='" + username + "'";
        stmt.executeUpdate(deleteQuery);
        stmt.close();
        con.close();
    }

    public User getUserByUsername(String username) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs;
        try{
            rs = stmt.executeQuery("SELECT * FROM users WHERE username = '" + username + "' LIMIT 1");
            if(rs.next()){
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                User user = gson.fromJson(json, User.class);
                return user;
            }
        }catch(Exception e){
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }finally{
            stmt.close();
            con.close();
        }
        return null;
    }


    public void updateUserFields(String username, JsonObject updates) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        try{
            StringBuilder query = new StringBuilder("UPDATE users SET ");
            boolean first = true;
            for(String key : updates.keySet()){
                if(!first){
                    query.append(", ");
                }
                query.append(key).append(" = ?");
                first = false;
            }
            query.append(" WHERE username = ?");
            PreparedStatement pstmt = con.prepareStatement(query.toString());
            int index = 1;
            for(String key : updates.keySet()){
                pstmt.setObject(index++, updates.get(key).getAsString());
            }
            pstmt.setString(index, username);
            pstmt.executeUpdate();
            pstmt.close();
        }catch(SQLException e){
            e.printStackTrace();
            throw e;
        }finally{
            con.close();
        }
    }
   
    
    public User databaseToUsers(String username, String password) throws SQLException, ClassNotFoundException{
         Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs;
        try {
            rs = stmt.executeQuery("SELECT * FROM users WHERE username = '" + username + "' AND password='"+password+"'");
            rs.next();
            String json=DB_Connection.getResultsToJSON(rs);
            Gson gson = new Gson();
            User user = gson.fromJson(json, User.class);
            return user;
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        return null;
    }
    
    public String databaseUserToJSON(String username, String password) throws SQLException, ClassNotFoundException{
         Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();

        ResultSet rs;
        try {
            rs = stmt.executeQuery("SELECT * FROM users WHERE username = '" + username + "' AND password='"+password+"'");
            rs.next();
            String json=DB_Connection.getResultsToJSON(rs);
            return json;
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        return null;
    }


     public void createUsersTable() throws SQLException, ClassNotFoundException{
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String query = "CREATE TABLE users "
                + "(user_id INTEGER not NULL AUTO_INCREMENT, "
                + "    username VARCHAR(30) not null unique,"
                + "    email VARCHAR(50) not null unique,	"
                + "    password VARCHAR(32) not null,"
                + "    firstname VARCHAR(30) not null,"
                + "    lastname VARCHAR(30) not null,"
                + "    birthdate DATE not null,"
                + "    gender  VARCHAR (7) not null,"
                + "    afm  VARCHAR (10) not null,"
                + "    country VARCHAR(30) not null,"
                + "    address VARCHAR(100) not null,"
                + "    municipality VARCHAR(50) not null,"
                + "    prefecture VARCHAR(15) not null,"
                + "    job VARCHAR(200) not null,"
                + "    telephone VARCHAR(14) not null unique,"
                  + "    lat DOUBLE,"
                + "    lon DOUBLE,"
                + " PRIMARY KEY (user_id))";
        stmt.execute(query);
        stmt.close();
    }
    
    
    /**
     * Establish a database connection and add in the database.
     *
     * @throws ClassNotFoundException
     */
    public void addNewUser(User user) throws ClassNotFoundException {
        try {
            Connection con = DB_Connection.getConnection();

            Statement stmt = con.createStatement();

            String insertQuery = "INSERT INTO "
                    + " users (username,email,password,firstname,lastname,birthdate,gender,afm,country,address,municipality,prefecture,"
                    + "job,telephone,lat,lon)"
                    + " VALUES ("
                    + "'" + user.getUsername() + "',"
                    + "'" + user.getEmail() + "',"
                    + "'" + user.getPassword() + "',"
                    + "'" + user.getFirstname() + "',"
                    + "'" + user.getLastname() + "',"
                    + "'" + user.getBirthdate() + "',"
                    + "'" + user.getGender() + "',"
                    + "'" + user.getAfm() + "',"
                    + "'" + user.getCountry() + "',"
                    + "'" + user.getAddress() + "',"
                    + "'" + user.getMunicipality() + "',"
                    + "'" + user.getPrefecture() + "',"
                    + "'" + user.getJob() + "',"
                    + "'" + user.getTelephone() + "',"
                    + "'" + user.getLat() + "',"
                    + "'" + user.getLon() + "'"
                    + ")";
            
            System.out.println(insertQuery);
            stmt.executeUpdate(insertQuery);
            System.out.println("# The user was successfully added in the database.");
            stmt.close();
        }catch (SQLException ex){
            Logger.getLogger(EditUsersTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}