import java.util.*;

class Node
{
	private String host;
	private int port;
	private String id;
	private String name;
	Node(String name, String host, int port)
	{
		this.host = host;
		this.port = port;
		this.name = name;
		this.id = name+"@"+host;
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
		this.id = name+"@"+host;
	}
}