package com.example.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity_Setting extends AppCompatActivity
{

    EditText nickName, senderPort, addressRecipient, portRecipient;
    String strNickname, strSenderPort, strAddressRecipient, strPortRecipient;
    Boolean isDelete = false;

    DatabaseHelper databaseHelper;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setting);

        databaseHelper = new DatabaseHelper(this, "messenger.db", null, 1);
        databaseHelper.getWritableDatabase();


        nickName = findViewById(R.id.editTextNickName);
        senderPort = findViewById(R.id.editTextSenderPort);
        addressRecipient = findViewById(R.id.editText_addressRecipient);
        portRecipient = findViewById(R.id.editTextRecipientPort);

        String str = databaseHelper.SelectSetting(1, "Nickname");
        nickName.setText(str);
        senderPort.setText(databaseHelper.SelectSetting(1, "portSender"));
        addressRecipient.setText(databaseHelper.SelectSetting(1, "address"));
        portRecipient.setText(databaseHelper.SelectSetting(1, "portRecipient"));
    }

    public void OnOk_Click(View v)
    {
        strNickname = nickName.getText().toString();
        strSenderPort = senderPort.getText().toString();
        strAddressRecipient = addressRecipient.getText().toString();
        strPortRecipient = portRecipient.getText().toString();

        databaseHelper.UpdateSetting(1, strNickname, "Nickname");
        databaseHelper.UpdateSetting(1, strSenderPort, "portSender");
        databaseHelper.UpdateSetting(1, strAddressRecipient, "address");
        databaseHelper.UpdateSetting(1, strPortRecipient, "portRecipient");

        intent = new Intent(); //this - экземпляр данного класса MainActivity
        intent.putExtra("isDelete", isDelete);

        setResult(RESULT_OK, intent);//Вернуть в ответ новый intent с данными
        this.finish();
    }

    public void OnCancel_Click(View v)
    {
        intent = new Intent(); //this - экземпляр данного класса MainActivity
        intent.putExtra("isDelete", isDelete);
        setResult(RESULT_CANCELED, intent);
        this.finish(); //Закрываем вторую Activity
    }

    public void OnDeleteMessage(View v)
    {
        databaseHelper.DeleteMessage();
        Toast toast = Toast.makeText(getApplicationContext(), R.string.message_for_user, Toast.LENGTH_LONG);
        toast.show();

        isDelete = true;
    }
}