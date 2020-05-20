package com.mAdmin.service;

import com.mAdmin.enumerator.ContractType;
import com.mAdmin.enumerator.InvoiceType;
import com.mAdmin.repository.ContractRepository;
import com.mAdmin.repository.GroupRepository;
import com.mAdmin.repository.SubscriptionRepository;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.InvoiceRecord;
import com.mAdmin.entity.Subscription;
import com.mAdmin.model.InvoiceRecordCreationModel;
import com.mAdmin.model.SubscriptionGenerationModel;
import com.mAdmin.model.SubscriptionCreationModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class SubscriptionGenerationServiceImp implements SubscriptionGenerationService {

    
    @Autowired
    private InvoiceRecordRepository invoiceRecordRepository;

    
    @Autowired
    private ContractRepository contractRepository;

    
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    
    @Autowired
    private GroupRepository groupRepository;

    
    @Autowired
    private GroupService groupService;

    
    @Autowired
    private InvoiceService invoiceService;

    
    @Autowired
    private SubscriptionService subscriptionService;

    
    @Autowired
    private InvoiceRecordService invoiceRecordService;

    
    @Autowired
    private PaymentService paymentService;

    
    private final Log logger = LogFactory.getLog(this.getClass());

    
    private List<Subscription> subscriptionsToCreate = new ArrayList<>();

    
    private List<Invoice> invoicesToCreate = new ArrayList<>();

    
    private Invoice invoice;

    
    private Subscription subscription;

    
    private BigDecimal totalPrice;

    
    @Override
    public Integer checkSequence(int j, List<Subscription> subscriptions, int sequence) {

        if (sequence != 0) {
            if (subscriptions.get(j).getInvoice().getClient() == subscriptions.get(j - 1).getInvoice().getClient()) {
                sequence++;
            }
        } else {
            if (subscriptions.size() > j + 1) {
                if (subscriptions.get(j).getInvoice().getClient() == subscriptions.get(j + 1).getInvoice()
                        .getClient()) {
                    sequence++;
                }
            }
        }
        return sequence;

    }

    
    @Override
    public boolean subscriptionGenerationCycle(int j, Attendee attendee, Integer sequence,
                                               SubscriptionGenerationModel subscriptionGenerationModel) {

        if (sequence != 0) {
            List<Subscription> subscriptions = subscriptionGenerationModel.getListOfSubscriptions();
            if (j + 1 < subscriptions.size()) {
                if (subscriptions.get(j).getInvoice().getClient() == subscriptions.get(j + 1).getInvoice()
                        .getClient()) {
                    return false;
                } else {
                    sequentialGenerationOfInvoicesAndSubscriptions(j, attendee, sequence, subscriptionGenerationModel);
                    return true;
                }
            } else {
                sequentialGenerationOfInvoicesAndSubscriptions(j, attendee, sequence, subscriptionGenerationModel);
                return true;
            }
        } else {
            final int fifteen = 15;
            invoice = new Invoice();
            subscription = new Subscription();
            if (!subscriptionGenerationModel.getListOfPrices().get(0).equals(BigDecimal.valueOf(0))) {
                    invoice.setType(InvoiceType.WORKOUTS_PAYMENT);
                    invoice.setClient(attendee.getClient());
                    invoice.setExpireDate(LocalDate.now().withDayOfMonth(fifteen));
                    invoicesToCreate.add(invoice);

                    subscription.setAttendee(attendee);
                    subscription.setStartDate(subscriptionGenerationModel.getListOfStartDates().get(0));

                    subscription.setEndDate(subscriptionGenerationModel.getListOfExpireDates().get(0));

                    subscription.setNumberOfMonths(subscriptionGenerationModel.getListOfMonths().get(0));
                    subscription.setPrice(subscriptionGenerationModel.getListOfPrices().get(0));
                    subscription.setWorkoutsPerWeek(
                            subscriptionGenerationModel.getListOfSubscriptions().get(j).getWorkoutsPerWeek());
                    subscriptionsToCreate.add(subscription);
            }
            return true;
        }
    }

    
    @Override
    public void continuousGenerationOfInvoiceAndSubscription() {

        for (int i = 0; i < invoicesToCreate.size(); i++) {
            Attendee recordAttendee = subscriptionsToCreate.get(i).getAttendee();
            Long groupId = recordAttendee.getGroup().getId();
            Optional<Group> optionalGroup = groupRepository.findById(groupId);
            if (optionalGroup.isPresent()) {
                Group group = optionalGroup.get();
                Long periodId = groupService.getGroupPeriodId(group);
                InvoiceRecord invoiceRecord = new InvoiceRecord();

                Invoice invoice = invoiceService.saveAndGenerateNumber(invoicesToCreate.get(i));
                paymentService.createInvoicePayment(invoice);


                logger.info("Invoice : " + invoice.getInvoiceNumber() + " generated.");

                subscriptionsToCreate.get(i).setInvoice(invoice);
                Subscription subscription = subscriptionRepository.save(subscriptionsToCreate.get(i));

                Contract contract = contractRepository.findTopByAttendeeAndTypeOrderByIdDesc(recordAttendee,
                        ContractType.ACTIVE);

                InvoiceRecordCreationModel invoiceRecordCreationModel = new InvoiceRecordCreationModel(
                        invoiceRecord, recordAttendee, contract, group, periodId, invoice,
                        subscription, subscription.getStartDate(), subscription.getEndDate());
                invoiceRecord = invoiceRecordService.createInvoiceRecord(invoiceRecordCreationModel);

                invoiceRecordRepository.save(invoiceRecord);
            }
        }
        subscriptionsToCreate.clear();
        invoicesToCreate.clear();
    }

    
    private void sequentialGenerationOfInvoicesAndSubscriptions(int j, Attendee attendee, Integer sequence,
                                                                SubscriptionGenerationModel subscriptionGenerationModel
                                                                ) {
        List<Subscription> subscriptions = subscriptionGenerationModel.getListOfSubscriptions();
        totalPrice = BigDecimal.valueOf(0);
        subscriptionGenerationModel.getListOfPrices().forEach(sp -> totalPrice = totalPrice.add(sp));
        if (!totalPrice.equals(BigDecimal.valueOf(0))) {
                    invoice = invoiceService.createInvoice(attendee);

                    logger.info("Invoice : " + invoice.getInvoiceNumber() + " generated.");

                    List<InvoiceRecord> invoiceRecords = new ArrayList<>();

                    sequence--;
                    for (int i = 0; i < sequence + 1; i++) {

                        subscription = new Subscription();
                        subscription.setAttendee(subscriptions.get(j - sequence + i).getAttendee());

                        Attendee recordAttendee = subscription.getAttendee();
                        Long groupId = recordAttendee.getGroup().getId();
                        Optional<Group> optionalGroup = groupRepository.findById(groupId);
                        Group group = null;
                        if (optionalGroup.isPresent()) {
                            group = optionalGroup.get();
                        }

                            SubscriptionCreationModel subscriptionCreationModel = new SubscriptionCreationModel(
                                    subscription, invoice, subscriptions.get(j).getWorkoutsPerWeek());

                            subscriptionCreationModel.setContract(
                                    subscriptionGenerationModel.getListOfContracts().get(i));
                            subscriptionCreationModel.setPrice(
                                    subscriptionGenerationModel.getListOfPrices().get(i));
                            subscriptionCreationModel.setExpireDate(
                                    subscriptionGenerationModel.getListOfExpireDates().get(i));
                            subscriptionCreationModel.setMonths(
                                    subscriptionGenerationModel.getListOfMonths().get(i));
                            subscriptionCreationModel.setStartDate(
                                    subscriptionGenerationModel.getListOfStartDates().get(i));

                            subscriptionService.createSubscription(subscriptionCreationModel);

                            InvoiceRecord invoiceRecord = new InvoiceRecord();
                            Long periodId = groupService.getGroupPeriodId(group);

                            InvoiceRecordCreationModel invoiceRecordCreationModel = new InvoiceRecordCreationModel(
                                    invoiceRecord, recordAttendee, subscriptionGenerationModel.getListOfContracts().get(i),
                                    group, periodId, invoice, subscription, subscription.getStartDate(),
                                    subscription.getEndDate());

                            invoiceRecord = invoiceRecordService.createInvoiceRecord(invoiceRecordCreationModel);

                            invoiceRecords.add(invoiceRecord);
                    }

                    invoiceRecordRepository.saveAll(invoiceRecords);
        }
    }


    public void setSubscriptionsToCreate(List<Subscription> subscriptionsToCreate) {
        this.subscriptionsToCreate = subscriptionsToCreate;
    }

    public void setInvoicesToCreate(List<Invoice> invoicesToCreate) {
        this.invoicesToCreate = invoicesToCreate;
    }
}
