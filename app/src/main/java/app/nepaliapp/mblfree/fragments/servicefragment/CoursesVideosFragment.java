package app.nepaliapp.mblfree.fragments.servicefragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.nepaliapp.mblfree.R;

public class CoursesVideosFragment extends Fragment {


    public CoursesVideosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_courses_videos, container, false);
    }
}