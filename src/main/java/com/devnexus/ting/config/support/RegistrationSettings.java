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
package com.devnexus.ting.config.support;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Gunnar Hillert
 *
 */
@Configuration
@ConfigurationProperties(prefix="devnexus.registration")
public class RegistrationSettings {

	private RegistrationState registrationState;

	public RegistrationState getState() {
		return registrationState;
	}

	public void setState(RegistrationState registrationState) {
		this.registrationState = registrationState;
	}

	public enum RegistrationState {
		OPEN, CLOSED, SOLD_OUT
	}

}

