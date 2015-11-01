import java.net.*;
import java.io.*;
import java.util.*;

public class PeerClient
{
   private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
   private static List<String> fileNames = new ArrayList<String>();
   private static MessageProtocol mp_send;
   private static MessageProtocol mp_rec;

   public static void main(String [] args)
   {
      if(args.length != 2)
      {
         System.out.println("Usage: java Client <server-ip> <server-port>");
         return;
      }

      System.out.print("Shared directory path: ");
      String sharedPath = null;
      try
      {
         /**
          * TODO :
          * Recursive search or not?
          * Hidden folders or not ?
          */
         sharedPath = br.readLine();
         if(sharedPath == null || sharedPath.equals(""))
         {
            System.out.println("Shared directory path cannot be empty...");
            System.out.print("Shared directory path: ");
         }
      }
      catch(IOException e1)
      {
         e1.printStackTrace();
      }
      FileList fl = new FileList(sharedPath);
      fileNames = fl.filesRec;
      StringBuffer flistbuffer = new StringBuffer();
      //System.out.println("Files: "+fileNames.toString());
      for(String fname : fileNames)
      {
         flistbuffer.append(fname+"$");
      }
      String sendFileList = flistbuffer.toString();

      String serverName = args[0];
      int port = Integer.parseInt(args[1]);
      try 
      (
         Socket client = new Socket(serverName, port);
         BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
         PrintWriter out = new PrintWriter(client.getOutputStream(), true);
      )
      {
         String mserv = "";
         String muser = "";
         String name = "";
         while( (mserv = in.readLine()) != null)
         {
            mp_rec = new MessageProtocol(mserv);
            if(mp_rec.getMessageType().equals("NAME"))
            {
               System.out.println("Server: "+mp_rec.getMessageContent());
               muser = br.readLine();
               name = muser;
               mp_send = new MessageProtocol("NAME",muser.length(), muser);
               out.println(mp_send.getMessageString());
            }
            else if(mp_rec.getMessageType().equals("WELC"))
            {
               System.out.println(mp_rec.getMessageContent());
               //System.out.println("File list to be sent: "+sendFileList);
               mp_send = new MessageProtocol("LIST", sendFileList.length(), sendFileList);
               out.println(mp_send.getMessageString());
               break;
            }
         }
         System.out.println("Available actions :\n1. srch : Search for a file\n2. mesg : Chat with users\n3. exit : Exit from server");
         while(true)
         {
            System.out.print(name+": ");
            muser = br.readLine();
            if(muser.startsWith("srch"))
            {
               System.out.print("Enter Search String: ");
               muser = br.readLine();
               mp_send = new MessageProtocol("SRCH", muser.length(), muser);
               out.println(mp_send.getMessageString());
               muser = in.readLine();
               mp_rec = new MessageProtocol(muser);
               if(mp_rec.getMessageType().equals("LIST") && (mp_rec.getMessageLength() == mp_rec.getMessageContent().length()))
               {
                  //System.out.println(mp_rec.getMessageContent());
                  String found_users[] = mp_rec.getMessageContent().split("\\$");
                  for(int i=0;i<found_users.length;i++)
                  {
                     System.out.println(found_users[i]);
                  }
               }
            }
            else if(muser.startsWith("exit"))
            {
               mp_send = new MessageProtocol("EXIT", 0, "");
               out.println(mp_send.getMessageString());
               break;
            }
            else 
            {
               mp_send = new MessageProtocol("MESG", muser.length(), muser);
               out.println(mp_send.getMessageString());
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