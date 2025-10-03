package app.nepaliapp.mblfree.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import app.nepaliapp.mblfree.R;

public class SubscriptionDialog {


    public static void show(Context context, JSONObject jsonObject) {
        final String[] messagetext = new String[1];
        // Create a builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String lifeTimeText = "लाइफटाइम अनलिमिटेड योजना खरिद गर्दा, तपाईंले एकपटकको भुक्तानी मात्र गर्नुहुनेछ र जीवनभरका लागि सबै सेवाहरूमा असीमित पहुँच प्राप्त गर्नुहुनेछ। यो योजना सबैभन्दा फाइदाजनक छ किनकि यसले तपाईलाई बारम्बार सदस्यता नविकरण गर्नबाट मुक्त गराउँछ र तपाईले भविष्यमा थपिने नयाँ सामग्रीहरू पनि पहुँच गर्न सक्नुहुनेछ।";
        String normalText= "तपाईंले कुनै पनि एक योजना (मासिक, अर्धवार्षिक, वा वार्षिक) खरिद गरेपछि, सबै सेवाहरू अनलक हुनेछ। यसको अर्थ तपाईंले सबै सामग्रीहरूमा असीमित पहुँच प्राप्त गर्नुहुनेछ, चाहे त्यो कुनै पनि प्रकारको सामग्री किन नहोस्।";
        // Inflate the layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_subscription, null);


        // Get references to UI elements
        Spinner spinnerSubscription = dialogView.findViewById(R.id.spinner_subscription);
        TextView textViewPrice = dialogView.findViewById(R.id.textview_price);
        Button buttonSubscribe = dialogView.findViewById(R.id.button_subscribe);
        ImageView closeBtn = dialogView.findViewById(R.id.closeicon);
        TextView packageExplainer = dialogView.findViewById(R.id.packexplainer);


        // Setup the spinner with subscription packages
        String[] subscriptionPackages = {"1 Month Unlimited", "6 Month Unlimited", "1 year Unlimited", "LifeTime Unlimited"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, subscriptionPackages);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerSubscription.setAdapter(adapter);

        // Set an item selected listener on the spinner
        spinnerSubscription.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update the price TextView based on the selected package
                String selectedPackage = subscriptionPackages[position];
                String price = "0";

                switch (selectedPackage) {
                    case "1 Month Unlimited":
                        packageExplainer.setText(normalText);
                        price = jsonObject.optString("month");
                        break;
                    case "6 Month Unlimited":
                        packageExplainer.setText(normalText);
                        price = jsonObject.optString("6 month");
                        break;
                    case "1 year Unlimited":
                        packageExplainer.setText(normalText);
                        price = jsonObject.optString("1 year");
                        break;
                    case "LifeTime Unlimited":
                        packageExplainer.setText(lifeTimeText);
                        price = jsonObject.optString("lifetime");
                        break;
                }
//                SystemDataHolder dataHolder = new SystemDataHolder();
//                String extraText= "\n  "+dataHolder.getUserIdAccess(context);
                textViewPrice.setText("Price:    " + price);
                messagetext[0] = "I want to buy " + selectedPackage + "Package at price of " + price;


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set the dialog view
        builder.setView(dialogView);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set click listener for the subscribe button
        buttonSubscribe.setOnClickListener(v -> {
            String messengerLink = "https://m.me/106704358421953" + "?text=" + Uri.encode(messagetext[0]);
            Uri uri = Uri.parse(messengerLink);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
            dialog.dismiss();
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
