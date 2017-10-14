package com.example.parth.livetv.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.KeyStore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    /**
     * Method to check whether Internet is connected or not.
     *
     * @return void
     */
    public static boolean isInternetConnected(Context mContext)

    {
        ConnectivityManager connectivity = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            final NetworkInfo activeNetwork = connectivity
                    .getActiveNetworkInfo();

            if (activeNetwork != null && activeNetwork.isConnected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns current version of application.
     *
     * @param context
     * @return
     */
    public static String getVersionCode(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("CA", "" + e);
        }
        return info.versionName;
    }


    /**
     * Convert Input stream into String
     *
     * @param is
     * @return
     */
    public static String getStringfromInputStream(InputStream is) {

        String result = null;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
//            Log.e("StringfromInputStream", "" + e);
        }
        return result;

    }

    /**
     * This method return whether the cvv is number or not
     *
     * @param number
     * @return boolean
     */
    public static boolean isOnlyNumer(String number) {

        Pattern pattern = null;
        Matcher matcher;
        final String regex = "\\+?\\d+";
        if (number != null && !number.isEmpty()) {
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(number);
            return matcher.matches();
        }
        return false;

    }

    /**
     * This methods is used to call http get method and returns the response as a String
     *
     * @param conUrl
     * @return String
     * @throws Exception
     */
    public static String getMethod(String conUrl) {
        Log.i("URL", conUrl);
        String result = null;
        InputStream is = null;
        try {
            HttpGet httpGet = new HttpGet(conUrl);
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("Accept", "application/json, text/javascript, */*;q=0.01");
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            result = getStringfromInputStream(is);
            if (entity != null) {
                entity.consumeContent();
            }
//            Log.e("TV", result + "");
            httpClient.getConnectionManager().shutdown();
        } catch (Exception e) {
//            Log.e("getMethod", "" + e);
        }


        return result;

    }

    public static String postMethod(String conUrl, JSONObject jsonValues)
            throws Exception {
        String result = null;
        InputStream is = null;
        int timeoutConnection = 10000;
        try {
//            HttpClient httpclient = new DefaultHttpClient();
//
//            HttpParams httpParameters = new BasicHttpParams();
//            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
//
            HttpClient httpclient = createHttpClient();
//            Log.e("marksheet App", conUrl);

            URI u = new URI(conUrl);
            HttpPost httppost = new HttpPost(u);

            httppost.setHeader("Content-Type", "application/json");
            httppost.setHeader("Accept", "application/json, text/javascript, */*;q=0.01");
            httppost.setEntity(new ByteArrayEntity(jsonValues.toString()
                    .getBytes()));

            HttpResponse response = httpclient.execute(httppost);

            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            result = getStringfromInputStream(is);
//            Log.e("marksheet App", "Result: " + result);
            if (entity != null) {
                entity.consumeContent();
            }
            httpclient.getConnectionManager().shutdown();
            return result;

        } catch (ConnectTimeoutException e) {

//            Log.e("TAg", "postMethod Exception: " + e);
        }catch (Exception e){
//            return "error";
            e.printStackTrace();
        }
        return result;

    }

    public static HttpClient createHttpClient(){
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 15000);
            HttpConnectionParams.setSoTimeout(params, 5000);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            e.printStackTrace();
            return new DefaultHttpClient();
        }
    }

    public static boolean isAboveV21() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        return currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP;
    }


}
