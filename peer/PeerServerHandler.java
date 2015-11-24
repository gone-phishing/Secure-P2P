/**
 * DO File exchange part here
 */
import java.net.*;
import java.io.*;
import java.util.*;

class PeerServerHandler extends Thread
{
	private Socket socket = null;
	private String sharedPath;
	private MessageProtocol mp_send;
    private MessageProtocol mp_rec;

	PeerServerHandler(Socket s, String sharedPath)
	{
		super("Peer server handler");
		this.socket = s;
		this.sharedPath = sharedPath;
	}

	public void run()
	{
		try
	    (
	        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        OutputStream os = socket.getOutputStream();
	        PrintWriter out = new PrintWriter(os, true);
	    )
	    {
	    	String recv = in.readLine();
	    	mp_rec = new MessageProtocol(recv);
	    	if(mp_rec.getMessageType().equals("PING"))
	    	{
	    		String req[] = mp_rec.getMessageContent().split("\\$");
	    		String question_text = "Do you want to share "+req[1]+" with "+req[0]+" ?";
	    		String zenity_command_parts[] = new String[6];
	    		zenity_command_parts[0] = "zenity";
	    		zenity_command_parts[1] = "--question";
	    		zenity_command_parts[2] = "--title";
	    		zenity_command_parts[3] = "Share file with peer";
	    		zenity_command_parts[4] = "--text";
	    		zenity_command_parts[5] = question_text;
	    		String question_op[] = execute_command_shell(zenity_command_parts);
	    		if(question_op[0].equals("1"))
	    		{
	    			String resp = "N";
	    			mp_send = new MessageProtocol("RACK", resp.length(), resp);
	    			out.println(mp_send.getMessageString());
	    		}
	    		else if(question_op[0].equals("0"))
	    		{
	    			String filepath = sharedPath+"/"+req[1];
	    			File myFile = new File(filepath);
	    			String file_size = ""+myFile.length();

	    			// Send response with file size
	    			String resp = "Y$"+file_size;
	    			mp_send = new MessageProtocol("RACK", resp.length(), resp);
	    			out.println(mp_send.getMessageString());

	    			// Copy the binary data of file to the array
			        byte [] mybytearray  = new byte [(int)myFile.length()];
			        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
			        bis.read(mybytearray,0,mybytearray.length);

			        // Sending file to the peer
			        //System.out.println("Sending " + req[1] + "(" + mybytearray.length + " bytes)");
			        os.write(mybytearray,0,mybytearray.length);
			        os.flush();
			        //System.out.println("Done.");	
	    		}
	    	}
	    }
	    catch(IOException ex1)
	    {
	    	ex1.printStackTrace();
	    }
	}

	/**
	 * @param : command to be executed on local shell
	 * @return : Exit status and result
	 */
	private String[] execute_command_shell(String command[])
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