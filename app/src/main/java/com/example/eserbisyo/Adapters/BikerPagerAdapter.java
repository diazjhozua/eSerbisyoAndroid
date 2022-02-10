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

public class BikerPagerAdapter extends PagerAdapter {

    private final Context context;

    public BikerPagerAdapter(Context context) {
        this.context = context;
    }

    private final int[] images = {
            R.drawable.bicycle,
            R.drawable.biker,
            R.drawable.age21,
            R.drawable.auction,
    };

    private final String[] titles = {
            "What it takes to be a biker delivery?",
            "Benefits",
            "Requirements",
            "Rules and Regulation"
    };

    private final String[] descs = {
            "To be a biker delivery in barangay Cupang you must know well the roads and familiar in each streets in this barangay.",
            "Being a biker gives you opportunity to have extra income where you will earn ₱ 60.00 in every delivery. When you are a approved biker, you are not " +
                    "obliged to deliver every documents available since it is just a part-time job",
            "You need to be at the legal age and agree to a waiver included that barangay is not in-charge in any accident occurs during the delivery. " +
                    "Lastly, you need to submit your valid id indicating your age and its background should be your bike.",
            "You must think before accepting an order delivery request since when you accept, you cannot cancel the order. You must be respectful to all of the customers."
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
