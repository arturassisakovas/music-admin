package com.mAdmin.service;

import com.mAdmin.repository.GroupWorkoutRepository;
import com.mAdmin.repository.PaymentRepository;
import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.InvoiceRecord;
import com.mAdmin.entity.Payment;
import com.mAdmin.model.InvoiceRecordCreationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class InvoiceRecordServiceImpl implements InvoiceRecordService {

    
    @Autowired
    private GroupService groupService;

    
    @Autowired
    private GroupWorkoutRepository groupWorkoutRepository;

    
    @Autowired
    private DiscountService discountService;

    
    @Autowired
    private InvoiceRecordRepository invoiceRecordRepository;

    
    @Autowired
    private PaymentRepository paymentRepository;

    
    @Autowired
    private InvoiceService invoiceService;

    
    @Override
    public InvoiceRecord createInvoiceRecord(InvoiceRecordCreationModel invoiceRecordCreationModel) {

        InvoiceRecord invoiceRecord = invoiceRecordCreationModel.getInvoiceRecord();

        invoiceRecord.setInvoice(invoiceRecordCreationModel.getInvoice());
        invoiceRecord.setAttendee(invoiceRecordCreationModel.getAttendee());
        invoiceRecord.setGroup(invoiceRecordCreationModel.getAttendee().getGroup());
        invoiceRecord.setContract(invoiceRecordCreationModel.getContract());

        int workoutDaysCount = groupWorkoutRepository
                .countByGroupIdAndDateBetween(invoiceRecordCreationModel.getGroup().getId(),
                        invoiceRecordCreationModel.getWorkoutStartDate(),
                        invoiceRecordCreationModel.getWorkoutEndDate());
        BigDecimal workoutDaysCountBigDecimal = new BigDecimal(workoutDaysCount);
        BigDecimal singleWorkoutPrice = groupService.getSingleWorkoutPrice(invoiceRecordCreationModel.getGroup());

        invoiceRecord.setGrossPrice(singleWorkoutPrice.multiply(workoutDaysCountBigDecimal));
        int discountRate = discountService.getDiscountRateBySubscriptionMonths(invoiceRecordCreationModel.getPeriodId(),
                invoiceRecordCreationModel.getSubscription().getNumberOfMonths());
        invoiceRecord.setDiscountRate(discountRate);

        invoiceRecord.setPrice(invoiceRecordCreationModel.getSubscription().getPrice());
        invoiceRecord.setWorkoutsAmount(workoutDaysCount);
        invoiceRecord.setPeriodStartDate(invoiceRecordCreationModel.getWorkoutStartDate());
        invoiceRecord.setPeriodEndDate(invoiceRecordCreationModel.getWorkoutEndDate());

        return invoiceRecord;
    }

    @Override
    public void formOneCentRecord(Invoice oneCentInvoice, Contract contract, Invoice invoice, Payment oneCentPayment,
                                    boolean setPrice) {
        InvoiceRecord oneCentRecord = new InvoiceRecord();

        oneCentRecord.setInvoice(oneCentInvoice);
        oneCentRecord.setAttendee(contract.getAttendee());
        oneCentRecord.setGroup(contract.getAttendee().getGroup());
        oneCentRecord.setContract(contract);
        if (setPrice) {
            final BigDecimal oneCent = BigDecimal.valueOf(0.01);
            oneCentRecord.setPrice(oneCent);
        } else {
            oneCentRecord.setPrice(BigDecimal.valueOf(0));
        }

        oneCentRecord.setGrossPrice(oneCentRecord.getPrice());
        oneCentRecord.setDiscountRate(0);

        InvoiceRecord tempRecord = invoiceRecordRepository
                .findByInvoiceAndAttendee(invoice, contract.getAttendee());

        oneCentRecord.setWorkoutsAmount(tempRecord.getWorkoutsAmount());
        oneCentRecord.setPeriodStartDate(tempRecord.getPeriodStartDate());
        oneCentRecord.setPeriodEndDate(tempRecord.getPeriodEndDate());

        invoiceService.saveAndGenerateNumber(oneCentInvoice);
        paymentRepository.save(oneCentPayment);
        invoiceRecordRepository.save(oneCentRecord);
    }

    @Override
    public InvoiceRecord findInvoiceOfSingleAttendee(Contract contract) {
        if (contract != null) {
            return invoiceRecordRepository.findFirstByContractEqualsOrderByIdDesc(contract);
        }
        return null;
    }

}
