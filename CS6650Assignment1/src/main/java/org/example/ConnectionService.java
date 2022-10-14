package org.example;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import java.io.IOException;
import java.util.Arrays;

public class ConnectionService {

    public CloseableHttpResponse connect(String url, String headerKey, String header, String request, String type) {
        try (CloseableHttpClient client = HttpClientBuilder.create()
                .build()) {
            CloseableHttpResponse closeableHttpResponse = null;
             HttpPost post = new HttpPost(url);
                if (headerKey != null) {
                    post.setHeader(headerKey, header);
                }
                    StringEntity entity = new StringEntity(request);
                    entity.setContentType(type);
                    post.setEntity(entity);
                closeableHttpResponse = client.execute(post);
            return closeableHttpResponse;
        } catch (IOException ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
        return null;
    }
}
