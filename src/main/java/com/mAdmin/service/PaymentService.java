package com.mAdmin.service;

import com.itextpdf.text.DocumentException;
import com.mAdmin.entity.Attendee;
import com.mAdmin.entity.Invoice;
import com.mAdmin.entity.Payment;
import org.apache.http.NameValuePair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;


public interface PaymentService {

    
    String urlEncodeUtf8(String param);

    
    String urlEncodeUtf8(Map<?, ?> params);

    
    String base64EncodeToString(String input);

    
    String base64DecodeToString(String encodedValue);

    
    String replaceSymbolsForEncoding(String string);

    
    String preparePaymentData(Map<?, ?> data);

    
    String preparePaymentSign(String data, String password);

    
    List<NameValuePair> preparePaymentRequestArguments(Map<?, ?> paymentData, String password);

    
    String replaceSymbolsForDecoding(String string);

    
    String getMD5EncryptedValue(String data);

    
    String decodePayseraCallback(Map<String, String> requestParams);

    
    Map<String, String> mapPayseraCallbackParameters(String parameters);

    
    Map<String, Boolean> comparePayerDataWithPayseraCallback(Map<?, ?> map);

    
    PublicKey getPublicKey(String certPath) throws FileNotFoundException, CertificateException;

    
    boolean verifySignedData(String data, String sign, PublicKey publicKey)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException;

    
    void processPayment(Map<String, Object> session) throws IOException, DocumentException;

    
    void processPayment(Invoice invoice, String personalCode, Long paymentId);

    
    Invoice createContractAndInvoice(Map<String, Object> session) throws IOException, DocumentException;

    
    boolean isInvoicePaid(Invoice invoice);

    
    Payment formOneCentPayment(Invoice oneCentInvoice);

    
    void createInvoicePayment(Invoice invoice);

    
    boolean isCallBackWithFullDataForContract(Map<String, String> map, BigDecimal price);

    
    void processPayment(Invoice invoice, Long paymentId);

    
    boolean isCallBackWithFullDataForServicePayment(Map<String, String> map, BigDecimal price);

    
    void extendPayTime(List<Attendee> attendees);
}
