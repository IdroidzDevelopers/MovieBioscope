package com.lib.location.databases;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class LocationProvider extends ContentProvider {

    private static final String TAG = LocationProvider.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static final String DATABASE_NAME = "bioscope.db";
    public static final String TABLE_LOCATION = "location_table";
    private static final int DATABASE_VERSION = 1;

    public static final String AUTHORITY = "com.lib.location.contentprovider.database.LocationProvider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI_LOCATION_TABLE = Uri.parse(CONTENT_URI + "/" + TABLE_LOCATION);
    public DatabaseHelper mDbHelper;
    private static final UriMatcher sUriMatcher;


    public interface LOCATION_COLUMNS {
        String ID = "_id";
        String SOURCE = "source";
        String DESTINATION = "destination";
        String CURRENT_LOCATION = "current_location";
        String TOTAL_DISTANCE = "total_distance";
        String TOTAL_TIME = "total_time";
        String DISTANCE_TO_SOURCE = "distance_to_source";
        String DISTANCE_TO_DESTINATION = "distance_to_destination";
        String TIME_TO_DESTINATION = "time_to_destination";
        String LAST_SYNC_TIME = "last_sync_time";
    }

    private static final String CREATE_LOCATION_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_LOCATION + "(" + LOCATION_COLUMNS.ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + LOCATION_COLUMNS.SOURCE + " TEXT," + LOCATION_COLUMNS.DESTINATION + " TEXT," + LOCATION_COLUMNS.CURRENT_LOCATION + " TEXT," + LOCATION_COLUMNS.TOTAL_DISTANCE + " TEXT," + LOCATION_COLUMNS.TOTAL_TIME + " TEXT," + LOCATION_COLUMNS.DISTANCE_TO_SOURCE + " TEXT," + LOCATION_COLUMNS.DISTANCE_TO_DESTINATION + " TEXT," + LOCATION_COLUMNS.TIME_TO_DESTINATION + " TEXT," + LOCATION_COLUMNS.LAST_SYNC_TIME + " TEXT" + ")";


    private static final int CASE_LOCATION_TABLE = 1;
    private static final int CASE_DEFAULT = 3;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, TABLE_LOCATION, CASE_LOCATION_TABLE);
        sUriMatcher.addURI(AUTHORITY, "/*", CASE_DEFAULT);
    }


    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CASE_LOCATION_TABLE:
                return AUTHORITY + "/" + TABLE_LOCATION;
            case CASE_DEFAULT:
                return AUTHORITY + "/*";
            default:
                return null;
        }
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(CREATE_LOCATION_TABLE);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor lCursor = null;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CASE_LOCATION_TABLE:
                queryBuilder.setTables(uri.getLastPathSegment());
                lCursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                break;
        }
        return lCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase lDb = mDbHelper.getWritableDatabase();
        Uri lInsertedUri = null;
        long lRowId = 0;
        switch (sUriMatcher.match(uri)) {
            case CASE_LOCATION_TABLE:
                lRowId = lDb.insertOrThrow(uri.getLastPathSegment(), null, values);
                break;

            default:
                break;
        }
        if (lRowId > 0) {
            lInsertedUri = ContentUris.withAppendedId(uri, lRowId);
        }
        if (DEBUG) Log.d(TAG, "inserted uri is " + lInsertedUri);
        return lInsertedUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CASE_LOCATION_TABLE:
                count = db.delete(uri.getLastPathSegment(), selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int lCount = 0;
        SQLiteDatabase lDb = mDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CASE_LOCATION_TABLE:
                lCount = lDb.update(uri.getLastPathSegment(), values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }
        if (DEBUG) Log.d(TAG, "updated lCount  " + lCount);
        return lCount;
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {


        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}

