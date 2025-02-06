package com.ptitB22CN539.LaptopShop.Service.SendEmail;

import java.util.Map;

public interface IEmailService {
    void sendEmail(String to, String subject, String template, Map<String, Object> properties);
}
