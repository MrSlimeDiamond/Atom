package net.zenoc.atom.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Date;

public class API implements Service {
    HttpServer server;
    Algorithm algorithm = Algorithm.HMAC256("FIXME: this is not secure");
    JWTVerifier verifier = JWT.require(algorithm).withIssuer("API").build();

    @Override
    public void startService() throws Exception {
        // TODO: Configurable port
        server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/ping", this::ping);
        server.createContext("/test", this::test);
        server.setExecutor(null);
        server.start();
    }

    private String generateToken(String name) {
        return JWT.create()
                .withIssuer("API")
                .withSubject(name)
                .withClaim("admin", true)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }
    public void ping(HttpExchange t) throws IOException {
        JSONObject json = new JSONObject()
                .put("result", "Pong");
        String response = json.toString();
        t.getResponseHeaders().set("Content-Type", "application/json");
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    public void test(HttpExchange t) throws IOException {
        String response = generateToken("findlayr");
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
