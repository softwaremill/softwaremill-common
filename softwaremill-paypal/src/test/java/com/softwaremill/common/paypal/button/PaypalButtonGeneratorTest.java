package com.softwaremill.common.paypal.button;

import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

public class PaypalButtonGeneratorTest {

    private static final String GO = "http://go.back";
    private static final String GO_CANCEL = "http://go.cancel";

    @Test
    public void shouldPassSimpleCase() {
        // given

        PaypalButtonGenerator pbg = new PaypalButtonGenerator("test@email.com", GO, GO_CANCEL);

        // when
        pbg.addItem("test", "10");

        String form = pbg.build();

        // then
        assertThat(form).isEqualTo("<form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\">\n" +
                "\t<input type=\"hidden\" name=\"cmd\" value=\"_cart\"/>\n" +
                "\t<input type=\"hidden\" name=\"upload\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"return\" value=\"http://go.back\"/>\n" +
                "\t<input type=\"hidden\" name=\"cancel_return\" value=\"http://go.cancel\"/>\n" +
                "\t<input type=\"hidden\" name=\"rm\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"business\" value=\"test@email.com\"/>\n" +
                "\t<input type=\"hidden\" name=\"currency_code\" value=\"USD\"/>\n" +
                "\t<input type=\"hidden\" name=\"item_name_1\" value=\"test\"/>\n" +
                "\t<input type=\"hidden\" name=\"amount_1\" value=\"10\"/>\n" +
                "\t<input type=\"hidden\" name=\"shipping_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"hidden\" name=\"tax_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"submit\" value=\"Pay with PayPal\"/>\n" +
                "</form>\n");
    }

    @Test
    public void shouldUseImageIfSet() {
        // given

        PaypalButtonGenerator pbg = new PaypalButtonGenerator("test@email.com", GO, GO_CANCEL);

        // when
        pbg.withSubmitImage("image_button.png").addItem("test", "10");

        String form = pbg.build();

        // then
        assertThat(form).isEqualTo("<form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\">\n" +
                "\t<input type=\"hidden\" name=\"cmd\" value=\"_cart\"/>\n" +
                "\t<input type=\"hidden\" name=\"upload\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"return\" value=\"http://go.back\"/>\n" +
                "\t<input type=\"hidden\" name=\"cancel_return\" value=\"http://go.cancel\"/>\n" +
                "\t<input type=\"hidden\" name=\"rm\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"business\" value=\"test@email.com\"/>\n" +
                "\t<input type=\"hidden\" name=\"currency_code\" value=\"USD\"/>\n" +
                "\t<input type=\"hidden\" name=\"item_name_1\" value=\"test\"/>\n" +
                "\t<input type=\"hidden\" name=\"amount_1\" value=\"10\"/>\n" +
                "\t<input type=\"hidden\" name=\"shipping_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"hidden\" name=\"tax_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"image\" alt=\"Pay with PayPal\" src=\"image_button.png\"/>\n" +
                "</form>\n");
    }

    @Test
    public void shouldPassWithAllParametersPassedCase() {
        // given

        PaypalButtonGenerator pbg = new PaypalButtonGenerator("test@email.com", GO, GO_CANCEL);

        // when
        pbg.addItem("test", "10", "1.23", "2.34");

        String form = pbg.build();

        // then
        assertThat(form).isEqualTo("<form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\">\n" +
                "\t<input type=\"hidden\" name=\"cmd\" value=\"_cart\"/>\n" +
                "\t<input type=\"hidden\" name=\"upload\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"return\" value=\"http://go.back\"/>\n" +
                "\t<input type=\"hidden\" name=\"cancel_return\" value=\"http://go.cancel\"/>\n" +
                "\t<input type=\"hidden\" name=\"rm\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"business\" value=\"test@email.com\"/>\n" +
                "\t<input type=\"hidden\" name=\"currency_code\" value=\"USD\"/>\n" +
                "\t<input type=\"hidden\" name=\"item_name_1\" value=\"test\"/>\n" +
                "\t<input type=\"hidden\" name=\"amount_1\" value=\"10\"/>\n" +
                "\t<input type=\"hidden\" name=\"shipping_1\" value=\"1.23\"/>\n" +
                "\t<input type=\"hidden\" name=\"tax_1\" value=\"2.34\"/>\n" +
                "\t<input type=\"submit\" value=\"Pay with PayPal\"/>\n" +
                "</form>\n");
    }

    @Test
    public void shouldPassWithDefaultTax() {
        // given

        PaypalButtonGenerator pbg = new PaypalButtonGenerator("test@email.com", GO, GO_CANCEL);

        // when
        pbg.withDefaultTax("23").addItemWithShipping("test", "10", "1.23");

        String form = pbg.build();

        // then
        assertThat(form).isEqualTo("<form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\">\n" +
                "\t<input type=\"hidden\" name=\"cmd\" value=\"_cart\"/>\n" +
                "\t<input type=\"hidden\" name=\"upload\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"return\" value=\"http://go.back\"/>\n" +
                "\t<input type=\"hidden\" name=\"cancel_return\" value=\"http://go.cancel\"/>\n" +
                "\t<input type=\"hidden\" name=\"rm\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"business\" value=\"test@email.com\"/>\n" +
                "\t<input type=\"hidden\" name=\"currency_code\" value=\"USD\"/>\n" +
                "\t<input type=\"hidden\" name=\"item_name_1\" value=\"test\"/>\n" +
                "\t<input type=\"hidden\" name=\"amount_1\" value=\"10\"/>\n" +
                "\t<input type=\"hidden\" name=\"shipping_1\" value=\"1.23\"/>\n" +
                "\t<input type=\"hidden\" name=\"tax_1\" value=\"23\"/>\n" +
                "\t<input type=\"submit\" value=\"Pay with PayPal\"/>\n" +
                "</form>\n");
    }

    @Test
    public void shouldPassWithDefaultShipping() {
        // given

        PaypalButtonGenerator pbg = new PaypalButtonGenerator("test@email.com", GO, GO_CANCEL);

        // when
        pbg.withDefaultShipping("11").addItemWithTax("test", "10", "1.5");

        String form = pbg.build();

        // then
        assertThat(form).isEqualTo("<form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\">\n" +
                "\t<input type=\"hidden\" name=\"cmd\" value=\"_cart\"/>\n" +
                "\t<input type=\"hidden\" name=\"upload\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"return\" value=\"http://go.back\"/>\n" +
                "\t<input type=\"hidden\" name=\"cancel_return\" value=\"http://go.cancel\"/>\n" +
                "\t<input type=\"hidden\" name=\"rm\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"business\" value=\"test@email.com\"/>\n" +
                "\t<input type=\"hidden\" name=\"currency_code\" value=\"USD\"/>\n" +
                "\t<input type=\"hidden\" name=\"item_name_1\" value=\"test\"/>\n" +
                "\t<input type=\"hidden\" name=\"amount_1\" value=\"10\"/>\n" +
                "\t<input type=\"hidden\" name=\"shipping_1\" value=\"11\"/>\n" +
                "\t<input type=\"hidden\" name=\"tax_1\" value=\"1.5\"/>\n" +
                "\t<input type=\"submit\" value=\"Pay with PayPal\"/>\n" +
                "</form>\n");
    }

    @Test
    public void shouldPassWithMoreItems() {
        // given

        PaypalButtonGenerator pbg = new PaypalButtonGenerator("test@email.com", GO, GO_CANCEL);

        // when
        pbg.addItem("foo", "12").addItem("bar", "10");

        String form = pbg.build();

        // then
        assertThat(form).isEqualTo("<form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\">\n" +
                "\t<input type=\"hidden\" name=\"cmd\" value=\"_cart\"/>\n" +
                "\t<input type=\"hidden\" name=\"upload\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"return\" value=\"http://go.back\"/>\n" +
                "\t<input type=\"hidden\" name=\"cancel_return\" value=\"http://go.cancel\"/>\n" +
                "\t<input type=\"hidden\" name=\"rm\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"business\" value=\"test@email.com\"/>\n" +
                "\t<input type=\"hidden\" name=\"currency_code\" value=\"USD\"/>\n" +
                "\t<input type=\"hidden\" name=\"item_name_1\" value=\"foo\"/>\n" +
                "\t<input type=\"hidden\" name=\"amount_1\" value=\"12\"/>\n" +
                "\t<input type=\"hidden\" name=\"shipping_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"hidden\" name=\"tax_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"hidden\" name=\"item_name_2\" value=\"bar\"/>\n" +
                "\t<input type=\"hidden\" name=\"amount_2\" value=\"10\"/>\n" +
                "\t<input type=\"hidden\" name=\"shipping_2\" value=\"0.0\"/>\n" +
                "\t<input type=\"hidden\" name=\"tax_2\" value=\"0.0\"/>\n" +
                "\t<input type=\"submit\" value=\"Pay with PayPal\"/>\n" +
                "</form>\n");
    }

    @Test
    public void shouldPassWithMoreItemsAndObjects() {
        // given

        PaypalButtonGenerator pbg = new PaypalButtonGenerator("test@email.com", GO, GO_CANCEL);

        // when
        pbg.addItem("foo", "12").addItem("bar", "10")
                .addItems(new PaypalCartItem("fooOb", "1", "1", "1"), new PaypalCartItem("barOb", "2", "2", "2"));

        String form = pbg.build();

        // then
        assertThat(form).isEqualTo("<form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\">\n" +
                "\t<input type=\"hidden\" name=\"cmd\" value=\"_cart\"/>\n" +
                "\t<input type=\"hidden\" name=\"upload\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"return\" value=\"http://go.back\"/>\n" +
                "\t<input type=\"hidden\" name=\"cancel_return\" value=\"http://go.cancel\"/>\n" +
                "\t<input type=\"hidden\" name=\"rm\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"business\" value=\"test@email.com\"/>\n" +
                "\t<input type=\"hidden\" name=\"currency_code\" value=\"USD\"/>\n" +
                "\t<input type=\"hidden\" name=\"item_name_1\" value=\"foo\"/>\n" +
                "\t<input type=\"hidden\" name=\"amount_1\" value=\"12\"/>\n" +
                "\t<input type=\"hidden\" name=\"shipping_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"hidden\" name=\"tax_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"hidden\" name=\"item_name_2\" value=\"bar\"/>\n" +
                "\t<input type=\"hidden\" name=\"amount_2\" value=\"10\"/>\n" +
                "\t<input type=\"hidden\" name=\"shipping_2\" value=\"0.0\"/>\n" +
                "\t<input type=\"hidden\" name=\"tax_2\" value=\"0.0\"/>\n" +
                "\t<input type=\"hidden\" name=\"item_name_3\" value=\"fooOb\"/>\n" +
                "\t<input type=\"hidden\" name=\"amount_3\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"shipping_3\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"tax_3\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"item_name_4\" value=\"barOb\"/>\n" +
                "\t<input type=\"hidden\" name=\"amount_4\" value=\"2\"/>\n" +
                "\t<input type=\"hidden\" name=\"shipping_4\" value=\"2\"/>\n" +
                "\t<input type=\"hidden\" name=\"tax_4\" value=\"2\"/>\n" +
                "\t<input type=\"submit\" value=\"Pay with PayPal\"/>\n" +
                "</form>\n");
    }

    @Test
    public void shouldUseCorrectDefaultTax() {
        // given

        PaypalButtonGenerator pbg = new PaypalButtonGenerator("test@email.com", GO, GO_CANCEL);

        // when
        pbg.withDefaultTax("12.5").addItem("test", "10");

        String form = pbg.build();

        // then
        assertThat(form).isEqualTo("<form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\">\n" +
                "\t<input type=\"hidden\" name=\"cmd\" value=\"_cart\"/>\n" +
                "\t<input type=\"hidden\" name=\"upload\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"return\" value=\"http://go.back\"/>\n" +
                "\t<input type=\"hidden\" name=\"cancel_return\" value=\"http://go.cancel\"/>\n" +
                "\t<input type=\"hidden\" name=\"rm\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"business\" value=\"test@email.com\"/>\n" +
                "\t<input type=\"hidden\" name=\"currency_code\" value=\"USD\"/>\n" +
                "\t<input type=\"hidden\" name=\"item_name_1\" value=\"test\"/>\n" +
                "\t<input type=\"hidden\" name=\"amount_1\" value=\"10\"/>\n" +
                "\t<input type=\"hidden\" name=\"shipping_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"hidden\" name=\"tax_1\" value=\"12.5\"/>\n" +
                "\t<input type=\"submit\" value=\"Pay with PayPal\"/>\n" +
                "</form>\n");
    }

    @Test
    public void shouldUseCorrectDefaultShipping() {
        // given

        PaypalButtonGenerator pbg = new PaypalButtonGenerator("test@email.com", GO, GO_CANCEL);

        // when
        pbg.withDefaultShipping("10.5").addItem("test", "10");

        String form = pbg.build();

        // then
        assertThat(form).isEqualTo("<form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\">\n" +
                "\t<input type=\"hidden\" name=\"cmd\" value=\"_cart\"/>\n" +
                "\t<input type=\"hidden\" name=\"upload\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"return\" value=\"http://go.back\"/>\n" +
                "\t<input type=\"hidden\" name=\"cancel_return\" value=\"http://go.cancel\"/>\n" +
                "\t<input type=\"hidden\" name=\"rm\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"business\" value=\"test@email.com\"/>\n" +
                "\t<input type=\"hidden\" name=\"currency_code\" value=\"USD\"/>\n" +
                "\t<input type=\"hidden\" name=\"item_name_1\" value=\"test\"/>\n" +
                "\t<input type=\"hidden\" name=\"amount_1\" value=\"10\"/>\n" +
                "\t<input type=\"hidden\" name=\"shipping_1\" value=\"10.5\"/>\n" +
                "\t<input type=\"hidden\" name=\"tax_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"submit\" value=\"Pay with PayPal\"/>\n" +
                "</form>\n");
    }

    @Test
    public void shouldUseCorrectPaypalLabel() {
        // given

        PaypalButtonGenerator pbg = new PaypalButtonGenerator("test@email.com", GO, GO_CANCEL);

        // when
        pbg.withPaypalButtonLabel("Pay me please").addItem("test", "10");

        String form = pbg.build();

        // then
        assertThat(form).isEqualTo("<form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\">\n" +
                "\t<input type=\"hidden\" name=\"cmd\" value=\"_cart\"/>\n" +
                "\t<input type=\"hidden\" name=\"upload\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"return\" value=\"http://go.back\"/>\n" +
                "\t<input type=\"hidden\" name=\"cancel_return\" value=\"http://go.cancel\"/>\n" +
                "\t<input type=\"hidden\" name=\"rm\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"business\" value=\"test@email.com\"/>\n" +
                "\t<input type=\"hidden\" name=\"currency_code\" value=\"USD\"/>\n" +
                "\t<input type=\"hidden\" name=\"item_name_1\" value=\"test\"/>\n" +
                "\t<input type=\"hidden\" name=\"amount_1\" value=\"10\"/>\n" +
                "\t<input type=\"hidden\" name=\"shipping_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"hidden\" name=\"tax_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"submit\" value=\"Pay me please\"/>\n" +
                "</form>\n");
    }

    @Test
    public void shouldUseCorrectCurrency() {
        // given

        PaypalButtonGenerator pbg = new PaypalButtonGenerator("test@email.com", GO, GO_CANCEL);

        // when
        pbg.withCurrency("PLN").addItem("test", "10");

        String form = pbg.build();

        // then
        assertThat(form).isEqualTo("<form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\">\n" +
                "\t<input type=\"hidden\" name=\"cmd\" value=\"_cart\"/>\n" +
                "\t<input type=\"hidden\" name=\"upload\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"return\" value=\"http://go.back\"/>\n" +
                "\t<input type=\"hidden\" name=\"cancel_return\" value=\"http://go.cancel\"/>\n" +
                "\t<input type=\"hidden\" name=\"rm\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"business\" value=\"test@email.com\"/>\n" +
                "\t<input type=\"hidden\" name=\"currency_code\" value=\"PLN\"/>\n" +
                "\t<input type=\"hidden\" name=\"item_name_1\" value=\"test\"/>\n" +
                "\t<input type=\"hidden\" name=\"amount_1\" value=\"10\"/>\n" +
                "\t<input type=\"hidden\" name=\"shipping_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"hidden\" name=\"tax_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"submit\" value=\"Pay with PayPal\"/>\n" +
                "</form>\n");
    }

    @Test
    public void shouldUseInvoiceNumber() {
        // given

        PaypalButtonGenerator pbg = new PaypalButtonGenerator("test@email.com", GO, GO_CANCEL);

        // when
        pbg.withInvoiceNumber("invoice/123/10").addItem("test", "10");

        String form = pbg.build();

        // then
        assertThat(form).isEqualTo("<form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\">\n" +
                "\t<input type=\"hidden\" name=\"cmd\" value=\"_cart\"/>\n" +
                "\t<input type=\"hidden\" name=\"upload\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"return\" value=\"http://go.back\"/>\n" +
                "\t<input type=\"hidden\" name=\"cancel_return\" value=\"http://go.cancel\"/>\n" +
                "\t<input type=\"hidden\" name=\"rm\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"business\" value=\"test@email.com\"/>\n" +
                "\t<input type=\"hidden\" name=\"currency_code\" value=\"USD\"/>\n" +
                "\t<input type=\"hidden\" name=\"item_name_1\" value=\"test\"/>\n" +
                "\t<input type=\"hidden\" name=\"amount_1\" value=\"10\"/>\n" +
                "\t<input type=\"hidden\" name=\"shipping_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"hidden\" name=\"tax_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"hidden\" name=\"invoice\" value=\"invoice/123/10\"/>\n" +
                "\t<input type=\"submit\" value=\"Pay with PayPal\"/>\n" +
                "</form>\n");
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void shouldFailOnBadAmount() {
        new PaypalCartItem("test", "badAmount", "0", "0");
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void shouldFailOnBadTaxAmount() {
        new PaypalCartItem("test", "0", "0", "taxBad");
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void shouldFailOnBadShippingAmount() {
        new PaypalCartItem("test", "0", "shippingBad", "0");
    }

    @Test
    public void shouldPassOnAllGoodAmounts() {
        new PaypalCartItem("test", "0", "0", "0");
    }

    @Test
    public void shouldUseNotifyUrl() {
        // given

        PaypalButtonGenerator pbg = new PaypalButtonGenerator("test@email.com", GO, GO_CANCEL).
                withNotifyUrl("http://notify.com/paypal");

        // when
        pbg.addItem("test", "10");

        String form = pbg.build();

        // then
        assertThat(form).isEqualTo("<form action=\"https://www.paypal.com/cgi-bin/webscr\" method=\"post\">\n" +
                "\t<input type=\"hidden\" name=\"cmd\" value=\"_cart\"/>\n" +
                "\t<input type=\"hidden\" name=\"upload\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"return\" value=\"http://go.back\"/>\n" +
                "\t<input type=\"hidden\" name=\"cancel_return\" value=\"http://go.cancel\"/>\n" +
                "\t<input type=\"hidden\" name=\"rm\" value=\"1\"/>\n" +
                "\t<input type=\"hidden\" name=\"business\" value=\"test@email.com\"/>\n" +
                "\t<input type=\"hidden\" name=\"currency_code\" value=\"USD\"/>\n" +
                "\t<input type=\"hidden\" name=\"item_name_1\" value=\"test\"/>\n" +
                "\t<input type=\"hidden\" name=\"amount_1\" value=\"10\"/>\n" +
                "\t<input type=\"hidden\" name=\"shipping_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"hidden\" name=\"tax_1\" value=\"0.0\"/>\n" +
                "\t<input type=\"hidden\" name=\"notify_url\" value=\"http://notify.com/paypal\"/>\n" +
                "\t<input type=\"submit\" value=\"Pay with PayPal\"/>\n" +
                "</form>\n");
    }
}
