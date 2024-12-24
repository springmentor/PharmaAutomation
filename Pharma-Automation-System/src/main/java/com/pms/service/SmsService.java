package com.pms.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    private static final String ACCOUNT_SID = "AC45a9dc14d72d7e2ead3050b7813cd34a";
    private static final String AUTH_TOKEN = "f8711495c79ad4a020bce9c351cae45d";
    private static final String FROM_PHONE = "+12762861768";

    public SmsService() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendSms(String to, String message) {
        Message.creator(new com.twilio.type.PhoneNumber(to),
                new com.twilio.type.PhoneNumber(FROM_PHONE),
                message).create();
    }
}
