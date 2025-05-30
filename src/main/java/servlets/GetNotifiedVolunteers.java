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

/**
 *
 * @author stavr
 */
@WebServlet(name="GetNotifiedVolunteers", urlPatterns={"/GetNotifiedVolunteers"})
public class GetNotifiedVolunteers extends HttpServlet {
   
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
            out.println("<title>Servlet GetNotifiedVolunteers</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GetNotifiedVolunteers at " + request.getContextPath () + "</h1>");
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
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            String username = request.getParameter("username");
            if (username == null || username.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Invalid username.\"}");
                return;
            }

            List<String> notifiedIncidentIds  = NotifyVolunteers.getNotifiedVolunteerKeys(username);
            if (notifiedIncidentIds  == null) {
                out.print("{\"success\": true, \"notifications\": []}");
                return ;
            }
            EditIncidentsTable editIncidentsTable = new EditIncidentsTable();
            List<Incident> fullNotifications = new ArrayList<>();
            for(String incidentId : notifiedIncidentIds){
                try{
                    Incident incident = editIncidentsTable.getIncidentById(incidentId);
                    if(incident != null) {
                        fullNotifications.add(incident);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            Gson gson = new Gson();
            String notificationsJson = gson.toJson(fullNotifications );
            out.print("{\"success\": true, \"notifications\": " + notificationsJson + "}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Error retrieving notifications.\"}");
        } finally {
            out.close();
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
