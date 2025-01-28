package net.slimediamond.atom.util.minecraftonline;

import net.slimediamond.atom.util.HTTPUtil;
import net.slimediamond.atom.util.minecraftonline.exceptions.UnknownPlayerException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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

    public static Optional<Integer> getBanCount() throws IOException {
        AtomicReference<String> temp = new AtomicReference<>();
        HTTPUtil.getDataFromURL("http://minecraftonline.com/cgi-bin/getbancount.sh").ifPresent(bans -> temp.set(bans.strip()));
        return Optional.of(Integer.parseInt(temp.get()));
    }

    public static Optional<String> getBanReason(String username) throws IOException {
        AtomicReference<String> temp = new AtomicReference<>();
        HTTPUtil.getDataFromURL("https://minecraftonline.com/cgi-bin/getplayerinfo?" + username).ifPresent(info -> {
            String[] data = info.split(";");
            if (data.length == 0 || data.length == 1) {
                temp.set(null);
            } else {
                temp.set(data[2]);
            }
        });
        if (temp.get() == null) {
            return Optional.empty();
        } else {
            return Optional.of(temp.get());
        }
    }

    public static Optional<Date> getBanTime(String username) throws IOException {
        AtomicReference<String> temp = new AtomicReference<>();
        HTTPUtil.getDataFromURL("https://minecraftonline.com/cgi-bin/getplayerinfo?" + username).ifPresent(info -> {
            String[] data = info.split(";");
            if (data.length == 0 || data.length == 1) {
                temp.set(null);
            } else {
                temp.set(data[1]);
            }
        });

        if (temp.get() == null) {
            return Optional.empty();
        }

        String data = temp.get().replaceAll("\\s+", "");
        long time = Long.parseLong(data);
        Date date = new Date(time * 1000);
        return Optional.of(date);
    }

    public static Optional<MCOBan> getBan(String username) throws IOException, UnknownPlayerException {
        Optional<String> data = HTTPUtil.getDataFromURL("https://minecraftonline.com/cgi-bin/getplayerinfo?" + username);
        if (data.isPresent()) {
            String[] info = data.get().split("\n");
            if (data.get().contains("NOTFOUND")) {
                throw new UnknownPlayerException(username);
            } else if (data.get().contains("NOTBANNED")) {
                return Optional.empty();
            } else {
                String banner = info[0];
                Date date = new Date(info[1]);
                String reason = info[2];

                return Optional.of(new MCOBanImpl(new MCOPlayer(username), new MCOPlayer(banner), date, reason));
            }
        } else {
            return Optional.empty();
        }
    }

    public static Optional<List<String>> getOnlinePlayers() throws IOException {
        AtomicReference<Optional<List<String>>> result = new AtomicReference<>(); // Fuck this

        HTTPUtil.getDataFromURL("https://minecraftonline.com/cgi-bin/getplayerlist.sh").ifPresent(info -> {
            String[] players = info.replace(" ", "").split(",");

            List<String> output = new ArrayList<>();
            Collections.addAll(output, players);

            if (output.isEmpty()) {
                // No players are online, or server is down
                result.set(Optional.empty());
            } else {
                result.set(Optional.of(output));
            }
        });

        return result.get();
    }
}
