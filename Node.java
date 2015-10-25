import java.util.*;

class Node
{
	private String host;
	private int port;
	private String id;
	Node(String host, int port)
	{
		this.host = host;
		this.port = port;
		this.id = host+"@"+port;
	}

	public String getHost()
	{
		return host;
	}

	public int getPort()
	{
		return port;
	}

	public String getID()
	{
		return id;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public void setID(String host, int port)
	{
		this.id = host+"@"+port;
	}
}