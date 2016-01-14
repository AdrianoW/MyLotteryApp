package br.com.awa.mylottery;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.awa.mylottery.dummy.DummyContent;

/**
 * A placeholder fragment containing a simple view.
 */
public class AvailableCouponDetailFragment extends Fragment {

    public static final String COUPON_ID = "AVAILABLE_COUPON_ID";
    private DummyContent.DummyItem mItem;

    public AvailableCouponDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(COUPON_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(COUPON_ID));

            Activity activity = this.getActivity();
            //CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.available_coupon_toolbar_layout);
            //if (appBarLayout != null) {
            //    appBarLayout.setTitle(mItem.content);
            //}
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_available_coupon_detail, container, false);
        if (mItem != null) {
            ((TextView)rootView.findViewById(R.id.available_coupon_test))
                    .setText(mItem.details);
        }

        return rootView;
    }
}
