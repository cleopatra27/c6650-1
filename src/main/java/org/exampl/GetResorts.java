package org.exampl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.exampl.ResponseBean.toError;

@WebServlet(name = "resorts", urlPatterns = "/resorts/*")
public class GetResorts extends HttpServlet {

    private DatabaseHandler databaseHandler;

    @Override
    public void init() throws ServletException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            java.sql.Connection dbConnection = DriverManager.getConnection(
                    "jdbc:mysql://cs6650.cjrovla2rbi4.us-west-2.rds.amazonaws.com:3306/HW3",
                    "admin",
                    "cs6650data");
            this.databaseHandler = new DatabaseHandler(dbConnection);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        super.init();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        System.out.println("request ==>" + request.getPathInfo());

        response = validatePathInfo(request.getPathInfo(), response);
        if(response.getStatus() != HttpServletResponse.SC_OK){
            out.flush();
        }else {
            String segments[] = request.getPathInfo().split("/");
            out.print(databaseHandler.handleResortQuery(Integer.parseInt(segments[1]), Integer.parseInt(segments[3]), Integer.parseInt(segments[5])));
            out.flush();

        }
    }

    private HttpServletResponse validatePathInfo(String pathInfo, HttpServletResponse response) throws IOException {

        PrintWriter out = response.getWriter();
        if (pathInfo == null || pathInfo.isEmpty()) {
            out.print(toError("Missing parameters"));
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return response;
        }
        String segments[] = pathInfo.split("/");

        try {

            //validate resortID
            if (Integer.parseInt(segments[1]) < 1 || Integer.parseInt(segments[1]) > 10) {
                out.print(toError("Invalid resortID format"));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return response;
            }
            //validate seasonID
            if (Integer.parseInt(segments[3]) != 2022) {
                out.print(toError("Invalid seasonID format"));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return response;
            }

            //validate dayID
            if (Integer.parseInt(segments[5]) != 1) {
                out.print(toError("Invalid dayID format"));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return response;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            return response;
        }catch (Exception e){
            out.print(toError("Invalid path format"));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return response;
        }
    }
}
