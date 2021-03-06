package com.hhn.mick.emotionanalizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.hhn.paulc.twittersentimentanalysis.Settings;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Mick on 27.04.2017.
 */
public class AnalizationHelper {

    public static final String TWITTER_EXPORTS_FOLDER = "twitter_exports";
    public static final String TWITTER_DEFAULT_PROJECT_FOLDER = "twitter_results";

    /**
     * this is true, while savin is in progress - so that we are able to block a new analization while saving
     */
    private boolean isBlocked = false;

    /**
     * steps saved for the live timeline
     */
    public static final int MAX_HISTORY_COUNT = 20;


    /**
     * represents the folder name, e.g. "twitter_results", which is the folder, where all files for
     * the history are saved to and loaded from.
     */
    private String analyzation_folder= "twitter_results";

    private static AnalizationHelper instance = new AnalizationHelper();


    public static void recreate(){
        instance = new AnalizationHelper();
    }

    private AnalizationHelper(){
    }

    public void loadSettings(Context a){
        SharedPreferences sharedPref = a.getSharedPreferences(Settings.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        Log.d("AppD","refresh settings");
        final String consumerkeytext = sharedPref.getString("consumerkey", "").trim();
        final String consumerkeytextscrt = sharedPref.getString("consumerkeyscrt", "").trim();
        final String accesstokentext = sharedPref.getString("accesstoken", "").trim();
        final String accesstokentextscrt = sharedPref.getString("accesstokenscrt", "").trim();
        String folderX = sharedPref.getString("folder",TWITTER_DEFAULT_PROJECT_FOLDER).trim();
        final String folder = TextUtils.isEmpty(folderX) ? TWITTER_DEFAULT_PROJECT_FOLDER : folderX;



        this.setAccessToken(accesstokentext);
        this.setAccessTokenSecret(accesstokentextscrt);
        this.setConsumerKey(consumerkeytext);
        this.setConsumerSecret(consumerkeytextscrt);
        this.setAnalyzation_folder(folder);
    }

    public static AnalizationHelper INSTANCE(){
        return AnalizationHelper.instance;
    }

    /**
     * true, if date is saved aka history data.
     * also true at startup, because there is no data to save
     */
    private boolean isSaved= true;

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    private volatile boolean isInitialized = false;

    private Object lock = new Object();
    private Object lock2 = new Object();

    private volatile AnalizationResult finalResult = null;
    private volatile LinkedList<AnalizationResult> result_steps = null;




    public String getAnalyzation_folder() {
        return this.analyzation_folder;
    }

    /**
     *
     * @param analyzation_folder
     */
    public void setAnalyzation_folder(String analyzation_folder) {
        this.analyzation_folder = analyzation_folder;
    }


    public synchronized AnalizationResult getFinalResult() {
        synchronized (lock) {
            return finalResult;
        }
    }

    /**
     * necessary for injecting history file
     * @param ar
     * @return
     */
    public synchronized AnalizationResult setFinalResult(AnalizationResult ar) {
        synchronized (lock) {
            return finalResult = ar;
        }
    }

    public synchronized AnalizationResult[] getSteps(){
        synchronized (lock2){
            return this.result_steps.toArray(new AnalizationResult[this.result_steps.size()]);
        }
    }

    public synchronized void addNextResultToFinalResult(AnalizationResult lastResult) {
        synchronized (lock) {
            this.finalResult.Add(lastResult);
        }
        synchronized (lock2){
            lastResult.finalize();
            this.result_steps.addLast(lastResult);

            if(this.result_steps.size() > MAX_HISTORY_COUNT){
                this.result_steps.removeFirst();
            }
        }
    }

    public void init(Context context){
        if(this.isInitialized) return;
        this.isInitialized = true;
        Log.d("analizer","init analizer");

        Log.d("analizer","init analizer successfull");


    }

    private boolean isRunning = false;


    private String consumerKey = "";
    private String consumerSecret = "";
    private String accessToken = "";
    private String AccessTokenSecret = "";

    private TwitterCrawler twitterCrawler = null;

    public TwitterCrawler getTwitterCrawler(){
        return this.twitterCrawler;
    }


    public boolean isBlocked() {
        return isRunning;
    }

    public void setBlocked(boolean isBlocked){
        this.isBlocked = isBlocked;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessTokenSecret() {
        return AccessTokenSecret;
    }

    public void setAccessTokenSecret(String accessTokenSecret) {
        AccessTokenSecret = accessTokenSecret;
    }

    public void stopAnalization() {
        this.twitterCrawler.stop();
        this.finalResult.finalize();
        this.isRunning = false;
        this.twitterCrawler = null;

       // Log.d("analize",this.getFinalResult().toJSON());
        EmotionAnalizer.Recreate();
    }

    private AnalizationResult currentResult = null;


    public  void startAnalization(String keywords,String keywordsProhibited, Context c){
        Log.d("analizer","start analizer!");

        EmotionAnalizer.INSTANCE.init(c);

        //split keywords to tokens and clean
        String[] kw = null;

        if(keywords != null && keywords.length()>0) {
            ArrayList<String> processKewords = new ArrayList<String>();
            kw = keywords.toLowerCase().split(",");
            for (int i = 0; i < kw.length; i++) {
               // kw[i] = kw[i].trim();
                String cur = kw[i].trim();
                if(cur != null && cur.length() > 0 && !processKewords.contains(cur)) {
                    processKewords.add(cur);
                }
            }
            kw = processKewords.toArray(new String[processKewords.size()]);
        }

        String[] kwP = null;
        if(keywordsProhibited != null && keywordsProhibited.length()>0) {
            ArrayList<String> processProhibitedKewords = new ArrayList<String>();
            kwP = keywordsProhibited.toLowerCase().split(",");
            for (int i = 0; i < kwP.length; i++) {
                // kw[i] = kw[i].trim();
                String cur = kwP[i].trim();
                if(cur != null && cur.length() > 0 && !processProhibitedKewords.contains(cur)) {
                    processProhibitedKewords.add(cur);
                }
            }
            kwP = processProhibitedKewords.toArray(new String[processProhibitedKewords.size()]);
        }

        synchronized (lock) {
            this.finalResult = new AnalizationResult(kw,kwP);
        }
        synchronized (lock2) {
            this.result_steps = new LinkedList<AnalizationResult>();
        }
        this.twitterCrawler = new TwitterCrawler();

        this.isRunning = true;
        try {
            this.twitterCrawler.start(kw,kwP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public boolean isInitialized() {
        return isInitialized;
    }


}