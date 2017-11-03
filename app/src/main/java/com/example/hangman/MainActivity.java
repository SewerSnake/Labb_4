package com.example.hangman;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * @author Eric Groseclos
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu_buttons, menu);
        return true;
    }

    /**
     * When the "Info" button is tapped, information
     * about the developer is shown. When the "Play"
     * button is tapped, a new game is started.
     * @param menuItem  The MenuItem that was pressed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.action_play:
                startActivity(new Intent(this, ChooseActivity.class));
                return true;
            default:
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /** Called when the user taps the "PLAY GAME" button. Starts a new activity,
     * which allows the user to play the "Hangman" game. */
    public void game(View view) {
        Intent intent = new Intent(this, ChooseActivity.class);
        SharedPreferences sharedPreferences = getSharedPreferences("firstTime", 0);
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.remove("firstTime");
        //editor.apply();
        startActivity(intent);
    }

    /** Called when the user taps the "ABOUT" button. Starts a new activity,
     * which shows the name of the application creator. */
    public void about(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }
}
