package com.keychera.cryptemail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.internal.StringUtil;


public class SimpleEmail implements Serializable {
  public static final String ENCRYPTED_TAG_START = "==ENCRYPTED VAL START==";
  public static final String ENCRYPTED_TAG_END = "==ENCRYPTED VAL END==";
  public static final String SIGNATURE_TAG_START = "==SIGNATURE START==";
  public static final String SIGNATURE_TAG_END = "==SIGNATURE END==";


  public String fromAddress;
  public String toAddress;
  public String subject;
  public Date sentDate;
  public Date receivedDate;
  public String bodyText;
  public String attachFiles;
  Object content;
  String contentType;


  SimpleEmail() {}

  SimpleEmail(SimpleEmail email) {
    fromAddress = email.fromAddress;
    toAddress = email.toAddress;
    subject = email.subject;
    sentDate = email.sentDate;
    receivedDate = email.receivedDate;
    bodyText = email.bodyText;
    attachFiles = email.attachFiles;
    content = email.content;
    contentType = email.contentType;
  }

  SimpleEmail(Message message) throws MessagingException, IOException {
    subject = message.getSubject();
    fromAddress = message.getFrom()[0].toString();
    toAddress = message.getRecipients(RecipientType.TO)[0].toString();
    sentDate = message.getSentDate();
    receivedDate = message.getReceivedDate();
    content = message.getContent();
    contentType = message.getContentType();
    getMessageContent();
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
    EmailUtil emailUtil = new EmailUtil();
    String result = "No Message";
    if (contentType.toLowerCase().contains("text/plain") || contentType.toLowerCase().contains("text/html")) {
      result = content.toString();
    } else if (contentType.toLowerCase().contains("multipart")) {
      MimeMultipart mimeMultipart = (MimeMultipart) content;
      result = EmailUtil.getTextFromMimeMultipart(mimeMultipart);
    }
    bodyText = result;
  }

  public List<File> getAttachment() throws MessagingException, IOException {
    if (contentType.contains("multipart")) {
      List<File> attachments = new ArrayList<>();
      Multipart multipart = (Multipart) content;
      for (int i = 0; i < multipart.getCount(); i++) {
        BodyPart bodyPart = multipart.getBodyPart(i);
        InputStream is = bodyPart.getInputStream();
        if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) &&
            StringUtils.isBlank(bodyPart.getFileName())) {
          continue; // dealing with attachments only
        }
        // --- SECURITY ISSUE --
        // do not do this in production code -- a malicious email can easily contain this filename: "../etc/passwd", or any other path: They can overwrite _ANY_ file on the system that this code has write access to!
        File f = new File(FileHelper.path + bodyPart.getFileName());
        FileOutputStream fos = new FileOutputStream(f);
        byte[] buf = new byte[4096];
        int bytesRead;
        while ((bytesRead = is.read(buf)) != -1) {
          fos.write(buf, 0, bytesRead);
        }
        fos.close();
        attachments.add(f);
      }
      return attachments;
    } else {
      return null;
    }
  }

  public String getEncryptedMessage() {
    if (bodyText != null) {
      String plain = StringUtils.substringBetween(bodyText, ENCRYPTED_TAG_START, ENCRYPTED_TAG_END);
      if (plain != null)
        return plain.replace("\n","");
      else
        return null;
    } else {
      return null;
    }
  }

  public String getSignature() {
    if (bodyText != null) {
      String plain = StringUtils.substringBetween(bodyText, SIGNATURE_TAG_START, SIGNATURE_TAG_END);
      if (plain != null)
        return plain.replace("\n","");
      else
        return null;
    } else {
      return null;
    }
  }

  public String getSignedData() {
    String signedData= getEncryptedMessage();
    if (signedData == null) {
      signedData = StringUtils.substringBefore(bodyText, SIGNATURE_TAG_START);

    }
    return signedData;
  }
}
