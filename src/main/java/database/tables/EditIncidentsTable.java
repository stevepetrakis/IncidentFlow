/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database.tables;

import mainClasses.Incident;
import com.google.gson.Gson;
import database.DB_Connection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Arrays;

/**
 *
 * @author Mike
 */
public class EditIncidentsTable {

    public void addIncidentFromJSON(String json) throws ClassNotFoundException {
        Incident bt = jsonToIncident(json);
        if (bt.getStart_datetime()==null){
            bt.setStart_datetime();
            bt.setDanger("unknown");
            bt.setStatus("submitted");
        }
        createNewIncident(bt);
    }

    public void finishIncident(Incident bt) throws ClassNotFoundException {
        bt.setEnd_datetime();
    }

    public Incident jsonToIncident(String json) {
        Gson gson = new Gson();
        Incident btest = gson.fromJson(json, Incident.class);
        return btest;
    }

    public String incidentToJSON(Incident bt) {
        Gson gson = new Gson();

        String json = gson.toJson(bt, Incident.class);
        return json;
    }

    public boolean incidentExists(String id) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String query = "SELECT COUNT(*) AS count FROM incidents WHERE incident_id='" + id + "'";
        ResultSet rs = stmt.executeQuery(query);
        boolean exists = false;
        if(rs.next()){
            exists = rs.getInt("count") > 0;
        }
        rs.close();
        stmt.close();
        con.close();
        return exists;
    }

    public Incident getIncidentById(String id) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String query = "SELECT * FROM incidents WHERE incident_id='" + id + "'";
        ResultSet rs = stmt.executeQuery(query);
        if(rs.next()){
            String json = DB_Connection.getResultsToJSON(rs);
            Gson gson = new Gson();
            Incident incident = gson.fromJson(json, Incident.class);
            rs.close();
            stmt.close();
            con.close();
            return incident;
        }else{
            rs.close();
            stmt.close();
            con.close();
            return null;
        }
    }

    public void updateIncidentFields(String incident_id, JsonObject updates) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        try{
            boolean isFinished = updates.has("status") && "finished".equalsIgnoreCase(updates.get("status").getAsString());
            if(isFinished){
                Incident incident = getIncidentById(incident_id);
                if(incident != null && incident.getEnd_datetime() == null){
                    finishIncident(incident);
                    updates.addProperty("end_datetime", incident.getEnd_datetime());
                }
            }
            StringBuilder query = new StringBuilder("UPDATE incidents SET ");
            boolean first = true;
            for(String key : updates.keySet()){
                if(!first){
                    query.append(", ");
                }
                query.append(key).append(" = ?");
                first = false;
            }
            query.append(" WHERE incident_id = ?");
            PreparedStatement pstmt = con.prepareStatement(query.toString());
            int index = 1;
            for(String key : updates.keySet()){
                pstmt.setObject(index++, updates.get(key).getAsString());
            }
            pstmt.setString(index, incident_id);
            pstmt.executeUpdate();
            pstmt.close();
        }catch(SQLException e){
            e.printStackTrace();
            throw e;
        }finally{
            con.close();
        }
    }

    public ArrayList<Incident> databaseToIncidents() throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Incident> pets = new ArrayList<Incident>();
        ResultSet rs;
        try {
            rs = stmt.executeQuery("SELECT * FROM incidents");
            while (rs.next()) {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                Incident pet = gson.fromJson(json, Incident.class);
                pets.add(pet);
            }
            return pets;

        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        return null;
    }

    public ArrayList<Incident> databaseToIncidentsSearch(String type,String status,String municipality) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Incident> incidents = new ArrayList<Incident>();
        ResultSet rs;
        String where="WHERE";
        if(!type.equals("all"))
            where+=" incident_type='" + type + "'";
        if(!status.equals("all")){
            if(!where.equals("WHERE")){
                where+=" and status='" + status + "'";
            }
            else{
                where+=" status='" + status + "'";
            }
        }
        if(!municipality.equals("all") && !municipality.equals("")){
            if(!where.equals("WHERE")){
                where+=" and municipality='" + municipality + "'";
            }
            else{
                where+=" municipality='" + municipality + "'";
            }
        }
        try {
            String query="SELECT * FROM incidents ";
            if(!where.equals("WHERE"))
                query+=where;
            System.out.println(query);
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                Incident incident = gson.fromJson(json, Incident.class);
                incidents.add(incident);
            }
            return incidents;
        } catch (Exception e) {
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        return null;
    }

    public ArrayList<Incident> databaseToHisrotyIncidentsSearch(String type, String status, String prefecture, String startDateTime, String endDateTime, int firemen, int vehicles) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        ArrayList<Incident> incidents = new ArrayList<Incident>();
        ResultSet rs;
        String where = "WHERE";
        if(!type.equals("all")){
            where += " incident_type='" + type + "'";
        }
        if(!status.equals("all")) {
            if(!where.equals("WHERE")){
                where += " AND status='" + status + "'";
            }else{
                where += " status='" + status + "'";
            }
        }
        if(!prefecture.equals("all")){
            if(!where.equals("WHERE")){
                where += " AND prefecture='" + prefecture + "'";
            }else{
                where += " prefecture='" + prefecture + "'";
            }
        }
        if(!startDateTime.isEmpty()){
            if(!where.equals("WHERE")){
                where += " AND start_datetime >= '" + startDateTime + "'";
            }else{
                where += " start_datetime >= '" + startDateTime + "'";
            }
        }
        if(!endDateTime.isEmpty()){
            if (!where.equals("WHERE")) {
                where += " AND end_datetime <= '" + endDateTime + "'";
            } else {
                where += " end_datetime <= '" + endDateTime + "'";
            }
        }
        if(firemen >= 0){
            if(!where.equals("WHERE")){
                where += " AND firemen=" + firemen;
            }else{
                where += " firemen=" + firemen;
            }
        }

        if(vehicles >= 0){
            if(!where.equals("WHERE")){
                where += " AND vehicles=" + vehicles;
            }else{
                where += " vehicles=" + vehicles;
            }
        }
        try{
            String query = "SELECT * FROM incidents ";
            if (!where.equals("WHERE")) {
                query += where;
            }
            System.out.println(query);
            rs = stmt.executeQuery(query);
            while(rs.next()){
                String json = DB_Connection.getResultsToJSON(rs);
                Gson gson = new Gson();
                Incident incident = gson.fromJson(json, Incident.class);
                incidents.add(incident);
            }
            return incidents;
        }catch(Exception e){
            System.err.println("Got an exception! ");
            System.err.println(e.getMessage());
        }
        return null;
    }

    public void updateIncident(String id, HashMap<String, String> updates) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        Incident bt = new Incident();
        for (String key : updates.keySet()) {
            String update = "UPDATE incidents SET " + key + "='" + updates.get(key) + "'" + "WHERE incident_id = '" + id + "'";
            stmt.executeUpdate(update);
        }
        stmt.close();
        con.close();
    }

    public boolean updateIncidentStatus(String incidentId, String newStatus) throws SQLException, ClassNotFoundException {
        Connection conn = DB_Connection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE incidents SET status = ? WHERE incident_id = ?")) {
            stmt.setString(1, newStatus);
            stmt.setString(2, incidentId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } finally {
            conn.close();
        }
    }

    public void deleteIncident(String id) throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String deleteQuery = "DELETE FROM incidents WHERE incident_id='" + id + "'";
        stmt.executeUpdate(deleteQuery);
        stmt.close();
        con.close();
    }

    public void createIncidentsTable() throws SQLException, ClassNotFoundException {
        Connection con = DB_Connection.getConnection();
        Statement stmt = con.createStatement();
        String sql = "CREATE TABLE incidents "
                + "(incident_id INTEGER not NULL AUTO_INCREMENT, "
                + "incident_type VARCHAR(10) not null,"
                + "description VARCHAR(100) not null,"
                + "user_phone VARCHAR(14) not null,"
                + "user_type VARCHAR(10)  not null, "
                + "address VARCHAR(100) not null,"
                + "lat DOUBLE, "
                + "lon DOUBLE, "
                + "municipality VARCHAR(50),"
                + "prefecture VARCHAR(15),"
                + "start_datetime DATETIME not null , "
                + "end_datetime DATETIME DEFAULT null, "
                + "danger VARCHAR (15), "
                + "status VARCHAR (15), "
                + "finalResult VARCHAR (200), "
                + "vehicles INTEGER, "
                + "firemen INTEGER, "
                + "PRIMARY KEY (incident_id ))";
        stmt.execute(sql);
        stmt.close();
        con.close();
    }

    /**
     * Establish a database connection and add in the database.
     *
     * @throws ClassNotFoundException
     */
    public void createNewIncident(Incident bt) throws ClassNotFoundException {

        try {
            Connection con = DB_Connection.getConnection();

            Statement stmt = con.createStatement();

            String insertQuery = "INSERT INTO "
                    + " incidents (incident_id,incident_type,"
                    + "description,user_phone,user_type,"
                    + "address,lat,lon,municipality,prefecture,start_datetime,danger,status,"
                    + "finalResult,vehicles,firemen) "
                    + " VALUES ("
                    + "'" + bt.getIncident_id() + "',"
                    + "'" + bt.getIncident_type() + "',"
                    + "'" + bt.getDescription() + "',"
                    + "'" + bt.getUser_phone() + "',"
                    + "'" + bt.getUser_type() + "',"
                    + "'" + bt.getAddress() + "',"
                    + "'" + bt.getLat() + "',"
                    + "'" + bt.getLon() + "',"
                    + "'" + bt.getMunicipality() + "',"
                    + "'" + bt.getPrefecture() + "',"
                    + "'" + bt.getStart_datetime() + "',"
                    + "'" + bt.getDanger() + "',"
                    + "'" + bt.getStatus() + "',"
                    + "'" + bt.getFinalResult() + "',"
                    + "'" + bt.getVehicles() + "',"
                    + "'" + bt.getFiremen() + "'"
                    + ")";
            //stmt.execute(table);
            System.out.println(insertQuery);
            stmt.executeUpdate(insertQuery);
            System.out.println("# The incident was successfully added in the database.");

            /* Get the member id from the database and set it to the member */
            stmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(EditIncidentsTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
