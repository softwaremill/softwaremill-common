package com.softwaremill.common.paypal.process.status;

import com.softwaremill.common.paypal.process.RequestParameters;

/**
 * @Author: lukasz.zuchowski at gmail dot com
 * Date: 05.04.12
 * Time: 13:16
 */
public interface PayPalStatusVerifier {

    public PayPalStatus verify(String url, RequestParameters requestParameters);
}
