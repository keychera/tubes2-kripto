package com.keychera.cryptemail;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

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
        return new PasswordAuthentication(Config.EMAIL, Config.PASSWORD);
      }
    };

    Session session = Session.getInstance(props, auth);
    MimeMessage msg = new MimeMessage(session);
    //set message headers
    msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
    msg.addHeader("format", "flowed");
    msg.addHeader("Content-Transfer-Encoding", "8bit");

    msg.setFrom(new InternetAddress(Config.EMAIL, Config.USERNAME));

    msg.setReplyTo(InternetAddress.parse(email.toAddress, false));

    msg.setSubject(email.subject, "UTF-8");

    msg.setText(email.message, "UTF-8");

    msg.setSentDate(new Date());

    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.toAddress, false));
    System.out.println("Message is ready");
    Transport.send(msg);

    System.out.println("Email Sent Successfully!!");
  }
}