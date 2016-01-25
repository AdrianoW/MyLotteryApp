package br.com.awa.mylottery.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by adriano on 22/01/16.
 */
public class LotteryContract {

    // the name of content provider
    public static final String CONTENT_AUTHORITY = "br.com.awa.mylottery";

    // the base for the uri
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // the possible information that can be accessed in the provider
    public static final String PATH_AVAILABLE = "available";
    public static final String PATH_MYCOUPONS = "mycoupons";

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static int normalizeDate(String dtStart) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        Date date = null;
        try {
            date= format.parse(dtStart);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return -1;
        }

        // convert to int
        long inLong = date.getTime()/1000;
        int inInt = (int) inLong;
        return inInt;
    }

    // the columns for the Available Coupons
    public static final class Available implements BaseColumns {

        // the uri to call this data
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_AVAILABLE).build();

        // type of the data
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AVAILABLE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_AVAILABLE;

        // table name
        public static final String TABLE_NAME = "available";

        // define the columns
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRIZE_A = "prize_a";
        public static final String COLUMN_PRIZE_B = "prize_b";
        public static final String COLUMN_PRIZE_C = "prize_c";
        public static final String COLUMN_STATUS = "status";

        public static Uri buildAvailableCouponUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // where clauses
        public static final String sWhereID = TABLE_NAME + "." + _ID + " = ?";
    }

    // information for my coupons
    public static final class MyCoupons implements BaseColumns{
        // the uri to call this data
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MYCOUPONS).build();

        // type of the data
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MYCOUPONS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MYCOUPONS;

        // table name
        public static final String TABLE_NAME = "mycoupons";

        // columns
        public static final String COLUMN_CAMPAIGN= "campaign_id";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_METHOD = "method";
        public static final String COLUMN_TOKEN = "token";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TICKET_ID = "ticket_id";
        public static final String COLUMN_TICKET_STATUS = "ticket_status";
        public static final String COLUMN_TICKET_TYPE = "ticket_type";

        public static Uri buildMyCouponUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // where clauses
        public static final String sWhereID = TABLE_NAME + "." + _ID + " = ?";
    }

}
