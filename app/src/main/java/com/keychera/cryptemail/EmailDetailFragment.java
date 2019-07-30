package com.keychera.cryptemail;

import android.widget.Button;
import android.widget.TextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EmailDetailFragment extends Fragment {

  public static final String ARG_SIMPLE_EMAIL = "simple-email";

  private SimpleEmail email;

  public static EmailDetailFragment newInstance(SimpleEmail email) {
    EmailDetailFragment fragment = new EmailDetailFragment();
    Bundle args = new Bundle();
    args.putSerializable(ARG_SIMPLE_EMAIL, email);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      email = (SimpleEmail) getArguments().getSerializable(ARG_SIMPLE_EMAIL);
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.email_detail_fragment, container, false);

    TextView fromAddressText = view.findViewById(R.id.from_address_text);
    TextView toAddressText = view.findViewById(R.id.to_address_text);
    TextView subjectText = view.findViewById(R.id.subject_text);
    TextView encryptionStatus = view.findViewById(R.id.status_encryption);
    TextView signatureStatus = view.findViewById(R.id.status_signature);
    Button decryptButton = view.findViewById(R.id.button_decrypt);
    Button verifySignatureButton = view.findViewById(R.id.button_verify_sign);

    fromAddressText.setText(email.fromAddress);
    toAddressText.setText(email.toAddress);
    subjectText.setText(email.subject);
    if (email.isEncrypted()) {
      encryptionStatus.setText(getString(R.string.encryption_yes));
      decryptButton.setEnabled(true);
    } else {
      encryptionStatus.setText(getString(R.string.encryption_no));
      decryptButton.setEnabled(false);
    }
    if (email.isEncrypted()) {
      signatureStatus.setText(getString(R.string.signature_yes));
      verifySignatureButton.setEnabled(true);
    } else {
      signatureStatus.setText(getString(R.string.signature_no));
      verifySignatureButton.setEnabled(false);
    }
    return view;
  }
}
