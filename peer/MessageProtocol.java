import java.util.*;

class MessageProtocol
{
	/**
	 * Messaage types :
	 * JOIN -> Joined the server
	 * NAME -> Ask for the name from the user
	 * WELC -> Username accepted by the server
	 * EXIT -> Quit connection from the server
	 * MESG -> Chat message for the server
	 * SRCH -> Search for a file on the server
	 * DATE -> Get server date
	 * LIST -> Send file list to server
	 * DUMP -> Sends all file lists
	 * PING -> Send file name to peer server
	 * RACK -> Acknowledge peer request
	 * FAIL -> No user list to send
	 * XXPM -> Personal message to a peer
	 */
	private String mtype = "";
	private int mlen = 0;
	private String mesg="";
	public MessageProtocol(String type, int len, String mesg)
	{
		this.mtype = type;
		this.mlen = len;
		this.mesg = mesg;
	}

	public MessageProtocol(String combination)
	{
		String str[] = combination.split("@");
		this.mtype = str[0];
		this.mlen = Integer.parseInt(str[1]);
		StringBuffer sb = new StringBuffer();
		for(int i=2; i<str.length; i++)
		{
			sb.append(str[i]);
		}
		this.mesg = sb.toString();
	}

	public String getMessageType()
	{
		return mtype;
	}

	public int getMessageLength()
	{
		return mlen;
	}

	public String getMessageContent()
	{
		return mesg;
	}

	public void setMessageType(String type)
	{
		this.mtype = type;
	}

	public void setMessageLength(int len)
	{
		this.mlen = len;
	}

	public void setMessageContent(String mesg)
	{
		this.mesg = mesg;
	}

	public String getMessageString()
	{
		return mtype+"@"+mlen+"@"+mesg;
	}
}