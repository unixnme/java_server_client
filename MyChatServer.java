import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MyChatServer {
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream streamIn = null;
    private DataOutputStream streamOut = null;
    private DataInputStream console = null;

    private Thread readThread = new Thread(new Runnable() {
        @Override
        public void run() {
            boolean done = false;
            while (!done) {
                try {
                    String line = streamIn.readUTF();
                    System.out.println(line);
                    done = line.equals(".bye");
                } catch (Throwable t) {
                    System.out.println(t.getMessage());
                }
            }
            try {
                if (socket != null) socket.close();
                if (streamIn != null) streamIn.close();
                if (streamOut != null) streamOut.close();
            } catch (Throwable t) {
                System.out.println(t.getMessage());
            }
        }
    });

    private Thread writeThread = new Thread(new Runnable() {
        @Override
        public void run() {
            String line;
            while (true) {
                try {
                    line = console.readUTF();
                    streamOut.writeUTF(line);
                    streamOut.flush();
                } catch (Throwable t) {
                    System.out.println(t.getMessage());
                }
            }
        }
    });

    private MyChatServer(int port) {
        System.out.println("Binding to port " + port + "...");
        try {
            server = new ServerSocket(port);
            System.out.println("Server started...");
            System.out.println("Waiting for a client...");

            socket = server.accept();
            System.out.println("Client joined");

            streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            streamOut = new DataOutputStream(socket.getOutputStream());
            console = new DataInputStream(System.in);

            readThread.start();
            writeThread.start();

        } catch(Throwable t) {
            System.out.println(t.getMessage());
        }
    }

    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("Usage: java MyChatServer port");
        } else {
            new MyChatServer(Integer.parseInt(args[0]));
        }
    }
}
