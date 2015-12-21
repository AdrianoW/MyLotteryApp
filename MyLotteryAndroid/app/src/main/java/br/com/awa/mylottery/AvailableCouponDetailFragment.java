package br.com.awa.mylottery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class AvailableCouponDetailFragment extends Fragment {

    public static final String COUPON_ID = "AVAILABLE_COUPON_ID";
    private String mCouponID;
    private TextView mText;

    public AvailableCouponDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get the passed arguments and create the view
        Bundle arguments = getArguments();
        if (arguments != null) {
            mCouponID = arguments.getString(AvailableCouponDetailFragment.COUPON_ID);
        }
        View rootView = inflater.inflate(R.layout.fragment_available_coupon_detail, container, false);
        mText = (TextView)rootView.findViewById(R.id.available_coupon_test);
        mText.setText(mCouponID);
        return rootView;
    }
}
