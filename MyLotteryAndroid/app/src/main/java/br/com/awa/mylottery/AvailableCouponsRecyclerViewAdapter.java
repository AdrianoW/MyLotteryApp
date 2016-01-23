package br.com.awa.mylottery;

import android.app.Activity;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.awa.mylottery.AvailableCouponsFragment.OnAvailableCouponsInteraction;
import br.com.awa.mylottery.data.LotteryContract;

/**
 * {@link RecyclerView.Adapter} that can display a item and makes a call to the
 * specified {@link OnAvailableCouponsInteraction}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AvailableCouponsRecyclerViewAdapter extends RecyclerView.Adapter<AvailableCouponsRecyclerViewAdapter.ViewHolder> {

    //private final List<DummyItem> mValues;
    private final OnAvailableCouponsInteraction mListener;
    private Cursor dataCursor;
    private final Activity ctx;

    public AvailableCouponsRecyclerViewAdapter(Activity mContext, Cursor data, OnAvailableCouponsInteraction listener) {
        dataCursor=data;
        ctx=mContext;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_availablecoupons, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        dataCursor.moveToPosition(position);

        final String id = String.valueOf(dataCursor.getInt(AvailableCouponsFragment.COL_AVAILABLE_ID));
        holder.mIdView.setText(id);

        holder.mContentView.setText(dataCursor.getString(AvailableCouponsFragment.COL_AVAILABLE_NAME));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onAvailableCouponsInteraction(
                            LotteryContract.Available.buildAvailableCouponUri(Long.parseLong(id)));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (dataCursor == null) ? 0 : dataCursor.getCount();
    }

    public Cursor swapCursor(Cursor cursor) {
        if (dataCursor == cursor) {
            return null;
        }
        Cursor oldCursor = dataCursor;
        this.dataCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        //public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
