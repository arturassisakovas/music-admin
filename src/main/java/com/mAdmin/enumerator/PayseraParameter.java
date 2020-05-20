package com.mAdmin.enumerator;

/**
 * Paysera parameters names enumerator.
 * Based on version of specification 1.6.
 * See <a href="https://developers.paysera.com/en/payments/current#integration-via-specification">
 * Integration with detailed specification</a>
 */
public enum PayseraParameter {

    /**
     * Length: 255
     * Required: Yes
     * Full address (URL), to which the client is directed after a successful payment.
     */
    ACCEPT_URL("accepturl"),

    /**
     * Account number from which payment has been made.
     */
    ACCOUNT("account"),

    /**
     * Length: 11
     * Required: No
     * Amount in cents the client has to pay.
     */
    AMOUNT("amount"),

    /**
     * Length: 255
     * Required: Yes
     * Full address (URL), to which a seller will get information about performed payment.
     * Script must return text "OK".Only then our system will register,
     * that information about the payment has been received.
     * If there is no answer "OK", the message will be sent 4 times
     * (when we get it, after an hour, after three hours and after 24 hours).
     */
    CALLBACK_URL("callbackurl"),

    /**
     * Length: 255
     * Required: Yes
     * Full address (URL), to which the client is directed after an unsuccessful payment or cancellation.
     */
    CANCEL_URL("cancelurl"),

    /**
     * Length: 2
     * Required: No
     * Payer's country (LT, EE, LV, GB, PL, DE).
     * All possible types of payment in that country are immediately indicated to the payer, after selecting a country.
     */
    COUNTRY("country"),

    /**
     * Length: 3
     * Required: No
     * Payment currency (i.e USD, EUR, etc.) you want the client to pay in.
     * If the selected currency cannot be accepted by a specific payment method,
     * the system will convert it automatically to the acceptable currency, according to the currency rate of the day.
     * Payamount and paycurrency answers will be sent to your website.
     */
    CURRENCY("currency"),

    /**
     * Length: 11
     * Required: No
     * In case you are labeled as a developer in our system, you have to transfer this parameter in your
     * installed project (projects). The value of the parameter - your unique user number.
     */
    DEVELOPER_ID("developerid"),

    /**
     * Length: 0
     * Required: No
     * Hide payment methods separated by comma.
     */
    DISALLOW_PAYMENTS("disalow_payments"),

    /**
     * Length: 3
     * Required: No
     * It is possible to indicate the user language (ISO 639-2/B: LIT, RUS, ENG, etc.).
     * If Paysera does not support the selected language, the system will automatically choose a language
     * according to the IP address or ENG language by default.
     */
    LANG("lang"),

    /**
     * Payer's name received from the payment system. Sent only if the payment system provides such.
     */
    NAME("name"),

    /**
     * Length: 0
     * Required: No
     * Show only those payment methods that are separated by commas.
     */
    ONLY_PAYMENTS("only_payments"),

    /**
     * Length: 40
     * Required: Yes
     * Order number from your system.
     */
    ORDER_ID("orderid"),

    /**
     * Amount of the transfer in cents. It can differ, if it was converted to another currency.
     */
    PAY_AMOUNT("payamount"),

    /**
     * The transferred payment currency (i.e USD, EUR, etc.). It can differ from the one you requested,
     * if the currency could not be accepted by the selected payment method.
     */
    PAY_CURRENCY("paycurrency"),

    /**
     * Length: 255
     * Required: No
     * Payer's city, to which goods will be sent (e.g.: Vilnius). Necessary for certain payment methods.
     */
    PAYER_CITY("p_city"),

    /**
     * Country of the payer established by the country of the payment method,
     * and if the payment method is international – by the IP address of the payer.
     * The country is provided in the two-character (ISO 3166-1 alpha-2) format, e.g.: LT, PL, RU, EE.
     */
    PAYER_COUNTRY("payer_country"),

    /**
     * Length: 2
     * Required: No
     * Payer's country code. The list with country codes can be found
     * <a href="http://en.wikipedia.org/wiki/List_of_ISO_country_codes">here</a>.
     * Necessary for certain payment methods.
     */
    PAYER_COUNTRY_CODE("p_countrycode"),

    /**
     * Length: 255
     * Required: No
     * Payer's email address is necessary. If the email address is not received,
     * the client will be requested to enter it.
     * Paysera system will inform the payer about the payment status by this address.
     */
    PAYER_EMAIL("p_email"),

    /**
     * Length: 255
     * Required: No
     * Payer's name. Requested in the majority of payment methods. Necessary for certain payment methods.
     */
    PAYER_FIRST_NAME("p_firstname"),

    /**
     * Country of the payer established by the IP address of the payer.
     * The country is provided in two-character (ISO 3166-1 alpha-2) format, e.g.: LT, PL, RU, EE.
     */
    PAYER_IP_COUNTRY("payer_ip_country"),

    /**
     * Length: 255
     * Required: No
     * Payer's surname. Requested in the majority of payment methods. Necessary for certain payment methods.
     */
    PAYER_LAST_NAME("p_lastname"),

    /**
     * Length: 20
     * Required: No
     * Payer's postal code. Lithuanian postal codes can be found <a href="http://www.post.lt/lt/?id=316">here</a>.
     * Necessary for certain payment methods.
     */
    PAYER_POSTAL_CODE("p_zip"),

    /**
     * Length: 20
     * Required: No
     * Payer's state code (necessary, when buying in USA). Necessary for certain payment methods.
     */
    PAYER_STATE("p_state"),

    /**
     * Length: 255
     * Required: No
     * Payer's address, to which goods will be sent (e.g.: Mėnulio g. 7 - 7). Necessary for certain payment methods.
     */
    PAYER_STREET("p_street"),

    /**
     * Length: 20
     * Required: No
     * Payment type. If provided, the payment will be made
     * by the method specified (for example by using the specified bank).
     * If not specified, the payer will be immediately provided with the payment types to choose from.
     * You can get payment types in real time by using WebToPay library.
     */
    PAYMENT("payment"),

    /**
     * Country of the payment method. If the payment method is available in more than one country (international)
     * – the parameter is not sent.
     * The country is provided in the two-character (ISO 3166-1 alpha-2) format, e.g.: LT, PL, RU, EE.
     */
    PAYMENT_COUNTRY("payment_country"),

    /**
     * Length: 255* (* Final length may vary depending on payment type specification)
     * Required: No
     * Payment purpose visible when making the payment. If not specified, default text is used:
     *     Payment for goods and services (for nb. [order_nr]) ([site_name]).
     *
     * If you specify the payment purpose, it is necessary to include the following variables,
     * which will be replaced with the appropriate values in the final purpose text:
     *  [order_nr] - payment number.
     *  [site_name] or [owner_name] - website address or company name.
     *
     * If these variables are not specified, the default purpose text will be used.
     *
     * Example of a payment purpose:
     *  Payment for goods made to order [order_nr] in website [site_name].
     */
    PAY_TEXT("paytext"),

    /**
     * Length: 255
     * Required: No
     * This parameter can be used for user authentication. If the user’s identification number is transferred,
     * together with callback Paysera will return personcodestatus parameter,
     * which will indicate whether the personal code corresponds to the transferred one.
     */
    PERSONAL_CODE("personcode"),

    /**
     * If you have provided personcode parameter when making the request, this parameter indicates whether
     * the given personal code matches the real one. Possible values:
     *  0 - personal code is yet unknown
     *  1 - personal code matches
     *  2 - personal code does not match
     *  3 - personal code is unknown
     *
     *  If the personal code is unknown at the moment callback is made,
     *  another callback will be made with status parameter set to 3, as soon as the personal code will be known.
     */
    PERSONAL_CODE_STATUS("personcodestatus"),

    /**
     * Length: 11
     * Required: Yes
     * Unique project number. Only activated projects can accept payments.
     */
    PROJECT_ID("projectid"),

    /**
     * It is a request number, which we receive when the user presses on the logo of the bank.
     * We transfer this request number to the link provided in the "callbackurl" field.
     */
    REQUEST_ID("requestid"),

    /**
     * Payment status:
     *  0 - payment has not been executed
     *  1 - payment successful
     *  2 - payment order accepted, but not yet executed (this status does not guarantee execution of the payment)
     *  3 - additional payment information.
     */
    STATUS("status"),

    /**
     * Payer's surname received from the payment system. Sent only if the payment system provides such.
     */
    SURNAME("surename"),

    /**
     * Length: 1
     * Required: No
     * The parameter, which allows to test the connection. The payment is not executed,
     * but the result is returned immediately, as if the payment has been made.
     * To test, it is necessary to activate the mode for a particular project by logging in and selecting:
     * "Manage projects" -> "Payment gateway" (for a specific project) -> "Allow test payments" (check).
     */
    TEST("test"),

    /**
     * Length: 19
     * Required: No
     * The parameter indicating the final date for payment; the date is given in “yyyy-mm-dd HH:MM:SS” format.
     * The minimum value is 15 minutes from the current moment; the maximum value is 3 days.
     * Note: works only with certain payment methods.
     */
    TIME_LIMIT("time_limit"),

    /**
     * Length: 9
     * Required: Yes
     * The version number of Paysera system specification (API).
     */
    VERSION("version");

    /**
     * Parameter name.
     */
    private final String name;

    /**
     * Constructor that sets parameter name.
     * @param name parameter name
     */
    PayseraParameter(final String name) {
        this.name = name;
    }

    /**
     * Returns parameter name.
     * @return parameter name
     */
    public String getName() {
        return this.name;
    }

}
