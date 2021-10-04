package ro.utcn.frontend.frontendServices.helper;

/**
 * Mail
 *
 * Created by Lucian on 7/26/2017.
 */
public class MailMessage {

    private String mailTo;
    private String message;
    private String subject;

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
