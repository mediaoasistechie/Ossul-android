package com.ossul.utility;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * This is contact helper class is use to perform operation
 * regarding contacts from different table(add,delete,fetch)
 */
public class ContactsUtils {

    public static ArrayList<String> getAllContactListFromContactBook(Activity activity) {
        List<Long> myAllContactBookIdList = new ArrayList<Long>();
        String[] mSelectionArgs = new String[1];
        mSelectionArgs[0] = "%";
        Cursor cursor = null;
        try {
            cursor = activity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts._ID},
                    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ? AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1", mSelectionArgs
                    , ContactsContract.Contacts.DISPLAY_NAME + " COLLATE NOCASE ASC ");
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (!myAllContactBookIdList.contains(id)) {
                    myAllContactBookIdList.add(cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        ArrayList<String> mContactList = new ArrayList<>();
        for (Long contactIds : myAllContactBookIdList) {
            ArrayList<String> list = readContacts(activity, String.valueOf(contactIds));
            if (list != null && list.size() > 0) {
                mContactList.addAll(list);
            }
        }
        return mContactList;
    }

    /**
     * method is use to read contacts from contact book
     *
     * @param contactId
     */
    public static ArrayList<String> readContacts(Context context, String contactId) {
        ArrayList<String> numberList = new ArrayList<>();

        String RAWCONTACTS_SELECTION = Data.CONTACT_ID + " = ?";
        String[] rawContacts_SelectionArgs = {""};
        String SORT_ORDER = Data.MIMETYPE;

        rawContacts_SelectionArgs[0] = contactId;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = cr.query(Data.CONTENT_URI, null, RAWCONTACTS_SELECTION, rawContacts_SelectionArgs, SORT_ORDER);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String MIME_TYPE = cursor.getString(cursor.getColumnIndex(Data.MIMETYPE));
                    if (MIME_TYPE.equalsIgnoreCase(Phone.CONTENT_ITEM_TYPE)) {
                        String data1 = cursor.getString(cursor.getColumnIndex(Data.DATA1));
                        numberList.add(removeExtraParameterFromNumber(data1));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return numberList;
    }

    /**
     * This method is use to remove extra parameter from number
     *
     * @param number
     * @return String
     */
    public static String removeExtraParameterFromNumber(String number) {
        if (number == null)
            return number;

        number = number.trim();
        number = number.replaceAll(" ", "");
        number = number.replaceAll("-", "");
        number = number.replaceAll("  ", "");
        number = number.replaceAll("\\(", "");
        number = number.replaceAll("\\)", "");
        return number.trim();
    }

}