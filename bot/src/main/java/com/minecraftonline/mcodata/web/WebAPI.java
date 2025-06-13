package com.minecraftonline.mcodata.web;

import com.minecraftonline.mcodata.api.exceptions.DataNotFoundException;
import com.minecraftonline.mcodata.util.HTTPUtil;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public final class WebAPI {

    private static final String MCO_API = "http://minecraftonline.com/cgi-bin/";
    private static final String NOTFOUND = "NOTFOUND";

    private WebAPI() {
        throw new UnsupportedOperationException("Util class");
    }

    private static Optional<String> queryMCO(String path) throws IOException {
        String url = MCO_API + path;

        return HTTPUtil.getDataFromURL(url).map(result -> result.replaceAll("\\s+",""));
    }

    private static Optional<Date> getDateData(String path) {
        try {
            Optional<String> result = queryMCO(path);
            if (result.isPresent()) {
                String data = result.get();
                if (data.equals(NOTFOUND)) {
                    return Optional.empty();
                }
                long time = Long.parseLong(data);
                Date date = new Date(time * 1000);
                return Optional.of(date);
            }
            return Optional.empty();
        } catch (IOException e) {
            // We should be able to get data without an IOException, throw up a runtime exception
            throw new DataNotFoundException(e);
        }
    }

    public static Optional<Date> getFirstseen(String username) {
        return getDateData("getfirstseen_unix?" + username);
    }

    public static Optional<Date> getLastseen(String username) {
        return getDateData("getlastseen_unix?" + username);
    }

    public static Optional<Integer> getTimeOnline(String username) {
        try {
            Optional<String> timeOnline = queryMCO("gettimeonline?" + username);
            if (timeOnline.isPresent()) {
                String data = timeOnline.get();
                if (data.equals(NOTFOUND)) {
                    return Optional.empty();
                }
                int time = Integer.parseInt(data);
                return Optional.of(time);
            }
            return Optional.empty();
        } catch (IOException e) {
            throw new DataNotFoundException(e);
        }
    }

    public static Optional<String> getBanReason(String username) {
        AtomicReference<String> temp = new AtomicReference<>();
        try {
            queryMCO("getplayerinfo?" + username).ifPresent(info -> {
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
        } catch (IOException e) {
            throw new DataNotFoundException(e);
        }
    }

    public static Optional<String> getCorrectName(String username) throws IOException {
        Optional<String> correctName = queryMCO("getcorrectname?" + username);
        if (correctName.isPresent()) {
            String data = correctName.get();
            if (data.equals(NOTFOUND)) {
                return Optional.empty();
            } else {
                return Optional.of(data);
            }
        }
        return Optional.empty();
    }

    public static Optional<List<String>> getOnlinePlayers() throws IOException {
        return queryMCO("getplayerlist.sh")
                .map(result -> result.replace(" ", "").split(","))
                .map(List::of);
    }

}
