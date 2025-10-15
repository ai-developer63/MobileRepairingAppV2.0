package app.nepaliapp.mblfree.fragments.servicefragment;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class VideoTabAdapter extends FragmentStateAdapter {

    public VideoTabAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Just placeholders for now
        if (position == 0) return new AppTutorialVideosFragment();
        else return new CoursesVideosFragment();
    }

    @Override
    public int getItemCount() {
        return 2; // Two tabs
    }
}
