package com.example.hangman;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ChooseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private int idOfArray;

    /**
     *  An Activity is created, where the player can choose
     *  between different categories. After selecting a
     *  category in the Spinner, the player must then tap
     *  the "CHOOSE" button TO start the game.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        Spinner spinner = (Spinner) findViewById(R.id.categories);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.labels, android.R.layout.simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
    }

    /**
     *  Inflates the menu. The necessary buttons are
     *  added to the Actionbar.
     */
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
        idOfArray = pos;
    }

    /**
     *  Overrided method from interface OnItemSelectedListener
     */
    public void onNothingSelected(AdapterView<?> parent) {}

    /**
     *  If the player taps the "CHOOSE" button,
     *  a game is started with the category listed
     *  in the spinner. This category dictates
     *  which words will appear in the game.
     */
    public void choose(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("category", idOfArray);
        startActivity(intent);
    }

}
