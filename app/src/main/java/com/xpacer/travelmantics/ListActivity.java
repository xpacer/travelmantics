package com.xpacer.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.xpacer.travelmantics.adapters.DealAdapter;
import com.xpacer.travelmantics.utils.FirebaseUtil;

public class ListActivity extends AppCompatActivity {

    RecyclerView rvDeals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        rvDeals = findViewById(R.id.rv_deals);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.list_activity_menu, menu);

        MenuItem newDealItem = menu.findItem(R.id.add_deal_menu);
        if (FirebaseUtil.isAdmin) {
            newDealItem.setVisible(true);
        } else {
            newDealItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_deal_menu:
                Intent intent = new Intent(this, DealActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseUtil.attachListener();
                            }
                        });
                FirebaseUtil.detachListener();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUtil.openReference("traveldeals", this);
        DealAdapter dealAdapter = new DealAdapter();
        rvDeals.setAdapter(dealAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvDeals.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.attachListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    public void showMenu() {
        invalidateOptionsMenu();
    }
}
