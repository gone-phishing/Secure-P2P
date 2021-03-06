import java.util.*;

class Node
{
	private String host;
	private int port;
	private String id;
	private String name;
	private int peerport;
	Node(String name, String host, int port, int peerport)
	{
		this.host = host;
		this.port = port;
		this.name = name;
		this.peerport = peerport;
		this.id = name+"@"+host+"@"+peerport;
	}

	public String getHost()
	{
		return host;
	}

	public int getPort()
	{
		return port;
	}

	public String getName()
	{
		return name;
	}

	public String getID()
	{
		return id;
	}

	public int getPeerPort()
	{
		return peerport;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}

	public void setID(String name, String host)
	{
		this.id = name+"@"+host+"@"+peerport;
	}

	public void setPeerPort(int port)
	{
		this.peerport = port;
	}
}