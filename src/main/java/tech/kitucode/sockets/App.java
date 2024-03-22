package tech.kitucode.sockets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;

/**
 * Hello world!
 */
public class App {
    private static final Logger log = LogManager.getLogger(App.class);
    private static final int DEFAULT_PORT = 8080;


    private static ServerSocket serverSocket;
    private static PrintWriter out;
    private static BufferedReader in;


    public static void main(String[] args) {

        Integer port = DEFAULT_PORT;
        if (args[0] != null) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception ignore) {
            }
        }
        log.info("system|about to start app on port:" + port);

//        initServer(port);
    }

    private static void initServer(Integer port) throws Exception {
        serverSocket = new ServerSocket(port);

    }

    // load app configs
}
