package com.hms.appointment.service.serviceImp;

import com.hms.appointment.service.WhatsAppService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;  // ← SAHI IMPORT
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppServiceImp implements WhatsAppService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.from}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    @Override
    public void sendMessage(String toPhone, String messageBody) {
        try {
            String cleanPhone = toPhone.replaceAll("[^0-9]", "");
            if (cleanPhone.length() == 10) {
                cleanPhone = "91" + cleanPhone;
            }
            Message.creator(
                    new PhoneNumber("whatsapp:+" + cleanPhone),
                    new PhoneNumber(fromNumber),
                    messageBody
            ).create();
            System.out.println("WhatsApp sent to: +" + cleanPhone);
        } catch (Exception e) {
            System.out.println("WhatsApp error: " + e.getMessage());
        }
    }
}