package com.example.eserbisyo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.eserbisyo.R;
public class ViewPagerAdapter extends PagerAdapter {

    private final Context context;

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    private final int[] images = {
            R.drawable.informative,
            R.drawable.feedback,
            R.drawable.statistics,
            R.drawable.certificate,
    };

    private final String[] titles = {
            "Browse",
            "Feedback",
            "Report",
            "Certificates"
    };

    private final String[] descs = {
            "You can browse latest barangay Cupang news, projects, ordinances, missing report.",
            "Feedbacks is crucial in terms of determining the opinion of the residents. This application allows you to submit a feedback",
            "If you have seen any illegal activities happening in the barangay cupang, you can now report it to the application with an option of submitting it as anonymously",
            "You can now order and fill up certificates with an option of getting the order via delivery"
    };

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.view_pager, container, false);

        //init views
        ImageView imageView = v.findViewById(R.id.imgViewPager);
        TextView txtTitle = v.findViewById(R.id.txtTitleViewPager);
        TextView txtDesc = v.findViewById(R.id.txtDescViewPager);

        imageView.setImageResource(images[position]);
        txtTitle.setText(titles[position]);
        txtDesc.setText(descs[position]);

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
