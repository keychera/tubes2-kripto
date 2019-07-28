package com.keychera.cryptemail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Email {
  public List<String> fromAddress;
  public List<String> toAddress;
  public String subject;
  public Date sentDate;

  public Email() {
    fromAddress = new ArrayList<>();
    toAddress = new ArrayList<>();
  }
}
