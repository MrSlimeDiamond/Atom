package net.slimediamond.atom.services;

import com.google.inject.Inject;
import io.javalin.Javalin;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.slimediamond.atom.common.annotations.Service;
import net.slimediamond.atom.reference.WebReference;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service("web api")
public class WebServer {
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    @Inject
    private JDA jda;

    @Service.Start
    public void onStart() {
        Javalin app = Javalin.create().start(WebReference.PORT);
        app.error(404, ctx -> ctx.json("{\"message\": \"404 not found\"}"));

        app.get("/discord/picture/{name}", ctx -> {
            List<Member> members = new ArrayList<>();
            jda.getGuilds().forEach(guild ->
                    members.addAll(guild.getMembersByEffectiveName(ctx.pathParam("name"), true))
            );

            members.stream().findFirst().ifPresentOrElse(user -> {
                String avatarUrl = user.getEffectiveAvatarUrl(); // Get the avatar URL
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(avatarUrl))
                            .build();

                    HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

                    if (response.statusCode() == 200) {
                        ctx.contentType("image/png"); // Set appropriate content type
                        ctx.result(response.body()); // Serve the image data
                    } else {
                        ctx.status(404).result("Failed to fetch avatar.");
                    }
                } catch (Exception e) {
                    ctx.status(500).result("Error fetching avatar.");
                }
            }, () -> ctx.status(404));
        });
    }
}
