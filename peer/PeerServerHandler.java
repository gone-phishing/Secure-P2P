/**
 * DO File exchange part here
 */
import java.net.*;
import java.io.*;
import java.util.*;

class PeerServerHandler extends Thread
{
	private Socket socket = null;
	private MessageProtocol mp_send;
    private MessageProtocol mp_rec;
    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	PeerServerHandler(Socket s)
	{
		super("Peer server handler");
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
	    	String recv = in.readLine();
	    	mp_rec = new MessageProtocol(recv);
	    	if(mp_rec.getMessageType().equals("PING"))
	    	{
	    		String req[] = mp_rec.getMessageContent().split("\\$");
	    		//System.out.println("Do you want to share "+req[1]+" with "+req[0]+" ? [Y/N]");
	    		String question_text = "Do you want to share "+req[1]+" with "+req[0]+" ?";
	    		String question_op[] = execute_command_shell("zenity --question --title \"Share File with Peer\" --text "+question_text);
	    		System.out.println(question_op[0] + " : "+question_op[1]);
	    		// String resp = "N";
	    		// if(resp.equals("N") || resp.equals("n"))
	    		// {
	    		// 	String addr[] = socket.getRemoteSocketAddress().toString().split(":");
	    		// 	for(String s : addr)
	    		// 	{
	    		// 		System.out.print(s+" -> ");
	    		// 	}
	    		// 	mp_send = new MessageProtocol("RACK", resp.length(), resp);
	    		// 	out.println(mp_send.getMessageString());
	    		// }
	    		// else if(resp.equals("Y") || resp.equals("y"))
	    		// {
	    		// 	mp_send = new MessageProtocol("RACK", resp.length(), resp);
	    		// 	out.println(mp_send.getMessageString());	
	    		// }
	    	}
	    }
	    catch(IOException ex1)
	    {
	    	ex1.printStackTrace();
	    }
	}

	private String[] execute_command_shell(String command)
	{
		StringBuffer op = new StringBuffer();
		String out[] = new String[2];
		Process process;
		try
		{
			process = Runtime.getRuntime().exec(command);
			process.waitFor();
			int exitStatus = process.exitValue();
			out[0] = ""+exitStatus;
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null)
			{
				op.append(line + "\n");
			}
			out[1] = op.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return out;
	}
}