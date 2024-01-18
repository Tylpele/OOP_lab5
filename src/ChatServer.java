import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);

                new Thread(clientHandler).start(); // каждый пользователь в отдельном потоке
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable, Serializable {
        private  Socket clientSocket;
        private  BufferedReader reader;
        private  PrintWriter writer;
        private String username;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                //инициализация переменных для ввода-вывода из потока клиентского сокета
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                //чтение имени пользователя от клиента
                username = reader.readLine();
                broadcast(username + " joined the chat.", null);

                String clientMessage;
                while ((clientMessage = reader.readLine()) != null) {
                    broadcast(username + ": " + clientMessage, this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    //удаление клиента при закрытии приложения
                    clients.remove(this);
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcast(String message, ClientHandler sender) {
            for (ClientHandler client : clients) { // перебор всех пользователей
                if (client != sender) { //исключение из отправки самого отправителя
                    try {
                        PrintWriter clientWriter = new PrintWriter(client.clientSocket.getOutputStream(), true);
                        clientWriter.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
