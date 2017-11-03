package com.example.hangman;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ChooseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private boolean hasChosen;

    private int idOfArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.labels, android.R.layout.simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.choose_activity_buttons, menu);
        return true;
    }

    /**
     * When the "Info" button is tapped, information
     * about the developer is shown.
     * @param menuItem  The MenuItem that was pressed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     *  A listener, which identifies what was selected from the spinner.
     */
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        hasChosen = true;
        idOfArray = pos;
    }

    /**
     * If the player does not choose anything, nothing
     * happens when he/she presses the "CHOOSE" button.
     */
    public void onNothingSelected(AdapterView<?> parent) {
        hasChosen = false;
    }

    /**
     *  If the player has chosen a category,
     *  a game is started with that specific
     *  category. The category dictates
     *  which words will appear in the game.
     */
    public void choose(View view) {
        if (hasChosen) {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("category", idOfArray);
            startActivity(intent);
        }
    }

}
