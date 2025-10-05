package app.nepaliapp.mblfree.fragments.workshop;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.nepaliapp.mblfree.R;

public class WorkshopModelFragment extends Fragment {


    public WorkshopModelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_workshop_model, container, false);

        return view;
    }
}