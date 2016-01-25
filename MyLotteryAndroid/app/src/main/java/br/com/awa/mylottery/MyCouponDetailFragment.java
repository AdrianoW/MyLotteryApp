package br.com.awa.mylottery;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyCouponDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    private static final String LOG_TAG = AvailableCouponDetailFragment.class.getSimpleName();

    public static final String COUPON_URI = "URI";
    private static final int DETAIL_LOADER = 2000;
    private Uri mUri;
    private TextView mTitle;
    private TextView mStatus;
    private TextView mMethod;
    private TextView mToken;
    private TextView mID;

    public MyCouponDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MyCouponDetailFragment.COUPON_URI);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_coupon_detail, container, false);

        // save the views for later use
        mTitle = (TextView) rootView.findViewById(R.id.mycoupon_detail_title);
        mStatus = (TextView) rootView.findViewById(R.id.mycoupon_detail_status);
        mMethod = (TextView) rootView.findViewById(R.id.mycoupon_detail_method);
        mToken = (TextView) rootView.findViewById(R.id.mycoupon_detail_token);
        mID = (TextView) rootView.findViewById(R.id.mycoupon_detail_id);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MyCouponsFragment.MYCOUPONS_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mTitle.setText(data.getString(MyCouponsFragment.COL_MYCOUPONS_CAMPAIGN_NAME));
            mStatus.setText(data.getString(MyCouponsFragment.COL_MYCOUPONS_STATUS));
            mMethod.setText(data.getString(MyCouponsFragment.COL_MYCOUPONS_METHOD));
            mToken.setText(data.getString(MyCouponsFragment.COL_MYCOUPONS_TOKEN));
            mID.setText(data.getString(MyCouponsFragment.COL_MYCOUPONS_ID));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
