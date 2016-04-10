package com.carlospaulino.parrotsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(new ArrayAdapter<>(this,
                                           android.R.layout.simple_list_item_1,
                                           getResources().getStringArray(R.array.greetings)));
    }

    @Override public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.select_language) {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}
