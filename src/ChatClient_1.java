import java.io.*;
import java.net.Socket;

public class ChatClient_1 {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8888);

            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            System.out.print("Enter your username: ");
            String username = consoleReader.readLine();
            writer.println(username);

            writeUsernameToFile(username);

            new Thread(() -> {
                try {
                    BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String serverMessage;
                    while ((serverMessage = serverReader.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            String message;
            while (true) {
                message = consoleReader.readLine();
                writer.println(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeUsernameToFile(String username) {
        try (PrintWriter fileWriter = new PrintWriter(new FileWriter("username.txt", true))) {
            fileWriter.println(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
