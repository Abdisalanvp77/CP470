package com.example.androidassignments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {
    private static final String ACTIVITY_NAME = "Chat Window";
    public ArrayList<String> chats = new ArrayList<>();
    Button sendButton;
    EditText textInput;
    ListView listView;
    ChatAdapter messageAdapter;
    ChatDatabaseHelper chatDatabaseHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        //initialize database
        chatDatabaseHelper = new ChatDatabaseHelper(this);
        db = chatDatabaseHelper.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from Messages", null );
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            Log.i(ACTIVITY_NAME, "SQL MESSAGE: " + cursor.getString( cursor.getColumnIndex( chatDatabaseHelper.KEY_MESSAGE)));
            Log.i(ACTIVITY_NAME, "Cursor\'s  column count = " + cursor.getColumnCount());
            chats.add(cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE)));
            cursor.moveToNext();
        }
        for (int i = 0; i < cursor.getColumnCount(); i++){
            Log.i(ACTIVITY_NAME, "Column name = " + cursor.getColumnName(i));
        }
        cursor.close();

        sendButton = findViewById(R.id.send_chat_button);
        textInput = findViewById(R.id.chat_text_input);
        listView = findViewById(R.id.list_chats_view);

        //in this case, “this” is the ChatWindow, which is-A Context object
        messageAdapter = new ChatAdapter( this );
        listView.setAdapter (messageAdapter);

    }

    public void sendChat(View view) {
        ContentValues values = new ContentValues();
        String message = textInput.getText().toString();
        if (message.length() > 0) {
            chats.add(message);
            values.put(ChatDatabaseHelper.KEY_MESSAGE, message);
            db.insert(ChatDatabaseHelper.TABLE_NAME, "NullPlaceholder", values) ;
            messageAdapter.notifyDataSetChanged(); //this restarts the process of getCount()/getView()
            textInput.setText("");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        chatDatabaseHelper.close();
    }


    /*Adaptor class*/
    public class  ChatAdapter extends ArrayAdapter<String> {

        public ChatAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @Override
        public int getCount()
        {
            return chats.size();
        }

        @Override
        public String getItem(int position) {
            return chats.get(position);
        }
        @Override
        public  View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result = null;
            if(position % 2 == 0)
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            else
                result = inflater.inflate(R.layout.chat_row_outgoing, null);

            TextView message = (TextView) result.findViewById(R.id.message_text);
            message.setText(getItem(position)); // get the string at position
            return result;

        }
    }
}