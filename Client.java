import java.net.*;
import java.io.*;
import java.util.*;

public class Client
{
   private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
   private static List<String> fileNames = new ArrayList<String>;

   public static void main(String [] args)
   {
      if(args.length != 2)
      {
         System.out.println("Usage: java Client <server-ip> <server-port>");
         return;
      }
      String serverName = args[0];
      int port = Integer.parseInt(args[1]);

      System.out.print("Shared directory path: ");
      String sharedPath = br.readLine();
      FileList fl = new FileList(sharedPath);
      fileNames = fl.filesRec;

      try 
      (
         Socket client = new Socket(serverName, port);
         BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
         PrintWriter out = new PrintWriter(client.getOutputStream(), true);
      )
      {
         String mserv = "";
         String muser = "";
         while( (mserv = in.readLine()) != null)
         {
            System.out.println("Server: "+mserv);
            if(mserv.startsWith("NAME"))
            {
               muser = br.readLine();
               out.println(muser);
            }
            else if(mserv.startsWith("WELC"))
            {
               System.out.println("Just connected to "+client.getRemoteSocketAddress());
               break;
            }
         }
         while(true)
         {
            muser  = br.readLine();
            out.println(muser);
            if(muser.equals("exit"))
            {
               break;
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
