package tech.kitucode.sockets;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class API {
    private static final Logger log = LogManager.getLogger(App.class);
    private static final int DEFAULT_PORT = 8080;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args != null && args.length > 0 && args[0] != null) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception ignore) {
            }
        }
        log.info("system|about to start app on port:" + port);
        Runtime.getRuntime().addShutdownHook(new App.ShutDownHook());
        try {
            initServer(port);
        } catch (Exception e) {
            log.error("system|error encountered while starting server");
        }
    }

    private static void initServer(Integer port) throws Exception {
        serverSocket = new ServerSocket(port);
        log.info("system|waiting for a client to connect");
        while (true) {
            Socket socket = serverSocket.accept();

            new Thread(() -> {
                log.info("system|client connected from host = " + socket.getLocalAddress() + "|port = " + socket.getPort());

                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));


                    String userInput;
                    while ((userInput = in.readLine()) != null) {
                        userInput = userInput.toLowerCase();

                        log.info("system|client connected from host = " + socket.getLocalAddress() + "|port = "
                                + socket.getPort() + "|received message : " + userInput);

                        Map<String, Object> response = new HashMap<>();
                        response.put("name", "Brian");
                        response.put("age", 24);
                        String responseStr = new Gson().toJson(response);

                        String httpResponse = createHTTPResponse(responseStr);
                        out.println(httpResponse);
                        in.close();
                        socket.close();

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }).start();
        }

    }

    public static String createHTTPResponse(String response) {

        return "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + response.getBytes().length + "\r\n" +
                "Content-Type: application/json\r\n" +
                "\n" +
                response;
    }


}
