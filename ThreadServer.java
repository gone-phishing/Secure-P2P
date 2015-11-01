import java.net.*;
import java.io.*;
import java.util.*;

public class ThreadServer extends Thread
{
   private Socket socket = null;
   private String name = null;
   private Node node = null;
   private MessageProtocol mp_send;
   private MessageProtocol mp_rec;
   private ArrayList<String> fileList = new ArrayList<String>();

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
            mp_send = new MessageProtocol("NAME",22,"Select your username ");
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
         System.out.println( "JOIN : "+name+" has joined the server");

         String addr[] = socket.getRemoteSocketAddress().toString().split(":");
         node = new Node(name, addr[0].substring(1, addr[0].length()), Integer.parseInt(addr[1]));
         synchronized (HostServer.nodes)
         {
            HostServer.nodes.add(node);
            HostServer.broadcastList.add(out);
         }

         String welcnote = "Welcome "+node.getName()+" to the P2P server";
         mp_send = new MessageProtocol("WELC", welcnote.length(), welcnote);
         out.println(mp_send.getMessageString());

         String recvList = in.readLine();
         mp_rec = new MessageProtocol(recvList);
         if(mp_rec.getMessageType().equals("LIST") && (mp_rec.getMessageLength() == mp_rec.getMessageContent().length()))
         {
            String str[] = mp_rec.getMessageContent().split("\\$");
            for(int i=0;i<str.length;i++)
            {
               fileList.add(str[i]);
            }
         }
         //System.out.println(fileList.toString());
         HostServer.metadata.put(node.getID(), fileList);

         String mclie = "";
         while( (mclie = in.readLine()) != null)
         {
            mp_rec = new MessageProtocol(mclie);
            if(mp_rec.getMessageType().equals("MESG"))
            {
               String message = name+": "+mp_rec.getMessageContent();
               System.out.println(message);
               // for(PrintWriter pw : HostServer.broadcastList)
               // {
               //    mp_send("MESG", message.length(), message);
               //    pw.println(name+": "+mp_rec.getMessageContent());
               // }
            }
            else if(mp_rec.getMessageType().equals("SRCH"))
            {
               String keywords = mp_rec.getMessageContent();
               System.out.println("Searching for "+keywords);
               StringBuffer respbuf= new StringBuffer();
               respbuf.append("Following users have the file:$");
               for(String str : HostServer.metadata.keySet())
               {
                  if(HostServer.metadata.get(str).contains(keywords))
                  {
                     String nodeinfo[] = str.split("@");
                     respbuf.append(nodeinfo[0]+" "+nodeinfo[1]+"$");
                     // System.out.println(nodeinfo[0]+" has it");
                  }
               }
               String resp = respbuf.toString();
               mp_send = new MessageProtocol("LIST",resp.length(), resp);
               out.println(mp_send.getMessageString());
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
            HostServer.nodes.remove(node);
            for(String key : HostServer.metadata.keySet())
            {
               if(key.equals(node.getID()))
               {
                  HostServer.metadata.remove(key);
                  break;
               }
            }
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
