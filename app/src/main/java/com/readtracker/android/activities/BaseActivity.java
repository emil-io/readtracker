package com.readtracker.android.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.widget.Toast;

import com.readtracker.android.ApplicationReadTracker;
import com.readtracker.android.support.ReadTrackerUser;

/** Base activity */
public class BaseActivity extends ActionBarActivity {
  protected final String TAG = this.getClass().getName();
  private ApplicationReadTracker mApplication;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mApplication = (ApplicationReadTracker) getApplication();
    requestWindowFeatures();

    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  }

  public final ApplicationReadTracker getApp() {
    return mApplication;
  }

  /**
   * Gets the currently logged in user.
   *
   * @return the currently logged in user or null.
   */
  public ReadTrackerUser getCurrentUser() {
    return getApp().getCurrentUser();
  }

  /**
   * Gets the readmill id of the current user.
   *
   * @return the id of the current user or -1 if no user is signed in.
   */
  public long getCurrentUserId() {
    ReadTrackerUser currentUser = getCurrentUser();
    return currentUser == null ? -1 : currentUser.getReadmillId();
  }

  /**
   * @return Internet connectivity status
   */
  protected boolean isNetworkAvailable() {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null;
  }

  /**
   * Override this method to change what features that gets requested for the activity.
   */
  protected void requestWindowFeatures() {
    // NOOP
  }

  /**
   * Display a short toast message to the user
   *
   * @param toastMessageId String resources to be displayed
   */
  protected void toast(int toastMessageId) {
    toast(getString(toastMessageId));
  }

  protected void toast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }

  /**
   * Display a long toast message to the user
   *
   * @param toastMessage Message to be displayed
   */
  protected void toastLong(String toastMessage) {
    Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
  }

  /**
   * Converts a device independent pixel value to pixels.
   *
   * @param dpValue The value in DIP
   * @return the value in pixels
   */
  public int getPixels(int dpValue) {
    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
  }
}