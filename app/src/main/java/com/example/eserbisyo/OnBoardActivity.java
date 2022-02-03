package com.example.eserbisyo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.eserbisyo.Adapters.ViewPagerAdapter;

public class OnBoardActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Button btnLeft, btnRight;
    private LinearLayoutCompat dotsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_on_board);
        init();
    }

    private void init() {
        viewPager = findViewById(R.id.view_pager);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        dotsLayout = findViewById(R.id.dotsLayout);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        addDots(0);
        viewPager.addOnPageChangeListener(listener); // create this listener
        viewPager.setAdapter(adapter);

        btnRight.setOnClickListener(v -> {
            //if button text is next we will go to next page of viewpager
            if (btnRight.getText().toString().equals("Next")) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                //else its finish we will start Auth activity
                startActivity(new Intent(OnBoardActivity.this,AuthActivity.class));
                finish();
            }
        });

        btnLeft.setOnClickListener(v -> {
            // if btn skip clicked then we go to page 3
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 3);
        });

    }

    //method to create dots from html code
    private void addDots(int position) {
        dotsLayout.removeAllViews();
        TextView[] dots = new TextView[4];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            //this html code creates dot
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.black));
            dotsLayout.addView(dots[i]);
        }

        // ok now lets change the selected dot color
        dots[position].setTextColor(getResources().getColor(R.color.white));
    }


    private final ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onPageSelected(int position) {
            addDots(position);
            //ok now we need to change the text of Next button to Finish if we reached page 3
            //and hide Skip button if we are not in page 1

            if (position == 0) {
                btnLeft.setVisibility(View.VISIBLE);
                btnLeft.setEnabled(true);
                btnRight.setText("Next");
            } else if (position == 1) {
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(false);
                btnRight.setText("Next");
            } else if (position == 2) {
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(false);
                btnRight.setText("Next");
            } else  {
                btnLeft.setVisibility(View.GONE);
                btnLeft.setEnabled(false);
                btnRight.setText("Finish");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}