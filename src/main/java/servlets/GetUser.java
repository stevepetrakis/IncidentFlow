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
@WebServlet(name="GetUser", urlPatterns={"/GetUser"})
public class GetUser extends HttpServlet {
   
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
            out.println("<title>Servlet GetUser</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GetUser at " + request.getContextPath () + "</h1>");
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
        String password = request.getParameter("password");
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        response.setContentType("application/json;charset=UTF-8");
        JSONObject jsonResponse = new JSONObject();
        try (PrintWriter out = response.getWriter()){
            con = DB_Connection.getConnection();
            String query = "SELECT firstname, lastname, username, password, email, birthdate, gender, afm, country, prefecture, municipality, address, job, telephone, lat, lon FROM users WHERE username = ? AND password = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            if(rs.next()){
                jsonResponse.put("success", true);
                JSONObject userDetails = new JSONObject();
                userDetails.put("firstname", rs.getString("firstname"));
                userDetails.put("lastname", rs.getString("lastname"));
                userDetails.put("username", rs.getString("username"));
                userDetails.put("password", rs.getString("password"));
                userDetails.put("email", rs.getString("email"));
                userDetails.put("birthdate", rs.getString("birthdate"));
                userDetails.put("gender", rs.getString("gender"));
                userDetails.put("afm", rs.getString("afm"));
                userDetails.put("country", rs.getString("country"));
                userDetails.put("prefecture", rs.getString("prefecture"));
                userDetails.put("municipality", rs.getString("municipality"));
                userDetails.put("address", rs.getString("address"));
                userDetails.put("job", rs.getString("job"));
                userDetails.put("telephone", rs.getString("telephone"));
                userDetails.put("lat", rs.getString("lat"));
                userDetails.put("lon", rs.getString("lon"));
                jsonResponse.put("user", userDetails);
            }else{
                jsonResponse.put("success", false);
                jsonResponse.put("message", "User not found or incorrect password.");
            }
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