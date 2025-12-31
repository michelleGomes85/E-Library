package br.elibrary.notification.consumer.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@ApplicationScoped
public class EmailService {

    private Session mailSession;
    
    private Properties readProps() {
        Properties props = new Properties();
        
        try (InputStream is = EmailService.class
                .getClassLoader()
                .getResourceAsStream("local-config.properties")) {
            
            if (is != null) {
                props.load(is);
                System.out.println("local-config.properties carregado do classpath");
                return props;
            } else {
                System.out.println("local-config.properties NÃO encontrado no classpath");
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar configuração: " + e.getMessage());
        }
        
        return props;
    }

    @PostConstruct
    public void init() {
        
    	Properties props = readProps();
        
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2"); 

        String username = props.getProperty("mail.user");
        String password = props.getProperty("mail.password");
        
        if (username == null || password == null) {
            throw new IllegalStateException(
                "Erro: mail.user ou mail.password não definidos em " + 
                System.getenv("ELIBRARY_CONFIG_DIR") + "/local-config.properties"
            );
        }

        mailSession = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public void send(String to, String subject, String body) throws MessagingException {
        MimeMessage msg = new MimeMessage(mailSession);
        msg.setFrom(new InternetAddress("gmichele498@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO, to);
        msg.setSubject(subject);
        msg.setText(body);
        Transport.send(msg);
    }
}