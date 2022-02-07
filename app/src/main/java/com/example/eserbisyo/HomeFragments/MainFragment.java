package com.example.eserbisyo.HomeFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeActivity;
import com.example.eserbisyo.OrderActivity.SelectPickupActivity;
import com.example.eserbisyo.R;

import es.dmoral.toasty.Toasty;

public class MainFragment extends Fragment {
    private View view;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((HomeActivity) requireActivity()).setHomeNavCheck();
        view = inflater.inflate(R.layout.fragment_main, container, false);
        init();
        return view;
    }

    private void init() {
        CardView cardAnnouncement = view.findViewById(R.id.cardAnnouncement);
        CardView cardReport = view.findViewById(R.id.cardReport);
        CardView cardFeedback = view.findViewById(R.id.cardFeedback);
        CardView cardOrdinance = view.findViewById(R.id.cardOrdinance);
        CardView cardDocument = view.findViewById(R.id.cardDocument);
        CardView cardProject= view.findViewById(R.id.cardProject);
        CardView cardEmployee = view.findViewById(R.id.cardEmployee);

        CardView cardMissingPerson = view.findViewById(R.id.cardMissingPerson);
        CardView cardMissingItem = view.findViewById(R.id.cardMissingItem);
        CardView cardComplaint = view.findViewById(R.id.cardComplaint);

        CardView cardCertificate = view.findViewById(R.id.cardCertificate);
        CardView cardOrder = view.findViewById(R.id.cardOrder);

        SharedPreferences userPref = requireContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);
        int isVerified =  userPref.getInt(Pref.IS_VERIFIED, 0);

        cardAnnouncement.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new AnnouncementFragment()));

        cardReport.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new ReportFragment()));

        cardFeedback.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new FeedbackFragment()));

        cardCertificate.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new CertificateFragment()));

        cardOrdinance.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new OrdinanceFragment()));

        cardDocument.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new DocumentFragment()));

        cardProject.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new ProjectFragment()));

        cardEmployee.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new EmployeeFragment()));

        cardMissingPerson.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new MissingPersonFragment()));

        cardMissingItem.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new MissingItemFragment()));

        cardComplaint.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new ComplaintFragment()));

        cardOrder.setOnClickListener(v->{
            if(checkVerified(isVerified)){
                startActivity(new Intent(requireContext(), SelectPickupActivity.class));
            }
        });
    }

    private boolean checkVerified (int isVerified) {
        if(isVerified != 1){
            Toasty.info(requireContext(), "This function is for verified user only.", Toast.LENGTH_LONG, true).show();
            return false;
        } else {
            return true;
        }
    }

}