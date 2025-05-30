/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import database.tables.EditUsersTable;
import database.tables.EditVolunteersTable;


/**
 *
 * @author stavr
 */
@WebServlet(name="Register", urlPatterns={"/Register"})
public class Register extends HttpServlet {
    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        String body = requestBody.toString();
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        String type = json.get("userType").getAsString();
        if(type.equals("user")){
            registerUser(response,body);
        }else if(type.equals("volunteer")){
            registerVolunteer(response,body);
        }else{            
        }
    }

    private void registerUser(HttpServletResponse response, String userJson) throws IOException {
        EditUsersTable eut = new EditUsersTable();
        try {
            eut.addUserFromJSON(userJson);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\": \"User registered successfully.\"}");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"An error occurred while registering the user.\"}");
        }
    }

    private void registerVolunteer(HttpServletResponse response, String volunteerJson) throws IOException {
        EditVolunteersTable evt = new EditVolunteersTable();
        try {
            evt.addVolunteerFromJSON(volunteerJson);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\": \"Volunteer registered successfully.\"}");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"An error occurred while registering the volunteer.\"}");
        }
    }
}
