package hello.springjpa.webapp.infra.mail;

public interface EmailService {

    void sendEmail(EmailMessage emailMessage);
}
