package com.hans.svandasek.fire.vyjezdy.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.StyleSpan;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hans.svandasek.fire.vyjezdy.ui.activities.MyApplication;
import com.hans.svandasek.fire.vyjezdy.ui.activities.NotificationArticleActivity;
import com.hans.svandasek.fire.vyjezdy.utils.DateUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    Context mContext;
    public String notify_url;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);

        if(Looper.myLooper() == Looper.getMainLooper()) {
            // Current Thread is Main Thread.
            Log.e("THREADING", "Current thread is main");
        }

        Log.d(TAG, "From: " + remoteMessage.getFrom());



        if (remoteMessage.getData().size() > 0) {
                String title_all = remoteMessage.getData().get("title");
                String time1 = remoteMessage.getData().get("time");
                String hours = remoteMessage.getData().get("time");
                notify_url = remoteMessage.getData().get("url");

            Log.e("URL", "this is the article url" + notify_url);

            Log.d(TAG, "Dostal jsem data, " + title_all + " time " + time1);

                try {
                    time1 = new DateUtil().getDate(time1);
                } catch (ParseException e) {
                    e.printStackTrace();
                    time1 = "Datum nedostupné";
                }


                Date d = new Date();
                CharSequence s  = DateFormat.format("d. MMMM yyyy", d.getTime());

                if (time1.contains(s)) {
                    try {
                        hours = new DateUtil().getTime(hours);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        hours = "Čas nedostupen";
                    }
                    time1 = "Dnes v " + hours;
                }




            String pozar = "Požár";
            String ostatni = "Ostatní";
            String dn = "Dopravní nehoda";
            String tp = "Technická pomoc";
            String zoz = "Záchrana osob/zvířat";
            String ch = "Chemické látky";

            String category = "";
            if (title_all.contains(pozar)) {
                category = pozar;
            } else if (title_all.contains(ostatni)){
                category = ostatni;
            } else if (title_all.contains(dn)){
                category = dn;
            } else if (title_all.contains(tp)){
                category = tp;
            } else if (title_all.contains(zoz)){
                category =  zoz;
            } else if (title_all.contains(ch)){
                category = ch;
            }


            //Přidělení okresu
            String blansko = "Blansko";
            String b_mesto = "Brno-město";
            String b_venkov = "Brno-venkov";
            String breclav = "Břeclav";
            String hodonin = "Hodonín";
            String vyskov = "Vyškov";
            String znojmo = "Znojmo";

            String okres = "";
            if (title_all.contains(blansko)) {
                okres = blansko;
            } else if (title_all.contains(b_mesto)){
                okres = b_mesto;
            } else if (title_all.contains(b_venkov)){
                okres = b_venkov;
            } else if (title_all.contains(breclav)){
                okres = breclav;
            } else if (title_all.contains(hodonin)){
                okres =  hodonin;
            } else if (title_all.contains(vyskov)){
                okres =  vyskov;
            } else if (title_all.contains(znojmo)){
                okres = znojmo;
            }

            String titlex = title_all
                    .replace("Požár - ","")
                    .replace("Ostatní - ","")
                    .replace("Dopravní nehoda - ", "")
                    .replace("Technická pomoc - ","")
                    .replace("Záchrana osob/zvířat - ","")
                    .replace("Chemické látky - ","")
                    .replace(" - Břeclav", "")
                    .replace(" - Blansko", "")
                    .replace(" - Brno-město", "")
                    .replace(" - Brno-venkov", "")
                    .replace(" - Hodonín", "")
                    .replace(" - Vyškov", "")
                    .replace(" - Znojmo", "")
                    .replace(" - Břeclav", "");


            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("title", titlex);
            edit.putString("time1", time1);
            edit.putString("url", notify_url);
            edit.putString("category", category);
            edit.putString("okres", okres);
            edit.apply();

            NotificationHelper notificationHelper = new NotificationHelper(MyApplication.getAppContext());
            notificationHelper.createNotification("Nový výjezd:" + " " + category + ", " + titlex, time1);


            new ArticleAsyncLoader(notify_url).execute();
        }

    }

    public class ArticleAsyncLoader extends AsyncTask<String, Integer, String> {

        String mUrl, mErrorMsg;

        public ArticleAsyncLoader(String mUrl) {
            this.mUrl = notify_url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document htmlDocument = Jsoup.connect(mUrl).get();



                Elements mPopis = htmlDocument.select("p:contains(Popis:)");
                String popis = mPopis.text();

                Elements mJednotky = htmlDocument.select("p:contains(Jednotky:)");
                String jednotky = mJednotky.text();

                Elements mObec = htmlDocument.select("p:contains(Obec:)");
                String obec = mObec.text();

                Elements mCastobce = htmlDocument.select("p:contains(Část Obce:)");
                String castobce = mCastobce.text();

                Elements mUlice = htmlDocument.select("p:contains(Ulice:)");
                String ulice = mUlice.text();

                Elements mStav = htmlDocument.select("p:contains(Stav:)");
                String stav = mStav.text();

                String content = String.join("\n\n", popis, jednotky, obec, castobce, ulice, stav);

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(MyFirebaseMessagingService.this);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("content", content);
                edit.apply();

                return "success";

            } catch (IOException e) {
                e.printStackTrace();
                mErrorMsg = e.getMessage();
                return "failure";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("success")) {
                Log.i("NotifyParse", "Succeeded" );

            } else if (s.equals("failure")) {
                Log.i("NotifyParse", "Failed" + mErrorMsg);
            }
            super.onPostExecute(s);
        }
    }
}