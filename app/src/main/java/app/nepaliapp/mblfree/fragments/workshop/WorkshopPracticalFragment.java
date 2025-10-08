package app.nepaliapp.mblfree.fragments.workshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import app.nepaliapp.mblfree.R;

public class WorkshopPracticalFragment extends Fragment {


    public WorkshopPracticalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workshop_practical, container, false);


        return view;
    }


}