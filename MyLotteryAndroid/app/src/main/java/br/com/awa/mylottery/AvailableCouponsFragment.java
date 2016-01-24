package br.com.awa.mylottery;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.awa.mylottery.data.LotteryContract;
//import br.com.awa.mylottery.dummy.DummyContent;
//import br.com.awa.mylottery.dummy.DummyContent.DummyItem;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnAvailableCouponsInteraction}
 * interface.
 */
public class AvailableCouponsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnAvailableCouponsInteraction mListener;
    Cursor data;

    AvailableCouponsRecyclerViewAdapter mAdapter;
    private static final int COUPONS_LOADER = 0;

    public static final String[] AVAILABLE_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            LotteryContract.Available.TABLE_NAME + "." + LotteryContract.Available._ID,
            LotteryContract.Available.COLUMN_NAME,
            LotteryContract.Available.COLUMN_PRIZE_A,
            LotteryContract.Available.COLUMN_PRIZE_B,
            LotteryContract.Available.COLUMN_PRIZE_C,
            LotteryContract.Available.COLUMN_STATUS
    };

    // These indices are tied to AVAILABLE_COLUMNS.  If AVAILABLE_COLUMNS changes, these
    // must change.
    static final int COL_AVAILABLE_ID = 0;
    static final int COL_AVAILABLE_NAME = 1;
    static final int COL_AVAILABLE_PRIZE_A = 2;
    static final int COL_AVAILABLE_PRIZE_B = 3;
    static final int COL_AVAILABLE_PRIZE_C = 4;
    static final int COL_AVAILABLE_STATUS = 5;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AvailableCouponsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AvailableCouponsFragment newInstance(int columnCount) {
        AvailableCouponsFragment fragment = new AvailableCouponsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_availablecoupons_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            // create the adapter that will be attached to the view
            mAdapter = new AvailableCouponsRecyclerViewAdapter(getActivity(), data, mListener);
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(COUPONS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAvailableCouponsInteraction) {
            mListener = (OnAvailableCouponsInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri = LotteryContract.Available.CONTENT_URI;
        return new CursorLoader(this.getActivity(), baseUri,
                AVAILABLE_COLUMNS, null, null, LotteryContract.Available._ID);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAvailableCouponsInteraction {
        // TODO: Update argument type and name
        void onAvailableCouponsInteraction(Uri idUri);
    }
}
