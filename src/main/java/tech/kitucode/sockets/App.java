package tech.kitucode.sockets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


public class App {
    private static final Logger log = LogManager.getLogger(App.class);
    private static final int DEFAULT_PORT = 8080;
    private static String[] choices = {"rock", "paper", "scissors"};

    private static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        if (args != null && args.length > 0 && args[0] != null) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception ignore) {
            }
        }
        log.info("system|about to start app on port:" + port);
        try {
            initServer(port);
        } catch (Exception e) {
            log.error("system|error encountered while starting server");
        }
    }

    private static void initServer(Integer port) throws Exception {
        serverSocket = new ServerSocket(port);

        while (true) {
            log.info("system|waiting for a client to connect");
            // this is a blo
            Socket socket = serverSocket.accept();

            new Thread(() -> {
                log.info("system|client connected from port " + socket.getPort() + "|host=" + socket.getLocalAddress());

                try {
                    PrintWriter out =                                            // 2nd statement
                            new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in =                                          // 3rd statement
                            new BufferedReader(
                                    new InputStreamReader(socket.getInputStream()));
                    out.println("Hello, welcome to rock paper and scissors game.....");
                    out.println("Make a choice below to start the game, enter rock, paper or scissors. Enter exit at any time to end game..");

                    String userInput;
                    while ((userInput = in.readLine()) != null) {
                        userInput = userInput.toLowerCase();

                        log.info("system|client connected from port " + socket.getPort() + "|host=" + socket.getLocalAddress() + "|received message : " + userInput);

                        if (userInput.equals("exit")) {
                            socket.close();
                        }

                        // Picking a random element from the array
                        Random random = new Random();
                        String randomChoice = choices[random.nextInt(choices.length)];

                        String response = play(userInput, randomChoice);
                        out.println(response);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }).start();

            // read from socket
        }

    }

    public static String play(String choice, String randomChoice) {
        choice = choice.trim();
        if (choice == null || choice.isEmpty()) {
            return "invalid choice, please enter something";
        }

        // rock, paper, scissors
        // input paper

        //
        if (choice.equals("rock") || choice.equals("paper") || choice.equals("scissors")) {
            // Rock wins against scissors; paper wins against rock; and scissors wins against paper
            // rock, paper, scissors
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("The computer chose ").append(randomChoice).append(".You chose ").append(choice).append(".\n");

            boolean rockWins = choice.equals("rock") && randomChoice.equals("scissors");
            boolean paperWins = choice.equals("paper") && randomChoice.equals("rock");
            boolean scissorsWins = choice.equals("scissors") && randomChoice.equals("paper");
            if (rockWins || paperWins || scissorsWins) {
                stringBuilder.append("Congratulations, you won");
            } else {
                if (choice.equals(randomChoice)) {
                    stringBuilder.append("It's a draw");
                } else {
                    stringBuilder.append("Oops, sorry you lost");
                }
            }

            stringBuilder.append("\nMake a choice below to continue the game, enter rock, paper or scissors, enter exit to end game...");
            stringBuilder.append("\n---------------------------------------------------------------------------------------------------------------------");
            return stringBuilder.toString();
        }else{
            return "invalid choice, please enter either rock, paper or scissors";
        }
    }

    // load app configs
}
