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
    public static final String TABLE_INTERMEDIATE_VIDEO_STATE = "video_intermediate_state";
    public static final String TABLE_SEQUENCE = "video_sequence";
    public static final String TABLE_ADS_SLOTS_CONFIG = "ads_slots_config";
    private static final int DATABASE_VERSION = 1;

    public static final String AUTHORITY = "com.lib.videoplayer.contentprovider.database.VideoProvider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI_VIDEO_TABLE = Uri.parse(CONTENT_URI + "/" + TABLE_VIDEO);
    public static final Uri CONTENT_URI_INTERMEDIATE_VIDEO_STATE = Uri.parse(CONTENT_URI + "/" + TABLE_INTERMEDIATE_VIDEO_STATE);
    public static final Uri CONTENT_URI_SEQUENCE_TABLE = Uri.parse(CONTENT_URI + "/" + TABLE_SEQUENCE);
    public static final Uri CONTENT_URI_ADS_SLOTS_CONFIG = Uri.parse(CONTENT_URI + "/" + TABLE_ADS_SLOTS_CONFIG);
    public DatabaseHelper mDbHelper;
    private static final UriMatcher sUriMatcher;


    public interface VIDEO_COLUMNS {
        String VIDEO_ID = "video_id";
        String NAME = "name";
        String DOWNLOAD_URL = "download_url";
        String TYPE = "type";
        String LANGUAGE = "language";
        String MESSAGE = "message";

        String DOWNLOADING_ID = "downloading_id";
        String DOWNLOAD_STATUS = "download_status";
        String PATH = "path";
        String LAST_PLAYED_TIME = "last_played_time";
        String PLAY_COUNT = "play_count";
        String TRANSACTION_ID = "transaction_id";
        String CLOUD_TIME = "cloud_time";
        String RECEIVED_TIME = "received_time";
        String SELECTED_STATE = "selected_state";
        String IS_PLAYING = "is_playing";
        String DELETE_STATUS = "delete_status";
        String PRIORITY = "priority";
    }

    public interface VIDEO_INTERMEDIATE_COLUMNS {
        String VIDEO_STATE = "video_state";
        String PREV_STATE = "prev_state";
        String CURRENT_STATE = "current_state";
        String MOVIE_URI = "movie_uri";
        String MOVIE_SEEK_TIME = "movie_seek_time";
        String OTHER_URI = "other_uri";
        String OTHER_SEEK_TIME = "other_seek_time";
        String BREAKING_URI = "breaking_uri";
        String BREAKING_SEEK_TIME = "breaking_seek_time";
        String PAUSED_STATES = "paused_states";
        String UPDATED_TIME = "updated_time";

    }

    public interface SEQUENCE_COLUMNS {
        String SEQUENCE_TYPE = "sequence_type";
        String VIDEO_TYPE = "video_type";
        String SEQUENCE_ORDER = "sequence_order";
        String SELECTED = "selected";
        String TOTAL_VIDEO_COUNT_FOR_TYPE = "total_video_count_for_type";
        String CURRENT_VIDEO_COUNT_FOR_TYPE = "current_video_count_for_type";
        String UPDATED_TIME = "updated_time";
    }

    public interface ADS_SLOTS_CONFIG_COLUMNS {
        String SLOT_TYPE = "slot_type";
        String SLOTS_PER_HOUR_COUNT = "slots_per_hour_count";
        String ADS_PER_SLOT_COUNT = "ads_per_slot_count";
    }


    public interface VIDEO_TYPE {
        String COMPANY_VIDEO = "company";
        String SAFETY_VIDEO = "safety";
        String TRAVELER_VIDEO = "traveler";
        String INTRO_VIDEO = "intro";
        String MOVIE = "movie";
        String ADV = "ad";
        String BREAKING_VIDEO = "news_video";
        String BREAKING_NEWS = "news_image";
        String COMPANY_AD = "company_ad";

        String TRAILER = "trailer";
        String COMEDY_SHOW = "comedy_show";
        String SERIAL = "serial";
        String DEVOTIONAL = "devotional";
        String SPORTS = "sports";


    }

    public interface DOWNLOAD_STATUS {
        String DOWNLOAD = "download";
        String DOWNLOADING = "downloading";
        String DOWNLOADED = "downloaded";
        String FAILED = "failed";
    }


    private static final String CREATE_VIDEO_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_VIDEO + "("
            + VIDEO_COLUMNS.VIDEO_ID + " TEXT PRIMARY KEY ," + VIDEO_COLUMNS.NAME + " TEXT," + VIDEO_COLUMNS.DOWNLOAD_URL + " TEXT," + VIDEO_COLUMNS.TYPE + " TEXT," + VIDEO_COLUMNS.LANGUAGE + " TEXT," + VIDEO_COLUMNS.MESSAGE + " TEXT," + VIDEO_COLUMNS.PATH + " TEXT," + VIDEO_COLUMNS.LAST_PLAYED_TIME + " TEXT," + VIDEO_COLUMNS.PLAY_COUNT + " INTEGER DEFAULT 0," + VIDEO_COLUMNS.DOWNLOADING_ID + " TEXT," + VIDEO_COLUMNS.DOWNLOAD_STATUS + " TEXT DEFAULT " + DOWNLOAD_STATUS.DOWNLOAD + "," + VIDEO_COLUMNS.TRANSACTION_ID + " TEXT," + VIDEO_COLUMNS.CLOUD_TIME + " TEXT," + VIDEO_COLUMNS.IS_PLAYING + " INTEGER DEFAULT 0," + VIDEO_COLUMNS.DELETE_STATUS + " INTEGER," + VIDEO_COLUMNS.RECEIVED_TIME + " TEXT," + VIDEO_COLUMNS.PRIORITY + " INTEGER DEFAULT 3 ," + VIDEO_COLUMNS.SELECTED_STATE + " INTEGER DEFAULT 0)";

    private static final String CREATE_VIDEO_INTERMEDIATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INTERMEDIATE_VIDEO_STATE + "("
            + VIDEO_INTERMEDIATE_COLUMNS.VIDEO_STATE + " INTEGER PRIMARY KEY ," + VIDEO_INTERMEDIATE_COLUMNS.PREV_STATE + " TEXT," + VIDEO_INTERMEDIATE_COLUMNS.CURRENT_STATE + " TEXT," + VIDEO_INTERMEDIATE_COLUMNS.MOVIE_URI + " TEXT," + VIDEO_INTERMEDIATE_COLUMNS.MOVIE_SEEK_TIME + " TEXT," + VIDEO_INTERMEDIATE_COLUMNS.OTHER_URI + " TEXT," + VIDEO_INTERMEDIATE_COLUMNS.OTHER_SEEK_TIME + " TEXT," + VIDEO_INTERMEDIATE_COLUMNS.BREAKING_URI + " TEXT," + VIDEO_INTERMEDIATE_COLUMNS.BREAKING_SEEK_TIME + " TEXT," + VIDEO_INTERMEDIATE_COLUMNS.PAUSED_STATES + " TEXT," + VIDEO_INTERMEDIATE_COLUMNS.UPDATED_TIME + " TEXT" + ")";


    private static final String CREATE_SEQUENCE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_SEQUENCE + "("
            + SEQUENCE_COLUMNS.SEQUENCE_TYPE + " TEXT ," + SEQUENCE_COLUMNS.VIDEO_TYPE + " TEXT," + SEQUENCE_COLUMNS.SEQUENCE_ORDER + " TEXT," + SEQUENCE_COLUMNS.SELECTED + " INTEGER DEFAULT 0 ," + SEQUENCE_COLUMNS.TOTAL_VIDEO_COUNT_FOR_TYPE + " INTEGER DEFAULT 1 ," + SEQUENCE_COLUMNS.CURRENT_VIDEO_COUNT_FOR_TYPE + " INTEGER DEFAULT 0 ," + SEQUENCE_COLUMNS.UPDATED_TIME + " TEXT" + ")";

    private static final String CREATE_ADS_SLOTS_CONFIG_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ADS_SLOTS_CONFIG + "("
            + ADS_SLOTS_CONFIG_COLUMNS.SLOT_TYPE + " TEXT ," + ADS_SLOTS_CONFIG_COLUMNS.SLOTS_PER_HOUR_COUNT + " INTEGER," + ADS_SLOTS_CONFIG_COLUMNS.ADS_PER_SLOT_COUNT + " INTEGER" + ")";

    private static final int CASE_VIDEO_TABLE = 1;
    private static final int CASE_VIDEO_INTERMEDIATE_TABLE = 2;
    private static final int CASE_SEQUENCE_TABLE = 3;
    private static final int CASE_ADS_SLOTS_CONFIG_TABLE = 4;
    private static final int CASE_DEFAULT = 6;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, TABLE_VIDEO, CASE_VIDEO_TABLE);
        sUriMatcher.addURI(AUTHORITY, TABLE_INTERMEDIATE_VIDEO_STATE, CASE_VIDEO_INTERMEDIATE_TABLE);
        sUriMatcher.addURI(AUTHORITY, TABLE_SEQUENCE, CASE_SEQUENCE_TABLE);
        sUriMatcher.addURI(AUTHORITY, TABLE_ADS_SLOTS_CONFIG, CASE_ADS_SLOTS_CONFIG_TABLE);
        sUriMatcher.addURI(AUTHORITY, "/*", CASE_DEFAULT);
    }


    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CASE_VIDEO_TABLE:
                return AUTHORITY + "/" + TABLE_VIDEO;
            case CASE_VIDEO_INTERMEDIATE_TABLE:
                return AUTHORITY + "/" + TABLE_INTERMEDIATE_VIDEO_STATE;
            case CASE_ADS_SLOTS_CONFIG_TABLE:
                return AUTHORITY + "/" + TABLE_ADS_SLOTS_CONFIG;
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
        db.execSQL(CREATE_VIDEO_INTERMEDIATE_TABLE);
        db.execSQL(CREATE_SEQUENCE_TABLE);
        db.execSQL(CREATE_ADS_SLOTS_CONFIG_TABLE);
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
            case CASE_VIDEO_INTERMEDIATE_TABLE:
                queryBuilder.setTables(uri.getLastPathSegment());
                lCursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CASE_SEQUENCE_TABLE:
                queryBuilder.setTables(uri.getLastPathSegment());
                lCursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CASE_ADS_SLOTS_CONFIG_TABLE:
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
                try {
                    lRowId = lDb.insertOrThrow(uri.getLastPathSegment(), null, values);
                } catch (Exception e) {

                }
                break;
            case CASE_VIDEO_INTERMEDIATE_TABLE:
                try {
                    lRowId = lDb.insertOrThrow(uri.getLastPathSegment(), null, values);
                } catch (Exception e) {

                }
                break;
            case CASE_SEQUENCE_TABLE:
                try {
                    lRowId = lDb.insertOrThrow(uri.getLastPathSegment(), null, values);
                } catch (Exception e) {

                }
                break;
            case CASE_ADS_SLOTS_CONFIG_TABLE:
                try {
                    lRowId = lDb.insertOrThrow(uri.getLastPathSegment(), null, values);
                } catch (Exception e) {

                }
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
            case CASE_VIDEO_INTERMEDIATE_TABLE:
                count = db.delete(uri.getLastPathSegment(), selection, selectionArgs);
                break;
            case CASE_SEQUENCE_TABLE:
                count = db.delete(uri.getLastPathSegment(), selection, selectionArgs);
                break;
            case CASE_ADS_SLOTS_CONFIG_TABLE:
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

            case CASE_VIDEO_INTERMEDIATE_TABLE:
                lCount = lDb.update(uri.getLastPathSegment(), values, selection, selectionArgs);
                break;
            case CASE_SEQUENCE_TABLE:
                lCount = lDb.update(uri.getLastPathSegment(), values, selection, selectionArgs);
                break;
            case CASE_ADS_SLOTS_CONFIG_TABLE:
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

