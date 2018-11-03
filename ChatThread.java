import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatThread extends Thread {
    private Socket socket = null;
    private Scanner inputStream = null;
    private String name = null;

    /**
     * Socket must be already connected
     * @param socket
     * @param name
     */
    public ChatThread(Socket socket, String name) throws Throwable {
        super(name);
        setSocket(socket);
        inputStream = new Scanner(socket.getInputStream());
    }

    public void run() {
        while (inputStream.hasNext()) {
            String inputString = inputStream.nextLine();
            System.out.println(inputString);
        }
        try {
            if (socket != null) socket.close();
            if (inputStream != null) inputStream.close();
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }
    }

    private void setSocket(Socket socket) throws Throwable {
        if (socket == null) throw new Throwable("invalid socket");
        if (!socket.isConnected()) throw new Throwable("socket not connected");
        this.socket = socket;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java ChatThread server_address port");
        } else {
            try {
                Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
                ChatThread t = new ChatThread(socket, "client");
                t.start();

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
    }
}
