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
import database.tables.EditParticipantsTable;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stavr
 */
@WebServlet(name="DeleteParticipant", urlPatterns={"/DeleteParticipant"})
public class DeleteParticipant extends HttpServlet {
   
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
            out.println("<title>Servlet DeleteParticipant</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DeleteParticipant at " + request.getContextPath () + "</h1>");
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
        String participantIdStr = request.getParameter("participant_id");
        JsonObject jsonResponse = new JsonObject();
        if(participantIdStr == null || participantIdStr.isEmpty()){
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Participant ID is required.");
            response.getWriter().write(jsonResponse.toString());
            return;
        }
        try{
            int participantId = Integer.parseInt(participantIdStr);
            EditParticipantsTable participantsTable = new EditParticipantsTable();
            boolean participantExists = participantsTable.participantExists(participantId);
            if(!participantExists){
                jsonResponse.addProperty("status", "error");
                jsonResponse.addProperty("message", "Participant with the given ID does not exist.");
                response.getWriter().write(jsonResponse.toString());
                return;
            }
            participantsTable.deleteParticipant(participantId);
            jsonResponse.addProperty("status", "success");
            jsonResponse.addProperty("message", "Participant deleted successfully.");
        }catch(NumberFormatException e){
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Invalid participant ID format.");
        }catch(ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DeleteParticipant.class.getName()).log(Level.SEVERE, null, ex);
            jsonResponse.addProperty("status", "error");
            jsonResponse.addProperty("message", "Error deleting participant: " + ex.getMessage());
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
