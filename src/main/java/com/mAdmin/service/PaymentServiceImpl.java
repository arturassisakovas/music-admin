package com.mAdmin.service;

import com.itextpdf.text.DocumentException;
import com.mAdmin.component.AuthenticationFacade;
import com.mAdmin.component.LocalDateTimeProvider;
import com.mAdmin.enumerator.ContractType;
import com.mAdmin.enumerator.InvoiceType;
import com.mAdmin.repository.AttendeeNonPaidRepository;
import com.mAdmin.repository.ContractRepository;
import com.mAdmin.repository.GroupRepository;
import com.mAdmin.repository.GroupWorkoutRepository;
import com.mAdmin.repository.PaymentRepository;
import com.mAdmin.repository.SubscriptionRepository;
import com.mAdmin.security.ClientPrincipal;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.AttendeeNonPaid;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.InvoiceRecord;
import com.mAdmin.entity.Payment;
import com.mAdmin.entity.Subscription;
import com.mAdmin.model.AttendeeRegistrationSessionModel;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class PaymentServiceImpl implements PaymentService {

    
    @Value("${paysera.project.password}")
    private String payseraProjectPassword;

    
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    
    @Autowired
    private Environment env;

    
    @Autowired
    private AuthenticationFacade authenticationFacade;

    
    @Autowired
    private ContractRepository contractRepository;

    
    @Autowired
    private InvoiceRecordRepository invoiceRecordRepository;

    
    @Autowired
    private GroupRepository groupRepository;

    
    @Autowired
    private GroupWorkoutRepository groupWorkoutRepository;

    
    @Autowired
    private GroupService groupService;

    
    @Autowired
    private DiscountService discountService;

    
    @Autowired
    private InvoiceService invoiceService;

    
    @Autowired
    private ContractService contractService;

    
    @Value("${paysera.payurl}")
    private String payseraPayUrl;

    
    @Value("${application.url}")
    private String applicationUrl;

    
    @Autowired
    private LocalDateTimeProvider localDateTimeProvider;

    
    @Autowired
    private AttendeeNonPaidRepository attendeeNonPaidRepository;

    
    @Autowired
    private PaymentRepository paymentRepository;

    
    private static final int HEX_VALUE_FF = 0xff;

    
    private static final String HASH_ENCRYPTION_ALGORITHM = "SHA1withRSA";

    
    private static final String CERT_FORMAT = "X.509";

    
    private BigDecimal totalPrice = BigDecimal.ZERO;

    
    private final int oneHundred = 100;

    
    @Override
    public String urlEncodeUtf8(String param) {
        return URLEncoder.encode(param, StandardCharsets.UTF_8);
    }

    
    @Override
    public String urlEncodeUtf8(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s", urlEncodeUtf8(entry.getKey().toString()),
                    urlEncodeUtf8(entry.getValue().toString())));
        }
        return sb.toString();
    }

    
    @Override
    public String base64EncodeToString(String input) {

        return Base64.getEncoder().encodeToString(input.getBytes());

    }

    
    @Override
    public String base64DecodeToString(String encodedValue) {

        return new String(Base64.getDecoder().decode(encodedValue));

    }

    
    @Override
    public String replaceSymbolsForEncoding(String string) {
        return string.replace("/", "_").replace("+", "-");
    }

    
    @Override
    public String preparePaymentData(Map<?, ?> data) {

        String urlEncodedData = urlEncodeUtf8(data);
        String base64EncodedData = base64EncodeToString(urlEncodedData);

        return replaceSymbolsForEncoding(base64EncodedData);

    }

    
    @Override
    public String preparePaymentSign(String data, String password) {

        return getMD5EncryptedValue(data + password);

    }

    
    @Override
    public List<NameValuePair> preparePaymentRequestArguments(Map<?, ?> paymentData, String password) {

        String data = preparePaymentData(paymentData);
        String sign = preparePaymentSign(data, password);

        List<NameValuePair> arguments = new ArrayList<>(2);
        arguments.add(new BasicNameValuePair("data", data));
        arguments.add(new BasicNameValuePair("sign", sign));

        return arguments;
    }

    
    @Override
    public String replaceSymbolsForDecoding(String string) {
        return string.replace("_", "/").replace("-", "+");
    }

    
    @Override
    public String getMD5EncryptedValue(String password) {
        final byte[] defaultBytes = password.getBytes();
        try {
            final MessageDigest md5MsgDigest = MessageDigest.getInstance("MD5");
            md5MsgDigest.reset();
            md5MsgDigest.update(defaultBytes);
            final byte[] messageDigest = md5MsgDigest.digest();
            final StringBuilder hexString = new StringBuilder();
            for (final byte element : messageDigest) {
                final String hex = Integer.toHexString(HEX_VALUE_FF & element);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            password = hexString + "";
        } catch (final NoSuchAlgorithmException exception) {
            exception.printStackTrace();
        }
        return password;
    }

    
    @Override
    public String decodePayseraCallback(Map<String, String> requestParams) {

        String data = requestParams.get("data");
        String ss1 = requestParams.get("ss1");

        String replacedEncodedString = replaceSymbolsForDecoding(data);
        String decodedString = base64DecodeToString(replacedEncodedString);
        String decodedUrl = URLDecoder.decode(decodedString, StandardCharsets.UTF_8);

        String hashedPaymentDetails = preparePaymentSign(data, payseraProjectPassword);

        
        return decodedUrl;

    }

    
    @Override
    public Map<String, String> mapPayseraCallbackParameters(String parameters) {

        String[] params = parameters.split("&");
        Map<String, String> map = new HashMap<>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = null;
            if (param.split("=").length > 1) {
                value = param.split("=")[1];
            }
            map.put(name, value);
        }

        return map;

    }

    
    @Override
    public Map<String, Boolean> comparePayerDataWithPayseraCallback(Map<?, ?> map) {
        boolean paymentSuccessful;
        boolean payerAuthenticated;

        String paymentStatus = map.get("status").toString();


        final int three = 3;
        paymentSuccessful = paymentStatus.equals("1") || paymentStatus.equals(Integer.toString(three));

        payerAuthenticated = true; 

        Map<String, Boolean> payseraResultMap = new HashMap<>();
        payseraResultMap.put("isPaymentSuccessful", paymentSuccessful);
        payseraResultMap.put("isPayerAuthenticated", payerAuthenticated);
        return payseraResultMap;
    }

    @Override
    public PublicKey getPublicKey(String publicKeyPath) throws CertificateException {

        InputStream inputStream = getClass().getResourceAsStream(publicKeyPath);
        CertificateFactory cf = CertificateFactory.getInstance(CERT_FORMAT);
        X509Certificate cert = (X509Certificate) cf.generateCertificate(inputStream);

        return cert.getPublicKey();
    }

    @Override
    public boolean verifySignedData(String data, String sign, PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {

        data = replaceSymbolsForDecoding(data);
        sign = replaceSymbolsForDecoding(sign);

        final Signature signature = Signature.getInstance(HASH_ENCRYPTION_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data.getBytes());

        final byte[] bytes = Base64.getDecoder().decode(sign);

        return signature.verify(bytes);
    }

    
    private void postPayseraRequest(Map<?, ?> requestData) {

        List<NameValuePair> arguments = preparePaymentRequestArguments(requestData, payseraProjectPassword);

        HttpClient client = HttpClientBuilder.create().disableRedirectHandling().build();
        HttpPost post = new HttpPost(payseraPayUrl);

        try {

            post.setEntity(new UrlEncodedFormEntity(arguments));
            HttpResponse response = client.execute(post);
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect(response.getLastHeader("Location").getValue());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    
    @Override
    public void processPayment(Map<String, Object> session) throws IOException, DocumentException {
        Invoice invoice = createContractAndInvoice(session);
        Payment payment = paymentRepository.findFirstByInvoiceOrderByIdDesc(invoice);
        Map<String, Object> map = new HashMap<>();
        map.put("projectid", env.getProperty("paysera.projectid"));
        map.put("orderid", payment.getId());
        map.put("site_name", applicationUrl);
        map.put("accepturl", env.getProperty("paysera.registration.accepturl"));
        map.put("cancelurl", env.getProperty("paysera.registration.cancelurl"));
        map.put("callbackurl", env.getProperty("paysera.registration.callbackurl"));
        map.put("amount", totalPrice.multiply(BigDecimal.valueOf(oneHundred)));
        map.put("paytext", formPaymentPurpose(invoice));

        map.put("lang", "LIT");
        map.put("country", "LT");

        final int thirtyOneMinutes = 31;
        LocalDateTime time = LocalDateTime.now().plusMinutes(thirtyOneMinutes).withNano(0);
        map.put("time_limit", time);
        map.put("test", env.getProperty("paysera.test"));

        postPayseraRequest(map);
        totalPrice = BigDecimal.ZERO;
    }

    
    @Override
    public void processPayment(Invoice invoice, String personalCode, Long paymentLong) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectid", env.getProperty("paysera.projectid"));
        map.put("orderid", paymentLong);
        map.put("site_name", applicationUrl);
        map.put("accepturl", env.getProperty("paysera.contract.signing.accepturl"));
        map.put("cancelurl", env.getProperty("paysera.registration.cancelurl"));
        map.put("callbackurl", env.getProperty("paysera.contract.signing.callbackurl"));
        map.put("amount", invoice.getPrice().multiply(BigDecimal.valueOf(oneHundred)));
        map.put("paytext", formPaymentPurpose(invoice));
        map.put("test", env.getProperty("paysera.test"));

        map.put("lang", "LIT");
        map.put("country", "LT");

        postPayseraRequest(map);
        totalPrice = BigDecimal.ZERO;
    }

    @Override
    public void processPayment(Invoice invoice, Long paymentId) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectid", env.getProperty("paysera.projectid"));
        map.put("orderid", paymentId);
        map.put("site_name", applicationUrl);
        map.put("accepturl", env.getProperty("paysera.service.payment.accepturl"));
        map.put("cancelurl", env.getProperty("paysera.registration.cancelurl"));
        map.put("callbackurl", env.getProperty("paysera.service.payment.callbackurl"));
        map.put("amount", invoice.getPrice().multiply(BigDecimal.valueOf(oneHundred)));
        map.put("paytext", formPaymentPurpose(invoice));
        map.put("test", env.getProperty("paysera.test"));
        map.put("lang", "LIT");
        map.put("country", "LT");

        postPayseraRequest(map);
        totalPrice = BigDecimal.ZERO;
    }

    @Override
    @Transactional
    public Invoice createContractAndInvoice(Map<String, Object> session) throws IOException, DocumentException {

        List<Contract> contracts = new ArrayList<>();
        Invoice invoice;
        Contract contract;

        ClientPrincipal clientPrincipal = (ClientPrincipal) authenticationFacade.getAuthentication().getPrincipal();
        Client client = clientPrincipal.getClient();

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        AttendeeRegistrationSessionModel attendeeRegistrationSessionModel = new AttendeeRegistrationSessionModel();

        List<Contract> oldContractsToEdit = new ArrayList<>();
        for (int i = 0; i < attendeeRegistrationSessionModel.getSubscriptions().size(); i++) {
            contract = new Contract();
            contract.setType(ContractType.NOT_ACTIVE);
            contract.setAttendee(attendeeRegistrationSessionModel.getSubscriptions().get(i).getAttendee());

            Contract tempContract = contractRepository
                    .findByAttendeeAndType(attendeeRegistrationSessionModel.getSubscriptions().get(i).getAttendee(),
                    ContractType.ACTIVE);
            if (tempContract != null) {
                tempContract.setValidTo(attendeeRegistrationSessionModel.getFirstDays().get(i)
                        .withDayOfMonth(1).minusDays(1));
                tempContract.setTerminationDate(
                        attendeeRegistrationSessionModel.getFirstDays().get(i).withDayOfMonth(1).minusDays(1));
                oldContractsToEdit.add(tempContract);
            }
            contract.setValidFrom(attendeeRegistrationSessionModel.getFirstDays().get(i).withDayOfMonth(1));
            contract.setValidTo(attendeeRegistrationSessionModel.getPeriods().get(i).getEndDate());

            contractService.saveAndGenerateNumber(contract);
            contracts.add(contract);
        }
        contractService.generatePdf(contracts, false);
        contractRepository.saveAll(oldContractsToEdit);

        invoice = new Invoice();
        invoice.setType(InvoiceType.CONTRACT_CONFIRMATION);
        invoice.setClient(client);
        invoice.setExpireDate(LocalDate.now());

        invoice = invoiceService.saveAndGenerateNumber(invoice);
        createInvoicePayment(invoice);

        List<InvoiceRecord> invoiceRecords = new ArrayList<>();

        for (Subscription sessionSubscription : attendeeRegistrationSessionModel.getSubscriptions()) {
            InvoiceRecord invoiceRecord = new InvoiceRecord();
            Attendee attendee = sessionSubscription.getAttendee();

            AttendeeNonPaid attendeeNonPaid = new AttendeeNonPaid();
            attendeeNonPaid.setAttendee(attendee);
            attendeeNonPaid.setCreatedAt(LocalDateTime.now());
            attendeeNonPaidRepository.save(attendeeNonPaid);

            Long groupId;
            if (attendee.getNewGroup() != null) {
                groupId = attendee.getNewGroup().getId();
            } else {
                groupId = attendee.getGroup().getId();
            }

            if (groupRepository.findById(groupId).isPresent()) {
                Group group = groupRepository.findById(groupId).get();
                Long periodId = groupService.getGroupPeriodId(group);
                LocalDate startDate = sessionSubscription.getStartDate();
                LocalDate endDate = sessionSubscription.getEndDate();
                Contract attendeeContract = contractRepository.findTopByAttendeeAndTypeOrderByIdDesc(attendee,
                        ContractType.NOT_ACTIVE);

                invoiceRecord.setInvoice(invoice);
                invoiceRecord.setAttendee(attendee);
                invoiceRecord.setGroup(attendee.getGroup());
                invoiceRecord.setContract(attendeeContract);

                BigDecimal singleWorkoutPrice = groupService.getSingleWorkoutPrice(group);
                int workoutDaysCount = groupWorkoutRepository.countByGroupIdAndDateBetween(group.getId(), startDate,
                        endDate);
                BigDecimal grossPrice = singleWorkoutPrice.multiply(new BigDecimal(workoutDaysCount));
                invoiceRecord.setGrossPrice(grossPrice);
                int discountRate = discountService.getDiscountRateBySubscriptionMonths(periodId,
                        sessionSubscription.getNumberOfMonths());
                invoiceRecord.setDiscountRate(discountRate);

                invoiceRecord.setPrice(sessionSubscription.getPrice());
                invoiceRecord.setWorkoutsAmount(workoutDaysCount);
                invoiceRecord.setPeriodStartDate(startDate);
                invoiceRecord.setPeriodEndDate(endDate);

                invoiceRecords.add(invoiceRecord);

                totalPrice = totalPrice.add(sessionSubscription.getPrice());
            }
        }

        invoiceRecordRepository.saveAll(invoiceRecords);

        for (Subscription ss : attendeeRegistrationSessionModel.getSubscriptions()) {
            ss.setInvoice(invoice);
        }

        subscriptionRepository.saveAll(attendeeRegistrationSessionModel.getSubscriptions());
        attendeeRegistrationSessionModel.getSubscriptions().clear();
        sessionMap.put("theSubscription", attendeeRegistrationSessionModel.getSubscriptions());

        return invoice;
    }

    
    private String formPaymentPurpose(Invoice invoice) {
        StringBuilder str = new StringBuilder();
        str.append("Mokėjimas už: ");

        List<Subscription> subscriptions = subscriptionRepository.findByInvoice(invoice);
        for (int i = 0; i < subscriptions.size(); i++) {
            Contract contract = contractRepository.findTopByAttendeeOrderByIdDesc(subscriptions.get(i).getAttendee());
            str.append(contract.getNumber());

            if (i != subscriptions.size() - 1) {
                str.append(", ");
            }
        }
        return str.toString();
    }

    
    public boolean isInvoicePaid(Invoice invoice) {
        return paymentRepository.findByInvoiceAndStatusTrue(invoice) != null;
    }

    @Override
    public Payment formOneCentPayment(Invoice oneCentInvoice) {
        Payment oneCentPayment = new Payment();
        oneCentPayment.setInvoice(oneCentInvoice);
        oneCentPayment.setStatus(false);
        return oneCentPayment;
    }

    
    public void createInvoicePayment(Invoice invoice) {
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        paymentRepository.save(payment);
    }

    @Override
    public boolean isCallBackWithFullDataForContract(Map<String, String> map, BigDecimal price) {
        return  isMapContainingGeneralData(map, price);
    }

    @Override
    public boolean isCallBackWithFullDataForServicePayment(Map<String, String> map, BigDecimal price) {
        return isMapContainingGeneralData(map, price);
    }

    
    private boolean isMapContainingGeneralData(Map<String, String> map, BigDecimal price) {
        int expectedPrice = price.multiply(BigDecimal.valueOf(oneHundred)).intValueExact();
        if (map.containsKey("status") && (map.get("status") != "2") && map.containsKey("payamount")
                && map.get("payamount").equals(Integer.toString(expectedPrice))
                && map.containsKey("currency") && map.get("currency").equals("EUR")) {
            return true;
        }

        return false;
    }

    @Override
    public void extendPayTime(List<Attendee> attendees) {
        final int extraTime = 30;

        AttendeeNonPaid attendeeNonPaid = attendeeNonPaidRepository.findByAttendeeIn(attendees);
        if (attendeeNonPaid != null) {
            attendeeNonPaid.setCreatedAt(localDateTimeProvider.getCurrentDateTime().plusMinutes(extraTime));
            attendeeNonPaidRepository.saveAndFlush(attendeeNonPaid);
        }
    }
}
