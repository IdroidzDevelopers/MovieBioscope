package com.lib.firebase.database;


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

public class FirebaseProvider extends ContentProvider {

    private static final String TAG = FirebaseProvider.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static final String DATABASE_NAME = "bioscope.db";
    public static final String FIREBASE_TOPICS_TABLE = "firebase_topics_table";
    public static final String FIREBASE_DATA_TABLE = "firebase_data_table";
    private static final int DATABASE_VERSION = 1;

    public static final String AUTHORITY = "com.lib.firebase.contentprovider.database.FirebaseProvider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI_FIREBASE_TOPICS_TABLE = Uri.parse(CONTENT_URI + "/" + FIREBASE_TOPICS_TABLE);
    public static final Uri CONTENT_URI_FIREBASE_DATA_TABLE = Uri.parse(CONTENT_URI + "/" + FIREBASE_DATA_TABLE);
    public DatabaseHelper mDbHelper;
    private static final UriMatcher sUriMatcher;


    public interface FIREBASECOLUMNS {
        String FIREBASE_TOPIC = "firebase_topic";
    }

    public interface FIREBASEDATACOLUMNS {
        String APP_NAME = "APP_NAME";
        String DATA = "data";
        String SENT_TIME = "sent_time";
        String RECEIVED_TIME = "received_time";
    }

    private static final String CREATE_FIREBASE_TOPIC_TABLE = "CREATE TABLE IF NOT EXISTS "
            + FIREBASE_TOPICS_TABLE + "(" + FIREBASECOLUMNS.FIREBASE_TOPIC + " TEXT " + ")";

    private static final String CREATE_FIREBASE_DATA_TABLE = "CREATE TABLE IF NOT EXISTS "
            + FIREBASE_DATA_TABLE + "(" + FIREBASEDATACOLUMNS.APP_NAME + " TEXT ,"
            + FIREBASEDATACOLUMNS.DATA + " TEXT ," + FIREBASEDATACOLUMNS.SENT_TIME + " INTEGER ," + FIREBASEDATACOLUMNS.RECEIVED_TIME + " INTEGER " + ")";

    private static final int CASE_FIREBASE_TOPIC_TABLE = 2;
    private static final int CASE_FIREBASE_DATA_TABLE = 3;
    private static final int CASE_DEFAULT = 5;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, FIREBASE_TOPICS_TABLE, CASE_FIREBASE_TOPIC_TABLE);
        sUriMatcher.addURI(AUTHORITY, FIREBASE_DATA_TABLE, CASE_FIREBASE_DATA_TABLE);
        sUriMatcher.addURI(AUTHORITY, "/*", CASE_DEFAULT);
    }


    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CASE_FIREBASE_TOPIC_TABLE:
                return AUTHORITY + "/" + FIREBASE_TOPICS_TABLE;
            case CASE_FIREBASE_DATA_TABLE:
                return AUTHORITY + "/" + FIREBASE_DATA_TABLE;
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
        db.execSQL(CREATE_FIREBASE_TOPIC_TABLE);
        db.execSQL(CREATE_FIREBASE_DATA_TABLE);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor lCursor = null;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CASE_FIREBASE_TOPIC_TABLE:
                queryBuilder.setTables(uri.getLastPathSegment());
                lCursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CASE_FIREBASE_DATA_TABLE:
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
            case CASE_FIREBASE_TOPIC_TABLE:
                lRowId = lDb.insertOrThrow(uri.getLastPathSegment(), null, values);
                break;
            case CASE_FIREBASE_DATA_TABLE:
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
            case CASE_FIREBASE_TOPIC_TABLE:
                count = db.delete(uri.getLastPathSegment(), selection, selectionArgs);
                break;
            case CASE_FIREBASE_DATA_TABLE:
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
            case CASE_FIREBASE_TOPIC_TABLE:
                lCount = lDb.update(uri.getLastPathSegment(), values, selection, selectionArgs);
                break;
            case CASE_FIREBASE_DATA_TABLE:
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

