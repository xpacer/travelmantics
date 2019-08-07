package com.xpacer.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.xpacer.travelmantics.models.TravelDeal;
import com.xpacer.travelmantics.utils.FirebaseUtil;

public class DealActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseReference;
    private EditText etTitle;
    private EditText etPrice;
    private EditText etDescription;
    private ImageView imageView;

    TravelDeal deal;
    private static final int IMAGE_RESULT = 988;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        etTitle = findViewById(R.id.etTitle);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        Button imageButton = findViewById(R.id.btn_image);
        imageView = findViewById(R.id.image);

        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        Intent intent = getIntent();
        TravelDeal travelDeal = (TravelDeal) intent.getSerializableExtra("Deal");

        if (travelDeal == null) {
            travelDeal = new TravelDeal();
        }

        this.deal = travelDeal;
        etTitle.setText(deal.getTitle());
        etPrice.setText(deal.getPrice());
        etDescription.setText(deal.getDescription());
        showImage(deal.getImageUrl());

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent, IMAGE_RESULT);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        MenuItem deleteItem = menu.findItem(R.id.delete_menu);
        MenuItem saveItem = menu.findItem(R.id.save_menu);

        if (FirebaseUtil.isAdmin) {
            deleteItem.setVisible(true);
            saveItem.setVisible(true);
            enableTexts(true);
        } else {
            deleteItem.setVisible(false);
            saveItem.setVisible(false);
            enableTexts(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDeal();
                clean();
                backToList();
                return true;
            case R.id.delete_menu:
                deleteDeal();
                backToList();
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
        deal.setTitle(etTitle.getText().toString());
        deal.setPrice(etPrice.getText().toString());
        deal.setDescription(etDescription.getText().toString());

        if (deal.getId() == null) {
            mDatabaseReference.push().setValue(deal);
        } else {
            mDatabaseReference.child(deal.getId())
                    .setValue(deal);
        }
    }

    private void deleteDeal() {
        if (deal.getId() == null) {
            Toast.makeText(this, "Please save this deal first", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabaseReference.child(deal.getId()).removeValue();

        if (deal.getImageName() != null && !deal.getImageName().isEmpty()) {
            StorageReference imageRef = FirebaseUtil.mStorageReference.child(deal.getImageName());
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Delete Image", "Image successfully deleted.");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Delete Image", e.getMessage());
                }
            });
        }
    }

    private void backToList() {
        onBackPressed();
    }

    private void enableTexts(boolean enabled) {
        etTitle.setEnabled(enabled);
        etPrice.setEnabled(enabled);
        etDescription.setEnabled(enabled);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_RESULT && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            final StorageReference ref = FirebaseUtil.mStorageReference.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    deal.setImageUrl(downloadUrl);
                                    showImage(downloadUrl);
                                }
                            });
                    String pictureName = taskSnapshot.getStorage().getPath();
                    deal.setImageName(pictureName);
                }
            });
        }
    }

    private void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, width * 2 / 3)
                    .centerCrop()
                    .into(imageView);
        }
    }
}
