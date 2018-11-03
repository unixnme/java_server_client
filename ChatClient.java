import java.net.*;
import java.io.*;

public class ChatClient
{  private Socket socket              = null;
   private DataInputStream  console   = null;
   private DataInputStream streamIn = null;
   private DataOutputStream streamOut = null;
   private boolean done = false;

   private Thread readThread = new Thread(new Runnable() {
      @Override
      public void run() {
         while (!done) {
            try {
               String line = streamIn.readUTF();
               System.out.println(line);
            } catch (Throwable t) {
               System.out.println(t.getMessage());
            }
         }
      }
   });

   public ChatClient(String serverName, int serverPort)
   {  System.out.println("Establishing connection. Please wait ...");
      try
      {  socket = new Socket(serverName, serverPort);
         System.out.println("Connected: " + socket);
         start();
      }
      catch(UnknownHostException uhe)
      {  System.out.println("Host unknown: " + uhe.getMessage());
      }
      catch(IOException ioe)
      {  System.out.println("Unexpected exception: " + ioe.getMessage());
      }
      readThread.start();
      String line = "";
      while (!line.equals(".bye"))
      {  try
         {  line = console.readLine();
            streamOut.writeUTF(line);
            streamOut.flush();
         }
         catch(IOException ioe)
         {  System.out.println("Sending error: " + ioe.getMessage());
         }
      }
      done = true;
      try {
          readThread.join();
      } catch (Throwable t) {
          System.out.println(t.getMessage());
      }
      stop();
   }
   public void start() throws IOException
   {  console   = new DataInputStream(System.in);
      streamOut = new DataOutputStream(socket.getOutputStream());
      streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
   }
   public void stop()
   {  try
      {  if (console   != null)  console.close();
         if (streamOut != null)  streamOut.close();
         if (socket    != null)  socket.close();
         if (streamIn  != null)  streamIn.close();
      }
      catch(IOException ioe)
      {  System.out.println("Error closing ...");
      }
   }
   public static void main(String args[])
   {  ChatClient client = null;
      if (args.length != 2)
         System.out.println("Usage: java ChatClient host port");
      else
         client = new ChatClient(args[0], Integer.parseInt(args[1]));
   }
}
