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
@WebServlet(name="GetAllUsers", urlPatterns={"/GetAllUsers"})
public class GetAllUsers extends HttpServlet {
   
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
            out.println("<title>Servlet GetAllUsers</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GetAllUsers at " + request.getContextPath () + "</h1>");
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
    throws ServletException, IOException{
        String userType = request.getParameter("userType");
        response.setContentType("application/json;charset=UTF-8");
        JSONObject jsonResponse = new JSONObject();
        if("user".equalsIgnoreCase(userType)) {
            getAllUsers(response, jsonResponse);
        }else if("volunteer".equalsIgnoreCase(userType)){
            getAllVolunteers(response, jsonResponse);
        }else{
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Invalid userType parameter.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try(PrintWriter out = response.getWriter()){
                out.print(jsonResponse.toString());
            }
        }
    }

    private void getAllUsers(HttpServletResponse response, JSONObject jsonResponse) throws IOException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try(PrintWriter out = response.getWriter()){
            con = DB_Connection.getConnection();
            String query = "SELECT firstname, lastname, username, email, birthdate, gender, afm, country, prefecture, municipality, address, job, telephone, lat, lon FROM users";
            pstmt = con.prepareStatement(query);
            rs = pstmt.executeQuery();
            JSONArray usersArray = new JSONArray();
            while(rs.next()){
                JSONObject userDetails = new JSONObject();
                userDetails.put("firstname", rs.getString("firstname"));
                userDetails.put("lastname", rs.getString("lastname"));
                userDetails.put("username", rs.getString("username"));
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
                usersArray.put(userDetails);
            }
            jsonResponse.put("success", true);
            jsonResponse.put("users", usersArray);
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

    private void getAllVolunteers(HttpServletResponse response, JSONObject jsonResponse) throws IOException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try(PrintWriter out = response.getWriter()){
            con = DB_Connection.getConnection();
            String query = "SELECT firstname, lastname, username, email, birthdate, gender, afm, country, prefecture, municipality, address, job, volunteer_type, telephone, lat, lon, height, weight FROM volunteers";
            pstmt = con.prepareStatement(query);
            rs = pstmt.executeQuery();
            JSONArray volunteersArray = new JSONArray();
            while(rs.next()){
                JSONObject volunteerDetails = new JSONObject();
                volunteerDetails.put("firstname", rs.getString("firstname"));
                volunteerDetails.put("lastname", rs.getString("lastname"));
                volunteerDetails.put("username", rs.getString("username"));
                volunteerDetails.put("email", rs.getString("email"));
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
                volunteerDetails.put("volunteer_type", rs.getString("volunteer_type"));
                volunteerDetails.put("height", rs.getString("height"));
                volunteerDetails.put("weight", rs.getString("weight"));
                volunteersArray.put(volunteerDetails);
            }
            jsonResponse.put("success", true);
            jsonResponse.put("volunteers", volunteersArray);
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
