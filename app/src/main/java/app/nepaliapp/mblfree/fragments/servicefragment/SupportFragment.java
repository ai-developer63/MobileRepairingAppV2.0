package app.nepaliapp.mblfree.fragments.servicefragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

import app.nepaliapp.mblfree.R;
import app.nepaliapp.mblfree.common.StorageClass;
import app.nepaliapp.mblfree.fragmentmanager.DashBoardManager;
import app.nepaliapp.mblfree.fragments.userdash.ProfileFragment;

public class SupportFragment extends Fragment {

    StorageClass storageClass;
    private MaterialCardView cardMessenger, cardCall, cardTicket;

    public SupportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);

        // Initialize cards
        cardMessenger = view.findViewById(R.id.card_messenger);
        cardCall = view.findViewById(R.id.card_call);
        cardTicket = view.findViewById(R.id.card_ticket);
        storageClass = new StorageClass(requireContext());
        // Messenger click: open Messenger link
        cardMessenger.setOnClickListener(v -> {
            StorageClass storageClass = new StorageClass(requireContext());

            // 1️⃣ Decide message based on country
            String messageText;
            if (storageClass.getUserCountry().equalsIgnoreCase("Nepal")) {
                messageText = "के सहयोग गर्न सक्छौं? कृपया टाइप गर्नु होला।"; // Nepali message
            } else {
                messageText = "I want to request support."; // English or default
            }

            // 2️⃣ Try to open Messenger app directly
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, messageText);
            intent.setPackage("com.facebook.orca"); // Messenger app package

            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent); // Messenger app opens with pre-filled message
            } else {
                // 3️⃣ Fallback: open in browser with m.me link
                String messengerLink;
                if (storageClass.getUserCountry().equalsIgnoreCase("Nepal")) {
                    messengerLink = "https://m.me/106704358421953?text=" + Uri.encode(messageText);
                } else {
                    messengerLink = "https://m.me/110702794806928?text=" + Uri.encode(messageText);
                }

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(messengerLink));
                startActivity(browserIntent);
            }
        });


        // Call click: open phone dialer
        cardCall.setOnClickListener(v -> {
            String phoneNumber = "++9779867331839"; // Replace with your support number
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        });

        // Ticket click: placeholder for future in-app messages
        cardTicket.setOnClickListener(v -> {
            String supportEmail = "subhakharsoftware@gmail.com";
            String subject = "Support Request";
            String body = "Hi Mobile Repairing Team,\n\n" +
                    "I need help with your services. I am contacting you from " + storageClass.getUserCountry() + ".\n\n" +
                    "\"Please Type Your Queries here\n\n" +
                    "Thanks!\n\n";


            // Use ACTION_SEND with proper MIME type for email
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822"); // ensures only email apps can handle
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{supportEmail});
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, body);

            // Modern check: only start if an email app exists
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent, "Send email using")); // shows chooser
            } else {
                // Friendly message if no email app is installed
                Toast.makeText(requireContext(), "No email app found on your device", Toast.LENGTH_SHORT).show();
            }
        });


        // Back press logic: return to ProfileFragment
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                fragmentChanger(new ProfileFragment());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                callback
        );

        return view;
    }

    // Helper method to replace fragment
    private void fragmentChanger(Fragment fragment) {
        if (getActivity() != null && getActivity() instanceof DashBoardManager) {
            ((DashBoardManager) getActivity()).replaceFragments(fragment);
        }
    }
}
