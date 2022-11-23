package org.example;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseHandler {

    private Connection connection;
    DatabaseHandler(Connection connection){
        this.connection = connection;
    }

    private final String INSERT_QUERY = "INSERT INTO skierData(skierID, liftID, time, dayID, seasonID, resortID) VALUES(?, ?, ?, ?, ?, ?)";

    public void handle(SkiersBean skier){
        try(PreparedStatement statement = this.connection.prepareStatement(INSERT_QUERY)){

            statement.setInt(1, skier.getSkierID());
            statement.setInt(2, skier.getLiftID());
            statement.setInt(3, skier.getTime());
            statement.setInt(4, skier.getDayID());
            statement.setInt(5, skier.getSeasonID());
            statement.setInt(6, skier.getResortID());

            int count = statement.executeUpdate();
            if(count == 0){
                throw new RuntimeException();
            }
//            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
