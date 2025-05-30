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
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author stavr
 */
@WebServlet(name="NotifyAdmin", urlPatterns={"/NotifyAdmin"})
public class NotifyAdmin extends HttpServlet {
   
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
            out.println("<title>Servlet NotifyAdmin</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet NotifyAdmin at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    private static final List<Map<String, String>> notifications = new ArrayList<>();

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
        try{
            if (notifications == null) {
                System.out.println("Notifications list is null.");
            } else if (notifications.isEmpty()) {
                System.out.println("Notifications list is empty.");
            } else {
                System.out.println("Notifications list has " + notifications.size() + " items.");
            }
            Gson gson = new Gson();
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("status", "success");
            responseData.put("notifications", notifications);
            System.out.println("Response data being sent: " + gson.toJson(responseData));
            out.print(gson.toJson(responseData));
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while fetching notifications.");
            out.print(new Gson().toJson(errorResponse));
            out.flush();
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder jsonInput = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null){
            jsonInput.append(line);
        }
        try{
            Gson gson = new Gson();
            Map<String, Object> requestData = gson.fromJson(jsonInput.toString(), Map.class);
            Object incidentIdObj = requestData.get("incidentId");
            Object usernameObj = requestData.get("username");
            String incidentId = (incidentIdObj instanceof Double)
                    ? String.valueOf(((Double) incidentIdObj).intValue())
                    : (String) incidentIdObj;
            String username = (String) usernameObj;
            System.out.println("Volunteer " + username + " has volunteered for incident with ID: " + incidentId);
            Map<String, String> notification = new HashMap<>();
            notification.put("incidentId", incidentId);
            notification.put("username", username);
            notifications.add(notification);
            System.out.println("Notification added to the list. Current size: " + notifications.size());
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            Map<String, String> responseData = new HashMap<>();
            responseData.put("status", "success");
            responseData.put("message", "Admin notified of your participation.");
            out.print(new Gson().toJson(responseData));
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An error occurred while processing the request.");
            out.print(new Gson().toJson(errorResponse));
            out.flush();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String incidentId = request.getParameter("incident_id");
        String username = request.getParameter("username");
        if(incidentId == null || username == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Missing incident_id or username");
            response.getWriter().write(new Gson().toJson(errorResponse));
            return;
        }
        boolean notificationRemoved = false;
        for(Map<String, String> notification : notifications){
            if (notification.get("incidentId").equals(incidentId) && notification.get("username").equals(username)) {
                notifications.remove(notification);
                notificationRemoved = true;
                break;
            }
        }
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        if(notificationRemoved){
            Map<String, String> responseData = new HashMap<>();
            responseData.put("status", "success");
            responseData.put("message", "Notification deleted successfully");
            out.print(new Gson().toJson(responseData));
        }else{
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Notification not found");
            out.print(new Gson().toJson(errorResponse));
        }
        out.flush();
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
