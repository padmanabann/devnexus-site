/*
 * Copyright 2013 the original author or authors.
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
package com.devnexus.ting.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.devnexus.ting.common.SpringProfile;
import com.devnexus.ting.config.support.MailSettings;
import com.devnexus.ting.core.service.impl.CfpToMailTransformer;


/**
 * Spring Social Configuration.
 * @author Keith Donald
 */
@Configuration
@ImportResource({
	"classpath:spring/spring-integration-cfp-context.xml"
})
public class CfpConfig {

	@Autowired
	private MailSettings mailSettings;

	@Bean
	@Profile(SpringProfile.MAIL_ENABLED)
	public CfpToMailTransformer cfpToMailTransformer() {
		CfpToMailTransformer cfpToMailTransformer = new CfpToMailTransformer();
		cfpToMailTransformer.setCcUser(mailSettings.getUser().getCc());
		cfpToMailTransformer.setFromUser(mailSettings.getUser().getFrom());
		cfpToMailTransformer.setMailSender(mailSender());
		return cfpToMailTransformer;
	}

	@Bean
	@Profile(SpringProfile.MAIL_ENABLED)
	public JavaMailSenderImpl mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(mailSettings.getSmtp().getHost());
		mailSender.setPort(mailSettings.getSmtp().getPort());
		mailSender.setUsername(mailSettings.getUser().getId());
		mailSender.setPassword(mailSettings.getUser().getPassword());

		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.debug", mailSettings.isDebugEnabled());
		javaMailProperties.put("mail.smtp.auth", mailSettings.isAuthenticationEnabled());
		mailSender.setJavaMailProperties(javaMailProperties);
		return mailSender;
	}

}