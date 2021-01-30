package net.trejj.rubytalksense.activities.main.contacts;

import android.Manifest;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.heetch.countrypicker.Country;
import com.heetch.countrypicker.CountryPickerCallbacks;
import com.heetch.countrypicker.CountryPickerDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.skydoves.elasticviews.ElasticImageView;

import net.trejj.rubytalksense.Function;
import net.trejj.rubytalksense.GetContacts;
import net.trejj.rubytalksense.R;
import net.trejj.rubytalksense.activities.main.MainActivity;
import net.trejj.rubytalksense.adapters.ContactAdapter;
import net.trejj.rubytalksense.fragments.BaseFragment;
import net.trejj.rubytalksense.model.ContactInfo;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static net.trejj.rubytalksense.activities.main.MainActivity.fab;
import static net.trejj.rubytalksense.activities.main.MainActivity.textStatusFab;
import static org.webrtc.ContextUtils.getApplicationContext;

/** Created by AwsmCreators * */
public class ContactsFragment extends BaseFragment implements View.OnClickListener {
    ElasticImageView dialpad;
    LinearLayout keyboard, hide;
    EditText number;
    TextView callcharges;
    LinearLayout one, two, three, four, five, six, seven, eight, nine, zero, clear;
    Button call;
    ImageView select_country;
    ImageView countrySelector;
    LinearLayout noContacts;
    private RecyclerView recyclerView;
    private List<ContactInfo> contactList;
    private ContactAdapter mAdapter;

    FirebaseAuth auth;
    DatabaseReference reference;
    boolean firstLogin=false;
    Double credits;


    public ContactsFragment() {
        // Required empty public constructor
    }
    View view;


    @Override
    public void onResume() {
        super.onResume();
        fab.setVisibility(View.GONE);
        textStatusFab.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_contacts,container,false);


        dialpad = view.findViewById(R.id.dialpad);
        keyboard = view.findViewById(R.id.keyboard);
        hide = view.findViewById(R.id.hide);

        one = view.findViewById(R.id.one);
        two = view.findViewById(R.id.two);
        three = view.findViewById(R.id.three);
        four = view.findViewById(R.id.four);
        five = view.findViewById(R.id.five);
        six = view.findViewById(R.id.six);
        seven = view.findViewById(R.id.seven);
        eight = view.findViewById(R.id.eight);
        nine = view.findViewById(R.id.nine);
        zero = view.findViewById(R.id.zero);
        clear = view.findViewById(R.id.clear);
        hide = view.findViewById(R.id.hide);
        call = view.findViewById(R.id.btn_call);
        callcharges = view.findViewById(R.id.call_charges);
        //call.setEnabled(false);
        number = view.findViewById(R.id.et_number);
        number.setText("+"+ Function.getCountryDialCode(getActivity()));
        String numberresult = number.getText().toString();

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    firstLogin = Boolean.parseBoolean(snapshot.child("firstLogin").getValue().toString());
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    firstTimeCredits();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        textStatusFab.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);

        if (numberresult.startsWith("+")){
            callcharges.setText("Call charges:"+" "+ Function.checkCountry(numberresult)+" "+"credits/min");
        }else {
            callcharges.setText("Call charges: Unknown/min");
        }
        //number.setCharactersNoChangeInitial("+91");
        select_country = view.findViewById(R.id.select_country);
        countrySelector = view.findViewById(R.id.country_selector);
        noContacts = view.findViewById(R.id.no_contacts);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        five.setOnClickListener(this);
        six.setOnClickListener(this);
        seven.setOnClickListener(this);
        eight.setOnClickListener(this);
        nine.setOnClickListener(this);
        zero.setOnClickListener(this);
        clear.setOnClickListener(this);
        call.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.recycler_view);
        contactList = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        checkPermissions();


        dialpad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboard.setVisibility(View.VISIBLE);
                dialpad.setVisibility(View.GONE);
            }
        });

        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboard.setVisibility(View.GONE);
                dialpad.setVisibility(View.VISIBLE);
            }
        });
        zero.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                number.append("+");
                return true;
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                searchView = MainActivity.searchView;

                textChangeLisener();
            }
        },2000);

        select_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryPickerDialog countryPicker =
                        new CountryPickerDialog(getActivity(), new CountryPickerCallbacks() {
                            @Override
                            public void onCountrySelected(Country country, int flagResId) {
                                // TODO handle callback
                                String code = country.getDialingCode();
                                number.setText("+"+code);
                                select_country.setImageDrawable(getResources().getDrawable(flagResId));
                            }
                        });
                countryPicker.show();

            }
        });

        countrySelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryPickerDialog countryPicker =
                        new CountryPickerDialog(getActivity(), new CountryPickerCallbacks() {
                            @Override
                            public void onCountrySelected(Country country, int flagResId) {
                                // TODO handle callback
                                String code = country.getDialingCode();
                                number.setText("+"+code);
                                select_country.setImageDrawable(getResources().getDrawable(flagResId));
                            }
                        });
                countryPicker.show();
            }
        });

        keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    keyboard.setVisibility(View.GONE);
                    dialpad.setVisibility(View.VISIBLE);
                    // Scrolling up
                } else {
                    // Scrolling down
                    //keyboard.setVisibility(View.GONE);
                    //dialpad.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Do something
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    // Do something
                } else {
                    // Do something
                }
            }
        });

        noContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboard.setVisibility(View.GONE);
                dialpad.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private void setCreditsData(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("credits");
        HashMap<String, Object> map = new HashMap<>();
        map.put("credits", 40000);
        map.put("sku","test.android.purchased");
        map.put("price",200);
        map.put("title","123");
        map.put("type","1");
        for(int i=0;i<4;i++){
            reference.push().setValue(map);
        }
    }

    private void firstTimeCredits() {
        if(!firstLogin){
            HashMap<String, Object> map = new HashMap<>();

            map.put("credits",500.0);
            map.put("firstLogin",true);

            reference.updateChildren(map);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
    }
    SearchView searchView;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onQueryTextChange(@Nullable String newText) {
        super.onQueryTextChange(newText);
//        TextChanged();
    }

    private void textChangeLisener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                TextChanged(newText);
                return true;
            }
        });
        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String result = number.getText().toString();
                if (result.startsWith("+")){
                    callcharges.setText("Call charges:"+" "+ Function.checkCountry(result)+" "+"credits/min");
                }else {
                    callcharges.setText("Call charges: Unknown/min");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.one) {
            number.append("1");

        }else if (v.getId() == R.id.two){
            number.append("2");

        }else if (v.getId() == R.id.three){
            number.append("3");

        }else if (v.getId() == R.id.four){
            number.append("4");

        }else if (v.getId() == R.id.five){
            number.append("5");

        }else if (v.getId() == R.id.six){
            number.append("6");

        }else if (v.getId() == R.id.seven){
            number.append("7");

        }else if (v.getId() == R.id.eight){
            number.append("8");

        }else if (v.getId() == R.id.nine){
            number.append("9");

        }else if (v.getId() == R.id.zero){
            number.append("0");

        }else if (v.getId() == R.id.clear){
            clearNumber();
        }else if (v.getId() == R.id.btn_call) {
            if (MainActivity.isCallEnabled){
                ((MainActivity)getActivity()).CallNumber(number.getText().toString(),number.getText().toString());
            }else {
                Toast.makeText(getActivity(), "Call is not ready", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void clearNumber() {
        String previousNumber = number.getText().toString();
        if (!(previousNumber.length() == 0)){
            previousNumber = previousNumber.substring(0, previousNumber.length() - 1);
            number.setText(previousNumber);
        }
    }
    private void checkPermissions() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.READ_CONTACTS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        //ContactLoader contactLoader = new ContactLoader();
                        //contactLoader.execute();
                        new GetContacts(getActivity(), new GetContacts.OnContactFetchListener() {
                            @Override
                            public void onContactFetch(List list) {
                                mAdapter = new ContactAdapter(getActivity(), list);
                                recyclerView.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                            }
                        }).execute();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        noContacts.setVisibility(View.VISIBLE);
                        if (response.isPermanentlyDenied()){
                            Toast.makeText(getActivity(), "Denied", Toast.LENGTH_SHORT).show();
                            noContacts.setVisibility(View.VISIBLE);
                            //finish();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }


    @Override
    public boolean showAds() {
        return false;
    }

    class ContactLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String[] projection = new String[] {
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                    //plus any other properties you wish to query
            };

            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            } catch (SecurityException e) {
                //SecurityException can be thrown if we don't have the right permissions
            }

            if (cursor != null) {
                try {
                    HashSet<String> normalizedNumbersAlreadyFound = new HashSet<>();
                    int indexOfNormalizedNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
                    int indexOfDisplayName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    int indexOfDisplayNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                    while (cursor.moveToNext()) {
                        String normalizedNumber = cursor.getString(indexOfNormalizedNumber);
                        if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {
                            String displayName = cursor.getString(indexOfDisplayName);
                            String displayNumber = cursor.getString(indexOfDisplayNumber);
                            ContactInfo contactInfo = new ContactInfo();
                            contactInfo.setName(displayName);
                            contactInfo.setNumber(displayNumber);
//                            contactList.add(contactInfo);


                            //haven't seen this number yet: do something with this contact!
                        } else {
                            //don't do anything with this contact because we've already found this number
                        }
                    }
                } finally {
                    cursor.close();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter = new ContactAdapter(getActivity(), contactList);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void TextChanged(String newText){
        mAdapter.getFilter().filter(newText);
    }
}