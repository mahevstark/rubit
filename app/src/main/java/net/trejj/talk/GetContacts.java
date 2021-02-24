package net.trejj.talk;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import net.trejj.talk.CountryToPhonePrefix;
import net.trejj.talk.model.ContactInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GetContacts extends AsyncTask<Void, Void, List> {
    private Context activity;
    private OnContactFetchListener listener;

    public GetContacts(Context context, OnContactFetchListener listener) {
        activity = context;
        this.listener = listener;
    }

    @Override
    protected List doInBackground(Void... voids) {
        List<ContactInfo> contactList = new ArrayList<>();
        String ISOPrefix = getCountryISO();
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                //plus any other properties you wish to query
        };

        Cursor cursor = null;
        try {
            cursor = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
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
                        displayNumber = displayNumber.replace(" ", "");
                        displayNumber = displayNumber.replace("-", "");
                        displayNumber = displayNumber.replace("(", "");
                        displayNumber = displayNumber.replace(")", "");

                        if(!String.valueOf(displayNumber.charAt(0)).equals("+"))
                            if(displayNumber.startsWith("0")){
                                displayNumber = ISOPrefix + displayNumber.substring(1);
                            }else {
                                displayNumber = ISOPrefix + displayNumber;
                            }

                        ContactInfo contactInfo = new ContactInfo();
                        contactInfo.setName(displayName);
                        contactInfo.setNumber(displayNumber);
                        contactList.add(contactInfo);
                        //haven't seen this number yet: do something with this contact!
                    } else {
                        //don't do anything with this contact because we've already found this number
                    }
                }
            } finally {
                cursor.close();
            }
        }

        return contactList;
    }
    private String getCountryISO() {
        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(activity.TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso()!=null)
            if (!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso = telephonyManager.getNetworkCountryIso().toString();

        return CountryToPhonePrefix.getPhone(iso);
    }

    @Override
    protected void onPostExecute(List list) {
        super.onPostExecute(list);
        if (listener != null) {
            listener.onContactFetch(list);
        }
    }

    public interface OnContactFetchListener {
        void onContactFetch(List list);
    }
}