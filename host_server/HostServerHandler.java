import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class HostServerHandler extends Thread
{
   private Socket socket = null;
   private String name = null;
   private Node node = null;
   private MessageProtocol mp_send;
   private MessageProtocol mp_rec;
   private ArrayList<String> fileList = new ArrayList<String>();

   public HostServerHandler(Socket s)
   {
      super("Host Server handler");
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

         // Send welcome message to the peer client
         String welcnote = "Welcome "+name+" to the P2P server";
         mp_send = new MessageProtocol("WELC", welcnote.length(), welcnote);
         out.println(mp_send.getMessageString());

         // Recieve the shared file list from the peer client
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

         // Set the peerport variable of the peer node
         int p_port = 0;
         String peerport = in.readLine();
         mp_rec = new MessageProtocol(peerport);
         if(mp_rec.getMessageType().equals("PORT"))
         {
            p_port = Integer.parseInt(mp_rec.getMessageContent());
         }

         // Add the peer node to the hostserver list
         String addr[] = socket.getRemoteSocketAddress().toString().split(":");
         node = new Node(name, addr[0].substring(1, addr[0].length()), Integer.parseInt(addr[1]), p_port);
         synchronized (HostServer.nodes)
         {
            HostServer.nodes.add(node);
            HostServer.broadcastList.add(out);
         }

         // Add the list of files to hostserver's metadata collection
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
               int num_users = 0;

               /**
                * For substring file name search
                */
               for(String str1 : HostServer.metadata.keySet())
               {
                  boolean flag = false;
                  for(String str2 : HostServer.metadata.get(str1))
                  {
                     if(str2.toLowerCase().contains(keywords.toLowerCase()))
                     {
                        String nodeinfo[] = str1.split("@");
                        if(nodeinfo[0].equals(name) && str2.equals(keywords))
                        {
                           num_users = -1;
                           flag = true;
                           break;
                        }
                        else 
                        {
                           ++num_users;
                           respbuf.append(num_users+". "+nodeinfo[0]+" "+str2+" "+nodeinfo[1]+" "+nodeinfo[2]+"$");
                        }     
                     }
                     if(flag)
                     {
                        break;
                     }
                  }

                  /**
                   * For exact file name search
                   */
                  // if(HostServer.metadata.get(str).contains(keywords))
                  // {
                  //    String nodeinfo[] = str.split("@");
                  //    if(nodeinfo[0].equals(name))
                  //    {
                  //       num_users = -1;
                  //    }
                  //    else 
                  //    {
                  //       ++num_users;
                  //       respbuf.append(num_users+". "+nodeinfo[0]+" "+nodeinfo[1]+"$");
                  //    }
                  // }
               }
               String resp = null;
               if(num_users == -1)
               {
                  resp = "File locally present"; 
                  mp_send = new MessageProtocol("FAIL",resp.length(), resp);
                  out.println(mp_send.getMessageString());
               }
               else if(num_users > 0)
               {
                  resp = respbuf.toString();
                  mp_send = new MessageProtocol("LIST",resp.length(), resp);
                  out.println(mp_send.getMessageString());
               }
               else
               {
                  resp = "Oops... File not found";
                  mp_send = new MessageProtocol("FAIL",resp.length(), resp);
                  out.println(mp_send.getMessageString());
               }
            }
            else if(mp_rec.getMessageType().equals("EXIT"))
            {
               break;
            }
            else if(mp_rec.getMessageType().equals("DUMP"))
            {
               StringBuffer respfilesbuf = new StringBuffer();
               respfilesbuf.append("File list on server:$");
               for(String str : HostServer.metadata.keySet())
               {
                  ArrayList<String> al = HostServer.metadata.get(str);
                  for(String str2 : al)
                  {
                     respfilesbuf.append(str2+"$");
                  } 
               }
               String respfiles = respfilesbuf.toString();
               mp_send = new MessageProtocol("DUMP", respfiles.length(), respfiles);
               out.println(mp_send.getMessageString());
            }
            else if(mp_rec.getMessageType().equals("DATE"))
            {
               DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
               Date date = new Date();
               String date_str = "Server date and time: "+dateFormat.format(date);
               mp_send = new MessageProtocol("DATE", date_str.length(), date_str);
               out.println(mp_send.getMessageString());
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
