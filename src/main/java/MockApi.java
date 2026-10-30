import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/** Minimal mock API used as the test environment for the API smoke tests. */
public class MockApi {
    private static final String PRODUCTS =
            "[{\"id\":1,\"name\":\"Laptop\",\"price\":999.99},"
            + "{\"id\":2,\"name\":\"Mouse\",\"price\":19.5}]";

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", MockApi::handle);
        server.start();
        System.out.println("MockApi listening on port " + port);
    }

    private static void handle(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();
        String path = ex.getRequestURI().getPath();

        if (method.equals("GET") && path.equals("/health")) {
            send(ex, 200, "{\"status\":\"UP\"}");
        } else if (method.equals("GET") && path.equals("/api/products")) {
            send(ex, 200, PRODUCTS);
        } else if (method.equals("POST") && path.equals("/api/login")) {
            String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            if (body.contains("\"username\":\"admin\"") && body.contains("\"password\":\"password123\"")) {
                send(ex, 200, "{\"token\":\"mock-token\"}");
            } else {
                send(ex, 401, "{\"error\":\"Invalid credentials\"}");
            }
        } else {
            send(ex, 404, "{\"error\":\"Not Found\"}");
        }
    }

    private static void send(HttpExchange ex, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
