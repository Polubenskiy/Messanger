package com.example.messenger;

public class Message
{
	private int _key;
	private String _nickname;
	private int _portSender;
	private String _information;
	private int _portRecipient;
	private String _timeMessage;

	Message(int key, String nickname, String information, int portSender, int portRecipient, String timeMessage)
	{
		_key = key;
		_nickname = nickname;
		_information = information;
		_portSender = portSender;
		_portRecipient = portRecipient;
		_timeMessage = timeMessage;
	}

	public String toString() {return _information; }
}
