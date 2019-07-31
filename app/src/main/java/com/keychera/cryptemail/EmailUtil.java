package com.keychera.cryptemail;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailUtil {

  public EmailUtil() {}

  /**
   * Utility method to send simple HTML email
   * @param email
   */
  public static void sendEmail(SimpleEmail email)
    throws MessagingException, UnsupportedEncodingException {
    Properties props = new Properties();
    props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
    props.put("mail.smtp.port", "587"); //TLS Port
    props.put("mail.smtp.auth", "true"); //enable authentication
    props.put("mail.smtp.starttls.enable", "true");

    Authenticator auth = new Authenticator() {
      //override the getPasswordAuthentication method
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(Config.getInstance().email, Config.getInstance().password);
      }
    };

    Session session = Session.getInstance(props, auth);
    MimeMessage msg = new MimeMessage(session);
    //set bodyText headers
    msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
    msg.addHeader("format", "flowed");
    msg.addHeader("Content-Transfer-Encoding", "8bit");

    msg.setFrom(new InternetAddress(Config.getInstance().email));

    msg.setReplyTo(InternetAddress.parse(email.toAddress, false));

    msg.setSubject(email.subject, "UTF-8");
    msg.setSentDate(new Date());

    if (email.attachFiles == null) {
      msg.setText(email.bodyText, "UTF-8");
    } else {
      Multipart multipart = new MimeMultipart();

      MimeBodyPart msgBodyPart = new MimeBodyPart();
      msgBodyPart.setText(email.bodyText);
      multipart.addBodyPart(msgBodyPart);

      msgBodyPart = new MimeBodyPart();
      DataSource source = new FileDataSource(email.attachFiles);
      msgBodyPart.setDataHandler(new DataHandler(source));
      msgBodyPart.setFileName(source.getName());
      multipart.addBodyPart(msgBodyPart);

      msg.setContent(multipart);
    }

    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.toAddress, false));
    System.out.println("Message is ready");
    Transport.send(msg);

    System.out.println("Email Sent Successfully!!");
  }

  public String getTextFromMessage(Message message) throws IOException, MessagingException {
    String result = "";
    if (message.isMimeType("text/plain")) {
      result = message.getContent().toString();
    } else if (message.isMimeType("multipart/*")) {
      MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
      result = getTextFromMimeMultipart(mimeMultipart);
    }
    return result;
  }

  public String getTextFromMimeMultipart(
      MimeMultipart mimeMultipart) throws IOException, MessagingException {

    int count = mimeMultipart.getCount();
    if (count == 0)
      throw new MessagingException("Multipart with no body parts not supported.");
    boolean multipartAlt = new ContentType(mimeMultipart.getContentType()).match("multipart/alternative");
    if (multipartAlt)
      // alternatives appear in an order of increasing
      // faithfulness to the original content. Customize as req'd.
      return getTextFromBodyPart(mimeMultipart.getBodyPart(count - 1));
    String result = "";
    for (int i = 0; i < count; i++) {
      BodyPart bodyPart = mimeMultipart.getBodyPart(i);
      result += getTextFromBodyPart(bodyPart);
    }
    return result;
  }

  public String getTextFromBodyPart(
      BodyPart bodyPart) throws IOException, MessagingException {

    String result = "";
    if (bodyPart.isMimeType("text/plain")) {
      result = (String) bodyPart.getContent();
    } else if (bodyPart.isMimeType("text/html")) {
      String html = (String) bodyPart.getContent();
      result = org.jsoup.Jsoup.parse(html).text();
    } else if (bodyPart.getContent() instanceof MimeMultipart){
      result = getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
    }
    return result;
  }
}