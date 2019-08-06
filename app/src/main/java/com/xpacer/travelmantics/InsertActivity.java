package com.xpacer.travelmantics;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.xpacer.travelmantics.models.TravelDeal;

public class InsertActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private EditText etTitle;
    private EditText etPrice;
    private EditText etDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        etTitle = findViewById(R.id.etTitle);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("traveldeals");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.menu.save_menu:
                saveDeal();
                clean();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void clean() {
        etTitle.setText("");
        etPrice.setText("");
        etDescription.setText("");
        etTitle.requestFocus();
    }

    private void saveDeal() {
        String title = etTitle.getText().toString();
        String price = etPrice.getText().toString();
        String description = etDescription.getText().toString();

        TravelDeal deal = new TravelDeal(title, price, description, "");
        mDatabaseReference.push().setValue(deal);
    }
}
