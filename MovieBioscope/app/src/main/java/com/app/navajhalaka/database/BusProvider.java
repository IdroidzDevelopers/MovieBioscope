package com.app.navajhalaka.database;


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

public class BusProvider extends ContentProvider {

    private static final String TAG = BusProvider.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static final String DATABASE_NAME = "bioscope.db";
    public static final String BUS_DETAIL_TABLE = "bus_details_table";
    private static final int DATABASE_VERSION = 1;

    public static final String AUTHORITY = "com.app.navajhalaka.contentprovider.database.BusProvider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI_BUS_DETAIL_TABLE = Uri.parse(CONTENT_URI + "/" + BUS_DETAIL_TABLE);
    public DatabaseHelper mDbHelper;
    private static final UriMatcher sUriMatcher;


    public interface COLUMNS {
        String BUS_ID = "bus_id";
        String BUS_NUMBER = "bus_number";
        String COMPANY_ID = "company_id";
        String COMPANY_NAME = "company_name";
    }


    private static final String CREATE_BUS_DETAIL_TABLE = "CREATE TABLE IF NOT EXISTS "
            + BUS_DETAIL_TABLE + "(" + COLUMNS.BUS_ID + " TEXT ,"
            + COLUMNS.BUS_NUMBER + " TEXT ,"+ COLUMNS.COMPANY_ID+ " TEXT ,"+ COLUMNS.COMPANY_NAME+ " TEXT " +")";


    private static final int CASE_BUS_DETAIL_TABLE = 1;
    private static final int CASE_DEFAULT = 5;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, BUS_DETAIL_TABLE, CASE_BUS_DETAIL_TABLE);
        sUriMatcher.addURI(AUTHORITY, "/*", CASE_DEFAULT);
    }



    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CASE_BUS_DETAIL_TABLE:
                return AUTHORITY + "/" + BUS_DETAIL_TABLE;
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
        db.execSQL(CREATE_BUS_DETAIL_TABLE);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor lCursor = null;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CASE_BUS_DETAIL_TABLE:
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
            case CASE_BUS_DETAIL_TABLE:
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
            case CASE_BUS_DETAIL_TABLE:
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
            case CASE_BUS_DETAIL_TABLE:
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

