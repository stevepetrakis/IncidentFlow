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
import database.DB_Connection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import database.tables.EditVolunteersTable;
import mainClasses.Volunteer;
import java.util.Map;
import java.util.HashMap;
import database.tables.EditIncidentsTable;
import mainClasses.Incident;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;

/**
 *
 * @author stavr
 */
@WebServlet(name="DeleteNotification", urlPatterns={"/DeleteNotification"})
public class DeleteNotification extends HttpServlet {
   
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
            out.println("<title>Servlet DeleteNotification</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DeleteNotification at " + request.getContextPath () + "</h1>");
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
        PrintWriter out = response.getWriter();
        try{
            BufferedReader reader = request.getReader();
            Gson gson = new Gson();
            JsonObject requestBody = gson.fromJson(reader, JsonObject.class);
            String username = requestBody.get("username").getAsString();
            String incidentId = requestBody.get("incident_id").getAsString();
            if(username == null || username.isEmpty() || incidentId == null || incidentId.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Invalid parameters.\"}");
                return;
            }
            boolean result = NotifyVolunteers.deleteNotification(username, incidentId);
            if(result){
                out.print("{\"success\": true}");
            }else{
                out.print("{\"success\": false, \"message\": \"Failed to delete notification.\"}");
            }
        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Error deleting notification.\"}");
        }finally{
            out.close();
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
