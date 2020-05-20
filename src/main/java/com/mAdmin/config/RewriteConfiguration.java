package com.mAdmin.config;

import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

import javax.servlet.ServletContext;

@org.springframework.context.annotation.Configuration
public class RewriteConfiguration extends HttpConfigurationProvider {

    @Override
    public Configuration getConfiguration(ServletContext context) {
        return ConfigurationBuilder.begin()
                
                .addRule(Join.path("/login").to("/login-page.xhtml"))
                .addRule(Join.path("/reset-password").to("reset-password.xhtml"))
                .addRule(Join.path("/forgot-password").to("forgot-password.xhtml"))
                .addRule(Join.path("/acceptPayment").to("/registration/step-6.xhtml"))
                .addRule(Join.path("/failedPayment").to("/payment-unsuccessful.xhtml"))
                
                .addRule(Join.path("/admin").to("/admin/index.xhtml"))
                .addRule(Join.path("/admin/change-password").to("/admin/changePassword.xhtml"))
                .addRule(Join.path("/admin/reports").to("/admin/reports.xhtml"))
                .addRule(Join.path("/admin/emails").to("/admin/emails.xhtml"))
                .addRule(Join.path("/admin/group-change").to("/admin/group-change.xhtml"))
                .addRule(Join.path("/admin/client-invoices").to("/admin/client-invoices.xhtml"))
                .addRule(Join.path("/admin/client-contracts").to("/admin/clientsContracts.xhtml"))
                .addRule(Join.path("/admin/inactive-clients").to("/admin/inactiveClients.xhtml"))
                .addRule(Join.path("/admin/users").to("/admin/users.xhtml"))
                .addRule(Join.path("/admin/medical-certificates").to("/admin/medicalCertificates.xhtml"))
                .addRule(Join.path("/admin/timetables").to("/admin/timetables.xhtml"))
                .addRule(Join.path("/admin/reserved-attendees").to("/admin/reserved-attendees.xhtml"))
                .addRule(Join.path("/admin/groups").to("/admin/groups.xhtml"))
                .addRule(Join.path("/admin/tracks").to("/admin/tracks.xhtml"))
                .addRule(Join.path("/admin/reserved-clients").to("/admin/reservedClients.xhtml"))
                .addRule(Join.path("/admin/reviewgroup").to("/admin/reviewgroup.xhtml"))
                .addRule(Join.path("/admin/client-preview").to("/admin/clientsPreview.xhtml"))
                .addRule(Join.path("/admin/timetables-management").to("/admin/timetables-management.xhtml"))
                .addRule(Join.path("/admin/clients").to("/admin/clients.xhtml"))
                .addRule(Join.path("/admin/client-records").to("/admin/clientsRecords.xhtml"))
                .addRule(Join.path("/admin/departments").to("/admin/departments.xhtml"))
                .addRule(Join.path("/admin/debtors").to("/admin/debtors.xhtml"))
                .addRule(Join.path("/admin/seasons-periods").to("/admin/seasons-periods.xhtml"))
                .addRule(Join.path("/admin/cabinets").to("/admin/cabinets.xhtml"))
                .addRule(Join.path("/admin/cabinet-periods").to("/admin/cabinet-periods.xhtml"))
                .addRule(Join.path("/admin/seasons").to("/admin/seasons.xhtml"))
                
                .addRule(Join.path("/client").to("/client/attendees.xhtml"))
                .addRule(Join.path("/client/attendees").to("/client/attendees.xhtml"))
                .addRule(Join.path("/client/profile").to("/client/profile.xhtml"))
                .addRule(Join.path("/client/change-password").to("/client/changePassword.xhtml"))
                .addRule(Join.path("/client/contracts").to("/client/contracts.xhtml"))
                .addRule(Join.path("/client/agreements").to("/client/agreements.xhtml"))
                .addRule(Join.path("/client/invoices").to("/client/invoices.xhtml"))
                
                .addRule(Join.path("/teacher").to("/teacher/profile.xhtml"))
                .addRule(Join.path("/teacher/groups").to("/teacher/groups.xhtml"))
                .addRule(Join.path("/teacher/attendance").to("/teacher/attendance.xhtml"))
                .addRule(Join.path("/teacher/attendees").to("/teacher/attendees.xhtml"))
                .addRule(Join.path("/teacher/change-password").to("/teacher/changePassword.xhtml"))
                
                .addRule(Join.path("/register").to("/registration.xhtml"))
                .addRule(Join.path("/registration/step-1").to("/registration/step-1.xhtml"))
                .addRule(Join.path("/registration/step-2").to("/registration/step-2.xhtml"))
                .addRule(Join.path("/registration/step-3").to("/registration/step-3.xhtml"))
                .addRule(Join.path("/registration/step-4").to("/registration/step-4.xhtml"))
                .addRule(Join.path("/registration/step-5").to("/registration/step-5.xhtml"))
                .addRule(Join.path("/registration/step-6").to("/registration/step-6.xhtml"))
                
                .addRule(Join.path("/admin/jobs").to("/admin/jobs.xhtml"));
    }

    @Override
    public int priority() {
        return 0;
    }

}
