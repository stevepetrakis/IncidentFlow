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
import java.util.Iterator;

/**
 *
 * @author stavr
 */
@WebServlet(name="NotifyVolunteers", urlPatterns={"/NotifyVolunteers"})
public class NotifyVolunteers extends HttpServlet {
   
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
            out.println("<title>Servlet NotifyVolunteers</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet NotifyVolunteers at " + request.getContextPath () + "</h1>");
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

    private static Map<String, List<Volunteer>> notifiedVolunteers = new HashMap<>();

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
        Gson gson = new Gson();
        try{
            String incidentId = request.getParameter("incident_id");
            String volunteerType = request.getParameter("volunteer_type");
            if(incidentId == null || volunteerType == null || incidentId.isEmpty() || volunteerType.isEmpty()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Missing or invalid parameters.\"}");
                return;
            }
            EditVolunteersTable editVolunteersTable = new EditVolunteersTable();
            ArrayList<Volunteer> volunteers = editVolunteersTable.getVolunteers(volunteerType);
            if(volunteers == null || volunteers.isEmpty()){
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"success\": false, \"message\": \"No volunteers available for the selected type.\"}");
                return;
            }
            synchronized (notifiedVolunteers) {
                notifiedVolunteers.put(incidentId, volunteers);
            }
            for(Volunteer volunteer : volunteers){
                System.out.println("Notifying volunteer: " + volunteer.getFirstname() + " " + volunteer.getLastname() +
                        ", Email: " + volunteer.getEmail() + ", Phone: " + volunteer.getTelephone());
            }
            String volunteerJson = gson.toJson(volunteers);
            response.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"success\": true, \"message\": \"Volunteers notified successfully.\", \"volunteers\": " + volunteerJson + "}");
        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"An error occurred while notifying volunteers.\"}");
        }finally{
            out.close();
        }
    }

    public static List<String> getNotifiedVolunteerKeys(String username) {
        List<String> keys = new ArrayList<>();
        synchronized (notifiedVolunteers){
            for (Map.Entry<String, List<Volunteer>> entry : notifiedVolunteers.entrySet()) {
                for (Volunteer volunteer : entry.getValue()) {
                    if (volunteer.getUsername().equals(username)) {
                        keys.add(entry.getKey());
                    }
                }
            }
        }
        return keys;
    }

    public static boolean deleteNotification(String username, String incidentId){
        synchronized(notifiedVolunteers){
            for(Map.Entry<String, List<Volunteer>> entry : notifiedVolunteers.entrySet()){
                if(entry.getKey().equals(incidentId)){
                    List<Volunteer> volunteers = entry.getValue();
                    Iterator<Volunteer> iterator = volunteers.iterator();
                    while (iterator.hasNext()) {
                        Volunteer volunteer = iterator.next();
                        if(volunteer.getUsername().equals(username)){
                            iterator.remove();
                            if(volunteers.isEmpty()){
                                notifiedVolunteers.remove(incidentId);
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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
