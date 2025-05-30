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

/**
 *
 * @author stavr
 */
@WebServlet(name="GetIncident", urlPatterns={"/GetIncident"})
public class GetIncident extends HttpServlet {
   
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
            out.println("<title>Servlet GetIncident</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GetIncident at " + request.getContextPath () + "</h1>");
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
        String incident_id = request.getParameter("incident_id");
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        response.setContentType("application/json;charset=UTF-8");
        JSONObject jsonResponse = new JSONObject();
        try(PrintWriter out = response.getWriter()){
            con = DB_Connection.getConnection();
            String query = "SELECT incident_id, incident_type, description, user_phone, user_type, address, prefecture, municipality, start_datetime, end_datetime, danger, status, finalResult, lat, lon, vehicles, firemen FROM incidents WHERE incident_id = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, incident_id);
            rs = pstmt.executeQuery();
            if(rs.next()){
                jsonResponse.put("success", true);
                JSONObject incidentDetails = new JSONObject();
                incidentDetails.put("incident_id", rs.getInt("incident_id"));
                incidentDetails.put("incident_type", rs.getString("incident_type"));
                incidentDetails.put("description", rs.getString("description"));
                incidentDetails.put("user_phone", rs.getString("user_phone"));
                incidentDetails.put("user_type", rs.getString("user_type"));
                incidentDetails.put("address", rs.getString("address"));
                incidentDetails.put("prefecture", rs.getString("prefecture"));
                incidentDetails.put("municipality", rs.getString("municipality"));
                incidentDetails.put("start_datetime", rs.getString("start_datetime"));
                incidentDetails.put("end_datetime", rs.getString("end_datetime"));
                incidentDetails.put("danger", rs.getString("danger"));
                incidentDetails.put("status", rs.getString("status"));
                incidentDetails.put("finalResult", rs.getString("finalResult"));
                incidentDetails.put("lat", rs.getDouble("lat"));
                incidentDetails.put("lon", rs.getDouble("lon"));
                incidentDetails.put("vehicles", rs.getInt("vehicles"));
                incidentDetails.put("firemen", rs.getInt("firemen"));
                jsonResponse.put("incident", incidentDetails);
            }else{
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Incident not found.");
            }

            out.print(jsonResponse.toString());
        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Server error: " + e.getMessage());
            try (PrintWriter out = response.getWriter()) {
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
