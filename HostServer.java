import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

class HostServer
{
    public static Set<String> usernames = new HashSet<String>();
    public static List<Node> nodes = new ArrayList<Node>();
    public static Map<String, ArrayList<String>> metadata = new ConcurrentHashMap<String, ArrayList<String>>();
    public static int count_user = 0;

    public static void main(String[] args) throws IOException
    {
        if(args.length != 1)
        {
            System.err.println("Usage: java HostServer <host-port>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        boolean listen = true;

        try (ServerSocket ss = new ServerSocket(port))
        {
            // TODO : Get server public ip from eth0 or wlan
            System.out.println("Server hosted at -> "+ss.getLocalSocketAddress());
            while(listen)
            {
                new ThreadServer(ss.accept()).start();
                ++count_user;
                System.out.println("User Count : "+count_user);
            }
        }
        catch(IOException ex)
        {
            System.err.println("Listening failed on port : "+port);
            System.exit(-1);
        }
    }
}