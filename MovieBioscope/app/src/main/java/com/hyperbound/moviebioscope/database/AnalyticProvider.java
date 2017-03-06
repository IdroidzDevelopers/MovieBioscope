package com.hyperbound.moviebioscope.database;


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

public class AnalyticProvider extends ContentProvider {

    private static final String TAG = AnalyticProvider.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static final String DATABASE_NAME = "bioscope.db";
    public static final String ANALYTIC_TABLE = "analytic_table";
    private static final int DATABASE_VERSION = 1;

    public static final String AUTHORITY = "com.hyperbound.moviebioscope.contentprovider.database.AnalyticProvider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI_ANALYTIC_TABLE = Uri.parse(CONTENT_URI + "/" + ANALYTIC_TABLE);
    public DatabaseHelper mDbHelper;
    private static final UriMatcher sUriMatcher;


    public interface COLUMNS {
        String ANALYTIC_ID = "analytic_id";
        String ASSET_ID = "asset_id";
        String ROUTE_ID = "route_id";
        String FLEET_ID = "fleet_id";
        String COMPANY_ID = "company_id";
        String PLAYED_TIME = "played_time";

    }


    private static final String CREATE_ANALYTIC_TABLE = "CREATE TABLE IF NOT EXISTS "
            + ANALYTIC_TABLE + "(" + COLUMNS.ANALYTIC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMNS.ASSET_ID + " TEXT ," + COLUMNS.ROUTE_ID + " TEXT ," + COLUMNS.FLEET_ID + " TEXT , " + COLUMNS.COMPANY_ID + " TEXT , " + COLUMNS.PLAYED_TIME + " TEXT " + ")";


    private static final int CASE_ANALYTIC_TABLE = 1;
    private static final int CASE_DEFAULT = 5;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, ANALYTIC_TABLE, CASE_ANALYTIC_TABLE);
        sUriMatcher.addURI(AUTHORITY, "/*", CASE_DEFAULT);
    }


    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CASE_ANALYTIC_TABLE:
                return AUTHORITY + "/" + ANALYTIC_TABLE;
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
        db.execSQL(CREATE_ANALYTIC_TABLE);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor lCursor = null;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CASE_ANALYTIC_TABLE:
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
            case CASE_ANALYTIC_TABLE:
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
            case CASE_ANALYTIC_TABLE:
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
            case CASE_ANALYTIC_TABLE:
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

