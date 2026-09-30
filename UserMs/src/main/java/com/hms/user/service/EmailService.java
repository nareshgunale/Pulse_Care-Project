package com.hms.user.service;

import com.hms.user.dto.MailBody;

public interface EmailService {

    void sendSimpleMessage(MailBody mailBody);
}
