import java.net.*;
import java.io.*;
import java.util.*;

public class PeerClient
{
   private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
   private static List<String> fileNames = new ArrayList<String>();
   private static MessageProtocol mp_send;
   private static MessageProtocol mp_rec;
   public static String name = "";
   private static int peerhostport;
   public static boolean download_status = false;

   public static void main(String [] args)
   {
      if(args.length != 3)
      {
         System.out.println("Usage: java Client <host-server-ip> <host-server-port> <peerserv-port>");
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

      /**
       * Get all files from the shared directory and append it 
       * to a string buffer using $ as the delimeter. The sendFileList
       * string contains the string to be transfered to the server.
       */
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
      peerhostport = Integer.parseInt(args[2]);

      // Start the peer server on the specified port
      new PeerServer(peerhostport).start();

      // Connect to the hostserver
      try 
      (
         Socket client = new Socket(serverName, port);
         BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
         PrintWriter out = new PrintWriter(client.getOutputStream(), true);
      )
      {
         String mserv = "";
         String muser = "";

         // Register username and then send file list and peer server port info.
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
               // Send file list and peer port to host server
               System.out.println(mp_rec.getMessageContent());
               mp_send = new MessageProtocol("LIST", sendFileList.length(), sendFileList);
               out.println(mp_send.getMessageString());
               String phostport = ""+peerhostport;
               mp_send = new MessageProtocol("PORT", phostport.length(), phostport);
               out.println(mp_send.getMessageString());
               break;
            }
         }

         // Tell available actions to the peer nodes
         System.out.println("Available actions :\n1. srch : Search for a file\n2. mesg : Chat with users\n3. dump : List of all files available\n4. date : Get server date and time\n5. update : Update file list on server\n6. exit : Exit from server");
         while(true)
         {
            System.out.print(name+": ");
            muser = br.readLine();

            if(muser.startsWith("srch"))
            {
               // Request search for a particular file
               System.out.print("Enter Search String: ");
               muser = br.readLine();
               mp_send = new MessageProtocol("SRCH", muser.length(), muser);
               out.println(mp_send.getMessageString());

               // Check hostserver's response for availability of the file
               muser = in.readLine();
               mp_rec = new MessageProtocol(muser);
               if(mp_rec.getMessageType().equals("FAIL") && (mp_rec.getMessageLength() == mp_rec.getMessageContent().length()))
               {
                  System.out.println(mp_rec.getMessageContent());
               }
               else if(mp_rec.getMessageType().equals("LIST") && (mp_rec.getMessageLength() == mp_rec.getMessageContent().length()))
               {
                  // Get list of all users having the file
                  String found_users[] = mp_rec.getMessageContent().split("\\$");
                  int num_users = found_users.length;
                  for(int i=0;i<found_users.length;i++)
                  {
                     System.out.println(found_users[i]);
                  }

                  // Making arraylist of all available users and removing the top helper line
                  List<String> found_user_list = new ArrayList<String>(Arrays.asList(found_users));
                  found_user_list.remove(0);

                  // Loop till successfull download or list exhausts
                  while(num_users > 0)
                  {
                     // Read peer number from the console
                     muser = br.readLine();
                     int sel_user = Integer.parseInt(muser);
                     --sel_user;

                     // Send username, filename, ip, port for file
                     String selected_user[] = found_user_list.get(sel_user).split(" ");
                     new FileDownload(selected_user[1] ,selected_user[2], selected_user[3], selected_user[4]);

                     if(download_status)
                     {
                        System.out.println("File download successfull :)");
                        break;
                     }
                     else
                     {
                        found_user_list.remove(sel_user);
                        for(int i=0;i<found_user_list.size();i++)
                        {
                           System.out.println(found_user_list.get(i));
                        }
                        --num_users;
                     }
                  }
                  if(num_users == 0)
                  {
                     System.out.println("Sorry, The file cannont be downloaded currently. Please try again later with a different username :)");
                  }
               }
            }
            else if(muser.startsWith("exit"))
            {
               mp_send = new MessageProtocol("EXIT", 0, "");
               out.println(mp_send.getMessageString());
               PeerServer.listen = false;
               break;
            }
            else if(muser.startsWith("dump"))
            {
               mp_send = new MessageProtocol("DUMP", 0, "");
               out.println(mp_send.getMessageString());
               muser = in.readLine();
               mp_rec = new MessageProtocol(muser);
               if(mp_rec.getMessageType().equals("DUMP") && (mp_rec.getMessageLength() == mp_rec.getMessageContent().length()))
               {
                  String file_list_dump[] = mp_rec.getMessageContent().split("\\$");
                  System.out.println(file_list_dump[0]);
                  for(int i=1; i< file_list_dump.length; i++)
                  {
                     System.out.println(i+". "+file_list_dump[i]);
                  }
               }
            }
            else if(muser.startsWith("date"))
            {
               mp_send = new MessageProtocol("DATE", 0, "");
               out.println(mp_send.getMessageString());
               muser = in.readLine();
               mp_rec = new MessageProtocol(muser);
               if(mp_rec.getMessageType().equals("DATE") && (mp_rec.getMessageLength() == mp_rec.getMessageContent().length()))
               {
                  System.out.println(mp_rec.getMessageContent());
               }
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
      finally
      {
         System.exit(0);
      }
   }
}
