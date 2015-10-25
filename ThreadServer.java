import java.net.*;
import java.io.*;
import java.util.*;

public class ThreadServer extends Thread
{
   private Socket socket = null;
   public ThreadServer(Socket s)
   {
      super("Multi threaded server");
      this.socket = s;
   }

   public void run()
   {
      try
      (
         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         //BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      )
      {
         System.out.println( "JOIN : "+socket.getRemoteSocketAddress());
         out.println("Connected to server");
         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
         String mclie = "";
         String mserv = "";
         while( (mclie = in.readLine()) != null)
         {
            System.out.println("Client: "+mclie);
            if(mclie.equals("QUIT"))
            {
               break;
            }
            mserv = br.readLine();
            if(mserv != null)
            {
               out.println(mserv);
            }
         }
         socket.close();
      }
      catch(IOException e)
      {
         e.printStackTrace();
      }
   }
}
