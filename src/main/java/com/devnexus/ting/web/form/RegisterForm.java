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
package com.devnexus.ting.web.form;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.devnexus.ting.model.BaseModelObject;
import javax.validation.constraints.Size;

/**
 *
 * @author Summers Pittman
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RegisterForm extends BaseModelObject {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1071233976549394025L;

    @NotNull
    private Integer ticketCount;

    @NotNull
    @Size(min = 1, max = 255)
    private String contactName;

    @NotNull
    @Size(min = 1, max = 255)
    private String contactEmailAddress;

    @NotNull
    @Size(min = 1, max = 255)
    private String contactPhoneNumber;

    
    @NotNull
    private Long ticketGroup;

    private String couponCode;

    public Integer getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(Integer ticketCount) {
        this.ticketCount = ticketCount;
    }

    public Long getTicketGroup() {
        return ticketGroup;
    }

    public void setTicketGroup(Long ticketGroup) {
        this.ticketGroup = ticketGroup;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmailAddress() {
        return contactEmailAddress;
    }

    public void setContactEmailAddress(String contactEmailAddress) {
        this.contactEmailAddress = contactEmailAddress;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    
    
}
