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
@WebServlet(name="GetEveryone", urlPatterns={"/GetEveryone"})
public class GetEveryone extends HttpServlet {
   
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
            out.println("<title>Servlet GetEveryone</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GetEveryone at " + request.getContextPath () + "</h1>");
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
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        response.setContentType("application/json;charset=UTF-8");
        JSONObject jsonResponse = new JSONObject();
        try (PrintWriter out = response.getWriter()) {
            con = DB_Connection.getConnection();
            String userQuery = "SELECT COUNT(user_id) AS user_count FROM users";
            pstmt = con.prepareStatement(userQuery);
            rs = pstmt.executeQuery();
            int userCount = 0;
            if(rs.next()){
                userCount = rs.getInt("user_count");
            }
            rs.close();
            pstmt.close();

            String volunteerQuery = "SELECT COUNT(volunteer_id) AS volunteer_count FROM volunteers";
            pstmt = con.prepareStatement(volunteerQuery);
            rs = pstmt.executeQuery();
            int volunteerCount = 0;
            if (rs.next()) {
                volunteerCount = rs.getInt("volunteer_count");
            }
            rs.close();
            pstmt.close();

            jsonResponse.put("success", true);
            jsonResponse.put("userCount", userCount);
            jsonResponse.put("volunteerCount", volunteerCount);
            out.print(jsonResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Server error: " + e.getMessage());
            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse.toString());
            }
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (con != null) con.close();
            } catch (Exception ex) {
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
