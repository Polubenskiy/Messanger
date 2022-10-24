package com.example.messenger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public class Server
{
	private DatagramSocket socket;
	private byte[] buffer = new byte[256];

	public Server (DatagramSocket datagramSocket)
	{
		this.socket = datagramSocket;
	}

	public String Receive()
	{
		DatagramPacket received_packet = new DatagramPacket(buffer, buffer.length);

		while(true)
		{
			try
			{
				socket.receive(received_packet); // get letter

			} catch (IOException e)
			{
				e.printStackTrace();
				return "";
			}


			String data = new String(received_packet.getData());

			return data;
		}
	}
}
