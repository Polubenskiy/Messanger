package com.example.messenger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Client
{
	private DatagramSocket socket;
	private DatagramPacket sendingPacket;
	private String inetAddress;
	private int portRecipient;
	byte[] sendBuffer = new byte[265];

	public Client(DatagramSocket datagramSocket, String address, int portRecipient)
	{
		this.socket = datagramSocket;
		this.inetAddress = address;
		this.portRecipient = portRecipient;
	}

	public void Send(String text)
	{
		// packet buffering

		sendBuffer =  text.getBytes(StandardCharsets.UTF_8);

		// package formation
		try
		{
			InetAddress remoteAddress = InetAddress.getByName(inetAddress.toString()); // get address recipient
			sendingPacket = new DatagramPacket(sendBuffer, sendBuffer.length, remoteAddress, portRecipient);

		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		}

		try
		{
				// sending packet
				socket.send(sendingPacket);

		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
