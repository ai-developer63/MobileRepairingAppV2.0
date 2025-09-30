package app.nepaliapp.mblfree.fragments.servicefragment;

import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.recyclerAdapter.PdfPageAdapter;

public class PdfViewFragment extends Fragment {

    private PdfRenderer pdfRenderer;
    private ParcelFileDescriptor fileDescriptor;
    private RecyclerView recyclerView;
    private FrameLayout loadingOverlay;
    TextView pageNumberTextView;
    ImageButton goToPageButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_view_fragmet, container, false);
        recyclerView = view.findViewById(R.id.pdfRecyclerView);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        pageNumberTextView = view.findViewById(R.id.pageNumberTextView);
        ImageButton goToPageButton = view.findViewById(R.id.goToPageButton);
        goToPageButton.setOnClickListener(v -> {
            if (pdfRenderer == null) return;

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Go to Page");

            final EditText input = new EditText(requireContext());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint("Enter page number (1 - " + pdfRenderer.getPageCount() + ")");
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String value = input.getText().toString();
                if (!value.isEmpty()) {
                    int pageNum = Integer.parseInt(value);
                    if (pageNum >= 1 && pageNum <= pdfRenderer.getPageCount()) {
                        recyclerView.scrollToPosition(pageNum - 1);
                        pageNumberTextView.setText(pageNum + "/" + pdfRenderer.getPageCount());
                    } else {
                        Toast.makeText(requireContext(), "Invalid page number", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        Bundle bundle = getArguments();
        if (bundle != null) {
            String pdfUrl = bundle.getString("pdf_url");
            if (pdfUrl != null && !pdfUrl.isEmpty()) {
                downloadAndOpenPdf(pdfUrl);
            }
        }

        return view;
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
                InputStream input = connection.getInputStream();
                FileOutputStream output = new FileOutputStream(pdfFile);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
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
            int totalPages = pdfRenderer.getPageCount();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int firstVisible = layoutManager.findFirstVisibleItemPosition();
                        int currentPage = firstVisible + 1; // pages start at 1
                        pageNumberTextView.setText(currentPage + "/" + totalPages);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
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
