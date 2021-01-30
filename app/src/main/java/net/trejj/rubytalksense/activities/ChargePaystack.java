package net.trejj.rubytalksense.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.trejj.rubytalksense.R;

import org.json.JSONException;
import org.json.JSONObject;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;


public class ChargePaystack extends Activity {

    private Card card;

    private Charge charge;
    private ProgressDialog pDialog;
//    private BalanceActivity balance;

    private EditText emailField;
    private EditText cardNumberField;
    private EditText expiryMonthField;
    private EditText expiryYearField;
    private EditText cvvField;
    private String USD,NGN,finalNGN;
//    IabHelper mHelper;

    private Boolean loading = false;

    private String SKU;
    private Integer custom_amount =0;
    private String credits;
    private String title;
    private String price;

    private String email, cardNumber, cvv;
    private int expiryMonth, expiryYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init paystack sdk
        PaystackSdk.initialize(getApplicationContext());
        setContentView(R.layout.content_charge_paystack);
            initpDialog();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {

            loading = savedInstanceState.getBoolean("loading");

        } else {

            loading = false;
        }



//        balance = new BalanceActivity();




        //init view

        Intent intent = getIntent();
        Button payBtn = (Button) findViewById(R.id.pay_button);

        if (intent != null){

//            SKU = bundle.getString("paystack_sku");
            price = intent.getStringExtra("price");
            title = intent.getStringExtra("title");
            credits = intent.getStringExtra("credits");
            previousPoints = intent.getStringExtra("points");

        }
        else{
            Toast.makeText(getApplicationContext(),"Bundle Error!", Toast.LENGTH_LONG).show();
            return;
        }

        emailField = (EditText) findViewById(R.id.edit_email_address);
        cardNumberField = (EditText) findViewById(R.id.edit_card_number);
        expiryMonthField = (EditText) findViewById(R.id.edit_expiry_month);
        expiryYearField = (EditText) findViewById(R.id.edit_expiry_year);
        cvvField = (EditText) findViewById(R.id.edit_cvv);


        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateForm()) {
                    return;
                }
                try {
                    showpDialog();
                    email = emailField.getText().toString().trim();
                    cardNumber = cardNumberField.getText().toString().trim();
                    expiryMonth = Integer.parseInt(expiryMonthField.getText().toString().trim());
                    expiryYear = Integer.parseInt(expiryYearField.getText().toString().trim());
                    cvv = cvvField.getText().toString().trim();

//                    String cardNumber = "4084084084084081";
//                    int expiryMonth = 11; //any month in the future
//                    int expiryYear = 18; // any year in the future
//                    String cvv = "408";

                    emailField.setText(email);
                    card = new Card(cardNumber, expiryMonth, expiryYear, cvv);

                    if (card.isValid()) {
//                        Toast.makeText(ChargePaystack.this, "Card is Valid", Toast.LENGTH_LONG).show();
//                        performCharge();
                        getFreshRates();

                    } else {
                        hidepDialog();
                        Toast.makeText(ChargePaystack.this, "Card is not Valid", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    hidepDialog();
                    e.printStackTrace();
                }
            }
        });

    }
    private void getFreshRates() {
        NGN="";USD="";finalNGN ="";

        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "https://radius.trejj.net/convert.php?from=USD&to=NGN&amount=1";

// prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        Log.e("Response:",response.toString());
//                                Log.e("Found:",response.getString("rate"));
                        try {
                            finalNGN = response.getString("rate");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        performCharge();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        );

// add it to the RequestQueue
        queue.add(getRequest);



    }
    /**
     * Method to perform the charging of the card
     */
    private void performCharge() {
        //create a Charge object
        charge = new Charge();

        Log.e("Gonna","Chagggggggghghghghg");

        Integer amount_to_charge=0;
        Integer amount_in_KOBO=0;
            if(finalNGN.equals("")){
                Toast.makeText(ChargePaystack.this, "finalNGN is null", Toast.LENGTH_SHORT).show();
                return;
            }
            double temp =(Double.parseDouble(price)* Double.parseDouble(finalNGN))*100;
            /*amount_to_charge = custom_amount;
            amount_in_KOBO = amount_to_charge * 100; //converting NGN to KOBO
*/
            amount_in_KOBO = Integer.parseInt(String.valueOf(Math.round(temp)));


        //set the card to charge
        charge.setCard(card);

        //call this method if you set a plan
        //charge.setPlan("PLN_yourplan");

        charge.setEmail(email); //dummy email address




//        if(SKU.equals("custom")) {
//            amount_to_charge = custom_amount;
//            amount_in_KOBO = amount_to_charge * 100; //converting NGN to KOBO
//
//
//        }
//        else
//        {
//
//            amount_to_charge = get_amount_(SKU);
//            amount_in_KOBO = amount_to_charge * 100; //converting NGN to KOBO
//
//
//        }


        charge.setAmount(amount_in_KOBO); // amount to Charge, accepts in KOBO, (1NGN * 100) = 1 KOBO


        Log.e("amount_in_kobooo","koboo"+amount_in_KOBO);

        PaystackSdk.chargeCard(ChargePaystack.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.
                chargeFinished(transaction);
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                // This is called only before requesting OTP.
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.
                Log.e("OTOp","Reuired");
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                Log.e("OTOp","Reuired2");
                //handle error here
            }

        });

//        PaystackSdk.chargeCard(ChargePaystack.this, charge, new Paystack.TransactionCallback() {
//            @Override
//            public void onSuccess(Transaction transaction) {
//
//                // This is called only after transaction is deemed successful.
//                // Retrieve the transaction, and send its reference to your server
//                // for verification.
//                String paymentReference = transaction.getReference();
////                Toast.makeText(ChargePaystack.this, "Transaction Successful! payment reference: "
////                        + paymentReference, Toast.LENGTH_LONG).show();
//
//
//                chargeFinished(transaction);
//            }
//
//            @Override
//            public void beforeValidate(Transaction transaction) {
//
//
//
//
//                // This is called only before requesting OTP.
//                // Save reference so you may send to server. If
//                // error occurs with OTP, you should still verify on server.
//            }
//
//            @Override
//            public void onError(Throwable error, Transaction transaction) {
//                hidepDialog();
////                Log.e("PaymentUndone",transaction.toString());
//                customDialog("Payment error","Payment was unsuccessful, Please try later "+error.getMessage());
//
//                //handle error here
//            }
//        });
    }
    private String previousPoints;
    public void chargeFinished(Transaction transaction)
    {

        hidepDialog();

        double newPoints = Double.parseDouble(previousPoints) + Double.parseDouble(credits);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("credits");
        reference.setValue(newPoints).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    AlertDialog.Builder  builder = new AlertDialog.Builder(ChargePaystack.this);
                    builder.setMessage("Congratulations, you have successfully purchased "+ credits + " credits!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.dismiss();
                                    ChargePaystack.super.onBackPressed();
                                }
                            }).show();
                }
            }
        });




    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String cardNumber = cardNumberField.getText().toString();
        if (TextUtils.isEmpty(cardNumber)) {
            cardNumberField.setError("Required.");
            valid = false;
        } else {
            cardNumberField.setError(null);
        }


        String expiryMonth = expiryMonthField.getText().toString();
        if (TextUtils.isEmpty(expiryMonth)) {
            expiryMonthField.setError("Required.");
            valid = false;
        } else {
            expiryMonthField.setError(null);
        }

        String expiryYear = expiryYearField.getText().toString();
        if (TextUtils.isEmpty(expiryYear)) {
            expiryYearField.setError("Required.");
            valid = false;
        } else {
            expiryYearField.setError(null);
        }

        String cvv = cvvField.getText().toString();
        if (TextUtils.isEmpty(cvv)) {
            cvvField.setError("Required.");
            valid = false;
        } else {
            cvvField.setError(null);
        }

        return valid;
    }

    private void cancelMethod1(){
        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void okMethod1(){



    }




    /**
     * Custom alert dialog that will execute method in the class
     * @param title
     * @param message
     */
    public void customDialog(String title, String message){
        AlertDialog.Builder  builder = new AlertDialog.Builder(ChargePaystack.this);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                }).show();
    }

    public void show_msg(String message){
        AlertDialog.Builder  builder = new AlertDialog.Builder(ChargePaystack.this);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                }).show();
    }
    protected void initpDialog() {

        pDialog = new ProgressDialog(ChargePaystack.this);
        pDialog.setMessage(getString(R.string.please_wait));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }





}
