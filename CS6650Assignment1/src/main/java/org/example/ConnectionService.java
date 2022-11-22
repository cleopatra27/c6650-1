package org.example;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class ConnectionService {

    public CloseableHttpResponse connect(String url, String headerKey, String header, String request, String type) {
        CloseableHttpClient client = HttpClientBuilder.create()
                .build();
        HttpPost post = new HttpPost(url);
        if (headerKey != null) {
            post.setHeader(headerKey, header);
        }
        StringEntity entity;
        try {
            entity = new StringEntity(request);
            entity.setContentType(type);
            post.setEntity(entity);
            return client.execute(post);
        } catch (IOException e) {
            return null;
        }

    }
}
