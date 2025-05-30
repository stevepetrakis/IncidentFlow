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
@WebServlet(name="UpdateParticipants", urlPatterns={"/UpdateParticipants"})
public class UpdateParticipants extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UpdateParticipants</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UpdateParticipants at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
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
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
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
            String volunteerType = requestData.get("volunteer_type").getAsString();
            requestData.remove("username");
            requestData.remove("volunteer_type");
            Gson gson = new Gson();
            Notification[] notifications = gson.fromJson(requestData.get("notifications"), Notification[].class);
            EditParticipantsTable editParticipantsTable = new EditParticipantsTable();
            for(Notification notification : notifications){
                Participant participant = new Participant();
                participant.setIncident_id(notification.getIncident_id());
                participant.setVolunteer_username(username);
                participant.setVolunteer_type(volunteerType);
                participant.setStatus("accepted");
                participant.setSuccess("null");
                participant.setComment("null");
                editParticipantsTable.createNewParticipant(participant);
            }
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Volunteer participation has been successfully updated.");
            response.setStatus(HttpServletResponse.SC_OK);
        }catch(Exception e){
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error while updating the participant data");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        response.getWriter().write(jsonResponse.toString());
    }

    private class Notification {
        private int incident_id;
        private String incident_type;
        private String address;
        private String description;
        private String danger;
        private String start_datetime;

        public int getIncident_id() {
            return incident_id;
        }

        public void setIncident_id(int incident_id) {
            this.incident_id = incident_id;
        }

        public String getIncident_type() {
            return incident_type;
        }

        public void setIncident_type(String incident_type) {
            this.incident_type = incident_type;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDanger() {
            return danger;
        }

        public void setDanger(String danger) {
            this.danger = danger;
        }

        public String getStart_datetime() {
            return start_datetime;
        }

        public void setStart_datetime(String start_datetime) {
            this.start_datetime = start_datetime;
        }
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
