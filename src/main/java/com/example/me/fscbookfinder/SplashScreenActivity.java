package com.example.me.fscbookfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class SplashScreenActivity extends AppCompatActivity {

    private int TIMER = 4; //stores the no. of seconds for which the splash screen activity runs
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //the above two lines of code is to display logo to the entire screen i.e to hide the task bar and show full screen the logo
        requestWindowFeature(getWindow().FEATURE_NO_TITLE); //this will remove the title bar which displays the app name
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // this will make the splash screen as full screen

        setContentView(R.layout.activity_splash_screen);

        LogoLauncher launch = new LogoLauncher();
        launch.start(); //this will start the LogoLauncher class thread
    }

    //the below class is the execution of logo splash screen
    private class LogoLauncher extends Thread{
        public void run(){
            try{
                sleep(1000 * TIMER); //this means the screen will comes to sleep after time mention as a parameter
            }catch(InterruptedException e){
                e.printStackTrace();
            }

            Intent intent = new Intent(SplashScreenActivity.this, LogInActivity.class);
            //to switch to the next activity, which in this case is SignInActivity activity
            startActivity(intent);

            SplashScreenActivity.this.finish();
            //to destroy the splash activity once it switch to another activity, so that the user cannot return to the splash activity when the back button pressed or another activity ends.
        }
    }
}
