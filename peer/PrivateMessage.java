import java.net.*;
import java.io.*;
import java.util.*;

class PrivateMessage
{
	private String pm_ip;
	private int pm_port;
	private String p_mesg;
	private MessageProtocol mp_send;
	private MessageProtocol mp_recv;
	private String name;
	PrivateMessage(String ip, int port, String mesg, String name)
	{
		this.pm_ip = ip;
		this.pm_port = port;
		this.p_mesg = mesg;
		this.name = name;
		try
        (
           Socket s = new Socket(pm_ip, pm_port);
           BufferedReader pm_inp = new BufferedReader(new InputStreamReader(s.getInputStream()));
           PrintWriter pm_out = new PrintWriter(s.getOutputStream(), true);
        )
        {
           p_mesg = name+" : "+p_mesg;
           mp_send = new MessageProtocol("XXPM", p_mesg.length(), p_mesg);
           pm_out.println(mp_send.getMessageString());
        }
        catch(Exception ex)
        {
           ex.printStackTrace();
        }
	}
}