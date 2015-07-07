package com.virginia.edu.cs2110.ghosts;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class GetContextApp extends MainActivity{
     private static Context context;
     public void onCreate(){
       context=getApplicationContext();
     }

     public static Context getCustomAppContext(){
       return context;
     } 
   }
