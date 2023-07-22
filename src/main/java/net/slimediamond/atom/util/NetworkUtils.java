package net.slimediamond.atom.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class NetworkUtils {
    public static String getIP() {
        try {
            URL url = new URL("https://checkip.amazonaws.com");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            return br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
