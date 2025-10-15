package app.nepaliapp.mblfree.fragments.servicefragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import app.nepaliapp.mblfree.R;

public class PdfViewFragment extends Fragment {
    boolean isFragmentAttached = false;
    private LinearLayout loadingOverlay;
    private ProgressBar progressBar;
    private TextView progressText, pageNumberTextView;
    private ImageButton goToPageButton, searchButton;
    private PDFView pdfView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        isFragmentAttached = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pdf_view_fragmet, container, false);

        // Init UI
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        progressBar = view.findViewById(R.id.progressBar);
        progressText = view.findViewById(R.id.progressText);
//        pageNumberTextView = view.findViewById(R.id.pageNumberTextView);
//        goToPageButton = view.findViewById(R.id.goToPageButton);
//        searchButton = view.findViewById(R.id.searchButton);
        pdfView = view.findViewById(R.id.pdfView);

        // Button placeholders
        goToPageButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "Go to page feature coming soon", Toast.LENGTH_SHORT).show());

        searchButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "Search feature coming soon", Toast.LENGTH_SHORT).show());

        // Back navigation
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                replaceWithCompany("SampleCompany");
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String pdfUrl = bundle.getString("pdf_url");
            if (pdfUrl != null && !pdfUrl.isEmpty()) {
                downloadAndOpenPdf(pdfUrl);
            }
        }

        return view;
    }

    private void replaceWithCompany(String companyName) {
        SchematricModelFragment fragment = new SchematricModelFragment();
        Bundle bundle = new Bundle();
        bundle.putString("companyName", companyName);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = ((FragmentActivity) requireContext())
                .getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutInMain, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private File getPdfFileFromUrl(String pdfUrl) {
        String fileName = pdfUrl.substring(pdfUrl.lastIndexOf('/') + 1);
        return new File(requireContext().getFilesDir(), fileName);
    }

    private void downloadAndOpenPdf(String pdfUrl) {
        File pdfFile = getPdfFileFromUrl(pdfUrl);

        if (pdfFile.exists()) {
            openPdf(pdfFile);
            return;
        }

        loadingOverlay.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                URL url = new URL(pdfUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                int fileLength = connection.getContentLength(); // total size in bytes

                InputStream input = connection.getInputStream();
                FileOutputStream output = new FileOutputStream(pdfFile);

                byte[] buffer = new byte[1024];
                int bytesRead;
                long totalDownloaded = 0;

                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                    totalDownloaded += bytesRead;

                    if (fileLength > 0 && getActivity() != null) { // avoid divide by zero
                        int progress = (int) ((totalDownloaded * 100) / fileLength);

                        getActivity().runOnUiThread(() -> {
                            // Update your ProgressBar or TextView here
                            progressBar.setProgress(progress);
                            progressText.setText(progress + "%");
                        });
                    }
                }

                output.close();
                input.close();
                connection.disconnect();

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        openPdf(pdfFile);
                        loadingOverlay.setVisibility(View.GONE);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> loadingOverlay.setVisibility(View.GONE));
                }
            }
        }).start();
    }


    private void openPdf(File pdfFile) {
        loadingOverlay.setVisibility(View.GONE);
        if (pdfView != null && isFragmentAttached) {
            pdfView.fromFile(pdfFile)
                    .defaultPage(0)
                    .enableAnnotationRendering(true)
                    .spacing(10)
                    .enableDoubletap(true)
                    .enableSwipe(true)
                    .load();
            pdfView.setMinZoom(1f);
            pdfView.setMaxZoom(15f);
        }

    }


}
