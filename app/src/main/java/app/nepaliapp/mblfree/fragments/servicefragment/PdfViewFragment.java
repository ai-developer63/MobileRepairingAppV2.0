package app.nepaliapp.mblfree.fragments.servicefragment;

import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.recyclerAdapter.PdfPageAdapter;

public class PdfViewFragment extends Fragment {

    private static final float RENDER_SCALE = 2.5f;
    private PdfRenderer pdfRenderer;
    private ParcelFileDescriptor fileDescriptor;
    private RecyclerView recyclerView;
    private LinearLayout loadingOverlay;
    ProgressBar progressBar;
    private TextView pageNumberTextView;
    private ImageButton goToPageButton, searchButton;
    private List<String> extractedTextPages = new ArrayList<>();
    TextView progressText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pdf_view_fragmet, container, false);
        init(view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));

        Bundle bundle = getArguments();
        if (bundle != null) {
            String pdfUrl = bundle.getString("pdf_url");
            if (pdfUrl != null && !pdfUrl.isEmpty()) {
                downloadAndOpenPdf(pdfUrl);
            }
        }

        setupButtons();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (bundle != null) {
                    String name = bundle.getString("name");
                    if (name != null && !name.isEmpty()) {
                        replaceWithCompany(name);
                    }

                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                callback
        );

        return view;
    }

    private void replaceWithCompany(String companyName) {
        SchematricModelFragment schematricModelFragment = new SchematricModelFragment();
        Bundle bundle = new Bundle();
        bundle.putString("companyName", companyName);
        schematricModelFragment.setArguments(bundle);
        FragmentTransaction transaction = ((FragmentActivity) requireContext()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutInMain, schematricModelFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void init(View view) {
        PDFBoxResourceLoader.init(requireContext());
        recyclerView = view.findViewById(R.id.pdfRecyclerView);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        pageNumberTextView = view.findViewById(R.id.pageNumberTextView);
        goToPageButton = view.findViewById(R.id.goToPageButton);
        searchButton = view.findViewById(R.id.searchButton);
        progressBar = view.findViewById(R.id.progressBar);
        progressText = view.findViewById(R.id.progressText);

    }


    private void setupButtons() {
        goToPageButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Go to page");

            final EditText input = new EditText(requireContext());
            input.setHint("Page number");
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String pageStr = input.getText().toString();
                if (!pageStr.isEmpty()) {
                    int pageIndex = Integer.parseInt(pageStr) - 1;
                    if (pageIndex >= 0 && pageIndex < pdfRenderer.getPageCount()) {
                        recyclerView.scrollToPosition(pageIndex);
                        pageNumberTextView.setText((pageIndex + 1) + "/" + pdfRenderer.getPageCount());
                    } else {
                        Toast.makeText(requireContext(), "Invalid page number", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        searchButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Search is coming Soon", Toast.LENGTH_SHORT).show();
//            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//            builder.setTitle("Search Text");
//
//            final EditText input = new EditText(requireContext());
//            input.setHint("Enter text to search");
//            builder.setView(input);
//
//            builder.setPositiveButton("OK", (dialog, which) -> {
//                String searchText = input.getText().toString();
//                if (!searchText.isEmpty()) {
//                    searchText(searchText);
//                }
//            });
//            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
//            builder.show();
        });
    }

    private File getPdfFileFromUrl(String pdfUrl) {
        String fileName = pdfUrl.substring(pdfUrl.lastIndexOf('/') + 1);
        return new File(requireContext().getFilesDir(), fileName);
    }

    private void downloadAndOpenPdf(String pdfUrl) {
        File pdfFile = getPdfFileFromUrl(pdfUrl);

        if (pdfFile.exists()) {
            openRendererAndSetup(pdfFile);
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
                        openRendererAndSetup(pdfFile);
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


    private void openRendererAndSetup(File file) {
        try {
            fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(fileDescriptor);

            PdfPageAdapter adapter = new PdfPageAdapter(requireContext(), pdfRenderer);
            recyclerView.setItemViewCacheSize(2);
            recyclerView.setAdapter(adapter);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                        if (pdfRenderer != null && firstVisibleItemPosition >= 0) {
                            pageNumberTextView.setText((firstVisibleItemPosition + 1) + "/" + pdfRenderer.getPageCount());
                        }
                    }
                }
            });


            // Extract text in background
            extractTextFromPdf(file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractTextFromPdf(File pdfFile) {
        new Thread(() -> {
            try {
                PDDocument document = PDDocument.load(pdfFile);
                int pageCount = document.getNumberOfPages();
                PDFTextStripper stripper = new PDFTextStripper();

                for (int i = 1; i <= pageCount; i++) {
                    stripper.setStartPage(i);
                    stripper.setEndPage(i);
                    String text = stripper.getText(document);
                    extractedTextPages.add(text);
                }
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void searchText(String query) {
        for (int i = 0; i < extractedTextPages.size(); i++) {
            Log.d("PDFText", "Page " + (i + 1) + ": " + extractedTextPages.get(i));
            if (extractedTextPages.get(i).toLowerCase().contains(query.toLowerCase())) {
                int pageIndex = i;
                recyclerView.scrollToPosition(pageIndex);
                pageNumberTextView.setText((pageIndex + 1) + "/" + pdfRenderer.getPageCount());
                Toast.makeText(requireContext(), "Found on page " + (pageIndex + 1), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(requireContext(), "Text not found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (pdfRenderer != null) pdfRenderer.close();
            if (fileDescriptor != null) fileDescriptor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
