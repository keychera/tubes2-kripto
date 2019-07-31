package com.keychera.cryptemail;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;


public class SimpleEmail implements Serializable {
  public static final String ENCRYPTED_TAG_START = "==ENCRYPTED VAL START==\n";
  public static final String ENCRYPTED_TAG_END = "\n==ENCRYPTED VAL END==";
  private static final String SIGNATURE_TAG_START = "\n==SIGNATURE START==\n";
  private static final String SIGNATURE_TAG_END = "\n==SIGNATURE END==\n";

  public String fromAddress;
  public String toAddress;
  public String subject;
  public Date sentDate;
  public Date receivedDate;
  public String message;
  public String contentType;
  public Object content;
  public String attachFiles;


  SimpleEmail() {}

  SimpleEmail(SimpleEmail email) {
    fromAddress = email.fromAddress;
    toAddress = email.toAddress;
    subject = email.subject;
    sentDate = email.sentDate;
    receivedDate = email.receivedDate;
    message = email.message;
    contentType = email.contentType;
    content = email.content;
    attachFiles = email.attachFiles;
  }

  SimpleEmail(Message message) throws MessagingException, IOException {
    subject = message.getSubject();
    fromAddress = message.getFrom()[0].toString();
    toAddress = message.getRecipients(RecipientType.TO)[0].toString();
    sentDate = message.getSentDate();
    receivedDate = message.getReceivedDate();
    contentType = message.getContentType();
    content = message.getContent();
  }

  boolean isValid() {
    return !subject.isEmpty() && !toAddress.isEmpty() && isEmailValid(toAddress);
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

  public void getMessageContent() throws IOException, MessagingException {
    StringBuilder messageContent = new StringBuilder();
    if (contentType.contains("text/plain")
        || contentType.contains("text/html")) {
      if (content != null) {
        messageContent.append(content.toString());
      }
    }

    if (contentType.contains("multipart")) {
      Multipart multipart = (Multipart) content;
      if (multipart != null) {
        for (int i = 0; i < multipart.getCount(); i++) {
          MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
          messageContent.append(part.getContent().toString());
        }
      }
      message = messageContent.toString();
    } else {
      message = String.valueOf(content);
    }

    attachFiles = "";

    if (contentType.contains("multipart")) {
      Multipart multipart = (Multipart) content;
      for (int i = 0; i < multipart.getCount(); i++) {
        MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
          String fileName = part.getFileName();
          attachFiles += fileName + ", ";
        }
      }
    }
    if (attachFiles.length() > 1) {
      attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
    }

  }

  public boolean isEncrypted() {
    if (message == null) {
      return false;
    } else {
      String encryptedRegex = ENCRYPTED_TAG_START + "(.*?)" + ENCRYPTED_TAG_END;
      Pattern pat = Pattern.compile(encryptedRegex, Pattern.DOTALL);
      Matcher matcher = pat.matcher(message);
      return matcher.matches();
    }
  }

  public String getEncryptedMessage() {
    if (message == null) {
      return null;
    } else {
      String encryptedRegex = ENCRYPTED_TAG_START + "(.*?)" + ENCRYPTED_TAG_END;
      Pattern pat = Pattern.compile(encryptedRegex, Pattern.DOTALL);
      Matcher matcher = pat.matcher(message);
      if (matcher.find()) {
        return matcher.group(1);
      }
      return null;
    }
  }

  public boolean isSigned() {
    return false;
  }
}
