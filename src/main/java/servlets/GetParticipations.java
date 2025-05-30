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
import org.json.JSONArray;

/**
 *
 * @author stavr
 */
@WebServlet(name="GetParticipations", urlPatterns={"/GetParticipations"})
public class GetParticipations extends HttpServlet {
   
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
            out.println("<title>Servlet GetParticipations</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GetParticipations at " + request.getContextPath () + "</h1>");
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
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();
        try{
            String volunteerUsername = request.getParameter("username");
            if(volunteerUsername != null && !volunteerUsername.isEmpty()){
                getVolunteerParticipations(volunteerUsername, jsonResponse);
            }else{
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Missing username parameter.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }catch(Exception e){
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Unexpected server error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }finally{
            out.print(jsonResponse.toString());
            out.close();
        }
    }

    private void getVolunteerParticipations(String volunteerUsername, JSONObject jsonResponse) throws Exception {
        try(Connection con = DB_Connection.getConnection();
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM participants WHERE volunteer_username = ?")){
            pstmt.setString(1, volunteerUsername);
            try(ResultSet rs = pstmt.executeQuery()){
                JSONArray participationsArray = new JSONArray();
                while(rs.next()){
                    JSONObject participationDetails = new JSONObject();
                    participationDetails.put("participant_id", rs.getInt("participant_id"));
                    participationDetails.put("incident_id", rs.getInt("incident_id"));
                    participationDetails.put("volunteer_type", rs.getString("volunteer_type"));
                    participationDetails.put("volunteer_username", rs.getString("volunteer_username"));
                    participationDetails.put("status", rs.getString("status"));
                    participationDetails.put("success", rs.getString("success"));
                    participationDetails.put("comment", rs.getString("comment"));
                    participationsArray.put(participationDetails);
                }
                if(participationsArray.length() > 0){
                    jsonResponse.put("success", true);
                    jsonResponse.put("participations", participationsArray);
                }else{
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "No participations found for the given volunteer.");
                }
            }
        }
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
