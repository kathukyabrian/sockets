package tech.kitucode.sockets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class App {
    private static final Logger log = LogManager.getLogger(App.class);
    private static final int DEFAULT_PORT = 8080;
    private static String[] choices = {"rock", "paper", "scissors"};
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
        Runtime.getRuntime().addShutdownHook(new ShutDownHook());
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

                    out.println(prepareWelcomeMessage());

                    String userInput;
                    while ((userInput = in.readLine()) != null) {
                        userInput = userInput.toLowerCase();

                        log.info("system|client connected from host = " + socket.getLocalAddress() + "|port = "
                                + socket.getPort() + "|received message : " + userInput);

                        if (userInput.equals("exit")) {
                            log.info("system|closing socket|host = " + socket.getLocalAddress() + "|port = "
                                    + socket.getPort());
                            socket.close();
                            in.close();
                        }

                        // pick a random choice from choices array
                        Random random = new Random();
                        String randomChoice = choices[random.nextInt(choices.length)];

                        StringBuilder response = play(userInput, randomChoice);
                        addResponseRider(response);
                        out.println(response);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }).start();
        }

    }

    public static StringBuilder play(String choice, String randomChoice) {
        choice = choice.trim();
        if (choice == null || choice.isEmpty()) {
            return new StringBuilder("invalid choice, please enter something\n");
        }

        if (choice.equals("rock") || choice.equals("paper") || choice.equals("scissors")) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("The computer chose ").append(randomChoice).append(".You chose ").append(choice).append(".\n");

            boolean won = checkIfUserWon(choice, randomChoice);
            if (won) {
                stringBuilder.append("Congratulations, you won\n");
            } else {
                if (choice.equals(randomChoice)) {
                    stringBuilder.append("It's a draw\n");
                } else {
                    stringBuilder.append("Oops, sorry you lost\n");
                }
            }
            return stringBuilder;
        } else {
            return new StringBuilder("invalid choice, please enter either rock, paper or scissors...\n");
        }
    }

    private static boolean checkIfUserWon(String choice, String randomChoice) {
        boolean rockWins = choice.equals("rock") && randomChoice.equals("scissors");
        boolean paperWins = choice.equals("paper") && randomChoice.equals("rock");
        boolean scissorsWins = choice.equals("scissors") && randomChoice.equals("paper");

        return rockWins || paperWins || scissorsWins;
    }

    private static void addResponseRider(StringBuilder response) {
        response.append("Make a choice below to continue the game, enter rock, paper or scissors, enter exit to end game...\n");
        response.append("---------------------------------------------------------------------------------------------------------------------");
    }

    private static String prepareWelcomeMessage() {
        return "Hello, welcome to rock paper and scissors game.....\n" +
                "Make a choice below to play the game, enter rock, paper or scissors. Enter exit at any time to end game..";
    }

    static class ShutDownHook extends Thread {
        @Override
        public void run() {
            log.info("system|closing application gracefully|cleaning up resources");
            try {
                serverSocket.close();
            } catch (IOException e) {
                log.error("system|error while closing server socket|port = " + serverSocket.getLocalPort());
            }
        }
    }
}
