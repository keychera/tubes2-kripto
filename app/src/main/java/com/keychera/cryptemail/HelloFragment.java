package com.keychera.cryptemail;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;


/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment must implement the
 * {@link HelloFragment.OnFragmentInteractionListener} interface to handle interaction events. Use
 * the {@link HelloFragment#newInstance} factory method to create an instance of this fragment.
 */
public class HelloFragment extends Fragment {
  private OnFragmentInteractionListener mListener;
  private Fragment thisFragment;
  private View thisView;
  private TextView textEmail;

  public HelloFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @return A new instance of fragment HelloFragment.
   */
  public static HelloFragment newInstance() {
    return new HelloFragment();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    thisFragment = this;
    View view = inflater.inflate(R.layout.fragment_hello, container, false);
    thisView = view;
    //set FAB
    FloatingActionButton fab = view.findViewById(R.id.compose_fab);
    fab.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        NavHostFragment.findNavController(thisFragment).navigate(R.id.composeFragment);
      }
    });

    textEmail = view.findViewById(R.id.text_email);
    textEmail.setText(Config.getInstance().email);

    Button loginButton = view.findViewById(R.id.button_login);
    loginButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        EditText emailText = view.getRootView().findViewById(R.id.input_email);
        EditText passText = view.getRootView().findViewById(R.id.input_password);
        Config.getInstance().email = emailText.getText().toString();
        Config.getInstance().password = passText.getText().toString();
        textEmail.setText(Config.getInstance().email);
        emailText.setText(null);
        passText.setText(null);
      }
    });

    Button generateKeyPairButton = view.findViewById(R.id.button_key_generate);
    generateKeyPairButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        TextView textKeyName = view.getRootView().findViewById(R.id.key_name);
        String keyname = textKeyName.getText().toString();
        new GenerateKeyPairTask().execute(keyname);
      }
    });

    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  /**
   * This interface must be implemented by activities that contain this fragment to allow an
   * interaction in this fragment to be communicated to the activity and potentially other fragments
   * contained in that activity.
   * <p>
   * See the Android Training lesson <a href= "http://developer.android.com/training/basics/fragments/communicating.html"
   * >Communicating with Other Fragments</a> for more information.
   */
  public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Uri uri);
  }

  private class GenerateKeyPairTask extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String... keynames) {
      Snackbar.make(thisView, "GENERATING KEYPAIR", Snackbar.LENGTH_SHORT)
          .setAction("Action", null).show();
      ECDSA generator = new ECDSA();
      String keyname = keynames[0];
      try {
        generator.saveKeyPair(keyname);
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
      return true;
    }

    @Override
    protected void onPostExecute(Boolean isSuccesful) {
      super.onPostExecute(isSuccesful);
      if(isSuccesful) {
        Snackbar.make(thisView, "Success! keypair saved to " + FileHelper.path, Snackbar.LENGTH_SHORT)
            .setAction("Action", null).show();
      } else {
        Snackbar.make(thisView, "Failed!", Snackbar.LENGTH_SHORT)
            .setAction("Action", null).show();
      }

    }
  }
}
