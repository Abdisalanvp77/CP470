package com.example.androidassignments;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TestToolbar extends AppCompatActivity {

    String menuOneText = "You selected item 1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "You clicked on the letter icon", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.menu_main, m );
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Log.d("Toolbar", "Option 1 selected");

                Snackbar.make(findViewById(R.id.test_toolbar), menuOneText, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case R.id.action_settings:
                //start an activity
                Log.d("Toolbar", "Option 2 selected");
                AlertDialog.Builder builder = new AlertDialog.Builder(TestToolbar.this);
                builder.setIcon(R.drawable.back);
                builder.setTitle(R.string.go_back);
                // Add the buttons
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();

                break;
            case R.id.action_logout:
                Log.d("Toolbar", "Option 3 selected");
                AlertDialog.Builder alert = new AlertDialog.Builder(TestToolbar.this);
                alert.setIcon(R.drawable.edit);
                final EditText edittext = new EditText(TestToolbar.this);
                alert.setMessage(R.string.enter_your_message);
                alert.setTitle(R.string.change_snackbar_message);

                alert.setView(edittext);

                alert.setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        menuOneText = edittext.getText().toString();
                    }
                });

                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();

                break;
            case R.id.action_about:
                Log.d("Toolbar", "Version 1.0, by Abdisalan Abdi");
                Toast.makeText(getBaseContext(), "Version 1.0, by Abdisalan Abdi", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}