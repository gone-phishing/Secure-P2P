import java.net.*;
import java.io.*;
import java.util.*;

class PeerServer extends Thread
{
	private int port;
	private String sharedPath;
	public static int count = 0;
	public static boolean listen = true;
	private int MAX_CONN = 5;

	public PeerServer(int port, String sharedPath)
	{
		this.port = port;
		this.sharedPath = sharedPath;
	}

	public void run()
	{
		try
		(
			ServerSocket ss = new ServerSocket(port);
		)
		{
			System.out.println("Peerserver started on port: "+port);
			while(listen)
			{
				new PeerServerHandler(ss.accept(), sharedPath).start();
				count++;
				if(count == MAX_CONN)
				{
					while(count == MAX_CONN)
					{
						try
						{
							Thread.sleep(10000);
						}
						catch(InterruptedException ie)
						{
							ie.printStackTrace();
						}
					}
				}
			}
		}
		catch(IOException e1)
		{
			e1.printStackTrace();
		}
	}
}