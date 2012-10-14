package com.readtracker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.readtracker.db.LocalHighlight;
import com.readtracker.db.LocalReading;
import com.readtracker.db.LocalSession;
import com.readtracker.value_objects.ReadingState;

import java.util.ArrayList;

/**
 * Handles the fragments for the book activity
 */
public class BookFragmentAdapter extends FragmentStatePagerAdapter {
  private static final String TAG = BookFragmentAdapter.class.getName();
  private boolean mBrowseMode;

  private FragmentRead mFragmentReadInstance;

  private LocalReading mLocalReading;
  private ArrayList<LocalSession> mLocalSessions;
  private ArrayList<LocalHighlight> mLocalHighlights;
  private ReadingState mReadingState;

  public BookFragmentAdapter(FragmentManager fm, ActivityBook.LocalReadingBundle bundle) {
    super(fm);
    setBundle(bundle);
  }

  public void setBundle(ActivityBook.LocalReadingBundle bundle) {
    mLocalReading = bundle.localReading;
    mLocalSessions = bundle.localSessions;
    mLocalHighlights = bundle.localHighlights;
  }

  @Override
  public Fragment getItem(int position) {
    if(position == getSessionsPageIndex()) {
      return FragmentSessions.newInstance(mLocalReading, mLocalSessions);
    } else if(position == getReadingPageIndex()) {
      // Keep a reference to the FragmentRead since we want the ability to
      // interrogate for the current session state
      long initialElapsed = mReadingState == null ? 0 : mReadingState.getElapsedMilliseconds();
      mFragmentReadInstance = (FragmentRead) FragmentRead.newInstance(mLocalReading, initialElapsed);
      return mFragmentReadInstance;
    } else if(position == getHighlightsPageIndex()) {
      return FragmentHighlight.newInstance(mLocalHighlights);
    }
    return null;
  }

  @Override
  public int getCount() {
    return mBrowseMode ? 2 : 3;
  }

  @Override public CharSequence getPageTitle(int position) {
    if(position == getSessionsPageIndex()) {
      return "Summary";
    } else if(position == getReadingPageIndex()) {
      return "Read";
    } else if(position == getHighlightsPageIndex()) {
      return "Highlights";
    }
    return "";
  }

  public int getSessionsPageIndex() {
    return 0;
  }

  public int getReadingPageIndex() {
    return mBrowseMode ? -1 : 1;
  }

  public int getHighlightsPageIndex() {
    return mBrowseMode ? 1 : 2;
  }

  public void setBrowserMode(boolean browserMode) {
    mBrowseMode = browserMode;
  }

  /**
   * Get the current reading state
   *
   * @return the current reading state as a value object or null
   */
  public ReadingState getReadingState() {
    if(mFragmentReadInstance != null) {
      return mFragmentReadInstance.getReadingState();
    }
    return null;
  }

  /**
   * Sets the current reading state
   *
   * @param readingState reading state value object to extract state from
   */
  public void setReadingState(ReadingState readingState) {
    mReadingState = readingState;
  }
}
