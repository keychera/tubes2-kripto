package com.keychera.cryptemail;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Message;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import androidx.arch.core.executor.DefaultTaskExecutor;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.io.IOException;
import javax.mail.MessagingException;

public class EmailDetailFragment extends Fragment {

  public static final String ARG_SIMPLE_EMAIL = "simple-email";
  public static final String ARG_DETAIL_TYPE = "detail-type";

  public static enum DetailType {
    VIEW,
    READY_SEND,
    TEMP_VIEW
  }

  private SimpleEmail email;
  private TextView messageText;
  private DetailType detail_type;
  private View thisView;
  private Fragment thisFragment;
  private TextView encryptionStatus;
  private TextView signatureStatus;
  private Button decryptButton;
  private Button verifySignatureButton;


  public static EmailDetailFragment newInstance(SimpleEmail email, DetailType type) {
    EmailDetailFragment fragment = new EmailDetailFragment();
    Bundle args = new Bundle();
    args.putSerializable(ARG_DETAIL_TYPE, type);
    args.putSerializable(ARG_SIMPLE_EMAIL, email);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    thisFragment = this;
    if (getArguments() != null) {
      email = (SimpleEmail) getArguments().getSerializable(ARG_SIMPLE_EMAIL);
      detail_type = (DetailType) getArguments().getSerializable(ARG_DETAIL_TYPE);
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.email_detail_fragment, container, false);
    thisView = view;
    TextView titleText = view.findViewById(R.id.detail_title);
    TextView fromAddressText = view.findViewById(R.id.from_address_text);
    TextView toAddressText = view.findViewById(R.id.to_address_text);
    TextView subjectText = view.findViewById(R.id.subject_text);
    encryptionStatus = view.findViewById(R.id.status_encryption);
    signatureStatus = view.findViewById(R.id.status_signature);
    decryptButton = view.findViewById(R.id.button_decrypt);
    verifySignatureButton = view.findViewById(R.id.button_verify_sign);
    FloatingActionButton fab = view.findViewById(R.id.send_fab);
    messageText = view.findViewById(R.id.message_text);

    fromAddressText.setText(email.fromAddress);
    toAddressText.setText(email.toAddress);
    subjectText.setText(email.subject);
    UpdateUI();

    if (detail_type == DetailType.VIEW) {

      titleText.setText(R.string.detail_title_inbox);
      fab.hide();
      new GetEmailContentTask().execute(email);

    } else if (detail_type == DetailType.READY_SEND) {

      titleText.setText(R.string.detail_title_ready_send);
      Snackbar.make(view, "Message Ready", Snackbar.LENGTH_LONG)
          .setAction("Action", null).show();
      messageText.setText(email.message);
      fab.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
            Snackbar.make(view, "SENDING", Snackbar.LENGTH_INDEFINITE)
                .setAction("Action", null).show();
            new SendEmailTask().execute(email);
        }
      });

    } else if (detail_type == DetailType.TEMP_VIEW) {

      titleText.setText(R.string.detail_title_temp_view);
      fab.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          NavHostFragment.findNavController(thisFragment).popBackStack();
        }
      });
      fab.setImageResource(R.drawable.ic_cancel);
      messageText.setText(email.message);
      new DecryptionTask().execute(email.message);

    } else {

      messageText.setText(R.string.test_string);
      fab.hide();

    }
    return view;
  }

  private void UpdateUI() {
    if (email.isEncrypted()) {
      encryptionStatus.setText(getString(R.string.encryption_yes));
      decryptButton.setEnabled(true);
      decryptButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          Bundle args = new Bundle();
          SimpleEmail decryptedEmail = new SimpleEmail(email);
          decryptedEmail.message = email.getEncryptedMessage();
          args.putSerializable(EmailDetailFragment.ARG_SIMPLE_EMAIL, decryptedEmail);
          args.putSerializable(EmailDetailFragment.ARG_DETAIL_TYPE, DetailType.TEMP_VIEW);
          NavHostFragment.findNavController(thisFragment).navigate(R.id.emailDetailFragment, args);
        }
      });
    } else {
      encryptionStatus.setText(getString(R.string.encryption_no));
      decryptButton.setEnabled(false);
    }
    if (email.isSigned()) {
      signatureStatus.setText(getString(R.string.signature_yes));
      verifySignatureButton.setEnabled(true);
    } else {
      signatureStatus.setText(getString(R.string.signature_no));
      verifySignatureButton.setEnabled(false);
    }
  }

  @SuppressLint("StaticFieldLeak")
  private class GetEmailContentTask extends AsyncTask<SimpleEmail, String, Void> {

    @Override
    protected Void doInBackground(SimpleEmail... simpleEmails) {
      for (SimpleEmail simpleEmail: simpleEmails) {
        try {
          simpleEmail.getMessageContent();
          publishProgress(simpleEmail.message);
        } catch (MessagingException| IOException e) {
          e.printStackTrace();
        }
      }
      return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
      super.onProgressUpdate(values);
      for (String value:values) {
        messageText.setText(value);
        UpdateUI();
      }
    }
  }


  @SuppressLint("StaticFieldLeak")
  private class SendEmailTask extends AsyncTask<SimpleEmail, Void, Boolean> {

    @Override
    protected Boolean doInBackground(SimpleEmail... emails) {
      try {
        for (SimpleEmail email : emails) {
          EmailUtil.sendEmail(email);
        }
        return true;
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
    }

    @Override
    protected void onPostExecute(Boolean successful) {
      super.onPostExecute(successful);
      createEmailStatusSnackBar(successful);
    }
  }

  private void createEmailStatusSnackBar(boolean isSuccessful) {
    if (isSuccessful) {
      NavHostFragment.findNavController(thisFragment).popBackStack();
      NavHostFragment.findNavController(thisFragment).popBackStack();
    } else {
      NavHostFragment.findNavController(thisFragment).popBackStack();
    }
  }

  @SuppressLint("StaticFieldLeak")
  private class DecryptionTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... toDecrypts) {
      Snackbar.make(thisView, "ENCRYPTING", Snackbar.LENGTH_INDEFINITE)
          .setAction("Action", null).show();
      String toDecrypt = toDecrypts[0];
      return PythonRunner.Decrypt(getContext(), toDecrypt, "key.dat");
    }

    @Override
    protected void onPostExecute(String s) {
      super.onPostExecute(s);
      messageText.setText(s);
      Snackbar.make(thisView, "Done", Snackbar.LENGTH_LONG)
          .setAction("Action", null).show();
    }
  }
}
