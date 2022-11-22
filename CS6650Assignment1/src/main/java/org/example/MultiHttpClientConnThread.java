package org.example;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MultiHttpClientConnThread extends Thread {
    private CloseableHttpClient client;
    private HttpPost post;

    MultiHttpClientConnThread(CloseableHttpClient client, HttpPost post){
        this.client = client;
        this.post = post;
    }

    // standard constructors
    public void run(){
        try {
            HttpResponse response = client.execute(post);
            EntityUtils.consume(response.getEntity());
        } catch (ClientProtocolException ex) {
        } catch (IOException ex) {
        }
    }

    public CloseableHttpResponse dos(String request) throws IOException {
        StringEntity entity = new StringEntity(request);
        entity.setContentType("application/json");
        post.setEntity(entity);
        return client.execute(post);
    }

}