/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.devnexus.ting.web.controller;

import com.devnexus.ting.common.SpringProfile;

import com.devnexus.ting.core.service.BusinessService;
import com.devnexus.ting.model.CouponCode;
import com.devnexus.ting.model.Event;
import com.devnexus.ting.model.EventSignup;
import com.devnexus.ting.model.PayPalPayment;
import com.devnexus.ting.model.PaypalLink;
import com.devnexus.ting.model.RegistrationDetails;
import com.devnexus.ting.model.ScheduleItemList;
import com.devnexus.ting.model.SpeakerList;
import com.devnexus.ting.model.TicketAddOn;
import com.devnexus.ting.model.TicketGroup;
import com.devnexus.ting.model.TicketOrderDetail;
import com.devnexus.ting.web.form.RegisterForm;
import com.devnexus.ting.web.form.SignupRegisterView;
import com.devnexus.ting.web.payment.PayPalSession;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * Display Register Form
 *
 * @author Summers Pittman
 */
@Controller
public class RegisterController {

    @Autowired
    private Environment environment;

    private void addOverSoldErrorsToFields(RegistrationDetails registerForm, BindingResult result, TicketAddOn addOn) {

        for (int index = 0; index < registerForm.getOrderDetails().size(); index++) {
            TicketOrderDetail orderDetails = registerForm.getOrderDetails().get(index);
            if (orderDetails.getTicketAddOn().equals(addOn)) {
                result.addError(new FieldError("registerFormPageTwo", "orderDetails[" + index + "].ticketAddOn", "Sorry, this workshop is sold out."));
            }
        }
    }

    private enum PaymentMethod {

        PAYPAL, INVOICE
    };

    @Autowired
    private BusinessService businessService;

    @RequestMapping(value = "/s/register", method = RequestMethod.GET)
    public String getRegistrationFormForCurrentEvent(Model model) {
        Event currentEvent = businessService.getCurrentEvent();
        EventSignup eventSignup = businessService.getEventSignup();
        prepareHeader(currentEvent, model);
        model.addAttribute("signupRegisterView", new SignupRegisterView(eventSignup));

        model.addAttribute("registerForm", new RegisterForm());

        return "register";
    }

    @RequestMapping(value = "/s/register/{registrationKey}", method = RequestMethod.GET)
    public String viewRegistrationFormForCurrentEvent(@PathVariable("registrationKey") String registrationKey, Model model) {

        RegistrationDetails registerForm = businessService.getRegistrationForm(registrationKey);

        Event currentEvent = businessService.getCurrentEvent();
        EventSignup eventSignup = businessService.getEventSignup();
        prepareHeader(currentEvent, model);
        model.addAttribute("signupRegisterView", new SignupRegisterView(eventSignup));
        model.addAttribute("registerFormPageTwo", registerForm);
        model.addAttribute("addOns", eventSignup.getAddOns());

        return "view-registration";
    }

    @RequestMapping(value = "/s/register", method = RequestMethod.POST)
    public String validateInitialFormAndPrepareDetailsForm(Model model, @Valid RegisterForm registerForm, BindingResult result) {

        TicketGroup ticketGroup = businessService.getTicketGroup(registerForm.getTicketGroup());
        Event currentEvent = businessService.getCurrentEvent();
        EventSignup eventSignup = businessService.getEventSignup();
        prepareHeader(currentEvent, model);
        model.addAttribute("signupRegisterView", new SignupRegisterView(eventSignup));

        if (registerForm.getTicketCount() < ticketGroup.getMinPurchase()) {
            result.addError(new FieldError("registerForm", "ticketCount", "You need to buy more tickets for this Registration Type."));
        }

        if (ticketGroup.getCouponCodes() != null && ticketGroup.getCouponCodes().size() > 0 && !Strings.isNullOrEmpty(registerForm.getCouponCode())) {
            if (!hasCode(ticketGroup.getCouponCodes(), registerForm.getCouponCode())) {
                result.addError(new FieldError("registerForm", "couponCode", "Invalid Coupon Code."));
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("registerForm", registerForm);
            return "register";
        }

        RegistrationDetails registerFormPageTwo = new RegistrationDetails();
        registerFormPageTwo.copyPageOne(registerForm);

        model.addAttribute("registerFormPageTwo", registerFormPageTwo);

        ArrayList<TicketAddOn> availableAddOns = new ArrayList<>(eventSignup.getAddOns().size());
        for (TicketAddOn addOn : eventSignup.getAddOns()) {
            if (addOn.getMaxAvailableTickets() > businessService.getCountOfAddonsSold(addOn.getId())) {
                availableAddOns.add(addOn);
            }
        }
        model.addAttribute("addOns", availableAddOns);

        return "register2";

    }

    @RequestMapping(value = "/s/lookupCouponCode/{ticketGroupId}/{couponCode}", method = RequestMethod.GET)
    @ResponseBody
    public String getCodedPrice(Model model, @PathVariable("ticketGroupId") final Long ticketGroupId,
            @PathVariable("couponCode") final String couponCode) {
        TicketGroup group = businessService.getTicketGroup(ticketGroupId);
        if (hasCode(group.getCouponCodes(), couponCode)) {
            CouponCode code = findCode(group.getCouponCodes(), couponCode);
            return code.getPrice().setScale(2).toPlainString();
        } else {
            return group.getPrice().setScale(2).toPlainString();
        }

    }

    @RequestMapping(value = "/s/registerPageTwo/{registrationKey}", method = RequestMethod.GET)
    public String loadPageTwo(@PathVariable("registrationKey") final String registrationKey, Model model) {

        RegistrationDetails registerForm = businessService.getRegistrationForm(registrationKey);

        Event currentEvent = businessService.getCurrentEvent();
        EventSignup eventSignup = businessService.getEventSignup();
        prepareHeader(currentEvent, model);
        model.addAttribute("signupRegisterView", new SignupRegisterView(eventSignup));
        model.addAttribute("registerFormPageTwo", registerForm);

        return "register2";

    }

    @RequestMapping(value = "/s/executeRegistration/{registrationKey}", method = RequestMethod.GET)
    public String confirmPayment(@PathVariable("registrationKey") final String registrationKey, @RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId, Model model) {

        RegistrationDetails registerForm = businessService.getRegistrationForm(registrationKey);

        Event currentEvent = businessService.getCurrentEvent();
        EventSignup eventSignup = businessService.getEventSignup();
        prepareHeader(currentEvent, model);
        model.addAttribute("signupRegisterView", new SignupRegisterView(eventSignup));
        model.addAttribute("registerFormPageTwo", registerForm);
        model.addAttribute("registrationKey", registrationKey);
        model.addAttribute("paymentId", paymentId);
        model.addAttribute("payerId", payerId);
        model.addAttribute("addOns", eventSignup.getAddOns());
        return "confirmRegistration";

    }

    @RequestMapping(value = "/s/executeRegistration/{registrationKey}", method = RequestMethod.POST)
    public String executePayment(@PathVariable("registrationKey") final String registrationKey, @RequestParam("paymentId") String paymentId, @RequestParam("payerId") String payerId, Model model) {

        PayPalSession payPalSession = payPalSession();

        Payment payment = payPalSession.execute(paymentId, payerId);
        RegistrationDetails registerForm = businessService.getRegistrationForm(registrationKey);

        registerForm.setPaymentState(RegistrationDetails.PaymentState.PAID);

        PayPalPayment payPalPayment = new PayPalPayment();
        payPalPayment.setPayerId(payerId);
        payPalPayment.setPaymentId(paymentId);
        payPalPayment.setRegistrationKey(registrationKey);
        for (Links link : payment.getLinks()) {
            PaypalLink paypalLink = new PaypalLink();
            paypalLink.setHref(link.getHref());
            paypalLink.setHttpMethod(link.getMethod());
            paypalLink.setRel(link.getRel());
            payPalPayment.addLink(paypalLink);
        }
        businessService.saveAndEmailPaidRegistration(registerForm, payPalPayment);

        return "redirect:/s/index";

    }

    @RequestMapping(value = "/s/registerPageTwo", method = RequestMethod.POST)
    public String validateDetailsForm(Model model, @Valid RegistrationDetails registerForm, BindingResult result) {

        TicketGroup ticketGroup = businessService.getTicketGroup(registerForm.getTicketGroup());
        Event currentEvent = businessService.getCurrentEvent();
        EventSignup eventSignup = businessService.getEventSignup();
        PaymentMethod paymentMethod = !Strings.isNullOrEmpty(registerForm.getInvoice()) ? PaymentMethod.INVOICE : PaymentMethod.PAYPAL;
        prepareHeader(currentEvent, model);
        model.addAttribute("signupRegisterView", new SignupRegisterView(eventSignup));
        model.addAttribute("registerFormPageTwo", registerForm);

        registerForm.setEvent(currentEvent);

        if (result.hasErrors()) {

            ArrayList<TicketAddOn> availableAddOns = new ArrayList<>(eventSignup.getAddOns().size());
            for (TicketAddOn addOn : eventSignup.getAddOns()) {
                if (addOn.getMaxAvailableTickets() > businessService.getCountOfAddonsSold(addOn.getId())) {
                    availableAddOns.add(addOn);
                }
            }
            model.addAttribute("addOns", availableAddOns);

            return "register2";
        }

        if (registerForm.getTicketCount() < ticketGroup.getMinPurchase()) {
            result.addError(new FieldError("registerFormPageTwo", "ticketCount", "You need to buy more tickets for this Registration Type."));
        }

        if (!com.google.common.base.Strings.isNullOrEmpty(registerForm.getCouponCode()) && ticketGroup.getCouponCodes() != null && ticketGroup.getCouponCodes().size() > 0) {
            if (!hasCode(ticketGroup.getCouponCodes(), registerForm.getCouponCode())) {
                result.addError(new FieldError("registerFormPageTwo", "couponCode", "Invalid Coupon Code."));
            }
        }

        Map<TicketAddOn, Integer> addOnCounts = new HashMap<>(registerForm.getOrderDetails().size());

        for (int index = 0; index < registerForm.getOrderDetails().size(); index++) {
            TicketOrderDetail orderDetails = registerForm.getOrderDetails().get(index);

            if (orderDetails.getTicketAddOn() != null) {
                TicketAddOn addon = businessService.findAddOn(orderDetails.getTicketAddOn());
                Integer count = addOnCounts.getOrDefault(addon, 0);
                addOnCounts.put(addon, count + 1);
            }
        }

        for (TicketAddOn addOn : addOnCounts.keySet()) {
            Long sold = businessService.getCountOfAddonsSold(addOn.getId());
            if (sold >= addOn.getMaxAvailableTickets()) {
                addOverSoldErrorsToFields(registerForm, result, addOn);
            }
        }

        for (int index = 0; index < registerForm.getOrderDetails().size(); index++) {
            TicketOrderDetail orderDetails = registerForm.getOrderDetails().get(index);

            if (StringUtils.isEmpty(orderDetails.getFirstName())) {
                result.rejectValue("orderDetails[" + index + "].firstName", "firstName.isRequired", "First Name is required.");
            }

            if (StringUtils.isEmpty(orderDetails.getLastName())) {
                result.rejectValue("orderDetails[" + index + "].lastName", "lastName.isRequired", "Last Name is required.");
            }

            if (StringUtils.isEmpty(orderDetails.getEmailAddress())) {
                result.rejectValue("orderDetails[" + index + "].emailAddress", "emailAddress.isRequired", "Email Address is required.");
            }

            if (StringUtils.isEmpty(orderDetails.getCity())) {
                result.rejectValue("orderDetails[" + index + "].city", "city.isRequired", "City is required.");
            }

            if (StringUtils.isEmpty(orderDetails.getState())) {
                result.rejectValue("orderDetails[" + index + "].state", "state.isRequired", "State is required.");
            }

            if (StringUtils.isEmpty(orderDetails.getCountry())) {
                result.rejectValue("orderDetails[" + index + "].country", "country.isRequired", "Country is required.");
            }

            if (StringUtils.isEmpty(orderDetails.getJobTitle())) {
                result.rejectValue("orderDetails[" + index + "].jobTitle", "jobTitle.isRequired", "Job Title is required.");
            }

            if (StringUtils.isEmpty(orderDetails.getCompany())) {
                result.rejectValue("orderDetails[" + index + "].company", "company.isRequired", "Company is required.");
            }

        }

        if (result.hasErrors()) {

            ArrayList<TicketAddOn> availableAddOns = new ArrayList<>(eventSignup.getAddOns().size());
            for (TicketAddOn addOn : eventSignup.getAddOns()) {
                if (addOn.getMaxAvailableTickets() > businessService.getCountOfAddonsSold(addOn.getId())) {
                    availableAddOns.add(addOn);
                }
            }
            model.addAttribute("addOns", availableAddOns);

            return "register2";
        }

        for (TicketOrderDetail detail : registerForm.getOrderDetails()) {
            detail.setRegistration(registerForm);
        }

        BigDecimal ticketPrice = ticketGroup.getPrice();
        if (!com.google.common.base.Strings.isNullOrEmpty(registerForm.getCouponCode())) {
            CouponCode code = findCode(ticketGroup.getCouponCodes(), registerForm.getCouponCode());
            if (CouponCode.EMPTY != code) {
                ticketPrice = code.getPrice();
            }
        }

        switch (paymentMethod) {

            case INVOICE:
                registerForm.setPaymentState(RegistrationDetails.PaymentState.REQUIRES_INVOICE);
                registerForm.setFinalCost(getTotal(registerForm, ticketPrice));
                businessService.createPendingRegistrationForm(registerForm);
                return "index";
            case PAYPAL:
                registerForm.setPaymentState(RegistrationDetails.PaymentState.PAYPAL_CREATED);
                registerForm.setFinalCost(getTotal(registerForm, ticketPrice));
                registerForm = businessService.createPendingRegistrationForm(registerForm);
                Payment createdPayment = runPayPal(registerForm, ticketPrice.setScale(2).toString());
                return "redirect:" + createdPayment.getLinks().stream().filter(link -> {
                    return link.getRel().equals("approval_url");
                }).findFirst().get().getHref();
            default:
                throw new IllegalStateException("The system did not understand the payment type.");

        }

    }

    private void prepareHeader(Event event, Model model) {
        final ScheduleItemList scheduleItemList = businessService.getScheduleForEvent(event.getId());

        model.addAttribute("event", event);
        SpeakerList speakers = new SpeakerList();
        speakers.setSpeakers(businessService.getSpeakersForEvent(event.getId()));
        model.addAttribute("speakerList", speakers);

        model.addAttribute("scheduleItemList", scheduleItemList);

    }

    private Payment runPayPal(RegistrationDetails registerForm, String price) {
        final PayPalSession payPalSession = payPalSession();

        Amount amount = new Amount();
        amount.setCurrency("USD");
        final BigDecimal total = getTotal(registerForm, new BigDecimal(price));

        Transaction transaction = new Transaction();
        transaction.setDescription("DevNexus Registration");
        transaction.setAmount(amount);

        ItemList itemlist = new ItemList();
        List<Item> items = new ArrayList<>(registerForm.getTicketCount());
        for (TicketOrderDetail order : registerForm.getOrderDetails()) {
            items.add(new Item("1", String.format("Registration for %s %s", order.getFirstName(), order.getLastName()), price, "USD"));
            if (order.getTicketAddOn() != null) {
                TicketAddOn addOn = businessService.findAddOn(order.getTicketAddOn());
                items.add(new Item("1", String.format("%s for %s %s", addOn.getLabel(), order.getFirstName(), order.getLastName()), addOn.getPrice().setScale(2).toString(), "USD"));
            }
        }
        itemlist.setItems(items);

        amount.setTotal(total.setScale(2).toString());
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);
        transaction.setItemList(itemlist);
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:8080/s/registerPageTwo/" + registerForm.getRegistrationFormKey());
        redirectUrls.setReturnUrl("http://localhost:8080/s/executeRegistration/" + registerForm.getRegistrationFormKey());
        payment.setRedirectUrls(redirectUrls);

        return payPalSession.createPayment(payment);
    }

    private BigDecimal getTotal(RegistrationDetails registerForm, BigDecimal ticketPrice) {
        BigDecimal total = BigDecimal.ZERO;

        for (TicketOrderDetail order : registerForm.getOrderDetails()) {
            total = total.add((ticketPrice));
            if (order.getTicketAddOn() != null) {
                TicketAddOn addOn = businessService.findAddOn(order.getTicketAddOn());
                total = total.add(addOn.getPrice());
            }
        }

        return total.setScale(2);
    }

    private boolean hasCode(Collection<CouponCode> couponCodes, String couponCode) {
        for (CouponCode code : couponCodes) {
            if (code.getCode().equals(couponCode)) {
                return true;
            }
        }
        return false;
    }

    private CouponCode findCode(List<CouponCode> couponCodes, String couponCode) {
        for (CouponCode code : couponCodes) {
            if (code.getCode().equals(couponCode)) {
                return code;
            }
        }
        return CouponCode.EMPTY;
    }

    public PayPalSession payPalSession() {
        if (environment.acceptsProfiles(SpringProfile.PAYPAL_SANDBOX)) {
            return PayPalSession.getSession(environment.getRequiredProperty("PAYPAL_CLIENT_ID"), environment.getRequiredProperty("PAYPAL_CLIENT_SECRET"));
        } else if (environment.acceptsProfiles(SpringProfile.PAYPAL_LIVE)) {
            return PayPalSession.getLiveSession(environment.getRequiredProperty("PAYPAL_CLIENT_ID"), environment.getRequiredProperty("PAYPAL_CLIENT_SECRET"));
        } else {
            return PayPalSession.DUMMY;
        }
    }

}
