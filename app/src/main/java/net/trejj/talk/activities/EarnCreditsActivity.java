package net.trejj.talk.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RaveUiManager;
import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.gson.JsonObject;

import net.trejj.talk.R;
import net.trejj.talk.activities.ChargePaystack;
import net.trejj.talk.adapters.SkuAdapter;
import net.trejj.talk.model.CreditsModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class EarnCreditsActivity extends AppCompatActivity implements SkuAdapter.OnItemClickListener, PurchasesUpdatedListener {

    TextView credits;
    Dialog dialog;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private Double previousPoints;
    static final String TAG = EarnCreditsActivity.class.getSimpleName();

    public static String SMALL_PACK_ID = "";
    public static String MEDIUM_PACK_ID= "";
    public static String BIG_PACK_ID = "";

    public static String SMALL_PACK_DESC = "";
    public static String MEDIUM_PACK_DESC = "";
    public static String BIG_PACK_DESC = "";

    public static Integer SMALL_PACK_CREDITS = 0;
    public static Integer MEDIUM_PACK_CREDITS = 0;
    public static Integer BIG_PACK_CREDITS = 0;

    //IAP
    private List<SkuDetails> mPaymentProductModels;
    private SkuAdapter mPaymentProductListAdapter;
    public RecyclerView mRecyclerView;

    private SkuDetails productModel;
    private SkuDetails monthlyModel;

    private BillingClient mBillingClient;
    ProgressDialog progressDialog;
    private CardView monthlyCD;
    private TextView sku_1_save, sku_1_title, sku_1_price_title;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earn_credits);

//        final Toolbar mToolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Buy Credits");

        monthlyCD = findViewById(R.id.monthlyCD);
        sku_1_price_title = findViewById(R.id.sku_1_price_title);
        sku_1_save = findViewById(R.id.sku_1_save);
        sku_1_title = findViewById(R.id.sku_1_title);

        getCreditsData();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        RetriveData();

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_progress_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable
                (Color.TRANSPARENT));
        dialog.setCancelable(false);

        credits = findViewById(R.id.credits);

        //IAP
        mPaymentProductModels = new ArrayList<>();
        mPaymentProductListAdapter = new SkuAdapter(mPaymentProductModels, this);

        mRecyclerView = findViewById(R.id.productList_productPackageList);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("adding_credits_to_wallet");
        progressDialog.setCancelable(false);


        monthlyCD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPurchaseFlow(monthlyModel);
            }
        });


    }
    private void initComponent() {
        mRecyclerView.setAdapter(mPaymentProductListAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);

        mRecyclerView.setItemViewCacheSize(12);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setBackgroundResource(R.color.white);
        mRecyclerView.setBackgroundColor(Color.WHITE);
        mRecyclerView.setLayoutManager(layoutManager);

        initPurchase();
    }
    private void initPurchase() {
        mBillingClient = BillingClient.newBuilder(this).setListener(this).enablePendingPurchases().build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    initSKUCredits();
                    initSKUMonthly();

                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });


    }

    public void initSKUMonthly() {

        List<String> creditsPurchase1 = new ArrayList<>();
        creditsPurchase1.add(creditsModel.get(3).getSku());

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(creditsPurchase1).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                        // Process the result.

                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if(skuDetailsList.size()>0){
                                monthlyModel = skuDetailsList.get(0);
                                sku_1_price_title.setText(monthlyModel.getPrice());
                            }else {
                                monthlyCD.setVisibility(View.GONE);
                            }
                        } else {
                        }
                    }
                });
    }

    public void initSKUCredits() {

        List<String> creditsPurchase = new ArrayList<>();
        creditsPurchase.add(SMALL_PACK_ID);
        creditsPurchase.add(MEDIUM_PACK_ID);
        creditsPurchase.add(BIG_PACK_ID);

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(creditsPurchase).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                        // Process the result.

                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (skuDetailsList.size() >0){
                                createList(skuDetailsList, skuDetailsList.get(0));
                            }else {
                            }
                        } else {
                        }
                    }
                });
    }
    private List<CreditsModel> creditsModel;

    public void getCreditsData(){

        creditsModel = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("credits");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    creditsModel.add(new CreditsModel(snapshot.getKey().toString(),snapshot.child("credits").getValue().toString(),
                            snapshot.child("sku").getValue().toString(),snapshot.child("type").getValue().toString(),
                            snapshot.child("title").getValue().toString(), snapshot.child("price").getValue().toString(),
                            snapshot.child("price_usd").toString(),snapshot.child("desc").getValue().toString()));

                }

                SMALL_PACK_ID=creditsModel.get(0).getSku();
                MEDIUM_PACK_ID=creditsModel.get(1).getSku();
                BIG_PACK_ID=creditsModel.get(2).getSku();

                SMALL_PACK_CREDITS = Integer.parseInt(creditsModel.get(0).getCredits());
                MEDIUM_PACK_CREDITS = Integer.parseInt(creditsModel.get(1).getCredits());
                BIG_PACK_CREDITS = Integer.parseInt(creditsModel.get(2).getCredits());

                SMALL_PACK_DESC = creditsModel.get(0).getDesc();
                MEDIUM_PACK_DESC = creditsModel.get(1).getDesc();
                BIG_PACK_DESC = creditsModel.get(2).getDesc();

                sku_1_save.setText(creditsModel.get(3).getCredits());
                sku_1_title.setText(creditsModel.get(3).getTitle());

                initComponent();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void createList(List<SkuDetails> skuDetailsList, SkuDetails skuDetails) {

        mPaymentProductModels.clear();
        mPaymentProductModels.addAll(skuDetailsList);
        // mPaymentProductModels.add(skuDetails);
        mPaymentProductListAdapter.notifyDataSetChanged();

        productModel = mPaymentProductModels.get(1);

    }
    public void initPurchaseFlow(SkuDetails details){

        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(details)
                .build();
        mBillingClient.launchBillingFlow(this, flowParams);

    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {

                if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                    // Acknowledge purchase and grant the item to the user

                    for(int i=0;i<creditsModel.size();i++){
                        if(purchase.getSku().equals(creditsModel.get(i).getSku())){
                            acknowledgePurchaseConsume(Integer.parseInt(creditsModel.get(i).getCredits()), purchase);
                            break;
                        }
                    }

//                    switch (purchase.getSku()) {
//                        case SMALL_PACK_ID:
//
//                            acknowledgePurchaseConsume(Variables.SMALL_PACK_CREDITS, purchase);
//
//                            break;
//                        case MEDIUM_PACK_ID:
//
//                            acknowledgePurchaseConsume(Variables.MEDIUM_PACK_CREDITS, purchase);
//                            break;
//                        case BIG_PACK_ID:
//
//                            acknowledgePurchaseConsume(Variables.BIG_PACK_CREDITS, purchase);
//
//                            break;
//                    }
//
                }
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Toast.makeText(this, "purchase canceled", Toast.LENGTH_SHORT).show();

        } else {
            // Handle any other error codes.
            Toast.makeText(this, "error_try_again_later", Toast.LENGTH_SHORT).show();
        }
    }
    public void acknowledgePurchaseConsume(final int credits, Purchase purchase){

        double newPoints = previousPoints + credits;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("credits");
        reference.setValue(newPoints).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(EarnCreditsActivity.this, "Purchased Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ConsumeParams consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();

        ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                Log.i("billingResult",billingResult.getDebugMessage());
                Log.i("purchaseToken",purchaseToken);
            }
        };

        mBillingClient.consumeAsync(consumeParams, consumeResponseListener);
    }

    String cred ="";
    String title = "";
    String price ="";
    @Override
    public void onItemClick(SkuDetails item) {
        mPaymentProductListAdapter.notifyDataSetChanged();
        productModel = item;

        AlertDialog.Builder  builder = new AlertDialog.Builder(EarnCreditsActivity.this);
        builder.setMessage("Select a payment method")
                .setPositiveButton("Google Play", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        initPurchaseFlow(productModel);
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("Flutterwave", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(int i=0;i<creditsModel.size();i++){
                    if(item.getSku().equals(creditsModel.get(i).getSku())){
                        cred = creditsModel.get(i).getCredits();
                        price = creditsModel.get(i).getPrice();
                        title = creditsModel.get(i).getTitle();
                        break;
                    }
                }
                List<Meta> meta = new ArrayList<Meta>();
                meta.add(new Meta("creds",cred));
                new RaveUiManager(EarnCreditsActivity.this).setAmount(Double.parseDouble( price))
                        .setCurrency("USD")
                        .setEmail("no@gmail.com")
                        .setfName("Buyer")
                        .setlName("Credits")
                        .setNarration("Buy credits for rubytalk")
                        .setPublicKey("FLWPUBK-91dc7de22da7f34f10f562c1184b73e8-X")
                        .setEncryptionKey("FLWSECK-9f7ddf5b716f604c840060d9ddfeb46e-X")
                        .setTxRef("eee"+new Random().nextInt(61) + 20)
//                        .setPhoneNumber(phoneNumber, boolean)
                    .acceptCardPayments(true)
                    .acceptGHMobileMoneyPayments(true)
                    .acceptUgMobileMoneyPayments(true)
                    .acceptZmMobileMoneyPayments(true)
                    .acceptRwfMobileMoneyPayments(true)
                    .allowSaveCardFeature(true)
//                    .onStagingEnv(boolean)
                    .setMeta(meta)
//                        .withTheme(styleId)
//                        .isPreAuth(boolean)
//                    .setSubAccounts(List<SubAccount>)
//                        .shouldDisplayFee(boolean)
                    .showStagingLabel(false)
                    .initialize();
            }

        })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            String creds = "0";
            try {
                JSONObject m = new JSONObject(message);
                String y = m.getString("data");
                Log.e("newWorld",cred);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            productModel.get
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
//                Toast.makeText(this, "SUCCESS " + message, Toast.LENGTH_SHORT).show();
                double newPoints = Double.parseDouble(previousPoints.toString()) + Double.parseDouble(cred);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("credits");
                reference.setValue(newPoints).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            AlertDialog.Builder  builder = new AlertDialog.Builder(EarnCreditsActivity.this);
                            builder.setMessage("Congratulations, you have successfully purchased "+ cred + " credits!")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        RetriveData();
                                        dialogInterface.dismiss();
                                        EarnCreditsActivity.super.onBackPressed();
                                    }
                                }).show();
                        }
                    }
                });
            }
            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);

//        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 111) {
                RetriveData();
            }
        }


    }

    private void RetriveData() {
        mDatabase.child("users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Double totalPoints = Double.parseDouble(dataSnapshot.child("credits").getValue().toString());
                    credits.setText("My Credits : "+ Double.toString(totalPoints));
                    previousPoints = totalPoints;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}