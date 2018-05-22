package com.ossul.network;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.ossul.BuildConfig;
import com.ossul.appconstant.AppConstants;
import com.ossul.apppreferences.AppPreferences;
import com.ossul.interfaces.INetworkEvent;
import com.ossul.network.constants.AppNetworkConstants;
import com.ossul.network.model.NetworkModel;
import com.ossul.network.parser.ParserKeys;
import com.ossul.utility.Validator;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author Rajan Tiwari
 */
public class NetworkService {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String mService;
    private String mMethod;
    private String action;
    private INetworkEvent networkEvent;
    private File imageFile = null;
    private RequestBody mRequestBody = null;


    public NetworkService(String serviceName, String method, INetworkEvent networkEvent) {
        mService = serviceName;
        mMethod = method;
        this.networkEvent = networkEvent;
    }


    public void setService(String serviceName) {
        mService = serviceName;
    }

    public void call(NetworkModel input) {
        new NetworkTask().execute(input);
    }

    public void call(NetworkModel input, File imageFile) {
        this.imageFile = imageFile;
        new NetworkTask().execute(input);
    }


    public void setRequestBody(RequestBody requestBody) {
        this.mRequestBody = requestBody;
    }

    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setSslSocketFactory(sslSocketFactory);
            okHttpClient.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class NetworkTask extends AsyncTask<NetworkModel, Void, String> {

        boolean isError = false;
        String message = "";

        @Override
        protected void onPreExecute() {

            if (networkEvent != null) {
                networkEvent.onNetworkCallInitiated(mService);
            }
        }

        @Override
        protected String doInBackground(NetworkModel... networkModels) {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            NetworkModel input = networkModels[0];
            int responseCode = -1;
            try {
                String service = NetworkService.this.mService.split(",")[0];

                final OkHttpClient client = getUnsafeOkHttpClient();

                client.setReadTimeout(30, TimeUnit.SECONDS);

//                client.networkInterceptors().add(new StethoInterceptor());
                if (!service.startsWith("http"))
                    service = AppNetworkConstants.BASE_URL + service;
                String jsonReq = null;
                if (input != null)
                    jsonReq = input.getJsonBody();

                String token = AppPreferences.get().getToken();
//                token = "a50f87f60640f4b55057cbdc0f99aa19";

                Log.e("token=>>", token);
                RequestBody requestBody;
                if (imageFile != null) {
                    requestBody = mRequestBody;
                } else {
                    if (mRequestBody == null)
                        requestBody = RequestBody.create(JSON, jsonReq);
                    else requestBody = mRequestBody;
                }

                Log.e("JSON ", "Request==>  " + mService);

                Request request = null;
                if (mMethod.equalsIgnoreCase(AppConstants.METHOD_POST)) {
                    if (!Validator.isEmptyString(token))
                        request = new Request.Builder()
                                .url(service)
                                .post(requestBody)
                                .addHeader(ParserKeys.token.toString(), token)
                                .addHeader(ParserKeys.version.toString(), BuildConfig.VERSION_NAME)
                                .addHeader(ParserKeys.lang.toString(), AppPreferences.get().getPrefLang())
                                .build();
                    else
                        request = new Request.Builder()
                                .url(service)
                                .post(requestBody)
                                .addHeader(ParserKeys.version.toString(), BuildConfig.VERSION_NAME)
                                .addHeader(ParserKeys.lang.toString(), AppPreferences.get().getPrefLang())
                                .build();
                } else if (mMethod.equalsIgnoreCase(AppConstants.METHOD_GET)) {
                    if (!Validator.isEmptyString(token)) {
                        request = new Request.Builder()
                                .url(service)
                                .get().addHeader(ParserKeys.token.toString(), token)
                                .get().addHeader(ParserKeys.version.toString(), BuildConfig.VERSION_NAME)
                                .addHeader(ParserKeys.lang.toString(), AppPreferences.get().getPrefLang())
                                .build();
                    } else {
                        request = new Request.Builder()
                                .url(service)
                                .addHeader(ParserKeys.version.toString(), BuildConfig.VERSION_NAME)
                                .addHeader(ParserKeys.lang.toString(), AppPreferences.get().getPrefLang())
                                .build();
                    }
                }

                Response response = client.newCall(request).execute();
                Log.e("response in NW", response.networkResponse().toString());
                if (response.isSuccessful()) {
                    isError = false;
                    return response.body().string();
                } else {
                    isError = true;
                    message = "response error";
                }

            } catch (UnsupportedEncodingException e) {
                isError = true;
                message = e.getMessage();
                e.printStackTrace();
            } catch (IOException e) {
                isError = true;
                message = e.getMessage();
                e.printStackTrace();
            } catch (android.net.ParseException e) {
                isError = true;
                message = e.getMessage();
                e.printStackTrace();
            }
            return responseCode + " " + message;


        }

        @Override
        protected void onPostExecute(String s) {
            if (networkEvent != null) {
                if (isError) {
                    networkEvent.onNetworkCallError(mService, message);
                } else {
                    networkEvent.onNetworkCallCompleted(mService, s);
                }
            }
        }
    }

}
