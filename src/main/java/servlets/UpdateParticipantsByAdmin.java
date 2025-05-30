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
import java.sql.SQLException;
import database.tables.EditParticipantsTable;
import mainClasses.Participant;
import com.google.gson.Gson;
import database.DB_Connection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;
import java.io.BufferedReader;

/**
 *
 * @author stavr
 */
@WebServlet(name="UpdateParticipantsByAdmin", urlPatterns={"/UpdateParticipantsByAdmin"})
public class UpdateParticipantsByAdmin extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        StringBuilder requestBody = new StringBuilder();
        try(BufferedReader reader = request.getReader()){
            String line;
            while((line = reader.readLine()) != null){
                requestBody.append(line);
            }
        }
        String body = requestBody.toString();
        JsonObject jsonResponse = new JsonObject();
        try{
            JsonObject requestData = JsonParser.parseString(body).getAsJsonObject();
            String username = requestData.get("username").getAsString();
            int incidentId = requestData.get("incident_id").getAsInt();
            String volunteerType = getVolunteerType(username);
            if(volunteerType == null){
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Volunteer not found.");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(jsonResponse.toString());
                return;
            }
            Participant participant = new Participant();
            participant.setIncident_id(incidentId);
            participant.setVolunteer_username(username);
            participant.setVolunteer_type(volunteerType);
            participant.setStatus("accepted");
            participant.setSuccess("null");
            participant.setComment("null");
            EditParticipantsTable editParticipantsTable = new EditParticipantsTable();
            editParticipantsTable.createNewParticipant(participant);
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Volunteer participation has been successfully updated.");
            response.setStatus(HttpServletResponse.SC_OK);
        }catch(Exception e){
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error while updating participant data.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        response.getWriter().write(jsonResponse.toString());
    }

    /**
     * Fetch the volunteer type based on the username from the database.
     * @param username the volunteer's username
     * @return the volunteer type, or null if not found
     */
    private String getVolunteerType(String username) {
        String volunteerType = null;
        try(Connection conn = DB_Connection.getConnection()){
            String query = "SELECT volunteer_type FROM volunteers WHERE username = ?";
            try(PreparedStatement ps = conn.prepareStatement(query)){
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                if(rs.next()){
                    volunteerType = rs.getString("volunteer_type");
                }
            }
        }catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return volunteerType;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

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
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
