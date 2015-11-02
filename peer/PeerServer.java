import java.net.*;
import java.io.*;
import java.util.*;

class PeerServer extends Thread
{
	private int port;
	public static int count = 0;
	//public static ServerSocket ss;
	public PeerServer(int port)
	{
		this.port = port;
	}

	public void run()
	{
		boolean listen = true;
		try
		(
			ServerSocket ss = new ServerSocket(port);
		)
		{
			System.out.println("Peerserver started on port: "+port);
			while(listen)
			{
				new PeerServerHandler(ss.accept()).start();
				count++;
			}
		}
		catch(IOException e1)
		{
			e1.printStackTrace();
		}
	}
}