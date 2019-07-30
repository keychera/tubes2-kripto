package com.keychera.cryptemail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;


public class SimpleEmail implements Serializable {
  public String fromAddress;
  public String toAddress;
  public String subject;
  public Date sentDate;
  public Date receivedDate;
  public String message;

  SimpleEmail() {}

  boolean isValid() {
    return !subject.isEmpty() && !message.isEmpty() && !toAddress.isEmpty() && isEmailValid(toAddress);
  }

  private boolean isEmailValid(String email)
  {
    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
        "[a-zA-Z0-9_+&*-]+)*@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
        "A-Z]{2,7}$";

    Pattern pat = Pattern.compile(emailRegex);
    if (email == null)
      return false;
    return pat.matcher(email).matches();
  }

  public boolean isEncrypted() {
    return false;
  }

  public boolean isSigned() {
    return false;
  }
}
