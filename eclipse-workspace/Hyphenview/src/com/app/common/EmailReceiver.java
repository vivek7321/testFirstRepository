
package com.app.common;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author kp
 */
public class EmailReceiver {

    private static final String TRUE_SYMBOL_Y = "y";

    private static final String MENU_SEND_OPTION = "1";
    private static final String MENU_RECEIVE_OPTION = "2";
    private static final String MENU_EXIT_OPTION = "3";

    public static void main(String[] args) {
        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        while (true) {
            try {

                System.out.print("Input option: ");
                String option = "2";

                if (MENU_SEND_OPTION.equalsIgnoreCase(option)) {
                    send();
                } else if (MENU_RECEIVE_OPTION.equalsIgnoreCase(option)) {
                    receive();
                } else if (MENU_EXIT_OPTION.equalsIgnoreCase(option)) {
                    // Sign out
                    break;
                }

                System.out.flush();
            } catch (Exception e) {
                System.out.println("I/O exception");
                e.printStackTrace();
            }
        }

        System.out.println("\r\nEND!");
    }

    private static void send() throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Please input smtp host: ");
        String host = reader.readLine();
        System.out.print("Please input smtp port: ");
        String port = reader.readLine();
        System.out.print("Please input email address: ");
        String email = reader.readLine();
        System.out.print("Please input email password: ");
        String password = reader.readLine();
        System.out.print("Is SSL(y/n): ");
        String isSsl = reader.readLine();
        String isStartTls = "";
        if (!TRUE_SYMBOL_Y.equalsIgnoreCase(isSsl)) {
            System.out.print("Is SSL(y/n): ");
            isStartTls = reader.readLine();
        }
        System.out.println("################### Message ########################");
        System.out.print("Please input receiver: ");
        String receiver = reader.readLine();
        System.out.print("Please input subject: ");
        String subject = reader.readLine();
        System.out.print("Please input content: ");
        String content = reader.readLine();
        System.out.println("##################### END ##########################");

        // Configure connection parameters
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", port);
        props.setProperty("mail.smtp.timeout", "20000");
        props.setProperty("mail.smtp.connectiontimeout", "20000");
        // The improper handling of SSL and STARTLS in this area needs to be corrected.
        if (TRUE_SYMBOL_Y.equalsIgnoreCase(isSsl)) {
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.smtp.ssl.enable", "true");
        } else if (TRUE_SYMBOL_Y.equalsIgnoreCase(isStartTls)) {
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.smtp.starttls.enable", "true");
        }

        try {
            // Getting Sessions
            Session session = Session.getInstance(props);

            // Building Mail Information
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
            message.setSubject(subject);
            // Setting content
            Multipart contentPart = new MimeMultipart();
            BodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(content, "text/html;charset=UTF-8");
            contentPart.addBodyPart(bodyPart);
            message.setContent(contentPart);

            // Connect and send
            Transport transport = session.getTransport();
            transport.connect(email, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            System.out.println("********************** Result **********************");
            System.out.println("send success!");
            System.out.println("****************************************************");
        } catch (Exception e) {
            System.out.println("********************** Result **********************");
            System.out.println("send fail!");
            System.out.println("****************************************************");
            e.printStackTrace();
        }
    }

    private static void receive() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Please input imap host: ");
        String host= "outlook.office365.com";
        System.out.print("Please input imap port: ");
        String port="993";
        System.out.print("Please input email address: ");
        String email ="test001@erasmith.com";
        System.out.print("Please input email password: ");
        String password ="Erasmith@123";
        System.out.print("Is SSL(y/n): ");
        String isSsl = "y";

        // Building basic configuration information
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imap.host", host);
        props.setProperty("mail.imap.port", port);
        props.setProperty("mail.imap.timeout", "20000");
        props.setProperty("mail.imap.connectiontimeout", "20000");
        if (TRUE_SYMBOL_Y.equalsIgnoreCase(isSsl)) {
            props.setProperty("mail.imap.auth", "true");
            props.setProperty("mail.imap.ssl.enable", "true");
            props.setProperty("mail.imap.ssl.trust", host);
        }

        try {
            Session session = Session.getDefaultInstance(props);
            Store store = session.getStore();
            store.connect(email, password);

            Folder folder = store.getFolder("INBOX");
            if (folder.isOpen()) {
                System.out.println("The folder is opened");
            } else {
                System.out.println("Open the folder");
                folder.open(Folder.READ_ONLY);
            }

            System.out.println("********************** Result **********************");
            System.out.println("receive success!");
            System.out.println(folder.getFullName());
            System.out.println("total email count: " + folder.getMessageCount());
            System.out.println("unread email count: " + folder.getUnreadMessageCount());
            System.out.println("deleted email count: " + folder.getDeletedMessageCount());
            System.out.println("new email count: " + folder.getNewMessageCount());
            System.out.println("****************************************************");
        } catch (Exception e) {
            System.out.println("********************** Result **********************");
            System.out.println("receive fail!");
            System.out.println("****************************************************");
            e.printStackTrace();
        }
    }
}