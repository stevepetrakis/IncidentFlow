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
import java.io.BufferedReader;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import database.tables.EditIncidentsTable;
import mainClasses.Incident;

/**
 *
 * @author stavr
 */
@WebServlet(name="ViewHisoryIncident", urlPatterns={"/ViewHisoryIncident"})
public class ViewHisoryIncident extends HttpServlet {
   
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
            out.println("<title>Servlet ViewHisoryIncident</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ViewHisoryIncident at " + request.getContextPath () + "</h1>");
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
        try(PrintWriter out = response.getWriter()){
            StringBuilder requestData = new StringBuilder();
            try(BufferedReader reader = request.getReader()){
                String line;
                while((line = reader.readLine()) != null){
                    requestData.append(line);
                }
            }
            JSONObject jsonData = new JSONObject(requestData.toString());
            String incidentType = jsonData.optString("incident_type", "all");
            String prefecture = jsonData.optString("prefecture", "all");
            String startDateTime = jsonData.optString("start_datetime", null);
            String endDateTime = jsonData.optString("end_datetime", null);
            int firemen = jsonData.optInt("firemen", 0);
            int vehicles = jsonData.optInt("vehicles", 0);
            String status = jsonData.optString("finished", "all");
            EditIncidentsTable incidentsTable = new EditIncidentsTable();
            ArrayList<Incident> incidents = incidentsTable.databaseToHisrotyIncidentsSearch(incidentType, status, prefecture, startDateTime, endDateTime, firemen, vehicles);
            JSONArray resultsArray = new JSONArray();
            if(incidents != null){
                for(Incident incident : incidents){
                    JSONObject incidentJson = new JSONObject();
                    incidentJson.put("incident_id", incident.getIncident_id());
                    incidentJson.put("incident_type", incident.getIncident_type());
                    incidentJson.put("description", incident.getDescription());
                    incidentJson.put("user_phone", incident.getUser_phone());
                    incidentJson.put("user_type", incident.getUser_type());
                    incidentJson.put("address", incident.getAddress());
                    incidentJson.put("lat", incident.getLat());
                    incidentJson.put("lon", incident.getLon());
                    incidentJson.put("municipality", incident.getMunicipality());
                    incidentJson.put("prefecture", incident.getPrefecture());
                    incidentJson.put("start_datetime", incident.getStart_datetime());
                    incidentJson.put("end_datetime", incident.getEnd_datetime());
                    incidentJson.put("danger", incident.getDanger());
                    incidentJson.put("status", incident.getStatus());
                    incidentJson.put("finalResult", incident.getFinalResult());
                    incidentJson.put("vehicles", incident.getVehicles());
                    incidentJson.put("firemen", incident.getFiremen());
                    resultsArray.put(incidentJson);
                }
            }
            JSONObject responseJson = new JSONObject();
            responseJson.put("results", resultsArray);
            out.print(responseJson.toString());
        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try(PrintWriter out = response.getWriter()){
                JSONObject errorJson = new JSONObject();
                errorJson.put("error", "Error occurred");
                out.print(errorJson.toString());
            }
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
