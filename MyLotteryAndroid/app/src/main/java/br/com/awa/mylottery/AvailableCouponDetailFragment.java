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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import br.com.awa.mylottery.dummy.DummyContent;

/**
 * A placeholder fragment containing a simple view.
 */
public class AvailableCouponDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    public static final String COUPON_URI = "URI";
    private static final int DETAIL_LOADER = 1000;
    private DummyContent.DummyItem mItem;
    private Uri mUri;
    private TextView mTitle;
    private TextView mPrize_a;
    private TextView mPrize_b;
    private TextView mPrize_c;
    private TextView mStatus;
    private Button mBuy;

    public AvailableCouponDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(AvailableCouponDetailFragment.COUPON_URI);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_available_coupon_detail, container, false);

        // save the views for later use
        mTitle = (TextView) rootView.findViewById(R.id.available_coupon_detail_title);
        mPrize_a = (TextView) rootView.findViewById(R.id.available_coupon_detail_prize_a);
        mPrize_b = (TextView) rootView.findViewById(R.id.available_coupon_detail_prize_b);
        mPrize_c = (TextView) rootView.findViewById(R.id.available_coupon_detail_prize_c);
        mStatus = (TextView) rootView.findViewById(R.id.available_coupon_detail_status);
        mBuy = (Button) rootView.findViewById(R.id.available_coupon_detail_buy_button);

        // add a listener to the button
        mBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getContext(), "Bought the ticket", Toast.LENGTH_LONG);
                toast.show();
            }
        });
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    AvailableCouponsFragment.AVAILABLE_COLUMNS,
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
            mTitle.setText(data.getString(AvailableCouponsFragment.COL_AVAILABLE_NAME));
            mPrize_a.setText(data.getString(AvailableCouponsFragment.COL_AVAILABLE_PRIZE_A));
            mPrize_b.setText(data.getString(AvailableCouponsFragment.COL_AVAILABLE_PRIZE_B));
            mPrize_c.setText(data.getString(AvailableCouponsFragment.COL_AVAILABLE_PRIZE_C));
            mStatus.setText(data.getString(AvailableCouponsFragment.COL_AVAILABLE_STATUS));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
