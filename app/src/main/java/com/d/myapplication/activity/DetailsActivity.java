package com.d.myapplication.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.d.myapplication.R;
import com.yelp.fusion.client.models.Business;

public class DetailsActivity extends AppCompatActivity {

    private Business bb;
    private ImageView mImage;
    private TextView mCategory;
    private TextView mRating;
    private TextView mPhone;
    private RecyclerView mPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            bb = (Business) extras.getSerializable("SELECTED_RES");
        } else {
            return;
        }
        setContentView(R.layout.activity_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(bb.getName());
        setSupportActionBar(toolbar);
        mImage = findViewById(R.id.header);
        mCategory = findViewById(R.id.category);
        mRating = findViewById(R.id.rating);
        mPhone = findViewById(R.id.phone);
        mPhotos = findViewById(R.id.photos);
        setDetails();
    }

    /**
     * set details of selected restaurant
     */
    private void setDetails() {
        mPhone.setText(bb.getPhone());
        mCategory.setText(bb.getCategories().get(0).getTitle());
        mRating.setText(String.valueOf(bb.getRating()));
        /*
         * set image
         */
        Glide.with(this)
                .load(bb.getImageUrl())
                .into(mImage);
        if (bb.getPhotos() != null && !bb.getPhotos().isEmpty()) {
            RecyclerViewAdapter adapter = new RecyclerViewAdapter(bb.getPhotos());
            mPhotos.setAdapter(adapter);
        }
    }
}
