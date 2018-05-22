package com.ossul.activities;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ossul.R;
import com.ossul.adapters.LangSpinnerAdapter;
import com.ossul.appconstant.AppConstants;
import com.ossul.apppreferences.AppPreferences;
import com.ossul.dialog.CustomDialogFragment;
import com.ossul.dialog.DialogManager;
import com.ossul.fragments.HomeFragment;
import com.ossul.fragments.MyOffersFragment;
import com.ossul.fragments.MyRequestFragment;
import com.ossul.fragments.SpecialOfferFragment;
import com.ossul.fragments.SubmitOfferFragment;
import com.ossul.fragments.SubmitRequestFragment;
import com.ossul.interfaces.INetworkEvent;
import com.ossul.listener.HeaderViewSetter;
import com.ossul.network.NetworkService;
import com.ossul.network.constants.AppNetworkConstants;
import com.ossul.network.model.NetworkModel;
import com.ossul.network.model.response.AllCounterResponse;
import com.ossul.network.model.response.GetLanguageResponse;
import com.ossul.network.model.response.GetProfileResponse;
import com.ossul.utility.AppUtilsMethod;
import com.ossul.utility.FragmentUtils;
import com.ossul.utility.Validator;
import com.ossul.view.HeaderHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;


public class MainActivity extends BaseActivity implements INetworkEvent, HeaderViewSetter {
    public static TextView mOfficeCountTV;
    public static TextView mRequestCountTV;
    private String API_GET_PROFILE = "";
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RelativeLayout mSliderDrawerLayout;
    private RelativeLayout mLogoutTV;
    private Dialog mProgressDialog;
    private TextView mUserNameTV, mUserEmailTV;
    private String API_GET_ALL_COUNTER = "";
    private RelativeLayout mMainMenuRL;
    private TextView mSentOfferTV, mReceivedOfferTV;
    private RelativeLayout mMyRequestMenuRL;
    private RelativeLayout mMyProfileRL;
    private RelativeLayout mLoginRL;
    private RelativeLayout mChangePassRL;
    private RelativeLayout toolbar_headerLayout;
    private HeaderHandler headerHandler;
    private boolean doubleBackToExitPressedOnce = false;
    private String API_GET_LABELS = "";
    private boolean showProgress = false;
    private JSONObject jsonObject = new JSONObject();
    private TextView mRealEstateTV, mManageEcatalogueTV, mSubmitRequestMenuTV, mMyRequestTV, mProfileTV, mChangePassTV;
    private TextView mLogout, mInviteTV, mLoginTV;
    private String no = "No", yes = "Yes", logoutMsg = "Would you like to logout?";
    private TextView mRealEstateNewsTV, mHomeTV, mEconomicNewsTV;
    private TextView mReceivedOffersCountTV, mSentOffersCountTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        showHomeFragment();
        if (getIntent().getExtras() != null) {
            initNotification(getIntent().getExtras());
        }
    }

    private void initNotification(Bundle bundle) {
        if (bundle != null) {
            String itemId = bundle.getString(AppConstants.ITEM_ID);
            int notificationId = bundle.getInt(AppConstants.NOTIFICATION_ID);
            String itemType = bundle.getString(AppConstants.ITEM_TYPE);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (mNotificationManager != null) {
                mNotificationManager.cancel(bundle.getInt(AppConstants.ID));
            }
            showNotificationPage(itemId, itemType);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (fragment != null) {
                return;
            }
            showHomeFragment();
        }
    }

    private void showNotificationPage(String itemId, String itemType) {
        if (Validator.isEmptyString(itemType))
            return;

        if (itemType.equalsIgnoreCase("offer") && !Validator.isEmptyString(itemId)) {
            if (AppPreferences.get().isLogin()) {
                MyOffersFragment receivedFragment = MyOffersFragment.newInstance("received", itemId);
                FragmentUtils.replaceFragment(getSupportFragmentManager(), receivedFragment.getClass().getSimpleName() + "received", true, R.id.container, receivedFragment);
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, 11001);
            }
            return;
        }
        if (itemType.equalsIgnoreCase("app_update")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showUpdatePopup();
                }
            }, 1000);
            return;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData();
        showHomeFragment();
        if (intent.getExtras() != null) {
            initNotification(intent.getExtras());
        }
    }

    private void calCounterAPI() {
        if (Validator.isConnectedToInternet(getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            API_GET_ALL_COUNTER = AppNetworkConstants.BASE_URL + "user/manage.php?action=get-all-counter";
            NetworkService service = new NetworkService(API_GET_ALL_COUNTER, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        }
    }

    @Override
    protected void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_headerLayout = (RelativeLayout) findViewById(R.id.toolbar_layout);
        headerHandler = new HeaderHandler(toolbar_headerLayout);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_home_root);
        mSliderDrawerLayout = (RelativeLayout) findViewById(R.id.nav_home_right_drawer);

        mUserNameTV = (TextView) findViewById(R.id.tv_user_name);
        mUserEmailTV = (TextView) findViewById(R.id.tv_user_email);

        mRealEstateTV = (TextView) findViewById(R.id.tv_estate_offices);
        mManageEcatalogueTV = (TextView) findViewById(R.id.tv_manage_e_catalogues);
        mHomeTV = (TextView) findViewById(R.id.tv_home);
        mRealEstateNewsTV = (TextView) findViewById(R.id.tv_real_estate_news);
        mEconomicNewsTV = (TextView) findViewById(R.id.tv_economic_news);
        mSubmitRequestMenuTV = (TextView) findViewById(R.id.tv_submit_requests);
        mMyRequestTV = (TextView) findViewById(R.id.tv_my_requests);
        mProfileTV = (TextView) findViewById(R.id.tv_my_profile);
        mChangePassTV = (TextView) findViewById(R.id.tv_change_password);

        mSentOffersCountTV = (TextView) findViewById(R.id.tv_sent_offer_count);
        mReceivedOffersCountTV = (TextView) findViewById(R.id.tv_received_offer_count);

        mLogout = (TextView) findViewById(R.id.tv_log_out);
        mInviteTV = (TextView) findViewById(R.id.tv_invite);
        mLoginTV = (TextView) findViewById(R.id.tv_login);
        mOfficeCountTV = (TextView) findViewById(R.id.tv_real_estate_offices_count);
        mRequestCountTV = (TextView) findViewById(R.id.tv_my_request_count);

        mLogoutTV = (RelativeLayout) findViewById(R.id.rl_log_out);
        mMyProfileRL = (RelativeLayout) findViewById(R.id.rl_profile);
        mLoginRL = (RelativeLayout) findViewById(R.id.rl_login);
        mChangePassRL = (RelativeLayout) findViewById(R.id.rl_change_password);
        mMyRequestMenuRL = (RelativeLayout) findViewById(R.id.rl_my_request);
        mReceivedOfferTV = (TextView) findViewById(R.id.tv_my_received_offers);
        mSentOfferTV = (TextView) findViewById(R.id.tv_my_sent_offers);
        mMainMenuRL = (RelativeLayout) findViewById(R.id.rl_main);
        final Spinner langSPN = (Spinner) findViewById(R.id.spn_lang);
        langSPN.setAdapter(new LangSpinnerAdapter(this));
        findViewById(R.id.rl_estate_offices).setOnClickListener(this);
        findViewById(R.id.rl_manage_e_catalogues).setOnClickListener(this);
        findViewById(R.id.rl_change_password).setOnClickListener(this);
        findViewById(R.id.rl_home).setOnClickListener(this);
        findViewById(R.id.rl_real_estate_news).setOnClickListener(this);
        findViewById(R.id.rl_economic_news).setOnClickListener(this);
        findViewById(R.id.rl_profile).setOnClickListener(this);
        findViewById(R.id.rl_my_received_offers).setOnClickListener(this);
        findViewById(R.id.rl_my_sent_offers).setOnClickListener(this);
        findViewById(R.id.action_menu).setOnClickListener(this);
        findViewById(R.id.iv_back_header).setOnClickListener(this);
        findViewById(R.id.rl_submit_request).setOnClickListener(this);

        mLogoutTV.setOnClickListener(this);
        mLoginRL.setOnClickListener(this);
        findViewById(R.id.rl_invite).setOnClickListener(this);
        mMyRequestMenuRL.setOnClickListener(this);
        resetDrawerItemsState(true);
        langSPN.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    showProgress = true;
                    updateLangAPI("en");

                } else if (position == 1) {
                    updateLangAPI("ar");
                    showProgress = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        changeLabels();
        if (!Validator.isEmptyString(mAppPreferences.getPrefLang())) {
            updateLangAPI(mAppPreferences.getPrefLang());
            showProgress = false;
            if (mAppPreferences.getPrefLang().equalsIgnoreCase("ar"))
                langSPN.setSelection(1);
            else langSPN.setSelection(0);
        }
    }

    private void changeLabels() {
        if (!Validator.isEmptyString(mAppPreferences.getLabels())) {
            try {
                jsonObject = new JSONObject(mAppPreferences.getLabels());
                if (jsonObject != null && jsonObject.length() != 0) {
//                    mRealEstateTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Real estate offices"));
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "OK"))) {
                        AppConstants.OK = AppUtilsMethod.getValueFromKey(jsonObject, "OK");
                    } else {
                        AppConstants.OK = getResources().getString(R.string.ok);
                    }
                    mManageEcatalogueTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Manage eCatalogues"));
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Home")))
                        mHomeTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Home"));
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Real Estate News")))
                        mRealEstateNewsTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Real Estate News"));
                    else {
                        mRealEstateNewsTV.setText(getResources().getString(R.string.real_estate_news));
                    }
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Economic News")))
                        mEconomicNewsTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Economic News"));
                    else {
                        mEconomicNewsTV.setText(getResources().getString(R.string.economic_news));
                    }
                    mSubmitRequestMenuTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Submit Request"));
                    mMyRequestTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "My Request"));
                    mSentOfferTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Sent Offers"));
                    mReceivedOfferTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Received Offers"));
                    mProfileTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Profile"));
                    mChangePassTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Change Password"));


                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Invite")))
                        mInviteTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Invite"));
                    else {
                        mInviteTV.setText(getResources().getString(R.string.invite));
                    }
                    if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Login/Register")))
                        mLoginTV.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Login/Register"));
                    else {
                        mLoginTV.setText(getResources().getString(R.string.login_register));
                    }
                    mLogout.setText(AppUtilsMethod.getValueFromKey(jsonObject, "Logout"));
                    logoutMsg = (AppUtilsMethod.getValueFromKey(jsonObject, "Would you like to logout?"));
                    if (!Validator.isEmptyString(mAppPreferences.getPrefLang()) && mAppPreferences.getPrefLang().equalsIgnoreCase("ar")) {
                        logoutMsg = "هل ترغب في الخروج؟";
                    }

                    yes = (AppUtilsMethod.getValueFromKey(jsonObject, "Yes"));
                    no = (AppUtilsMethod.getValueFromKey(jsonObject, "No"));

                    //header tabs
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
                    if (fragment != null && fragment instanceof HomeFragment) {
                        ((HomeFragment) fragment).changeLabels();
                    } else if (fragment != null && fragment instanceof SubmitRequestFragment) {
                        ((SubmitRequestFragment) fragment).changeLabels();
                    } else if (fragment != null && fragment instanceof MyRequestFragment) {
                        ((MyRequestFragment) fragment).changeLabels();
                    } else if (fragment != null && fragment instanceof SubmitOfferFragment) {
                        ((SubmitOfferFragment) fragment).changeLabels();
                    } else if (fragment != null && fragment instanceof SpecialOfferFragment) {
                        ((SpecialOfferFragment) fragment).changeLabels();
                    } else if (fragment != null && fragment instanceof MyOffersFragment) {
                        ((MyOffersFragment) fragment).changeLabels();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!AppPreferences.get().isLogin()) {
            mLogoutTV.setVisibility(View.GONE);
            mChangePassRL.setVisibility(View.GONE);
            mMyProfileRL.setVisibility(View.GONE);
            mUserNameTV.setVisibility(View.GONE);
            mUserEmailTV.setVisibility(View.GONE);
            mLoginRL.setVisibility(View.VISIBLE);
        } else {
            mLoginRL.setVisibility(View.GONE);
            mLogoutTV.setVisibility(View.VISIBLE);
            mChangePassRL.setVisibility(View.VISIBLE);
            mMyProfileRL.setVisibility(View.VISIBLE);
            mUserNameTV.setVisibility(View.VISIBLE);
            mUserEmailTV.setVisibility(View.VISIBLE);
        }
    }

    private void updateLangAPI(String lang) {

        Resources res = getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(lang.toLowerCase());
        res.updateConfiguration(conf, dm);
        if (Validator.isConnectedToInternet(getApplicationContext())) {
            NetworkModel networkModel = new NetworkModel();
            mAppPreferences.setPrefLang(lang);
            API_GET_LABELS = AppNetworkConstants.BASE_URL + "user/auth.php?action=update-language&lang=" + lang;
            NetworkService service = new NetworkService(API_GET_LABELS, AppConstants.METHOD_GET, this);
            service.call(networkModel);
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        resetDrawerItemsState(true);
        switch (v.getId()) {
            case R.id.rl_submit_request:
                if (AppPreferences.get().isLogin()) {
                    SubmitRequestFragment submitRequestFragment = new SubmitRequestFragment();
                    FragmentUtils.replaceFragment(getSupportFragmentManager(), submitRequestFragment.getClass().getSimpleName(), true, R.id.container, submitRequestFragment);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 11001);
                }
                break;
            case R.id.tv_submit_offer:
                if (AppPreferences.get().isLogin()) {
                    SubmitOfferFragment submitOfferFragment = new SubmitOfferFragment();
                    FragmentUtils.replaceFragment(getSupportFragmentManager(), submitOfferFragment.getClass().getSimpleName(), true, R.id.container, submitOfferFragment);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 11001);
                }
                break;
            case R.id.rl_my_request:
                if (AppPreferences.get().isLogin()) {
                    MyRequestFragment myRequestFragment = new MyRequestFragment();
                    FragmentUtils.replaceFragment(getSupportFragmentManager(), myRequestFragment.getClass().getSimpleName(), true, R.id.container, myRequestFragment);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 11001);
                }
                break;
            case R.id.rl_login: {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, 11001);
            }
            break;
            case R.id.rl_my_sent_offers:
                if (AppPreferences.get().isLogin()) {
                    MyOffersFragment fragment = MyOffersFragment.newInstance("sent");
                    FragmentUtils.replaceFragment(getSupportFragmentManager(), fragment.getClass().getSimpleName() + "sent", true, R.id.container, fragment);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 11001);
                }
                break;
            case R.id.rl_my_received_offers:
                if (AppPreferences.get().isLogin()) {
                    MyOffersFragment receivedFragment = MyOffersFragment.newInstance("received");
                    FragmentUtils.replaceFragment(getSupportFragmentManager(), receivedFragment.getClass().getSimpleName() + "received", true, R.id.container, receivedFragment);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 11001);
                }
                break;
            case R.id.action_menu:
                closeOrOpenDrawer();
                break;
            case R.id.iv_back_header:
                onBackPressed();
                break;
            case R.id.rl_profile:
                if (AppPreferences.get().isLogin()) {
                    startActivity(new Intent(this, ProfileActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 11001);
                }
                break;

            case R.id.rl_home:
                FragmentUtils.clearStack(getSupportFragmentManager());
                showHomeFragment();
                break;
            case R.id.rl_real_estate_news:
                startActivity(new Intent(this, RealEstateNewsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;
            case R.id.rl_economic_news:
                startActivity(new Intent(this, EconomicNewsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                break;
            case R.id.rl_change_password:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.rl_invite:
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String link = "https://play.google.com/store/apps/details?id=com.ossul&hl=en";
                if (!Validator.isEmptyString(AppUtilsMethod.getValueFromKey(jsonObject, "Invite playstore")))
                    link = AppUtilsMethod.getValueFromKey(jsonObject, "Invite playstore");
                intent.putExtra(Intent.EXTRA_TEXT, link);
                startActivity(Intent.createChooser(intent, "Invite" + "..."));

                break;
            case R.id.rl_log_out:
                CustomDialogFragment.getInstance(this, null, logoutMsg, null, no, yes, 0, new CustomDialogFragment.OnDialogClickListener() {
                    @Override
                    public void onClickOk() {
                        AppPreferences.get().clearPreferences();
                        Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                        intentLogin.putExtra("from", "logout");
                        startActivity(intentLogin);
                        finish();
                    }

                    @Override
                    public void onClickCancel() {

                    }
                });
                break;
            case R.id.rl_estate_offices:
                HomeFragment.showFragment(1, this);
                break;

            case R.id.rl_manage_e_catalogues:
                HomeFragment.showFragment(2, this);
                break;
        }

    }

    private void closeDrawer() {
        if (drawerLayout.isDrawerOpen(mSliderDrawerLayout)) {
            drawerLayout.closeDrawer(mSliderDrawerLayout);
        }
    }

    private void closeOrOpenDrawer() {
        if (drawerLayout.isDrawerOpen(mSliderDrawerLayout)) {
            drawerLayout.closeDrawer(mSliderDrawerLayout);
        } else {
            drawerLayout.openDrawer(mSliderDrawerLayout);
        }
    }

    private void handleDeepLink(Uri data) {
        clearStackShowHomeFragment();
        List<String> segments = data.getPathSegments();
        int size;
        String type = "";
        String title = "";
        String id = "";
        if (null != segments) {
            size = segments.size();
            if (size >= 1 && !Validator.isEmptyString(segments.get(0))) {
                type = segments.get(0);
            }
            if (size >= 2 && !Validator.isEmptyString(segments.get(1))) {
                title = segments.get(1);
            }
            if (size >= 3 && !Validator.isEmptyString(segments.get(2))) {
                id = segments.get(2);
            }

        }
        if (!Validator.isEmptyString(id)) {
            handleDetailDeepLink(type, id, title);
        } else if (!Validator.isEmptyString(type) && !type.equalsIgnoreCase("home")) {
            openList(type);
        } else {
            showHomeFragment();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11001 && resultCode == RESULT_OK) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    private void handleDetailDeepLink(String type, String id, String title) {
        if (!Validator.isEmptyString(type)) {
            if (type.equalsIgnoreCase("economic-news")) {
                if (!Validator.isEmptyString(id) && TextUtils.isDigitsOnly(id)) {
                    Intent intent = new Intent(MainActivity.this, NewsDetailsActivity.class);
                    intent.putExtra("for", "economic");
                    intent.putExtra("news_id", id);
                    startActivityForResult(intent, 103);

                } else {
                    Intent intent = new Intent(MainActivity.this, EconomicNewsActivity.class);
                    startActivityForResult(intent, 104);
                }
            } else if (type.equalsIgnoreCase("real-estate-news")) {
                if (!Validator.isEmptyString(id) && TextUtils.isDigitsOnly(id)) {
                    Intent askIntent = new Intent(MainActivity.this, NewsDetailsActivity.class);
                    askIntent.putExtra("for", "real");
                    askIntent.putExtra("news_id", id);
                    startActivityForResult(askIntent, 105);
                } else {
                    Intent askIntent = new Intent(MainActivity.this, RealEstateNewsActivity.class);
                    startActivityForResult(askIntent, 106);
                }
            } else if (type.equalsIgnoreCase("my-request")) {
                MyRequestFragment myRequestFragment = new MyRequestFragment();
                FragmentUtils.replaceFragmentWithSlide(getSupportFragmentManager(), myRequestFragment.getClass().getSimpleName(), true, R.id.container, myRequestFragment);
            } else if (type.equalsIgnoreCase("special-offers")) {
                if (!Validator.isEmptyString(id) && TextUtils.isDigitsOnly(id)) {
                    Intent intent = new Intent(MainActivity.this, PropertyDetailsActivity.class);
                    intent.putExtra("for", "property");
                    intent.putExtra("id", id);
                    startActivityForResult(intent, 107);
                } else {
                    Intent intent = new Intent(MainActivity.this, SpecialOffersActivity.class);
                    startActivityForResult(intent, 107);
                }

            } else if (type.equalsIgnoreCase("submit-offer")) {
                SubmitOfferFragment submitOfferFragment = new SubmitOfferFragment();
                FragmentUtils.replaceFragmentWithSlide(getSupportFragmentManager(), submitOfferFragment.getClass().getSimpleName(), true, R.id.container, submitOfferFragment);
            } else {
                showHomeFragment();
            }
        }
    }

    private void openList(String type) {
        if (!Validator.isEmptyString(type)) {
            if (type.equalsIgnoreCase("economic-news")) {
                Intent intent = new Intent(MainActivity.this, EconomicNewsActivity.class);
                startActivityForResult(intent, 104);
            } else if (type.equalsIgnoreCase("real-estate-news")) {
                Intent askIntent = new Intent(MainActivity.this, RealEstateNewsActivity.class);
                startActivityForResult(askIntent, 104);
            } else if (type.equalsIgnoreCase("my-request")) {
                MyRequestFragment myRequestFragment = new MyRequestFragment();
                FragmentUtils.replaceFragmentWithSlide(getSupportFragmentManager(), myRequestFragment.getClass().getSimpleName(), true, R.id.container, myRequestFragment);
            } else if (type.equalsIgnoreCase("special-offers")) {
                Intent intent = new Intent(MainActivity.this, SpecialOffersActivity.class);
                startActivityForResult(intent, 107);
            } else if (type.equalsIgnoreCase("submit-offer")) {
                SubmitOfferFragment submitOfferFragment = new SubmitOfferFragment();
                FragmentUtils.replaceFragmentWithSlide(getSupportFragmentManager(), submitOfferFragment.getClass().getSimpleName(), true, R.id.container, submitOfferFragment);
            } else {
                showHomeFragment();
            }
        }
    }

    private void clearStackShowHomeFragment() {
        FragmentUtils.clearStack(getSupportFragmentManager());
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment == null) {
            showHomeFragment();
        }
    }


    @Override
    protected void initVariables() {
        mMainMenuRL.setVisibility(View.VISIBLE);
        mProgressDialog = new DialogManager(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        calCounterAPI();
        if (Validator.isEmptyString(mAppPreferences.getUserDetailPref())) {
            getProfileAPI();
        } else {
            setData(GetProfileResponse.fromJson(mAppPreferences.getUserDetailPref()));
        }
        if (null != AppConstants.intentPath && null != AppConstants.intentPath.getData()) {
            handleDeepLink(AppConstants.intentPath.getData());
        }
    }

    private void getProfileAPI() {
        if (Validator.isConnectedToInternet(getApplicationContext())) {
            API_GET_PROFILE = AppNetworkConstants.BASE_URL + "user/manage.php?action=get-profile";
            NetworkService service = new NetworkService(API_GET_PROFILE, AppConstants.METHOD_GET, this);
            service.call(new NetworkModel());
        } else {
            CustomDialogFragment.getInstance(this, null, getResources().getString(R.string.internet_error), null, AppConstants.OK, null, 1, null);
        }
    }

    private void setData(GetProfileResponse getProfileResponse) {
        if (getProfileResponse != null && getProfileResponse.userData != null) {
            mUserNameTV.setText(getProfileResponse.userData.displayName);
            mUserEmailTV.setText(getProfileResponse.userData.email);
        }

    }

    private void showHomeFragment() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();

//        mMyRequestLL.setTag("unselected");
//        mSubmitOfferLL.setTag("unselected");
        if (getSupportFragmentManager() != null)
            FragmentUtils.clearStack(getSupportFragmentManager());
        HomeFragment homeFragment = new HomeFragment();
        FragmentUtils.replaceFragment(getSupportFragmentManager(), homeFragment.getClass().getSimpleName(), false, R.id.container, homeFragment);
    }

    private void showUpdatePopup() {
        String message = "A new version is available on playstore. Please update for better experience";
        CustomDialogFragment.getInstance(this, "UPDATE AVAILABLE", message, null, no, yes, AppConstants.APP_UPDATE, new CustomDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickOk() {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }

            @Override
            public void onClickCancel() {

            }
        });
    }

    @Override
    public void onNetworkCallInitiated(String service) {
        if (mProgressDialog != null) {
            if (service.equalsIgnoreCase(API_GET_LABELS)) {
                if (showProgress)
                    mProgressDialog.show();
            } else {
                mProgressDialog.show();
            }
        }
    }

    @Override
    public void onNetworkCallCompleted(String service, String response) {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();

        if (service.equalsIgnoreCase(API_GET_PROFILE)) {
            GetProfileResponse getProfileResponse = GetProfileResponse.fromJson(response);
            if (getProfileResponse != null && getProfileResponse.success) {
                mAppPreferences.setUserId(getProfileResponse.userData.userId);
                String s = getProfileResponse.toJson();
                if (!Validator.isEmptyString(s)) {
                    mAppPreferences.setUserDetailPref(s);
                    setData(getProfileResponse);
                }
            }
        } else if (service.equalsIgnoreCase(API_GET_ALL_COUNTER)) {
            AllCounterResponse allCounterResponse = AllCounterResponse.fromJson(response);
            if (allCounterResponse != null && allCounterResponse.success) {
                if (!Validator.isEmptyString(allCounterResponse.data.receivedOfferCount))
                    mReceivedOffersCountTV.setText(allCounterResponse.data.receivedOfferCount);
                if (!Validator.isEmptyString(allCounterResponse.data.sentOfferCount))
                    mSentOffersCountTV.setText(allCounterResponse.data.sentOfferCount);

            }
        } else if (service.equalsIgnoreCase(API_GET_LABELS)) {
            GetLanguageResponse languageResponse = GetLanguageResponse.fromJson(response);
            if (languageResponse != null && languageResponse.data != null) {
                mAppPreferences.setLabels(languageResponse.data.toString());
                changeLabels();
            }
        }
    }

    @Override
    public void onNetworkCallError(String service, String errorMessage) {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(mSliderDrawerLayout)) {
            drawerLayout.closeDrawer(mSliderDrawerLayout);
            return;
        }
        resetDrawerItemsState(true);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment != null && fragment instanceof HomeFragment) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getResources().getString(R.string.press_again_exit), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
        }
    }

    private void resetDrawerItemsState(boolean isClose) {
        if (isClose)
            closeDrawer();
        mSentOfferTV.setSelected(false);
        mReceivedOfferTV.setSelected(false);
    }

    @Override
    public void setHeaderVisibility(String headerTitle) {

        if (null == headerHandler) {
            headerHandler = new HeaderHandler(toolbar_headerLayout);
        }
        headerHandler.setHeaderVisibility(headerTitle);
        resetDrawerItemsState(true);
        switch ("" + headerTitle) {
            case HeaderHandler.KEY_EMPTY:
                break;
            case HeaderHandler.SUBMIT_REQUEST:
                break;
            case HeaderHandler.EDIT_REQUEST:
                mReceivedOfferTV.setSelected(true);
                break;
            case HeaderHandler.MY_REQUEST:
                break;
            case HeaderHandler.SUBMIT_OFFER:
                break;
            case HeaderHandler.SENT_REQUEST:
                mSentOfferTV.setSelected(true);
                break;
            case HeaderHandler.RECEIVED_REQUEST:
                mReceivedOfferTV.setSelected(true);
                break;
            default:
                break;
        }
    }
}
