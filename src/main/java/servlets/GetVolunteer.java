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
@WebServlet(name="GetVolunteer", urlPatterns={"/GetVolunteer"})
public class GetVolunteer extends HttpServlet {
   
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
            out.println("<title>Servlet GetVolunteer</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GetVolunteer at " + request.getContextPath () + "</h1>");
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
            String query = "SELECT firstname, lastname, username, password, email, birthdate, gender, afm, country, volunteer_type, prefecture, municipality, address, job, telephone, lat, lon, height, weight FROM volunteers WHERE username = ? AND password = ?";
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            if(rs.next()){
                jsonResponse.put("success", true);
                JSONObject volunteerDetails = new JSONObject();
                volunteerDetails.put("firstname", rs.getString("firstname"));
                volunteerDetails.put("lastname", rs.getString("lastname"));
                volunteerDetails.put("username", rs.getString("username"));
                volunteerDetails.put("password", rs.getString("password"));
                volunteerDetails.put("email", rs.getString("email"));
                volunteerDetails.put("volunteer_type", rs.getString("volunteer_type"));
                volunteerDetails.put("height", rs.getString("height"));
                volunteerDetails.put("weight", rs.getString("weight"));
                volunteerDetails.put("birthdate", rs.getString("birthdate"));
                volunteerDetails.put("gender", rs.getString("gender"));
                volunteerDetails.put("afm", rs.getString("afm"));
                volunteerDetails.put("country", rs.getString("country"));
                volunteerDetails.put("prefecture", rs.getString("prefecture"));
                volunteerDetails.put("municipality", rs.getString("municipality"));
                volunteerDetails.put("address", rs.getString("address"));
                volunteerDetails.put("job", rs.getString("job"));
                volunteerDetails.put("telephone", rs.getString("telephone"));
                volunteerDetails.put("lat", rs.getString("lat"));
                volunteerDetails.put("lon", rs.getString("lon"));
                jsonResponse.put("volunteer", volunteerDetails);
            }else{
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Volunteer not found or incorrect password.");
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
