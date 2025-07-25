package com.api.Event_Management_API.util;

import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

public class OnlinePaymentUtil {
    String clientId = System.getProperty("PAYOS_CLIENT_ID");
    String apiKey = System.getProperty("PAYOS_API_KEY");
    String checksumKey = System.getProperty("PAYOS_CHECKSUM_KEY");

    String domain = "http://localhost:10000/";

    final PayOS payOS = new PayOS(clientId, apiKey, checksumKey);

    public String getPaymentURL(String paymentName, Integer price, long expireDate, String successUrl, String cancelUrl) {
        Long orderCode = System.currentTimeMillis() / 1000;

        ItemData itemData = ItemData
            .builder()
            .name(paymentName)
            .quantity(1)
            .price(price)
            .build();

        PaymentData paymentData = PaymentData
            .builder()
            .orderCode(orderCode)
            .amount(price)
            .description(paymentName)
            .expiredAt(expireDate)
            .returnUrl(domain + successUrl)
            .cancelUrl(domain + cancelUrl)
            .item(itemData)
            .build();

        CheckoutResponseData result;

        try {
            result = payOS.createPaymentLink(paymentData);
        } catch(Exception e) {
            return null;
        }
    
        return result.getCheckoutUrl();
    }
}
