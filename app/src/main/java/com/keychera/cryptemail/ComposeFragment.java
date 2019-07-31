package com.keychera.cryptemail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.fragment.NavHostFragment;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.keychera.cryptemail.EmailDetailFragment.DetailType;


/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment must implement the
 * {@link OnComposeFragmentInteractionListener} interface to handle interaction events. Use
 * the {@link ComposeFragment#newInstance} factory method to create an instance of this fragment.
 */
public class ComposeFragment extends Fragment {

  private OnComposeFragmentInteractionListener mListener;
  private EditText toAddressText, subjectText, messageText;
  private CheckBox encryptCheckBox, signCheckBox;
  private ComposeFragment thisFragment;
  private View thisView;

  private  enum ComposeStatus {
    ENCRYPTING,
    SIGNING,
  }

  public ComposeFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @return A new instance of fragment ComposeFragment.
   */
  public static ComposeFragment newInstance() {
    return new ComposeFragment();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    thisFragment = this;

    // Inflate the layout for this fragment
    thisView = inflater.inflate(R.layout.fragment_compose, container, false);
    toAddressText = thisView.findViewById(R.id.to_address_text);
    subjectText = thisView.findViewById(R.id.subject_text);
    messageText = thisView.findViewById(R.id.message_text);
    encryptCheckBox = thisView.findViewById(R.id.checkbox_encrypt);
    signCheckBox = thisView.findViewById(R.id.checkbox_sign);

    //set FAB
    FloatingActionButton fab = thisView.findViewById(R.id.send_fab);
    fab.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        ComposeBundle composeBundle = new ComposeBundle(getContext());
        composeBundle.emailToCompose = getEmailContent();
        composeBundle.encryptFlag = encryptCheckBox.isChecked();
        composeBundle.signFlag = signCheckBox.isChecked();
        new ComposeEmailTask().execute(composeBundle);
      }
    });

    return thisView;
  }


  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnComposeFragmentInteractionListener) {
      mListener = (OnComposeFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement OnComposeFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public interface OnComposeFragmentInteractionListener {

    void onFragmentInteraction(Uri uri);
  }

  public SimpleEmail getEmailContent() {
    SimpleEmail email = new SimpleEmail();
    email.fromAddress = Config.EMAIL;
    email.toAddress = toAddressText.getText().toString();
    email.subject = subjectText.getText().toString();
    email.message = messageText.getText().toString();
    return email;
  }

  private class ComposeBundle {
    Context context;
    public SimpleEmail emailToCompose;
    public boolean encryptFlag;
    public boolean signFlag;
    public String encryptKeyFilename;
    public String signPrivKeyFilename;

    public ComposeBundle(Context context) {
      this.context = context;
      encryptFlag = false;
      signFlag = false;
      encryptKeyFilename = "key.dat";
      signPrivKeyFilename = null;
    }
  }

  private class ComposeEmailTask extends AsyncTask<ComposeBundle, ComposeStatus, SimpleEmail> {
    @Override
    protected SimpleEmail doInBackground(ComposeBundle... composeBundles) {
      ComposeBundle bundle = composeBundles[0];
      SimpleEmail email = bundle.emailToCompose;
      if (email.isValid()) {
        if (bundle.encryptFlag) {
          publishProgress(ComposeStatus.ENCRYPTING);
          email.message = PythonRunner.Encrypt(bundle.context, email.message, bundle.encryptKeyFilename);
        }
        if (bundle.signFlag) {
          publishProgress(ComposeStatus.SIGNING);

        }
        return email;
      } else {
        return null;
      }
    }



    @Override
    protected void onProgressUpdate(ComposeStatus... values) {
      super.onProgressUpdate(values);
      ComposeStatus status = values[0];
      if(status == ComposeStatus.ENCRYPTING) {
        Snackbar.make(thisView, "ENCRYPTING", Snackbar.LENGTH_INDEFINITE)
            .setAction("Action", null).show();

      } else if (status == ComposeStatus.SIGNING){
        Snackbar.make(thisView, "SIGNING", Snackbar.LENGTH_INDEFINITE)
            .setAction("Action", null).show();
      } else {
        Snackbar.make(thisView, "DONE", Snackbar.LENGTH_SHORT)
            .setAction("Action", null).show();
      }
    }

    @Override
    protected void onPostExecute(SimpleEmail email) {
      if (email != null) {
        super.onPostExecute(email);
        Bundle args = new Bundle();
        args.putSerializable(EmailDetailFragment.ARG_SIMPLE_EMAIL, email);
        args.putSerializable(EmailDetailFragment.ARG_DETAIL_TYPE, DetailType.READY_SEND);
        NavHostFragment.findNavController(thisFragment).navigate(R.id.emailDetailFragment, args);
      } else {
        Snackbar.make(thisView, "Invalid Input", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    }
  }
}

