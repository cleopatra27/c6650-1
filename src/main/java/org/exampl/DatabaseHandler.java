package org.exampl;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {

    private Connection connection;
    DatabaseHandler(Connection connection){
        this.connection = connection;
    }

    private final String SKIER_GET_QUERY = "SELECT * FROM skierData WHERE resortID = ? AND seasonID = ? AND dayID = ? AND skierID = ?";
    private final String SKIER_VERTICAL_GET_QUERY = "SELECT * FROM skierData WHERE skierID = ?";
    private final String RESORT_GET_QUERY = "SELECT * FROM skierData WHERE resortID = ? AND seasonID = ? AND dayID = ?";

    public List<JSONObject> handleResortQuery(int resortID, int seasonID, int dayID){
        try(PreparedStatement statement = this.connection.prepareStatement(RESORT_GET_QUERY)){

            statement.setInt(1, resortID);
            statement.setInt(2, seasonID);
            statement.setInt(3, dayID);

            return handle(statement);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<JSONObject> handleSkierVerticalQuery(int skierID){
        try(PreparedStatement statement = this.connection.prepareStatement(SKIER_VERTICAL_GET_QUERY)){

            statement.setInt(1, skierID);

            return handle(statement);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<JSONObject> handleSkierQuery(int resortID, int seasonID, int dayID, int skierID){
        try(PreparedStatement statement = this.connection.prepareStatement(SKIER_GET_QUERY)){

            statement.setInt(1, resortID);
            statement.setInt(2, seasonID);
            statement.setInt(3, dayID);
            statement.setInt(4, skierID);

            return handle(statement);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<JSONObject> handle(PreparedStatement statement){
        try{
            ResultSet resultSet = statement.executeQuery();
            List<JSONObject> respList = new ArrayList<>();
            JSONObject resp = new JSONObject();
            while (resultSet.next()) {
                resp.put("id", resultSet.getObject("id"));
                resp.put("resortID", resultSet.getObject("resortID"));
                resp.put("seasonID", resultSet.getObject("seasonID"));
                resp.put("dayID", resultSet.getObject("dayID"));
                resp.put("time", resultSet.getObject("time"));
                resp.put("liftID", resultSet.getObject("liftID"));
                respList.add(resp);
            }
            return respList;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
