import java.net.*;
import java.io.*;
import java.util.*;

class HostServer
{
    private static int MAX_CONNECTION = 5;
    public static void main(String[] args) throws IOException
    {
        if(args.length != 1)
        {
            System.err.println("Usage: java HostServer <host-port>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        boolean listen = true;
        int count_user = 0;
        try (ServerSocket ss = new ServerSocket(port))
        {
            System.out.println("Server hosted at -> "+ss.getLocalSocketAddress());
            while(listen)
            {
                new ThreadServer(ss.accept()).start();
                ++count_user;
                System.out.println("User Count : "+count_user);
                if(count_user == MAX_CONNECTION)
                {
                    listen = false;
                }
            }
        }
        catch(IOException ex)
        {
            System.err.println("Listening failed on port : "+port);
            System.exit(-1);
        }
    }
}