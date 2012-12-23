package com.readtracker.support;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Represents a current timing session.
 * A timing session is measured by two parts:
 * - the elapsed time, in milliseconds, up until the timer was last stopped
 * - a timestamp when the timer was last started
 *
 * The timer is considered active when a timestamp since last started is present.
 */
public class SessionTimer implements Parcelable {
  private long mElapsedBeforeTimestamp = 0;
  private long mActiveTimestamp;

  public SessionTimer() {
    mElapsedBeforeTimestamp = 0;
    mActiveTimestamp = 0;
  }

  public SessionTimer(int localReadingId, long elapsedMilliseconds, long activeTimestamp) {
    mElapsedBeforeTimestamp = elapsedMilliseconds;
    mActiveTimestamp = activeTimestamp;
  }

  public long getElapsedBeforeTimestamp() {
    return mElapsedBeforeTimestamp;
  }

  public long getActiveTimestamp() {
    return mActiveTimestamp;
  }

  /**
   * Indicate if the timing is active or not
   *
   * @return true if active, false otherwise
   */
  public boolean isActive() {
    return mActiveTimestamp > 0;
  }

  /**
   * Get the total elapsed time, including any time passed since the
   * active timestamp.
   */
  public long getTotalElapsed() {
    return getTotalElapsed(System.currentTimeMillis());
  }

  public long getTotalElapsed(long now) {
    final long elapsedSinceTimestamp = isActive() ? (mActiveTimestamp - now) : 0;
    return mElapsedBeforeTimestamp + elapsedSinceTimestamp;
  }

  public String toString() {
    return String.format("SessionTimer: Elapsed: %dms (%s)", mElapsedBeforeTimestamp, (isActive() ? "Active, started: " + new Date(mActiveTimestamp) : "Inactive"));
  }

  // Parcelable interface

  public static Parcelable.Creator<SessionTimer> CREATOR = new Parcelable.Creator<SessionTimer>() {
    @Override
    public SessionTimer createFromParcel(Parcel parcel) {
      return new SessionTimer(parcel);
    }

    @Override public SessionTimer[] newArray(int size) {
      return new SessionTimer[size];
    }
  };

  public SessionTimer(Parcel parcel) {
    mElapsedBeforeTimestamp = parcel.readLong();
    mActiveTimestamp = parcel.readLong();
  }

  @Override public int describeContents() { return 0; }

  @Override public void writeToParcel(Parcel parcel, int flags) {
    parcel.writeLong(mElapsedBeforeTimestamp);
    parcel.writeLong(mActiveTimestamp);
  }

  /**
   * Pauses the reading state.
   */
  public void pause() {
    pause(System.currentTimeMillis());
  }

  /**
   * Pauses the reading state.
   */
  public void pause(long now) {
    if(isActive()) {
      final long elapsedSinceTimeStamp = now - mActiveTimestamp;
      mElapsedBeforeTimestamp += elapsedSinceTimeStamp;
      mActiveTimestamp = 0;
    }
  }

  /**
   * Start timing
   */
  public void start() {
    start(System.currentTimeMillis());
  }

  public void start(long now) {
    mActiveTimestamp = now;
  }
}