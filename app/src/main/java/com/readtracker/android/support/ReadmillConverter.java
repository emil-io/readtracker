package com.readtracker.android.support;

import com.readtracker.android.db.LocalHighlight;
import com.readtracker.android.db.LocalReading;
import com.readtracker.android.db.LocalSession;

import org.json.JSONException;
import org.json.JSONObject;

import static com.readtracker.android.support.ReadmillApiHelper.*;
import static com.readtracker.android.support.ReadmillApiHelper.parseISO8601;
import static com.readtracker.android.support.ReadmillApiHelper.toIntegerState;

/**
 * Converts data from Readmill to ReadTracker and vice versa.
 */
public class ReadmillConverter {
  public static final String TAG = ReadmillConverter.class.getName();

  /**
   * Creates a LocalReading instance from a Readmill reading JSON.
   * <p/>
   * The JSON must follow the format specified by:
   * http://developer.readmill.com/api/docs/v2/get/readings/id.html
   *
   * @param source source json
   * @return the created LocalReading instance
   * @throws org.json.JSONException if source was not in the required format
   */
  public static LocalReading createLocalReadingFromReadingJSON(JSONObject source) throws JSONException {
    LocalReading localReading = new LocalReading();
    mergeLocalReadingWithJSON(localReading, source);
    return localReading;
  }

  /**
   * Merges a LocalReading instance with information from a Readmill reading JSON.
   * <p/>
   * The JSON must follow the format specified by:
   * http://developer.readmill.com/api/docs/v2/get/readings/id.html
   *
   * @param localReading the local reading to merge into
   * @param source       source json
   * @throws org.json.JSONException if source was not in the required format
   */
  public static void mergeLocalReadingWithJSON(LocalReading localReading, JSONObject source) throws JSONException {
    guardNullSource(source);
    JSONObject jsonBook = source.getJSONObject("book");
    JSONObject jsonUser = source.getJSONObject("user");

    localReading.readmillReadingId = source.getLong("id");
    localReading.setTouchedAt(parseISO8601(source.getString("touched_at")));
    localReading.readmillState = toIntegerState(source.getString("state"));
    localReading.progress = source.getDouble("progress");
    localReading.setLastReadAt(localReading.getRemoteTouchedAt());
    localReading.readmillPrivate = source.getBoolean("private");
    localReading.setStartedAt(parseISO8601(source.getString("started_at")));

    localReading.readmillClosingRemark = optString("closing_remark", null, source);

    if(!localReading.hasClosedAt()) {
      localReading.setClosedAt(parseISO8601(source.getString("ended_at")));
    }

    // Extract book
    localReading.readmillBookId = jsonBook.getLong("id");
    localReading.title = optString("title", "Unknown title", jsonBook);
    localReading.author = optString("author", "Unknown author", jsonBook);
    localReading.coverURL = optString("cover_url", null, jsonBook);

    // Extract user
    localReading.readmillUserId = jsonUser.getLong("id");
  }

  /**
   * Merge an instance of a LocalHighlight with a Readmill highlight JSON.
   * <p/>
   * The JSON must follow the format specified by:
   * http://developer.readmill.com/api/docs/v2/get/highlights/id.html
   *
   * @param target The local highlight to merge with
   * @param source the Readmill highlight data to merge in
   * @throws org.json.JSONException if source is not in the required format
   */
  public static void mergeLocalHighlightWithJson(LocalHighlight target, JSONObject source) throws JSONException {
    guardNullSource(source);
    target.content = source.getString("content");
    target.readmillHighlightId = source.getLong("id");
    target.readmillReadingId = source.getJSONObject("reading").getLong("id");
    target.readmillUserId = source.getJSONObject("user").getLong("id");
    target.highlightedAt = parseISO8601(source.getString("highlighted_at"));
    target.readmillPermalinkUrl = source.getString("permalink_url");
    target.position = source.optDouble("position", 0.0);
    target.likeCount = source.optInt("likes_count", 0);
    target.commentCount = source.optInt("comments_count", 0);
  }

  /**
   * Creates a ReadingHighlight instance from a Readmill highlight JSON.
   * <p/>
   * The JSON must follow the format specified by:
   * http://developer.readmill.com/api/docs/v2/get/highlights/id.html
   *
   * @param source source json
   * @return the created ReadingHighlight
   * @throws org.json.JSONException if source was not in the required format
   */
  public static LocalHighlight createHighlightFromReadmillJSON(JSONObject source) throws JSONException {
    LocalHighlight target = new LocalHighlight();
    mergeLocalHighlightWithJson(target, source);
    return target;
  }

  /**
   * Creates a ReadingSession instance from a Readmill Reading Period.
   *
   * @param source the json object representing the readmill period
   * @return the ReadingSession instance
   * @throws org.json.JSONException if source was not in the required format
   */
  public static LocalSession createReadingSessionFromReadmillPeriod(JSONObject source) throws JSONException {
    guardNullSource(source);
    LocalSession session = new LocalSession();
    session.durationSeconds = source.getLong("duration");
    session.occurredAt = parseISO8601(source.getString("started_at"));
    session.progress = source.getDouble("progress");
    session.readmillReadingId = source.getJSONObject("reading").getLong("id");
    session.sessionIdentifier = source.getString("identifier");

    return session;
  }

  /**
   * Work around to get a string response from JSON, avoiding the
   * (intentional) JSON string bug.
   *
   * Use this method instead of JSObject.optString()
   *
   * @link http://code.google.com/p/android/issues/detail?id=13830
    */
  public static String optString(String key, String opt, JSONObject source) throws JSONException {
    return source.isNull(key) ? opt : source.getString(key);
  }

  /**
   * Guard against the argument being null by throwing an exception
   *
   * @param jsonObject The object to guard against null
   * @throws org.json.JSONException if the object is null
   */
  private static void guardNullSource(JSONObject jsonObject) throws JSONException {
    if(jsonObject == null) {
      throw new JSONException("Received NULL object");
    }
  }
}