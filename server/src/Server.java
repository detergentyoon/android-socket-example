import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 9998;
    private static List<ClientHandler> clientHandlers = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("서버가 시작되었습니다.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("새로운 클라이언트 접속: " + clientSocket.getInetAddress().getHostAddress());

                // 클라이언트 핸들러 생성 및 시작
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String clientAddress = clientSocket.getInetAddress().getHostAddress();
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(clientAddress + " >>> " + message);
                    // 모든 클라이언트에게 메시지 전송
                    for (ClientHandler handler : clientHandlers) {
                        handler.sendMessage(message);
                    }
                }
            } catch (IOException e) {
                System.out.println("클라이언트가 연결을 해제했습니다.");
            } finally {
                try {
                    clientSocket.close();
                    clientHandlers.remove(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 클라이언트에게 메시지 전송
        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
