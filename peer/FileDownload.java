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
			//BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			InputStream is = s.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(is)); 
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
				String rack_op[] = mp_rec.getMessageContent().split("\\$");
				if(rack_op[0].equals("Y"))
				{
					FileOutputStream fos = new FileOutputStream(filename);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					int file_size = Integer.parseInt(rack_op[1]);
					int bytesRead;
					int current = 0;
					byte [] mybytearray  = new byte [file_size+10];
				    bytesRead = is.read(mybytearray,0,mybytearray.length);
				    current = bytesRead;
				    do 
				    {
				    	System.out.println("Read: "+((current*100.0)/file_size) + "%");
				        bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
				        if(bytesRead >= 0) current += bytesRead;
				    } 
				    while(bytesRead > -1);

				    bos.write(mybytearray, 0 , current);
				    bos.flush();
				    System.out.println("File " + filename + " downloaded (" + current + " bytes read)");
				    fos.close();
				    bos.close();
					PeerClient.download_status = true;
				}
				else if(rack_op[0].equals("N"))
				{
					System.out.println(username+" denied download request");
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}