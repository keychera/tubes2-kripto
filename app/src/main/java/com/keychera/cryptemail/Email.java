package com.keychera.cryptemail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Email {
  public String fromAddress;
  public String toAddress;
  public String subject;
  public Date sentDate;
  public String message;

  public Email() {

  }

  public boolean isValid() {
    return !subject.isEmpty() && !message.isEmpty() && !toAddress.isEmpty();
  }
}
