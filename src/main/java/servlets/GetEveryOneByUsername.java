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
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import database.DB_Connection;

/**
 *
 * @author stavr
 */
@WebServlet(name="GetEveryOneByUsername", urlPatterns={"/GetEveryOneByUsername"})
public class GetEveryOneByUsername extends HttpServlet {
   
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
            out.println("<title>Servlet GetEveryOneByUsername</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GetEveryOneByUsername at " + request.getContextPath () + "</h1>");
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
        String username = request.getParameter("username");
        response.setContentType("application/json;charset=UTF-8");
        JSONObject jsonResponse = new JSONObject();
        if(username == null || username.trim().isEmpty()){
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Username parameter is required.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try(PrintWriter out = response.getWriter()){
                out.print(jsonResponse.toString());
            }
            return;
        }
        try(PrintWriter out = response.getWriter()){
            Connection con = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            try{
                con = DB_Connection.getConnection();
                String userQuery = "SELECT username FROM users WHERE username = ?";
                pstmt = con.prepareStatement(userQuery);
                pstmt.setString(1, username);
                rs = pstmt.executeQuery();
                if(rs.next()){
                    jsonResponse.put("success", true);
                    jsonResponse.put("exists", true);
                    jsonResponse.put("type", "user");
                }else{
                    String volunteerQuery = "SELECT username FROM volunteers WHERE username = ?";
                    pstmt = con.prepareStatement(volunteerQuery);
                    pstmt.setString(1, username);
                    rs = pstmt.executeQuery();
                    if(rs.next()){
                        jsonResponse.put("success", true);
                        jsonResponse.put("exists", true);
                        jsonResponse.put("type", "volunteer");
                    }else{
                        jsonResponse.put("success", true);
                        jsonResponse.put("exists", false);
                    }
                }
                out.print(jsonResponse.toString());
            }catch(Exception e){
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Server error: " + e.getMessage());
                out.print(jsonResponse.toString());
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
