package com.mAdmin.controller;

import com.itextpdf.text.DocumentException;
import com.mAdmin.enumerator.ClientStatus;
import com.mAdmin.enumerator.ContractType;
import com.mAdmin.enumerator.InvoiceType;
import com.mAdmin.repository.AttendeeNonPaidRepository;
import com.mAdmin.repository.AttendeeRepository;
import com.mAdmin.repository.ClientRepository;
import com.mAdmin.repository.ContractRepository;
import com.mAdmin.repository.PaymentRepository;
import com.mAdmin.repository.SubscriptionRepository;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.AttendeeNonPaid;
import com.mAdmin.entity.Client;
import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.InvoiceRecord;
import com.mAdmin.entity.Payment;
import com.mAdmin.entity.Subscription;
import com.mAdmin.service.EmailService;
import com.mAdmin.service.InvoiceRecordRepository;
import com.mAdmin.service.InvoiceRecordService;
import com.mAdmin.service.InvoiceService;
import com.mAdmin.service.PaymentService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.faces.context.FacesContext;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
public class PaymentController {

    
    @Autowired
    private ContractRepository contractRepository;

    
    @Autowired
    private ClientRepository clientRepository;

    
    @Autowired
    private PaymentService paymentService;

    
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    
    @Autowired
    private EmailService emailService;

    
    @Value("${paysera.project.password}")
    private String payseraProjectPassword;

    
    @Autowired
    private AttendeeNonPaidRepository attendeeNonPaidRepository;

    
    @Autowired
    private AttendeeRepository attendeeRepository;

    
    @Autowired
    private InvoiceService invoiceService;

    
    @Autowired
    private PaymentRepository paymentRepository;

    
    @Autowired
    private InvoiceRecordRepository invoiceRecordRepository;

    
    @Autowired
    private InvoiceRecordService invoiceRecordService;

    
    private static final String PAYSERA_PUBLIC_KEY_PATH = "/paysera_public.key";

    
    private static final String PAYSERA_DATA_KEY = "data";

    
    private static final String PAYSERA_SS2_KEY = "ss2";

    
    private final Log logger = LogFactory.getLog(this.getClass());

    
    private boolean isPayerAuthenticated;

    
    private boolean isPaymentSuccessful;

    
    private boolean isSignedDataVerified;

    
    private String personalCode;

    
    private boolean isForeigner;

    
    private boolean isNewClient;

    
    private Client client;

    
    private static final BigDecimal ONE_CENT = BigDecimal.valueOf(0.01);

    
    private List<Subscription> subscriptions;

    
    public void onLoad() {
        personalCode = null;
        setNewClient(false);
        client = Faces.getSessionAttribute("newClient");
        if (client != null) {
            setNewClient(true);
            setForeigner(client.getForeigner());
        }
    }

    
    @GetMapping("/registration-callback")
    public @ResponseBody
    String payseraRegistrationCallback(@RequestParam Map<String, String> requestParams)
            throws DocumentException, IOException {

        Map<String, String> map = callBackValidation(requestParams);

        if (isSignedDataVerified) {
            Payment payment = paymentRepository.findById(Long.parseLong(map.get("orderid"))).orElse(null);
            if (payment != null) {
                Invoice invoice = payment.getInvoice();
                if (invoice != null) {
                    if (paymentService.isCallBackWithFullDataForContract(map, invoice.getPrice())
                            && isPayerAuthenticated && isPaymentSuccessful) {
                        setClientSubscriptionAndEmailFromInvoice(invoice);

                        client.setStatus(ClientStatus.ACTIVE);
                        client = clientRepository.save(client);

                        List<Contract> attendeesContracts = new ArrayList<>();
                        List<Attendee> attendees = new ArrayList<>();

                        boolean clientHasActiveContract = false;

                        for (Attendee tempAttendee : client.getAttendees()) {
                            if (tempAttendee.getContract() != null
                                    && tempAttendee.getContract().getType().equals(ContractType.ACTIVE)) {
                                clientHasActiveContract = true;
                                break;
                            }
                        }

                        for (Subscription subscription : subscriptions) {
                            Contract contract = subscription.getAttendee().getContract();
                            contract.setDateSigned(LocalDateTime.now());
                            contract.setSigned(true);

                            if (contract.getAttendee().getNewGroup() == null) {
                                contract.setType(ContractType.ACTIVE);
                            } else {
                                contract.setType(ContractType.NOT_ACTIVE);
                            }

                            attendeesContracts.add(contract);
                            subscription.getAttendee().setAttendeeNonPaid(null);
                            attendees.add(subscription.getAttendee());
                            attendeeNonPaidRepository.deleteByAttendee(subscription.getAttendee());
                        }

                        contractRepository.saveAll(attendeesContracts);
                        attendeeRepository.saveAll(attendees);

                        payment.setStatus(true);
                        payment.setDatePaid(LocalDateTime.now());
                        paymentRepository.save(payment);

                        invoiceService.generatePdf(invoice, payment);

                        if (!clientHasActiveContract) {
                            emailService.formAndSendMemoMail(client, "successful.payment.memo",
                                    "mail-templates/successful-payment-memo-template.html");
                        }
                    } else if (paymentService.isCallBackWithFullDataForContract(map, invoice.getPrice())
                                && isPaymentSuccessful) {
                                setClientSubscriptionAndEmailFromInvoice(invoice);

                                List<Contract> attendeesContracts = new ArrayList<>();
                                List<Attendee> attendees = new ArrayList<>();

                                Contract contract = new Contract();

                                for (Subscription subscription : subscriptions) {
                                    contract = iteratorLogic(subscription, attendeesContracts, attendees,
                                            payment, invoice);
                                }

                                InvoiceRecord clientOneCentRecord = invoiceRecordRepository
                                        .findByContractAndPrice(contract, ONE_CENT);

                                if (clientOneCentRecord == null) {

                                    Invoice oneCentInvoice = invoiceService.formOneCentInvoice(
                                            client, invoice, subscriptions);
                                    Payment oneCentPayment = paymentService.formOneCentPayment(oneCentInvoice);

                                    boolean setPrice = true;
                                    for (int i = 0; i < subscriptions.size(); i++) {
                                    invoiceRecordService.formOneCentRecord(
                                            oneCentInvoice, subscriptions.get(i).getAttendee().getContract(), invoice,
                                            oneCentPayment, setPrice);
                                        setPrice = false;
                                    }
                                }
                        emailService.formAndSendMemoMail(client, "unsuccessful.payment.memo",
                                "mail-templates/unsuccessful-payment-memo-template.html");
                    }
                    return "OK";
                }
            }
        }
        return "false";
    }

    
    @GetMapping("/contract-signing-callback")
    public @ResponseBody
    String payseraContractSigningCallback(@RequestParam Map<String, String> requestParams)
            throws DocumentException, IOException {
        Map<String, String> map = callBackValidation(requestParams);

        if (isSignedDataVerified) {
            Payment payment = paymentRepository.findById(Long.parseLong(map.get("orderid"))).orElse(null);
            if (payment != null) {
                Invoice invoice = payment.getInvoice();
            Contract contract = invoice.getRecords().get(0).getContract();
            if (paymentService.isCallBackWithFullDataForContract(map, invoice.getPrice())
                    && isPaymentSuccessful && (isPayerAuthenticated)) {

                for (InvoiceRecord invoiceRecord : invoice.getRecords()) {

                    contract = invoiceRecord.getContract();
                    if (invoiceRecord.getAttendee().getNewGroup() == null) {
                        contract.setType(ContractType.ACTIVE);
                    }
                    contract.setDateSigned(LocalDateTime.now());
                    contract.setSigned(true);

                    if (attendeeNonPaidRepository.findByAttendee(invoiceRecord.getAttendee()) != null) {
                        attendeeNonPaidRepository.deleteByAttendee(invoiceRecord.getAttendee());
                        if (invoice.getPrice().equals(ONE_CENT)) {
                            InvoiceRecord tempInvoiceRecord = invoiceRecordRepository
                                    .findByContractAndAttendeeAndPriceIsNot(contract, contract.getAttendee(), ONE_CENT);
                            Invoice originalInvoice = tempInvoiceRecord.getInvoice();
                            Payment originalPayment = originalInvoice.getPayment();
                            originalPayment.setStatus(true);
                            originalPayment.setDatePaid(LocalDateTime.now());
                            paymentRepository.save(originalPayment);
                        }
                    }
                    contractRepository.saveAndFlush(contract);
                }

                Client client = invoice.getClient();
                if (client.getStatus() != ClientStatus.ACTIVE) {
                    client.setStatus(ClientStatus.ACTIVE);
                    clientRepository.save(client);
                }
                payment.setStatus(true);
                payment.setDatePaid(LocalDateTime.now());
                paymentRepository.save(payment);

                invoiceService.generatePdf(invoice, payment);
            } else if (paymentService.isCallBackWithFullDataForContract(map, invoice.getPrice())
                        && isPaymentSuccessful && !isPayerAuthenticated && !invoice.getPrice().equals(ONE_CENT)) {

                    setClientSubscriptionAndEmailFromInvoice(invoice);

                    List<Contract> attendeesContracts = new ArrayList<>();
                    List<Attendee> attendees = new ArrayList<>();

                    for (Subscription subscription : subscriptions) {
                        iteratorLogic(subscription, attendeesContracts, attendees, payment, invoice);
                    }

                    Invoice oneCentInvoice = invoiceService.formOneCentInvoice(client, invoice, subscriptions);
                    Payment oneCentPayment = paymentService.formOneCentPayment(oneCentInvoice);

                    boolean setPrice = true;
                    for (int i = 0; i < subscriptions.size(); i++) {

                        invoiceRecordService.formOneCentRecord(oneCentInvoice,
                                subscriptions.get(i).getAttendee().getContract(), invoice, oneCentPayment, setPrice);
                        setPrice = false;
                    }
                    emailService.formAndSendMemoMail(client, "unsuccessful.payment.memo",
                            "mail-templates/unsuccessful-payment-memo-template.html");
                }
            return "OK";
        }
    }
        return "false";
    }

    
    @GetMapping("/payment-callback")
    public @ResponseBody
    String payseraServiceCallback(@RequestParam Map<String, String> requestParams)
            throws DocumentException, IOException {
        Map<String, String> map = callBackValidation(requestParams);

        if (isSignedDataVerified) {
            Payment payment = paymentRepository.findById(Long.parseLong(map.get("orderid"))).orElse(null);
            if (payment != null) {
                Invoice invoice = payment.getInvoice();
                Contract contract = invoice.getRecords().get(0).getContract();

                if (paymentService.isCallBackWithFullDataForServicePayment(map, invoice.getPrice())
                        && isPaymentSuccessful && isPayerAuthenticated) {

                    if (invoice.getType().equals(InvoiceType.IMMINENT_TERMINATION)) {
                        invoice.setType(InvoiceType.WORKOUTS_PAYMENT);
                        for (InvoiceRecord invoiceRecord : invoice.getRecords()) {
                            contract = invoiceRecord.getContract();
                            contract.setTerminationDate(null);
                            contractRepository.saveAndFlush(contract);

                        }
                    }

                    payment.setStatus(true);
                    payment.setDatePaid(LocalDateTime.now());
                    paymentRepository.save(payment);

                    invoiceService.generatePdf(invoice, payment);
                }
                return "OK";
            }
        }
        return "false";
    }

    
    public void validateForm() throws IOException, DocumentException {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, Object> session = context.getExternalContext().getSessionMap();


        paymentService.processPayment(session);
        session.remove("attendeeWrittenToDb");
        if (client != null) {

            clientRepository.save(client);
        }
        session.put("currentRegistrationStep", RegisterController.SIXTH_STEP);
    }

    
    public Map<String, String> callBackValidation(@RequestParam Map<String, String> requestParams) {

        String callbackData = paymentService.decodePayseraCallback(requestParams);
        Map<String, String> map = paymentService.mapPayseraCallbackParameters(callbackData);

        if (map.containsKey("original_paytext")) {
            logger.info(map.get("original_paytext"));
        }

        isSignedDataVerified = false;
        try {

            isSignedDataVerified = paymentService.verifySignedData(requestParams.get(PAYSERA_DATA_KEY),
                    requestParams.get(PAYSERA_SS2_KEY), paymentService.getPublicKey(PAYSERA_PUBLIC_KEY_PATH));

        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | FileNotFoundException
                | CertificateException e) {
            e.printStackTrace();
        }
        if (isSignedDataVerified) {
            Map<String, Boolean> payseraResultMap = paymentService.comparePayerDataWithPayseraCallback(map);
            isPayerAuthenticated = payseraResultMap.get("isPayerAuthenticated");
            isPaymentSuccessful = payseraResultMap.get("isPaymentSuccessful");
        }

        return map;

    }

    
    private Contract iteratorLogic(Subscription subscription, List<Contract> attendeesContracts,
                                   List<Attendee> attendees, Payment payment, Invoice invoice)
            throws IOException, DocumentException {

        Contract contract = subscription.getAttendee().getContract();
        contract.setSigned(false);
        attendeesContracts.add(contract);
        AttendeeNonPaid attendeeNonPaid = attendeeNonPaidRepository
                .findByAttendee(subscription.getAttendee());
        if (attendeeNonPaid != null) {
            final int extraTime = 30;
            attendeeNonPaid.setCreatedAt(LocalDateTime.now().plusMinutes(extraTime));
        }
        attendees.add(subscription.getAttendee());

                    contractRepository.saveAll(attendeesContracts);
                    attendeeRepository.saveAll(attendees);

                    payment.setStatus(false);
                    payment.setDatePaid(LocalDateTime.now());
                    paymentRepository.save(payment);

                    invoiceService.generatePdf(invoice, payment);
        return contract;
    }

    
    private void setClientSubscriptionAndEmailFromInvoice(Invoice invoice) {
        client = invoice.getClient();
        subscriptions = subscriptionRepository.findByInvoice(invoice);
    }

    
    public String getPersonalCode() {
        return personalCode;
    }

    
    public void setPersonalCode(String personalCode) {
        this.personalCode = personalCode;
    }

    
    public boolean isForeigner() {
        return isForeigner;
    }

    
    public void setForeigner(boolean foreigner) {
        isForeigner = foreigner;
    }

    
    public boolean isNewClient() {
        return isNewClient;
    }

    
    public void setNewClient(boolean newClient) {
        isNewClient = newClient;
    }
}
