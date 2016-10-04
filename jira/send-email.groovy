import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.mail.Email
import com.atlassian.mail.server.MailServerManager
import com.atlassian.mail.server.SMTPMailServer


class Mailer {
    private SMTPMailServer mailServer
    public Mailer() {
        MailServerManager mailServerManager = ComponentAccessor.getMailServerManager()
        this.mailServer = mailServerManager.getDefaultSMTPMailServer()
    }
    
    void sendMail(String emailAddress, String subject, String content) {
        Email email = new Email(emailAddress);
        email.setSubject(subject);
        email.setBody(content)
        mailServer.send(email)
    }
}

mailer = new Mailer();
mailer.sendMail("john.doe@domain.tld", "test subject", "some content");

