package edu.upenn.cis350.groupproject;

import com.parse.Parse;
import android.app.Application;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Setup a parse for the app on heroku
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("myAppId")
                // if desired
                .clientKey("myMasterKey")
                .server("https://groupproject8.herokuapp.com")
                .build();
        Parse.initialize(configuration);
    }
}