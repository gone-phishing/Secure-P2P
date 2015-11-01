import java.net.*;
import java.io.*;
import java.util.*;

public class ThreadServer extends Thread
{
   private Socket socket = null;
   private String name = null;
   private MessageProtocol mp_send;
   private MessageProtocol mp_rec;
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
            mp_send = new MessageProtocol("NAME",22,"Select your username: ");
            out.println(mp_send.getMessageString());
            String namemesg = in.readLine();
            mp_rec = new MessageProtocol(namemesg);
            if(mp_rec.getMessageType().equals("NAME"))
            {
               name = mp_rec.getMessageContent();
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
         }
         System.out.println( "JOIN : "+name);

         String addr[] = socket.getRemoteSocketAddress().toString().split(":");
         Node node = new Node(name, addr[0].substring(1, addr[0].length()), Integer.parseInt(addr[1]));
         synchronized (HostServer.nodes)
         {
            HostServer.nodes.add(node);
         }
         String welcnote = "Welcome "+node.getName()+" to the P2P server";
         mp_send = new MessageProtocol("WELC", welcnote.length(), welcnote);
         out.println(mp_send.getMessageString());

         String mclie = "";
         while( (mclie = in.readLine()) != null)
         {
            mp_rec = new MessageProtocol(mclie);
            if(mp_rec.getMessageType().equals("MESG"))
            {
               System.out.println(name+": "+mp_rec.getMessageContent());
            }
            else if(mp_rec.getMessageType().equals("SRCH"))
            {
               
            }
            if(mp_rec.getMessageType().equals("EXIT"))
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
