package net.zenoc.atom.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;

public class HTTPUtil {
    public static Optional<JSONObject> getJsonDataFromURL(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        CloseableHttpResponse response =  httpClient.execute(request);

        String jsonasString = EntityUtils.toString(response.getEntity(), "UTF-8");
        JSONObject json = new JSONObject(jsonasString);

        response.close(); // make sure to close for better

        return Optional.of(json);

    }

    public static Optional<String> getDataFromURL(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        CloseableHttpResponse response =  httpClient.execute(request);
        return Optional.of(EntityUtils.toString(response.getEntity(), "UTF-8"));
    }
}
