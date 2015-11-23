import java.util.*;
import java.io.*;
import java.net.*;

class FileDownload
{
	private String username="";
	private String filename="";
	private String ip = "";
	private int port = 0;
	private MessageProtocol mp_send;
    private MessageProtocol mp_rec;

	FileDownload(String username, String filename, String ip, String prt)
	{
		this.username = username;
		this.filename = filename;
		this.ip = ip;
		this.port = Integer.parseInt(prt);
		try
		(
			Socket s = new Socket(ip, port);
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
         	PrintWriter out = new PrintWriter(s.getOutputStream(), true);
		)
		{
			String req = PeerClient.name + "$" + filename;
			mp_send = new MessageProtocol("PING", req.length(), req);
			out.println(mp_send.getMessageString());
			String muser = in.readLine();
			mp_rec = new MessageProtocol(muser);
			if(mp_rec.getMessageType().equals("RACK"))
			{
				if(mp_rec.getMessageContent().equals("Y"))
				{

					PeerClient.download_status = true;
				}
				else if(mp_rec.getMessageContent().equals("N"))
				{
					System.out.println(username+" denied download request");
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			System.out.println(username+" does not have free slots for download");
		}
	}
}