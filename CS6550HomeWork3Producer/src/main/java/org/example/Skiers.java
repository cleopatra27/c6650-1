package org.example;

import com.google.gson.GsonBuilder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import static org.example.ResponseBean.toError;
import static org.example.ResponseBean.toSuccess;


@WebServlet(name = "skiers", urlPatterns = "/skiers/*")
public class Skiers extends HttpServlet {
    private Connection conn;
    private final static String QUEUE_NAME = "threadExQ";

    private final static String QUEUE_URI = "amqp://guest:guest@ec2-35-88-223-110.us-west-2.compute.amazonaws.com:5672";
    private int seasonID;
    private int resortID;
    private int dayID;
    private int skierID;

    @Override
    public void init() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(QUEUE_URI);
            conn = factory.newConnection();

        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException | TimeoutException |
                 IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        response = validatePathInfo(request.getPathInfo(), response);
        if(response.getStatus() != HttpServletResponse.SC_OK){
            out.flush();
        }else {

            StringBuilder responseString = new StringBuilder();
            BufferedReader reader = request.getReader();
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseString.append(line).append('\n');
                }
            } finally {
                reader.close();

                new Thread(() ->{
                    try {
                        Channel channel = conn.createChannel();
                        CreateSkiersBean createSkiersBean = new GsonBuilder().create().
                                fromJson(responseString.toString(), CreateSkiersBean.class);
                        createSkiersBean.setSeasonID(seasonID);
                        createSkiersBean.setResortID(resortID);
                        createSkiersBean.setDayID(dayID);
                        createSkiersBean.setSkierID(skierID);

                        QueueHandler.send(createSkiersBean.toString(), channel, QUEUE_NAME);
                        channel.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).start();

            }

            out.print(toSuccess());
            out.flush();
        }
    }

    @Override
    public void destroy() {
        try {
            conn.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpServletResponse validatePathInfo(String pathInfo, HttpServletResponse response) throws IOException {
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

            //validate skierID
            if (Integer.parseInt(segments[7]) < 1 || Integer.parseInt(segments[7]) > 100000) {
                out.print(toError("Invalid skierID format"));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return response;
            }

            resortID = Integer.parseInt(segments[1]);
            seasonID = Integer.parseInt(segments[3]);
            dayID = Integer.parseInt(segments[5]);
            skierID = Integer.parseInt(segments[7]);
            response.setStatus(HttpServletResponse.SC_OK);
            return response;
        }catch (Exception e){
            out.print(toError("Invalid path format"));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return response;
        }
    }


}
