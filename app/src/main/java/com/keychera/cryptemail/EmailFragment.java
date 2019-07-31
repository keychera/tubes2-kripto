package com.keychera.cryptemail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

/**
 * A fragment representing a list of Items. <p /> Activities containing this fragment MUST implement
 * the {@link onEmailListFragmentInteraction} interface.
 */
public class EmailFragment extends Fragment implements OnRefreshListener {

  public static final String ARG_EMAIL_FOLDER_NAME = "email-folder-name";
  private Fragment thisFragment;
  private onEmailListFragmentInteraction mListener;

  private EmailListRecyclerViewAdapter emailListRecyclerViewAdapter;
  private SwipeRefreshLayout mSwipeRefreshLayout;
  private String EmailFolderName;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
   * screen orientation changes).
   */
  public EmailFragment() {
  }

  // TODO: Customize parameter initialization
  @SuppressWarnings("unused")
  public static EmailFragment newInstance(String columnCount) {
    EmailFragment fragment = new EmailFragment();
    Bundle args = new Bundle();
    args.putString(ARG_EMAIL_FOLDER_NAME, columnCount);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      EmailFolderName = getArguments().getString(ARG_EMAIL_FOLDER_NAME);
    }

    thisFragment = this;
    if (PropertiesSingleton.getInstance().emails == null) {
      PropertiesSingleton.getInstance().emails = new ArrayList<>();
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_email_list, container, false);

    // SwipeRefreshLayout
    mSwipeRefreshLayout = view.findViewById(R.id.swipe_container);
    mSwipeRefreshLayout.setOnRefreshListener(this);
    mSwipeRefreshLayout.setColorSchemeResources(
        R.color.colorPrimary,
        android.R.color.holo_green_dark,
        android.R.color.holo_orange_dark,
        android.R.color.holo_blue_dark);

    if (PropertiesSingleton.getInstance().emails.isEmpty()) {
      mSwipeRefreshLayout.post(new Runnable() {
        @Override
        public void run() {
          // Fetching data
          fetchData();
        }
      });
    }

    // Set the adapter
    Context context = view.getContext();
    RecyclerView recyclerView = view.findViewById(R.id.email_list);
    recyclerView.setLayoutManager(new LinearLayoutManager(context));
    List<SimpleEmail> emails = PropertiesSingleton.getInstance().emails;
    emailListRecyclerViewAdapter = new EmailListRecyclerViewAdapter(emails, mListener);
    recyclerView.setAdapter(emailListRecyclerViewAdapter);

    //set FAB
    FloatingActionButton fab = view.findViewById(R.id.compose_fab);
    fab.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        NavHostFragment.findNavController(thisFragment).navigate(R.id.composeFragment);
      }
    });

    return view;
  }

  private void fetchData() {
    mSwipeRefreshLayout.setRefreshing(true);
    new FetchEmailTask().execute();
  }


  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof onEmailListFragmentInteraction) {
      mListener = (onEmailListFragmentInteraction) context;
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
  public void onRefresh() {
    fetchData();
  }

  public interface onEmailListFragmentInteraction {

    void onEmailItemFragmentInteraction(SimpleEmail item);
  }

  @SuppressLint("StaticFieldLeak")
  private class FetchEmailTask extends AsyncTask<Void, SimpleEmail, Void> {

    @Override
    protected Void doInBackground(Void... voids) {
      Store store;
      String user = Config.getInstance().email;
      String pass = Config.getInstance().password;
      try {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(props, null);
        store = session.getStore("imaps");
        store.connect("imap.gmail.com", user, pass);
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }

      Message[] messages;
      try {
        Folder folder = store.getFolder(EmailFolderName);
        folder.open(Folder.READ_WRITE);
        messages = folder.getMessages();
      } catch (MessagingException e) {
        e.printStackTrace();
        return null;
      }

      for ( int i = 0; (i < messages.length && i < 5); i++) {
        Message message = messages[messages.length - i - 1];
        SimpleEmail email;
        try {
          email = new SimpleEmail(message);
          publishProgress(email);
        } catch (MessagingException | IOException e) {
          e.printStackTrace();
        }
      }
      return null;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      PropertiesSingleton.getInstance().emails.clear();
      emailListRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onProgressUpdate(SimpleEmail... comingEmails) {
      super.onProgressUpdate(comingEmails);
      for (SimpleEmail email:comingEmails) {
        PropertiesSingleton.getInstance().emails.add(email);
      }
      emailListRecyclerViewAdapter.notifyItemInserted(PropertiesSingleton.getInstance().emails.size());
    }

    @Override
    protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      mSwipeRefreshLayout.setRefreshing(false);
    }
  }
}
