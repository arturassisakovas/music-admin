package com.mAdmin.service;

import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Subscription;
import com.mAdmin.model.SubscriptionGenerationModel;

import java.util.List;


public interface SubscriptionGenerationService {

    
    boolean subscriptionGenerationCycle(int j, Attendee attendee, Integer sequence,
                                        SubscriptionGenerationModel subscriptionGenerationModel);

    
    Integer checkSequence(int j, List<Subscription> subscriptions, int sequence);

    
    void continuousGenerationOfInvoiceAndSubscription();

}
