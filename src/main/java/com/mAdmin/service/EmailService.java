package com.mAdmin.service;

import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.InvoiceRecord;
import com.mAdmin.entity.Mail;
import com.mAdmin.entity.PasswordResetToken;
import com.mAdmin.entity.Subscription;
import com.mAdmin.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@PropertySource("classpath:email_subject.properties")
public class EmailService {

    
    @Autowired
    private JavaMailSender emailSender;

    
    @Autowired
    private TemplateEngine emailTemplateEngine;

    
    @Value("${application.url}")
    private String applicationUrl;

    
    @Value("${application.mail.from}")
    private String mailFrom;

    
    @Async
    public void sendEmail(SimpleMailMessage email) {
        emailSender.send(email);
    }

    
    @Autowired
    private Environment env;

    
    private static final String SIGNATURE = "MAdmin©";

    
    private static final int DELAY_TIME = 6000;

    
    public Mail formPasswordRecoveryMail(User user, PasswordResetToken token) {

        Mail mail = new Mail();
        prepareGenericMailData(mail, user, env.getProperty("password.reset.title"));

        Map<String, Object> model = passwordRecoverySetUp(user, token);
        mail.setModel(model);

        return mail;
    }

    
    public String[] generateRecipientsQueue(List<Client> clients) {
        String[] recipients = new String[clients.size()];

        for (int i = 0; i < clients.size(); i++) {

            recipients[i] = clients.get(i).getEmail();

        }

        return recipients;
    }

    
    public Mail formNewUserPasswordMail(User user, PasswordResetToken token) {
        Mail mail = new Mail();
        prepareGenericMailData(mail, user, env.getProperty("password.create"));

        Map<String, Object> model = passwordRecoverySetUp(user, token);
        mail.setModel(model);

        return mail;
    }

    
    public Mail formCustomMailPreview(String text, String subject) {
        Mail mail = new Mail();
        prepareGenericMailData(mail, subject);
        Map<String, Object> model = new HashMap<>();

        model.put("customText", text);
        model.put("applicationurl", applicationUrl);
        mail.setModel(model);

        return mail;
    }

    
    public void formAndSendMemoMail(Client client, String subjectKey, String templatePath) {

        Mail mail = new Mail();
        prepareGenericMailData(mail, client, env.getProperty(subjectKey));
        Map<String, Object> model = new HashMap<>();

        model.put("applicationUrl", applicationUrl);

        model.put("applicationurl", applicationUrl);
        mail.setModel(model);
        sendEmailByModel(mail, templatePath);
    }

    
    private Mail formContractExpiringMail() {
        Mail mail = new Mail();
        prepareGenericMailData(mail, env.getProperty("client.contract.expires"));
        Map<String, Object> model = new HashMap<>();

        model.put("applicationurl", applicationUrl);
        mail.setModel(model);

        return mail;
    }

    
    public Mail formInvoiceMail(Invoice invoice, List<InvoiceRecord> invoiceRecordList) {

        Mail mail = new Mail();
        prepareGenericMailData(mail, invoice.getClient(), env.getProperty("new.invoice"));
        Map<String, Object> model = new HashMap<>();

        model.put("client", invoice.getClient());
        model.put("invoiceRecord", invoiceRecordList);
        model.put("applicationUrl", applicationUrl);

        model.put("applicationurl", applicationUrl);
        mail.setModel(model);

        return mail;
    }

    
    public Mail formInvoiceReminderMail(Client client, List<InvoiceRecord> invoiceRecordList) {
        Mail mail = new Mail();
        prepareGenericMailData(mail, client, env.getProperty("new.invoice"));
        Map<String, Object> model = new HashMap<>();

        model.put("invoiceRecord", invoiceRecordList);

        model.put("applicationurl", applicationUrl);
        mail.setModel(model);

        return mail;
    }

    
    public void formAndSendReservedAttendeeMail(Client client, String template) {

        Mail mail = new Mail();
        prepareGenericMailData(mail, client, env.getProperty("attendee.saved.to.reserve"));

        Map<String, Object> model = new HashMap<>();

        model.put("applicationurl", applicationUrl);
        mail.setModel(model);
        sendEmailByModel(mail, template);
    }

    
    @Async
    public void sendEmailByModel(Mail mail, String template) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariables(mail.getModel());
            String html = emailTemplateEngine.process(template, context);

            helper.setTo(mail.getTo());
            helper.setText(html, true);
            helper.setSubject(mail.getSubject());
            helper.setFrom(mail.getFrom());

            emailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    @Async
    public synchronized void sendEmailByModelForMultipleRecipients(Mail mail, String template, String[] recipients) {

        for (String recipient : recipients) {

            try {

                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message,
                        MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

                Context context = new Context();
                context.setVariables(mail.getModel());
                String html = emailTemplateEngine.process(template, context);

                helper.setTo(recipient);
                helper.setText(html, true);
                helper.setSubject(mail.getSubject());
                helper.setFrom(mail.getFrom());
                Thread.sleep(DELAY_TIME);
                emailSender.send(message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

    }

    
    public String formCustomMailPreviewHtml(Mail mail, String template) {
        Context context = new Context();
        context.setVariables(mail.getModel());
        return emailTemplateEngine.process(template, context);
    }

    
    public Mail formBirthdaysGreetingMail(User user, List<Attendee> attendeeList, String weekStart, String weekEnd) {
        Mail mail = new Mail();

        String message = MessageFormat.format(Objects.requireNonNull(env.getProperty("birthday.greeting")),
                weekStart, weekEnd);

        prepareGenericMailData(mail, user, message);

        Map<String, Object> model = new HashMap<>();
        model.put("applicationurl", applicationUrl);
        model.put("user", user);
        model.put("attendeeList", attendeeList);
        model.put("weekStart", weekStart);
        model.put("weekEnd", weekEnd);
        mail.setModel(model);

        return mail;
    }

    
    public Mail formInvoicesNotPaidMail(User user, List<Invoice> invoices, List<String> contractIDs) {

        Mail mail = new Mail();
        prepareGenericMailData(mail, user, "Skolininkai");
        Map<String, Object> model = new HashMap<>();
        model.put("user", user);
        model.put("invoices", invoices);
        model.put("contracts", contractIDs);
        model.put("applicationurl", applicationUrl);
        mail.setModel(model);
        return mail;
    }

    
    public void formAndSendSubscriptionWillExpireMail(User user, List<Subscription> subscriptions, String template) {

        Mail mail = new Mail();
        prepareGenericMailData(mail, user, env.getProperty("subscription.reminder"));
        Map<String, Object> model = new HashMap<>();
        model.put("user", user);
        model.put("subscriptions", subscriptions);
        model.put("applicationurl", applicationUrl);
        mail.setModel(model);
        sendEmailByModel(mail, template);
    }

    
    public Mail formEmailConfirmationMail(Client user, String confirmationCode) {
        Mail mail = new Mail();
        prepareGenericMailData(mail, user, env.getProperty("mail.confirmation"));
        Map<String, Object> model = new HashMap<>();
        model.put("user", user);
        model.put("confirmationCode", confirmationCode);
        model.put("applicationurl", applicationUrl);
        mail.setModel(model);

        return mail;
    }

    
    public void sendCoachesUnmarkedAttendanceMail(List<Map<String, Object>> unmarkedCoachesDetails,
            String[] recipients) {

        try {

            final String template = "mail-templates/unmarked-attendance";

            Map<String, Object> model = new HashMap<>();
            model.put("unmarkedCoachesDetails", unmarkedCoachesDetails);

            Map<String, Object> map = formMimeMessageMailMap(model, template);

            MimeMessageHelper helper = (MimeMessageHelper) map.get("helper");
            helper.setTo(recipients);
            helper.setText((String) map.get("html"), true);
            helper.setSubject(Objects.requireNonNull(env.getProperty("unmarked.attendance")));
            helper.setFrom(mailFrom);

            emailSender.send((MimeMessage) map.get("message"));

        } catch (Exception e) {

            throw new RuntimeException(e);

        }
    }

    
    public void sendAttendeesMissedWorkoutsMail(String[] recipients) {

        try {

            final String template = "mail-templates/attendees-missed-workouts";
            Map<String, Object> model = new HashMap<>();
            Map<String, Object> map = formMimeMessageMailMap(model, template);

            MimeMessageHelper helper = (MimeMessageHelper) map.get("helper");

            mimeHelperMethod(helper, (String) map.get("html"),
                    env.getProperty("attendees.missed.workouts"), recipients, (MimeMessage) map.get("message"));

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

    
    public void sendExpiringMedicalCertificateReminder(Client client) {

        try {
            sendMimeMessageMail(client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    
    public void sendPendingMedicalCertificatesReminder(String[] recipients) {

        try {
            sendMimeMessageMail(recipients);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    public void formAndSendContractTerminationMail(User user, String subject, List<Contract> contracts,
                                                   String template) {

        Mail mail = new Mail();
        prepareGenericMailData(mail, user, env.getProperty(subject));

        Map<String, Object> model = new HashMap<>();
        model.put("applicationurl", applicationUrl);
        model.put("user", user);
        model.put("contracts", contracts);
        mail.setModel(model);

        sendEmailByModel(mail, template);
    }

    
    public void formAndSendContractTerminationNotificationMail(User user, String subject, Contract contract,
                                                               String template) {

        Mail mail = new Mail();
        Map<String, Object> model = new HashMap<>();

        prepareGenericMailData(mail, user, env.getProperty(subject));
        model.put("user", user);

        model.put("contract", contract);
        model.put("applicationurl", applicationUrl);
        mail.setModel(model);
        sendEmailByModel(mail, template);
    }

    
    private void prepareGenericMailData(Mail mail, User user, String subject) {
        mail.setFrom(mailFrom);
        mail.setSubject(subject);
        if (user != null) {
            mail.setTo(user.getEmail());
        }
    }

    
    private void prepareGenericMailData(Mail mail, String subject) {
        prepareGenericMailData(mail, null, subject);
    }

    
    public void formAndSendEmailToClientAboutConfirmCode(Client client) {
        String emailText = client.getConfirmationCode();

        Mail mail = formEmailConfirmationMail(client, emailText);
        sendEmailByModel(mail, "mail-templates/confirmation-template.html");
    }

    
    public String[] prepUserEmails(List<User> users) {

        String[] emails = new String[users.size()];

        for (int i = 0; i < users.size(); i++) {

            emails[i] = users.get(i).getEmail();

        }
        return emails;
    }

    
    private void mimeHelperMethod(MimeMessageHelper helper, String html, String subject, String[] recipients,
                                  MimeMessage message, Client client) throws MessagingException {
        if (client == null) {
            helper.setTo(recipients);
        } else {
            helper.setTo(client.getEmail());
        }
        helper.setText(html, true);
        helper.setSubject(subject);
        helper.setFrom(mailFrom);

        emailSender.send(message);
    }

    
    private void mimeHelperMethod(MimeMessageHelper helper, String html, String subject,
                                  MimeMessage message, Client client) throws MessagingException {
        mimeHelperMethod(helper, html, subject, null, message, client);
    }

    
    private void mimeHelperMethod(MimeMessageHelper helper, String html, String subject, String[] recipients,
                                  MimeMessage message) throws MessagingException {
        mimeHelperMethod(helper, html, subject, recipients, message, null);
    }

    
    public void formAndSendMailNotifyingClientsAboutContractTermination(User user, String template) {

        Mail mail = new Mail();

        mail.setFrom(mailFrom);
        mail.setSubject(env.getProperty("notification.about.termination"));
        mail.setTo(user.getEmail());
        sendEmailByModel(mail, template);
    }

    
    public void formAndSendRefundReminderMail(User user, List<Client> clients, List<BigDecimal> prices,
                                              String template) {

        Mail mail = new Mail();
        prepareGenericMailData(mail, user, env.getProperty("refund.reminder"));
        Map<String, Object> model = new HashMap<>();
        model.put("user", user);
        model.put("clients", clients);
        model.put("prices", prices);
        model.put("applicationurl", applicationUrl);
        mail.setModel(model);
        sendEmailByModel(mail, template);
    }

    
    private Map<String, Object> passwordRecoverySetUp(User user, PasswordResetToken token) {
        Map<String, Object> model = new HashMap<>();
        String resetUrl = applicationUrl + "/reset-password?token=" + token.getToken();

        model.put("user", user);
        model.put("signature", SIGNATURE);
        model.put("resetUrl", resetUrl);
        model.put("applicationurl", applicationUrl);
        return model;
    }

    
    private Map<String, Object> formMimeMessageMailMap(Map<String, Object> model, String template)
            throws MessagingException {

        model.put("applicationUrl", applicationUrl);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(model);
        String html = emailTemplateEngine.process(template, context);
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("message", message);
        returnMap.put("helper", helper);
        returnMap.put("html", html);

        return returnMap;
    }

    
    private void sendMimeMessageMail(String template, String subject, String[] recipients, Client client)
            throws MessagingException {

        Map<String, Object> model = new HashMap<>();
        Map<String, Object> map = formMimeMessageMailMap(model, template);
        MimeMessageHelper helper = (MimeMessageHelper) map.get("helper");

        if (client == null) {
            mimeHelperMethod(helper, (String) map.get("html"),
                    subject, recipients, (MimeMessage) map.get("message"));
        } else {
            mimeHelperMethod(helper, (String) map.get("html"),
                    subject, (MimeMessage) map.get("message"), client);
        }
    }

    
    private void sendMimeMessageMail(String[] recipients)
            throws MessagingException {

        final String template = "mail-templates/medical-certificates-pending";
        sendMimeMessageMail(template, env.getProperty("pending.medical.certificate"), recipients, null);
    }

    
    private void sendMimeMessageMail(Client client)
            throws MessagingException {

        final String template = "mail-templates/medical-certificate-expiring";
        sendMimeMessageMail(template, env.getProperty("pending.medical.certificate"), null, client);
    }

    
    public void sendEmailToClientsWithExpiringContract(List<Client> clients) {
        sendEmailByModelForMultipleRecipients(formContractExpiringMail(),
                "mail-templates/contract-expiring-template.html", generateRecipientsQueue(clients));
    }
}
