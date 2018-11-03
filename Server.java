import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private ChatThread chatThread = null;
    private ServerSocket serverSocket = null;
    private Socket socket = null;

    public Server(String name, int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("waiting for a client...");

            socket = serverSocket.accept();
            System.out.println("client connected");

            chatThread = new ChatThread(socket, name);
            chatThread.start();

            PrintWriter outputStream = new PrintWriter(socket.getOutputStream());

            Scanner keyboard = new Scanner(System.in);
            while (keyboard.hasNext()) {
                String msg = keyboard.nextLine();
                outputStream.println(msg);
                outputStream.flush();
            }

            socket.close();
            keyboard.close();
            outputStream.close();

        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Server port");
        } else {
            new Server("server", Integer.parseInt(args[0]));
        }
    }
}
