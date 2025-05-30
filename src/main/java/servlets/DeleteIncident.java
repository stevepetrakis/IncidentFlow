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
import com.google.gson.JsonObject;
import database.tables.EditIncidentsTable;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stavr
 */
@WebServlet(name="DeleteIncident", urlPatterns={"/DeleteIncident"})
public class DeleteIncident extends HttpServlet {
   
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
            out.println("<title>Servlet DeleteIncident</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DeleteIncident at " + request.getContextPath () + "</h1>");
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
        String incidentId = request.getParameter("incident_id");
        JsonObject jsonResponse = new JsonObject();
        if(incidentId == null || incidentId.isEmpty()){
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Incident ID is required.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }
        try{
            EditIncidentsTable incidentsTable = new EditIncidentsTable();
            boolean incidentExists = incidentsTable.incidentExists(incidentId);
            if(!incidentExists){
                jsonResponse.addProperty("status", "error");
                jsonResponse.addProperty("message", "Incident with the given ID does not exist");
                response.getWriter().write(jsonResponse.toString());
                return;
            }
            incidentsTable.deleteIncident(incidentId);
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("message", "Incident deleted successfully");
        }catch(ClassNotFoundException | SQLException ex){
            Logger.getLogger(DeleteIncident.class.getName()).log(Level.SEVERE, null, ex);
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Error deleting incident: " + ex.getMessage());
        }
        response.getWriter().write(jsonResponse.toString());
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
