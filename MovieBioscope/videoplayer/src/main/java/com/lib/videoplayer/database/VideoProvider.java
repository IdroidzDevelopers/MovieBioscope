package com.lib.videoplayer.database;


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

public class VideoProvider extends ContentProvider {

    private static final String TAG = VideoProvider.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static final String DATABASE_NAME = "bioscope.db";
    public static final String TABLE_VIDEO = "video_table";
    private static final int DATABASE_VERSION = 1;

    public static final String AUTHORITY = "com.lib.videoplayer.contentprovider.database.VideoProvider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI_VIDEO_TABLE = Uri.parse(CONTENT_URI + "/" + TABLE_VIDEO);
    public DatabaseHelper mDbHelper;
    private static final UriMatcher sUriMatcher;


    public interface VIDEO_COLUMNS {
        String ID = "_id";
        String NAME = "name";
        String PATH = "path";
        String TYPE = "type";
        String LAST_PLAYED_TIME = "last_played_time";
        String PLAY_COUNT = "play_count";
    }

    public interface VIDEO_TYPE {
        String MOVIE = "movie";
        String ADV = "adv";
        String BREAKING_NEWS = "breaking_news";
    }


    private static final String CREATE_VIDEO_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_VIDEO + "(" + VIDEO_COLUMNS.ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + VIDEO_COLUMNS.NAME + " TEXT," + VIDEO_COLUMNS.PATH + " TEXT," + VIDEO_COLUMNS.TYPE + " TEXT," + VIDEO_COLUMNS.LAST_PLAYED_TIME + " TEXT," + VIDEO_COLUMNS.PLAY_COUNT + " INTEGER" + ")";

    private static final int CASE_VIDEO_TABLE = 1;
    private static final int CASE_DEFAULT = 3;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, TABLE_VIDEO, CASE_VIDEO_TABLE);
        sUriMatcher.addURI(AUTHORITY, "/*", CASE_DEFAULT);
    }



    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CASE_VIDEO_TABLE:
                return AUTHORITY + "/" + TABLE_VIDEO;
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
        db.execSQL(CREATE_VIDEO_TABLE);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor lCursor = null;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CASE_VIDEO_TABLE:
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
            case CASE_VIDEO_TABLE:
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
            case CASE_VIDEO_TABLE:
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
            case CASE_VIDEO_TABLE:
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
