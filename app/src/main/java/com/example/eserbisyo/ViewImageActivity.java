package com.example.eserbisyo;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ViewImageActivity extends AppCompatActivity {

    ImageView myImage;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        myImage = findViewById(R.id.imageView);
        url = getIntent().getStringExtra("image_url");

        Picasso.get().load(url).error(R.drawable.no_picture).into(myImage);
    }
}