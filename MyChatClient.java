import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class MyChatClient {
    private Socket socket = null;
    private DataInputStream streamIn = null;
    private DataOutputStream streamOut = null;
    private DataInputStream console = null;

    private Thread readThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    String line = streamIn.readUTF();
                    System.out.println(line);
                } catch (Throwable t) {
                    System.out.println(t.getMessage());
                }
            }
        }
    });

    private Thread writeThread = new Thread(new Runnable() {
        @Override
        public void run() {
            String line = "";
            while (line.equals(".bye")) {
                try {
                    line = console.readUTF();
                    streamOut.writeUTF(line);
                    streamOut.flush();
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

    private MyChatClient(String server, int port) {
        try {
            socket = new Socket(server, port);
            System.out.println("Connection established with server");
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }

        readThread.start();
        writeThread.start();
    }

    static public void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java MyChatClient server port");
        } else {
            new MyChatClient(args[0], Integer.parseInt(args[1]));
        }
    }
}
