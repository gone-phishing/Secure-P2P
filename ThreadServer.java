import java.net.*;
import java.io.*;
import java.util.*;

public class ThreadServer extends Thread
{
   private Socket socket = null;
   private String name = null;
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
         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      )
      {
         while(true)
         {
            out.println("NAME");
            name = in.readLine();
            if(name == null)
            {
               return;
            }
            synchronized (HostServer.usernames)
            {
               if(!HostServer.usernames.contains(name))
               {
                  HostServer.usernames.add(name);
                  break;
               }
            }
         }
         System.out.println( "JOIN : "+name);

         String addr[] = socket.getRemoteSocketAddress().toString().split(":");
         Node node = new Node(name, addr[0].substring(1, addr[0].length()), Integer.parseInt(addr[1]));
         synchronized (HostServer.nodes)
         {
            HostServer.nodes.add(node);
         }
         out.println("WELCOME "+node.getName()+" to the P2P server");

         String mclie = "";
         while( (mclie = in.readLine()) != null)
         {
            if(mclie.startsWith("MESG"))
            {
               System.out.println(name+": "+mclie.substring(5,mclie.length()));
            }
            if(mclie.equals("EXIT"))
            {
               break;
            }
         }
      }
      catch(IOException e)
      {
         e.printStackTrace();
      }
      finally
      {
         if(name != null)
         {
            HostServer.usernames.remove(name);
            --HostServer.count_user;
            System.out.println("QUIT : "+name+" left the server");
         }
         try
         {
            socket.close();
         }
         catch(IOException e1)
         {
            e1.printStackTrace();
         }
      }
   }
}
