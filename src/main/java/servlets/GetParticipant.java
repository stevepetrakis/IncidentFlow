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
import org.json.JSONArray;

/**
 *
 * @author stavr
 */
@WebServlet(name="GetParticipant", urlPatterns={"/GetParticipant"})
public class GetParticipant extends HttpServlet {
   
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
            out.println("<title>Servlet GetParticipant</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GetParticipant at " + request.getContextPath () + "</h1>");
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
        String userType = request.getParameter("userType");
        response.setContentType("application/json;charset=UTF-8");
        JSONObject jsonResponse = new JSONObject();
        if("participant".equalsIgnoreCase(userType)){
            getAllParticipants(response, jsonResponse);
        }else{
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid userType parameter.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try(PrintWriter out = response.getWriter()){
                out.print(jsonResponse.toString());
            }
        }
    }

    private void getAllParticipants(HttpServletResponse response, JSONObject jsonResponse) throws IOException{
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try(PrintWriter out = response.getWriter()){
            con = DB_Connection.getConnection();
            String query = "SELECT participant_id, incident_id, volunteer_type, volunteer_username, status, success, comment FROM participants";
            pstmt = con.prepareStatement(query);
            rs = pstmt.executeQuery();
            JSONArray participantsArray = new JSONArray();
            while(rs.next()){
                JSONObject participantDetails = new JSONObject();
                participantDetails.put("participant_id", rs.getInt("participant_id"));
                participantDetails.put("incident_id", rs.getInt("incident_id"));
                participantDetails.put("volunteer_type", rs.getString("volunteer_type"));
                participantDetails.put("volunteer_username", rs.getString("volunteer_username"));
                participantDetails.put("status", rs.getString("status"));
                participantDetails.put("success", rs.getString("success"));
                participantDetails.put("comment", rs.getString("comment"));
                participantsArray.put(participantDetails);
            }
            jsonResponse.put("success", true);
            jsonResponse.put("participants", participantsArray);
            out.print(jsonResponse.toString());
        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Server error: " + e.getMessage());
            try(PrintWriter out = response.getWriter()){
                out.print(jsonResponse.toString());
            }
        }finally{
            try{
                if(rs != null) rs.close();
                if(pstmt != null) pstmt.close();
                if(con != null) con.close();
            }catch(Exception ex){
                ex.printStackTrace();
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
