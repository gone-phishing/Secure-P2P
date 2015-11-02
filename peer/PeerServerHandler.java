/**
 * DO File exchange part here
 */
import java.net.*;
import java.io.*;
import java.util.*;

class PeerServerHandler extends Thread
{
	Socket s = null;
	PeerServerHandler(Socket s)
	{
		this.s = s;
	}
}