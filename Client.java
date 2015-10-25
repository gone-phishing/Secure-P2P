import java.net.*;
import java.io.*;

public class Client
{
   public static void main(String [] args)
   {
      if(args.length != 2)
      {
         System.out.println("Usage: java Client <server-ip> <server-port>");
         return;
      }
      String serverName = args[0];
      int port = Integer.parseInt(args[1]);
      try 
      (
         Socket client = new Socket(serverName, port);
         BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
         //BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
         PrintWriter out = new PrintWriter(client.getOutputStream(), true);
      )
      {
         System.out.println("Just connected to "+client.getRemoteSocketAddress());
         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
         String mserv = "";
         String muser = "";
         while( (mserv = in.readLine()) != null)
         {
            System.out.println("Server: "+mserv);
            if(mserv.equals("QUIT"))
            {
               break;
            }
            muser = br.readLine();
            if(muser != null)
            {
               out.println(muser);
            }
         }
      }
      catch(UnknownHostException e1)
      {
         System.err.println("Error 404 : Host not found");
         System.exit(1);
      }
      catch(IOException e2)
      {
         System.err.println("I/O streams not working properly for "+serverName);
      }
   }
}
