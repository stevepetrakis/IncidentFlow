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
import database.DB_Connection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import mainClasses.Message;
import java.sql.SQLException;
import com.google.gson.Gson;
import database.tables.EditMessagesTable;
import java.io.BufferedReader;

/**
 *
 * @author stavr
 */
@WebServlet(name="GetSendMessages", urlPatterns={"/GetSendMessages"})
public class GetSendMessages extends HttpServlet {
   
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
            out.println("<title>Servlet GetSendMessages</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GetSendMessages at " + request.getContextPath () + "</h1>");
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
        String user1 = request.getParameter("user1");
        String user2 = request.getParameter("user2");
        response.setContentType("application/json;charset=UTF-8");
        JSONObject jsonResponse = new JSONObject();
        if(user1 == null || user2 == null || user1.isEmpty() || user2.isEmpty()){
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Both usernames are required.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse.toString());
            }
            return;
        }
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try(PrintWriter out = response.getWriter()){
            con = DB_Connection.getConnection();
            String query = "SELECT sender, message " + "FROM messages " + "WHERE (sender = ? AND recipient = ?) OR (sender = ? AND recipient = ?) " +
                    "ORDER BY date_time ASC";

            pstmt = con.prepareStatement(query);
            pstmt.setString(1, user1);
            pstmt.setString(2, user2);
            pstmt.setString(3, user2);
            pstmt.setString(4, user1);
            rs = pstmt.executeQuery();
            JSONArray messagesArray = new JSONArray();
            while(rs.next()){
                JSONObject messageObject = new JSONObject();
                messageObject.put("sender", rs.getString("sender"));
                messageObject.put("message", rs.getString("message"));
                messagesArray.put(messageObject);
            }
            jsonResponse.put("success", true);
            jsonResponse.put("messages", messagesArray);
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
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        StringBuilder requestBody = new StringBuilder();
        try(BufferedReader reader = request.getReader()){
            String line;
            while((line = reader.readLine()) != null){
                requestBody.append(line);
            }
        }
        String body = requestBody.toString();        
        EditMessagesTable editMessagesTable = new EditMessagesTable();
        try{
            editMessagesTable.addMessageFromJSON(body);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\": \"Message sent successfully.\"}");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Class not found while processing the message.\"}");
        }
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
