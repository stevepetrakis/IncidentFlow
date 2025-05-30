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

/**
 *
 * @author stavr
 */
@WebServlet(name="CheckDuplicate", urlPatterns={"/CheckDuplicate"})
public class CheckDuplicate extends HttpServlet {
   

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String telephone = request.getParameter("telephone");
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            // Check username in users and volunteers tables
            con = DB_Connection.getConnection();
            String checkUsernameUsersQuery = "SELECT COUNT(*) AS total FROM users WHERE username = ?";
            pstmt = con.prepareStatement(checkUsernameUsersQuery);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt("total") > 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Duplicate username found in users");
                return;
            }
            String checkUsernameVolunteersQuery = "SELECT COUNT(*) AS total FROM volunteers WHERE username = ?";
            pstmt = con.prepareStatement(checkUsernameVolunteersQuery);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt("total") > 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Duplicate username found in volunteers");
                return;
            }

            // Check email in users and volunteers tables
            String checkemailUsersQuery = "SELECT COUNT(*) AS total FROM users WHERE email = ?";
            pstmt = con.prepareStatement(checkemailUsersQuery);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt("total") > 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Duplicate email found in users");
                return;
            }
            String checkemailVolunteersQuery = "SELECT COUNT(*) AS total FROM volunteers WHERE email = ?";
            pstmt = con.prepareStatement(checkemailVolunteersQuery);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt("total") > 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Duplicate email found in volunteers");
                return;
            }

            // Check telephone in users and volunteers tables
            String checkTelephoneUsersQuery = "SELECT COUNT(*) AS total FROM users WHERE telephone = ?";
            pstmt = con.prepareStatement(checkTelephoneUsersQuery);
            pstmt.setString(1, telephone);
            rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt("total") > 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Duplicate telephone found in users");
                return;
            }
            String checkTelephoneVolunteersQuery = "SELECT COUNT(*) AS total FROM volunteers WHERE telephone = ?";
            pstmt = con.prepareStatement(checkTelephoneVolunteersQuery);
            pstmt.setString(1, telephone);
            rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt("total") > 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Duplicate telephone found in volunteers");
                return;
            }
            response.getWriter().write("No duplicates found");
        }catch (Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error: " + e.getMessage());
        }finally{
            try{
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (con != null) con.close();
            }catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

