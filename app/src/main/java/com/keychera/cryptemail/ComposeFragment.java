package com.keychera.cryptemail;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.keychera.cryptemail.EmailDetailFragment.DetailType;
import com.keychera.cryptemail.PropertiesSingleton.PropertyListener;


/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment must implement the
 * {@link OnComposeFragmentInteractionListener} interface to handle interaction events. Use
 * the {@link ComposeFragment#newInstance} factory method to create an instance of this fragment.
 */
public class ComposeFragment extends Fragment implements PropertyListener {

  private OnComposeFragmentInteractionListener mListener;
  private EditText toAddressText, subjectText, messageText;
  private TextView encryptFile, signFile, attachFile, textToChange;
  private CheckBox encryptCheckBox, signCheckBox, checkBoxToChange;
  private Button attachButton;
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
    encryptFile = thisView.findViewById(R.id.status_encryption);
    signFile = thisView.findViewById(R.id.status_signature);
    attachFile = thisView.findViewById(R.id.status_attached_file);
    attachButton = thisView.findViewById(R.id.button_attach);

    encryptCheckBox.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        checkBoxToChange = (CheckBox) view;
        textToChange = encryptFile;
        if (checkBoxToChange.isChecked()) {
          PropertiesSingleton.getInstance().subscribe(thisFragment);
          FileHelper.CallFilePicker(getActivity(),1, null);
        } else {
          textToChange.setText(null);
        }
      }
    });

    signCheckBox.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        checkBoxToChange = (CheckBox) view;
        textToChange = signFile;
        if (checkBoxToChange.isChecked()) {
          PropertiesSingleton.getInstance().subscribe(thisFragment);
          FileHelper.CallFilePicker(getActivity(),1, null);
        } else {
          textToChange.setText(null);
        }
      }
    });

    attachButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        textToChange = attachFile;
        PropertiesSingleton.getInstance().subscribe(thisFragment);
        FileHelper.CallFilePicker(getActivity(),1, null);
      }
    });

    //set FAB
    FloatingActionButton fab = thisView.findViewById(R.id.send_fab);
    fab.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        ComposeBundle composeBundle = new ComposeBundle(getContext());
        composeBundle.emailToCompose = getEmailContent();
        composeBundle.encryptFlag = encryptCheckBox.isChecked();
        composeBundle.signFlag = signCheckBox.isChecked();
        TextView encryptFile = view.getRootView().findViewById(R.id.status_encryption);
        composeBundle.encryptKeyFilename = encryptFile.getText().toString();
        TextView signFile = view.getRootView().findViewById(R.id.status_signature);
        composeBundle.signPrivKeyFilename = signFile.getText().toString();
        new ComposeEmailTask().execute(composeBundle);
      }
    });

    return thisView;
  }

  @Override
  public void OnPropertyChanged() {
    int requestCode = PropertiesSingleton.getInstance().sharedInt;
    if (requestCode == 1) {
      String filename = PropertiesSingleton.getInstance().sharedString;
      if (filename != null) {
        textToChange.setText(filename);
      } else {
        checkBoxToChange.setChecked(false);
      }
    }
    PropertiesSingleton.getInstance().sharedString = null;
    PropertiesSingleton.getInstance().unsubscribe(thisFragment);
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
    email.fromAddress = Config.getInstance().email;
    email.toAddress = toAddressText.getText().toString();
    email.subject = subjectText.getText().toString();
    email.bodyText = messageText.getText().toString();
    email.attachFiles = attachFile.getText().toString();
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
      encryptKeyFilename = null;
      signPrivKeyFilename = null;
    }
  }

  private class ComposeEmailTask extends AsyncTask<ComposeBundle, ComposeStatus, SimpleEmail> {

    @Override
    protected SimpleEmail doInBackground(ComposeBundle... composeBundles) {
      ComposeBundle bundle = composeBundles[0];
      SimpleEmail email = bundle.emailToCompose;
      if (email.isValid()) {
        StringBuilder stringBuilder = new StringBuilder();
        if (bundle.encryptFlag) {
          publishProgress(ComposeStatus.ENCRYPTING);
          String encrypted_message = PythonRunner.Encrypt(bundle.context, email.bodyText, bundle.encryptKeyFilename);
          stringBuilder
              .append(SimpleEmail.ENCRYPTED_TAG_START)
              .append("\n")
              .append(encrypted_message)
              .append(SimpleEmail.ENCRYPTED_TAG_END)
              .append("\n");
          email.bodyText = encrypted_message;
        } else {
          stringBuilder.append(email.bodyText);
        }
        if (bundle.signFlag) {
          publishProgress(ComposeStatus.SIGNING);
          ECDSA signer = new ECDSA();
          byte[] encodedPv = FileHelper.ReadFileBytes(bundle.signPrivKeyFilename, bundle.context);
          String hashToSign = SHA.SHA1(email.bodyText);
          SimpleSignedData signedData = signer.signData(hashToSign.getBytes(), encodedPv);
          stringBuilder
              .append("\n")
              .append(SimpleEmail.SIGNATURE_TAG_START)
              .append("\n")
              .append(signedData.getSignatureString())
              .append(SimpleEmail.SIGNATURE_TAG_END);
        }
        email.bodyText = stringBuilder.toString();
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

