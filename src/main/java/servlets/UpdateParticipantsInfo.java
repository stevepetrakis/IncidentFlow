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
@WebServlet(name="UpdateParticipantsInfo", urlPatterns={"/UpdateParticipantsInfo"})
public class UpdateParticipantsInfo extends HttpServlet {
   
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
            out.println("<title>Servlet UpdateParticipantsInfo</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UpdateParticipantsInfo at " + request.getContextPath () + "</h1>");
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
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        String body = requestBody.toString();
        JsonObject jsonResponse = new JsonObject();
        try{
            JsonObject requestData = JsonParser.parseString(body).getAsJsonObject();
            int participantId = requestData.get("participant_id").getAsInt();
            String success = requestData.has("success") ? requestData.get("success").getAsString() : null;
            String comment = requestData.has("comment") ? requestData.get("comment").getAsString() : null;
            String status = requestData.has("status") ? requestData.get("status").getAsString() : null;
            try(Connection con = DB_Connection.getConnection()){
                String query = "UPDATE participants SET success = ?, comment = ?, status = ? WHERE participant_id = ?";
                try (PreparedStatement pstmt = con.prepareStatement(query)) {
                    pstmt.setString(1, success);
                    pstmt.setString(2, comment);
                    pstmt.setString(3, status);
                    pstmt.setInt(4, participantId);
                    int rowsUpdated = pstmt.executeUpdate();
                    if(rowsUpdated > 0){
                        jsonResponse.addProperty("success", true);
                        jsonResponse.addProperty("message", "Participant updated successfully");
                        response.setStatus(HttpServletResponse.SC_OK);
                    }else{
                        jsonResponse.addProperty("success", false);
                        jsonResponse.addProperty("message", "Participant not found or not updated");
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error while updating participant: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        try (PrintWriter out = response.getWriter()) {
            out.write(jsonResponse.toString());
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
