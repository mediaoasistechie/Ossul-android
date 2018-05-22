package com.ossul.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;


public class AppUtilsMethod {

    /**
     * This method is used to hide the keyboard
     *
     * @param activity
     * @return void
     */
    //hides the soft keyboard
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null
                && inputMethodManager != null)
            inputMethodManager.hideSoftInputFromWindow(activity
                            .getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * This method is used to get the application version
     *
     * @param context
     * @return String
     */
    public static String getAppVersion(Context context) {
        String appVersion = "";
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersion;
    }

    /**
     * This method is used to show the keyboard
     *
     * @param activity
     * @param editText
     * @return void
     */
    public static void showKeyBoard(Activity activity, EditText editText) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to check if the email address entered is valid or not
     *
     * @param email
     * @return boolean
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    /**
     * This method is used to check if the phone address entered is valid or not
     *
     * @param phone
     * @return boolean
     */
    public static boolean isPhoneValid(String phone) {
        String expression = "/^(\\+\\d{1,3}[- ]?)?\\d{10}$/";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(phone);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }


    /**
     * used to convert dp value into pixel value
     *
     * @param context
     * @param dp
     * @return pixel value (int)
     */
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    public static Bitmap getNotificationBitmap(String icoPath) {
        // If the icon path is not specified
        if (icoPath == null || icoPath.equals("")) {
            return null;
        }
        return getBitmapFromURL(icoPath);
    }

    private static Bitmap getBitmapFromURL(String srcUrl) {
        // Safe bet, won't have more than three /s
        srcUrl = srcUrl.replace("///", "/");
        srcUrl = srcUrl.replace("//", "/");
        srcUrl = srcUrl.replace("http:/", "http://");
        srcUrl = srcUrl.replace("https:/", "https://");
        HttpURLConnection connection = null;
        try {
            URL url = new URL(srcUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            Log.v("", "Couldn't download the notification icon. URL was: " + srcUrl);
            return null;
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Throwable t) {
                Log.v("", "Couldn't close connection!", t);
            }
        }
    }


    /**
     * This method is used to get Formatted date which will be updated to the server
     *
     * @param number
     * @return date as an String
     */
    public static String getMonthName(int number) {
        String month = null;
        switch (number) {
            case 1:
                month = "JAN";
                break;
            case 2:
                month = "FEB";
                break;

            case 3:
                month = "MAR";
                break;

            case 4:
                month = "APR";
                break;

            case 5:
                month = "MAY";
                break;

            case 6:
                month = "JUN";
                break;

            case 7:
                month = "JUL";
                break;

            case 8:
                month = "AUG";
                break;

            case 9:
                month = "SEP";
                break;

            case 10:
                month = "OCT";
                break;

            case 11:
                month = "NOV";
                break;

            case 12:
                month = "DEC";
                break;
        }
        return month;
    }
/*  --- */



    /*----------*/

    /**
     * used to format date into specific format
     *
     * @param date
     * @return String date
     */
    public static String formateDateForServer(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String finalDate = timeFormat.format(myDate);
        return finalDate;
    }

    public static String formattedDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String finalDate = timeFormat.format(myDate);
        return finalDate;
    }


    /**
     * used to format date into specific format
     *
     * @param date
     * @return String date
     */
    public static String formattedDateTimeToDisplay(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String finalDate = timeFormat.format(myDate);
        return finalDate;
    }

    /**
     * This method is used to convert the milliseconds to UTC time
     *
     * @param time
     * @return String
     */
    public static String getUTCTimeFromMilliseconds(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String UTCTime = simpleDateFormat.format(calendar.getTime());
        return UTCTime;
    }

    /**
     * This method is use to return exact string to be visible on chat as header on behalf of msg time
     *
     * @param updateDateMilliSec
     * @return {@link String}
     */
    public static final String getDaysAgo(long updateDateMilliSec, Context context) {
        if (updateDateMilliSec < 0) {
            return "less than 0";
        }
        Calendar calendar = Calendar.getInstance();
        int currentMessageHour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.setTimeInMillis(updateDateMilliSec);

        int messageHour = calendar.get(Calendar.HOUR_OF_DAY);
        int messageMinute = calendar.get(Calendar.MINUTE);
        int dateOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int monthOfYear = calendar.get(Calendar.MONTH);

        StringBuilder days = null;
        try {
            Calendar currentTm = Calendar.getInstance();
            long currentDateMilliSec = currentTm.getTime().getTime();
            Log.e("Current Mil=>" + currentDateMilliSec + " differen " + (currentDateMilliSec - updateDateMilliSec), "updated time Millis>" + updateDateMilliSec);
            if (updateDateMilliSec < currentDateMilliSec) {
                long diffDays = (currentDateMilliSec - updateDateMilliSec) / (86400000);
                if (diffDays < 0) {


                    days = new StringBuilder();
                    days.append(dateOfMonth < 10 ? "0" + dateOfMonth : dateOfMonth);
                    days.append("/");
                    days.append(++monthOfYear < 10 ? "0" + monthOfYear : monthOfYear);
                    days.append(" " + getFormattedString(messageHour, messageMinute, context));
                    return days.toString();
                }
                if (diffDays < 1) {
                    days = new StringBuilder("Today ");
                    if (currentMessageHour < messageHour) {
                        days = new StringBuilder("Yesterday ");
                    }
                    days.append(getFormattedString(messageHour, messageMinute, context));
                } else if (diffDays < 7) {
                    if (diffDays == 1) {
                        days = new StringBuilder("Yesterday ").append(getFormattedString(messageHour, messageMinute, context));
                    } else {
                        StringBuilder stringBuilder = new StringBuilder("" + diffDays);
                        stringBuilder.append(" days ago ");
                        days = stringBuilder.append(getFormattedString(messageHour, messageMinute, context));

                    }
                } else {
                    days = new StringBuilder();
                    days.append(dateOfMonth < 10 ? "0" + dateOfMonth : dateOfMonth);
                    days.append("/");
                    days.append(++monthOfYear < 10 ? "0" + monthOfYear : monthOfYear);
                    days.append(" " + getFormattedString(messageHour, messageMinute, context));
                }
            } else {
                Log.e("In else of", "get daysss agoooo");

                days = new StringBuilder();
                days.append(dateOfMonth < 10 ? "0" + dateOfMonth : dateOfMonth);
                days.append("/");
                days.append(++monthOfYear < 10 ? "0" + monthOfYear : monthOfYear);
                days.append(" " + getFormattedString(messageHour, messageMinute, context));
            }
        } catch (Exception e) {
            days = null;
            e.printStackTrace();
        }
        return days == null ? "" : days.toString();
    }

    /**
     * This methos will return formated string into 12 hour format
     *
     * @param messageHour
     * @param messageMinute
     */
    public static String getFormattedString(int messageHour, int messageMinute, Context context) {
        StringBuilder builder = new StringBuilder();
        if (android.text.format.DateFormat.is24HourFormat(context)) {
            builder.append(messageHour < 10 ? "0" + messageHour : messageHour);
            builder.append(":");
            builder.append(messageMinute < 10 ? "0" + messageMinute : messageMinute);
        } else {
            String amPmString = messageHour >= 12 ? " PM" : " AM";
            if (messageHour > 12)
                messageHour = messageHour - 12;
            builder.append(messageHour < 10 ? "0" + messageHour : messageHour);
            builder.append(":");
            builder.append(messageMinute < 10 ? "0" + messageMinute : messageMinute);
            builder.append(amPmString);
        }
        return builder.toString();
    }

    public static String get24HoursFormat(String time) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh a");
        Date date = null;
        try {
            date = parseFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return displayFormat.format(date);
    }

    public static long convertDateTimeInMillis(String dateString) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        Date mDate = new Date();
        try {
            mDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mDate.getTime();
    }


    public static void appendLog(String response) {
        File logFile = new File("sdcard/ussol.file");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        response = response + " time " + dateFormat.format(date);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(response);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);


    }

    /**
     * This method is used to convert the UTC time to milliseconds
     *
     * @param time
     * @return long
     */
    public static long getMillisecondsFromUTCTime(String time) {
        if (time == null) {
            return -1;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(Calendar.getInstance().getTimeZone().getDisplayName()));
        Date date;
        try {
            date = simpleDateFormat.parse(time);
            long milliseconds = date.getTime();
            return milliseconds;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * This method is used to convert the UTC time to milliseconds
     *
     * @param time
     * @return long
     */
    public static long getMillisecondsFromUTCTimeWithSecond(String time) {
        if (time == null) {
            return -1;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(Calendar.getInstance().getTimeZone().getDisplayName()));
        Date date;
        try {
            date = simpleDateFormat.parse(time);
            long milliseconds = date.getTime();
            return milliseconds;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static String getCurrentTime() {
        String formattedTime = "";
        Calendar c = Calendar.getInstance();
        System.out.println("Current time =&gt; " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("HH:mm aa");
        String formattedDate = df.format(c.getTime());

        String[] timeSplit = formattedDate.split(":");
        if (Integer.parseInt(timeSplit[0]) > 12) {
            int time = Integer.parseInt(timeSplit[0]) - 12;
            formattedTime = time + ":" + timeSplit[1];
        } else {
            formattedTime = formattedDate;
        }

        return formattedTime;
    }

    public static double convertKmToMi(double kilometers) {
        // Assume there are 0.621 miles in a kilometer.
        double miles = kilometers * 0.621;
        return miles;
    }

    public static void getLocalityFromLocation(final double latitude, final double longitude,
                                               final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        result = address.getLocality().toString();
                    }
                } catch (Exception e) {
                    Log.e("error", "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
//                        result = "Latitude: " + latitude + " Longitude: " + longitude +
//                                "\n\nAddress:\n" + result;
                        bundle.putString("address", result + "");
                        bundle.putString("latitude", latitude + "");
                        bundle.putString("longitude", longitude + "");
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Latitude: " + latitude + " Longitude: " + longitude +
                                "\n Unable to get address for this lat-long.";
                        bundle.putString("address", "" + null);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }

    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }


//    public static LatLng getLocationFromAddress(String strAddress, Activity activity) /*{
//        Geocoder coder = new Geocoder(activity);
//        List<Address> address;
//        LatLng p1 = null;
//
//        try {
//            address = coder.getFromLocationName(strAddress, 5);
//            if (address == null) {
//                return null;
//            }
//            Address location = address.get(0);
//            location.getLatitude();
//            location.getLongitude();
//            location.getLocality();
//
//            p1 = new LatLng(location.getLatitude(), location.getLongitude());
//
//        } catch (Exception ex) {
//
//            ex.printStackTrace();
//        }
//        return p1;
//    }*/

    public static String getValueFromKey(JSONObject jsonObject, String key) {
        if (jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key)) {
            try {
                return jsonObject.get(key).toString();
            } catch (JSONException e) {
                return "";
            }
        } else
            return "";
    }

    public static void openFile(Context context, String url) throws IOException {
        // Create URI
        File myFile = new File(url);
        Uri uri = Uri.fromFile(myFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/zip");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static void openFileInBrowser(Context context, String url) throws IOException {
        if(Validator.isEmptyString(url))
            return;
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

    public static String formatToYesterdayOrToday(String date) {
        //2018-01-22 08:31:47
        try {
            Date dateTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateTime);
            Calendar today = Calendar.getInstance();
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE, -1);
            DateFormat timeFormatter = new SimpleDateFormat("hh:mma");
            DateFormat dateFormat = new SimpleDateFormat("d MMM, yy hh:mma");

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                return "Today " + timeFormatter.format(dateTime);
            } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
                return "Yesterday " + timeFormatter.format(dateTime);
            } else {
                return dateFormat.format(date);
            }
        } catch (Exception e) {
            return "";
        }
    }

}