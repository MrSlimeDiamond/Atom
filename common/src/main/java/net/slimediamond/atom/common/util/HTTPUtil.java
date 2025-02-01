package net.slimediamond.atom.common.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

public class HTTPUtil {
    private static final OkHttpClient httpClient = new OkHttpClient();
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

    // ChatGPT :)
    public static File downloadFile(String fileUrl, String fileName) throws IOException {
        Request request = new Request.Builder().url(fileUrl).build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Failed to download file: " + response);

            // Get system temp directory
            String tempDir = System.getProperty("java.io.tmpdir");
            File tempFile = new File(tempDir, fileName);

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(response.body().bytes());
            }

            return tempFile;
        }
    }
}
