package com.example.messenger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
{
    // Displaying messages
    ListView listViewMessages;
    Message message;
    ArrayAdapter<Message> adapterMessages;

    // packages.java.net
    private DatagramSocket socket;



    // Thread
    Thread receivingThread;

    Boolean IsActiveThread = true;
    Boolean IsClear = false;

    // Database
    private DatabaseHelper databaseMessage;

    // Setting
    private String nickName = "Name", addressRecipient = "0.0.0.0";
    private int portRecipient = 19000;
    private int senderPort = 19000;
    private int waiting = 0;

    // Message
    private String text;
    private String timeMessage;
    private static final int MAX_MESSAGE_LENGTH = 256;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewMessages = findViewById(R.id.messages);

        // Creating an ArrayAdapter adapter to bind an array to a listview
        adapterMessages = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        // Bind the array via the adapter to the ListView
        listViewMessages.setAdapter(adapterMessages);

        // creating database
        databaseMessage = new DatabaseHelper(MainActivity.this, "messenger.db", null, 1);

        // if null, creating settings, else read from the database
        if (databaseMessage.SelectSetting(1, "Nickname").equals(""))
            databaseMessage.AddSetting(1, nickName, addressRecipient, senderPort, portRecipient);
        else
        {
            nickName = databaseMessage.SelectSetting(1, "Nickname");
            portRecipient = Integer.parseInt(databaseMessage.SelectSetting(1, "portRecipient"));
            senderPort = Integer.parseInt(databaseMessage.SelectSetting(1, "portSender"));
            addressRecipient = databaseMessage.SelectSetting(1, "address");
        }

        int countMessage = databaseMessage.CountMessage();
        // extracting messages from the database
        if (countMessage != 0)
        {
            for (int i = 0; i < countMessage; i++)
            {
                message = new Message(Integer.parseInt((databaseMessage.SelectMessages(i, "id")).trim()), databaseMessage.SelectMessages(i, "Nickname"), databaseMessage.SelectMessages(i, "Information"), Integer.parseInt(databaseMessage.SelectMessages(i, "portSender")), Integer.parseInt(databaseMessage.SelectMessages(i, "portRecipient")), databaseMessage.SelectMessages(i, "timeMessage"));
                adapterMessages.add(message);
            }
            adapterMessages.notifyDataSetChanged();
        }

        // creating socket
        try
        {
            InetAddress local_network = InetAddress.getByName("0.0.0.0"); // subnet address, to which we will accept
            InetSocketAddress local_address = new InetSocketAddress(local_network, senderPort); // listening port
            socket = new DatagramSocket(null);
            socket.setReuseAddress(true);
            socket.setBroadcast(true);
            socket.bind(local_address); // waiting for packet from everywhere
        } catch (IOException e)
        {
            // UnknownHostException | SocketException e
            e.printStackTrace();
        }

        Runnable receiver = () ->
        {
            while (true)
            {
                while (IsActiveThread)
                {
                    byte[] buffer = new byte[256];
                    Log.i("TEST", "Forming received_Packet");
                    DatagramPacket received_packet = new DatagramPacket(buffer, buffer.length);

                    try
                    {
                        Log.i("TEST", "Start receiving: " + "LocalAddress: "
                                + socket.getLocalAddress() + " " + "LocalSocketAddress: "
                                + socket.getLocalSocketAddress() + " " + "Port: "
                                + socket.getPort() + " " + "LocalPort: "
                                + socket.getLocalPort());

                        socket.receive(received_packet); // get letter
                        buffer = null;
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    String data = new String(received_packet.getData(), 0, 256);
                    Log.i("TEST", "Get data with receive: " + data.trim());

                    // extract data, if not null
                    if (!data.trim().equals(""))
                    {
                        String[] informationSplit = data.split(" ");
                        String[] textInformationSplit = data.split("> ");

                        String nicknameReceived = informationSplit[4];
                        int senderPortReceived = Integer.parseInt(informationSplit[0]);
                        int recipientPortReceived = Integer.parseInt(informationSplit[2]);
                        String timeMessageReceived = informationSplit[5];
                        String textReceived = textInformationSplit[2];

                        // content packet
                        String text = recipientPortReceived + " <= "
                                + senderPortReceived + " \n "
                                + nicknameReceived.trim() + " "
                                + timeMessageReceived.trim() + " \n"
                                + "> " + textReceived.trim();

                        try
                        {
                            Thread.sleep(1000);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }

                        Log.i("TEST", "Adding receive message in database: " + databaseMessage.getKey());
                        databaseMessage.AddMessage(nicknameReceived, senderPortReceived, text, recipientPortReceived, timeMessageReceived);
                        Log.i("TEST", "Added receive message in database: " + databaseMessage.getKey());
                        message = new Message(databaseMessage.CountMessage() - 1, nicknameReceived, text, senderPortReceived, recipientPortReceived, timeMessageReceived);
                        Log.i("TEST", "Create receive message with key: " + (databaseMessage.CountMessage() - 1));

                        runOnUiThread(() ->
                        {
                            Log.i("TEST", "Adding receive message in adapterMessages");
                            adapterMessages.add(message);
                            adapterMessages.notifyDataSetChanged();
                        });
                    }
                }
            }
        };
        receivingThread = new Thread(receiver);
        receivingThread.setName("ReceivngTr123");
        receivingThread.start();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(this, MainActivity_Setting.class);
            intent.putExtra("Clear", IsClear);
            Log.i("TEST", "Stopping ReceiveThread");
            IsActiveThread = false;
            socket.close();

            startActivityForResult(intent, 1); // передаем вт
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        runOnUiThread(() ->
        {
            if (data != null)
            {
                IsClear = data.getBooleanExtra("isDelete", false);
            }

            if (IsClear && !adapterMessages.isEmpty())
            {
                adapterMessages.clear();
                adapterMessages.notifyDataSetChanged();
            }
        });

        if (requestCode == 1)
        {
            int changePort = Integer.parseInt(databaseMessage.SelectSetting(1, "portSender"));

            try
            {
                socket = new DatagramSocket(null);
                socket.setReuseAddress(true);
                socket.setBroadcast(true);
                socket.bind(new InetSocketAddress("0.0.0.0", changePort));
            } catch (SocketException e)
            {
                e.printStackTrace();
            }

            Log.i("TEST", "Starting ReceiveThread: "
                    + "LocalAddress: " + socket.getLocalAddress()  + " "
                    + "LocalSocketAddress: " + socket.getLocalSocketAddress() + " "
                    + "Port: " + socket.getPort() + " "
                    + "LocalPort: " + socket.getLocalPort());
            Log.i("TEST", "___________" + receivingThread.isAlive());
            IsActiveThread = true;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void SendingLetter(View v)
    {
        EditText txt_information = findViewById(R.id.editTextInputField);
        String textMessenger = txt_information.getText().toString();

        // getting setting from database
        nickName = databaseMessage.SelectSetting(1, "Nickname");
        senderPort = Integer.parseInt(databaseMessage.SelectSetting(1, "portSender"));
        addressRecipient = databaseMessage.SelectSetting(1, "address");
        portRecipient = Integer.parseInt(databaseMessage.SelectSetting(1, "portRecipient"));

        // checking "Empty message"
        if (textMessenger.equals(""))
        {
            Toast.makeText(getApplicationContext(),
                    R.string.empty_Message,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // checking for "Long message"
        if (textMessenger.length() > MAX_MESSAGE_LENGTH)
        {
            Toast.makeText(getApplicationContext(),
                    R.string.message_TooLong,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // getting time 00:00:00
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        timeMessage = formatter.format(calendar.getTime());

        // content packet
        text = senderPort + " => "
                + portRecipient + " \n "
                + nickName + " " + timeMessage + " \n"
                + "> " + textMessenger;

        Runnable sending = () ->
        {
            Client client = new Client(socket, addressRecipient, portRecipient);
            Log.i("TEST", "Start sending: "
                    + socket.getLocalAddress()  + " "
                    + socket.getLocalSocketAddress() + " "
                    + socket.getPort() + " "  + socket.getLocalPort());
            // sending packet
            client.Send(text);


            Log.i("TEST", "Adding sending message in database: " + databaseMessage.getKey());
            databaseMessage.AddMessage(nickName, senderPort, text, portRecipient, timeMessage);
            Log.i("TEST", "Added sending message in database: " + databaseMessage.getKey());
            message = new Message(databaseMessage.CountMessage() - 1, nickName, text, senderPort, portRecipient, timeMessage);
            Log.i("TEST", "Create sending message with key: " + (databaseMessage.CountMessage() - 1));

            runOnUiThread(() ->
            {
                Log.i("TEST", "Adding sending message in adapterMessages");
                // view send message
                adapterMessages.add(message);
                // adapterMessages.notifyDataSetChanged();
                adapterMessages.notifyDataSetChanged();
                // clean editText
                txt_information.setText("");
            });
        };

        // Create flow for send
        Thread sendingThread = new Thread(sending);
        sendingThread.setName("sendThre123");
        sendingThread.start();
    }
}