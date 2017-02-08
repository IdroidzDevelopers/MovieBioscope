package com.lib.cloud.databases;


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

public class CloudProvider extends ContentProvider {

    private static final String TAG = CloudProvider.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static final String DATABASE_NAME = "bioscope.db";
    public static final String TABLE_CLOUD = "cloud_table";
    private static final int DATABASE_VERSION = 1;

    public static final String AUTHORITY = "com.lib.cloud.contentprovider.database.CloudProvider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI_CLOUD_TABLE = Uri.parse(CONTENT_URI + "/" + TABLE_CLOUD);
    public DatabaseHelper mDbHelper;
    private static final UriMatcher sUriMatcher;


    public interface COLUMNS {
        String ID = "_id";
        String CLOUD_ID = "cloud_id";
        String DATA = "data";
        String STATUS = "status";//received , processed etc
        String CLOUD_SEND_TIME = "cloud_time";
        String CREATED_TIME = "created_time";
    }

    private static final String CREATE_CLOUD_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CLOUD + "(" + COLUMNS.ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + COLUMNS.CLOUD_ID + " TEXT," + COLUMNS.DATA + " TEXT," + COLUMNS.STATUS + " TEXT," + COLUMNS.CLOUD_SEND_TIME + " TEXT," + COLUMNS.CREATED_TIME + " TEXT " + ")";

    private static final int CASE_CLOUD_TABLE = 1;
    private static final int CASE_DEFAULT = 3;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, TABLE_CLOUD, CASE_CLOUD_TABLE);
        sUriMatcher.addURI(AUTHORITY, "/*", CASE_DEFAULT);
    }


    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CASE_CLOUD_TABLE:
                return AUTHORITY + "/" + TABLE_CLOUD;
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
        db.execSQL(CREATE_CLOUD_TABLE);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor lCursor = null;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CASE_CLOUD_TABLE:
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
            case CASE_CLOUD_TABLE:
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
            case CASE_CLOUD_TABLE:
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
            case CASE_CLOUD_TABLE:
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

