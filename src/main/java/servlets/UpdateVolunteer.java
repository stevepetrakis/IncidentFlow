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
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import database.tables.EditVolunteersTable;
import java.io.BufferedReader;
import javax.servlet.http.HttpSession;
import mainClasses.Volunteer;

/**
 *
 * @author stavr
 */
@WebServlet(name="UpdateVolunteer", urlPatterns={"/UpdateVolunteer"})
public class UpdateVolunteer extends HttpServlet {

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
            out.println("<title>Servlet UpdateVolunteer</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UpdateVolunteer at " + request.getContextPath () + "</h1>");
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
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        String body = requestBody.toString();
        JsonObject jsonResponse = new JsonObject();
        try {
            JsonObject requestData = JsonParser.parseString(body).getAsJsonObject();
            String username = requestData.get("username").getAsString();
            requestData.remove("username");
            EditVolunteersTable evt = new EditVolunteersTable();
            evt.updateVolunteerFields(username, requestData);
            Volunteer updatedVolunteer = evt.getVolunteerByUsername(username);
            if (updatedVolunteer != null) {
                Gson gson = new Gson();
                String volunteerJson = gson.toJson(updatedVolunteer);
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Volunteer updated successfully");
                jsonResponse.add("updatedVolunteer", JsonParser.parseString(volunteerJson));
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Volunteer updated but could not be retrieved");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error while updating the volunteer");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
    }
}