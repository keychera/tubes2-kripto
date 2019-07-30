package com.keychera.cryptemail;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keychera.cryptemail.EmailFragment.onEmailListFragmentInteraction;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SimpleEmail} and makes a call to the
 * specified {@link onEmailListFragmentInteraction}.
 * for your data type.
 */
public class EmailListRecyclerViewAdapter extends
    RecyclerView.Adapter<EmailListRecyclerViewAdapter.ViewHolder> {

  private final List<SimpleEmail> mValues;
  private final onEmailListFragmentInteraction mListener;

  public EmailListRecyclerViewAdapter(List<SimpleEmail> items,
      onEmailListFragmentInteraction listener) {
    mValues = items;
    mListener = listener;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_email, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    holder.mItem = mValues.get(position);
    holder.mFromView.setText(mValues.get(position).fromAddress);
    holder.mSubjectView.setText(mValues.get(position).subject);

    holder.mView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (null != mListener) {
          // Notify the active callbacks interface (the activity, if the
          // fragment is attached to one) that an item has been selected.
          mListener.onEmailItemFragmentInteraction(holder.mItem);
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return mValues.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public final View mView;
    public final TextView mFromView;
    public final TextView mSubjectView;
    public SimpleEmail mItem;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      mFromView = (TextView) view.findViewById(R.id.from_email);
      mSubjectView = (TextView) view.findViewById(R.id.email_subject);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + mSubjectView.getText() + "'";
    }
  }
}
