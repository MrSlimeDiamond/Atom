package net.zenoc.atom.util;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.mariadb.jdbc.util.StringUtils;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

public class MinecraftOnlineAPI {
    public static Optional<Date> getPlayerFirstseenByName(String username) throws IOException {
        Optional<String> firstseen = HTTPUtil.getDataFromURL("http://minecraftonline.com/cgi-bin/getfirstseen_unix/?" + username);
        if (firstseen.isPresent()) {
            String firstseenData = firstseen.get().replaceAll("\\s+","");
            if (firstseenData.equals("NOTFOUND")) {
                return Optional.empty();
            } else {
                long time = Long.parseLong(firstseenData);
                Date date = new Date(time * 1000);
                return Optional.of(date);
            }
        }
        return Optional.empty();
    }

    public static Optional<Date> getPlayerLastseenByName(String username) throws IOException {
        Optional<String> lastseen = HTTPUtil.getDataFromURL("http://minecraftonline.com/cgi-bin/getlastseen_unix/?" + username);
        if (lastseen.isPresent()) {
            String data = lastseen.get().replaceAll("\\s+","");
            if (data.equals("NOTFOUND")) {
                return Optional.empty();
            } else {
                long time = Long.parseLong(data);
                Date date = new Date(time * 1000);
                return Optional.of(date);
            }
        }
        return Optional.empty();
    }

    public static Optional<Long> getPlayerPlaytime(String username) throws IOException {
        Optional<String> playtime = HTTPUtil.getDataFromURL("http://minecraftonline.com/cgi-bin/gettimeonline/?" + username);
        if (playtime.isPresent()) {
            String data = playtime.get().replaceAll("\\s+", "");
            if (data.equals("NOTFOUND")) {
                return Optional.empty();
            } else {
                return Optional.of(Long.parseLong(data));
            }
        }
        return Optional.empty();
    }

    public static Optional<String> getCorrectUsername(String username) throws IOException {
        Optional<String> correctname = HTTPUtil.getDataFromURL("http://minecraftonline.com/cgi-bin/getcorrectname?" + username);
        if (correctname.isPresent()) {
            String data = correctname.get().replaceAll("\\s+","");
            if (data.equals("NOTFOUND")) {
                return Optional.empty();
            } else {
                return Optional.of(data);
            }
        }
        return Optional.empty();
    }
}
