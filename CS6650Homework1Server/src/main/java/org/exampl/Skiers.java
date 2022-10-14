package org.exampl;

import com.google.gson.GsonBuilder;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import static org.exampl.ResponseBean.toError;
import static org.exampl.ResponseBean.toSuccess;

@WebServlet(name = "skiers", urlPatterns = "/skiers/*")
public class Skiers extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        System.out.println("request ==>" + request.getPathInfo());

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

                CreateSkiersBean req = new GsonBuilder().create().
                        fromJson(responseString.toString(), CreateSkiersBean.class);

                System.out.println(req);
            }


//            System.out.println(responseString);

            out.print(toSuccess());
            out.flush();
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
            if (Integer.parseInt(segments[1]) < 1 || Integer.parseInt(segments[1]) > 100000) {
                out.print(toError("Invalid skierID format"));
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
