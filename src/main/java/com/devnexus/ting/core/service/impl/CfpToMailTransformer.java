package com.devnexus.ting.core.service.impl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.pegdown.PegDownProcessor;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.stringtemplate.v4.ST;

import com.devnexus.ting.common.SystemInformationUtils;
import com.devnexus.ting.common.TingUtil;
import com.devnexus.ting.core.model.CfpSubmission;

public class CfpToMailTransformer {

	private JavaMailSender mailSender;
	private String fromUser;

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public MimeMessage prepareMailToSpeaker(CfpSubmission cfpSubmission) {

		String template = SystemInformationUtils.getCfpEmailTemplate();
		String renderedTemplate = applyStringTemplate(cfpSubmission, template);

		PegDownProcessor markdownProcessor = TingUtil.getMarkDownProcessor();

		String messageText = markdownProcessor.markdownToHtml(renderedTemplate);

		StringBuilder sb = new StringBuilder();
		sb.append("<html><body>");
		sb.append(messageText);
		sb.append("</body></html>");

		MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		MimeMessageHelper messageHelper;
		try {
			messageHelper = new MimeMessageHelper(mimeMessage, true);
			messageHelper.setText(renderedTemplate, sb.toString());

			messageHelper.setFrom(fromUser);
			messageHelper.setTo(cfpSubmission.getEmail());
			messageHelper.setCc("test@hillert.com");
			messageHelper.setSubject("DevNexus 2014 - CFP - " + cfpSubmission.getFirstLastName());

		} catch (MessagingException e) {
			throw new IllegalStateException("Error creating mail message.", e);
		}

		return messageHelper.getMimeMessage();
	}

	public String applyStringTemplate(CfpSubmission cfpSubmission, String template) {
		ST stringTemplate = new ST(template);

		stringTemplate.add("firstName", cfpSubmission.getFirstName());
		stringTemplate.add("lastName", cfpSubmission.getLastName());
		stringTemplate.add("bio", cfpSubmission.getBio());
		stringTemplate.add("description", cfpSubmission.getDescription());
		stringTemplate.add("email", cfpSubmission.getEmail());
		stringTemplate.add("googlePlusId", cfpSubmission.getGooglePlusId());
		stringTemplate.add("linkedInId", cfpSubmission.getLinkedInId());
		stringTemplate.add("twitterId", cfpSubmission.getTwitterId());
		stringTemplate.add("phone", cfpSubmission.getPhone());
		stringTemplate.add("presentationType", cfpSubmission.getPresentationType().getName());
		stringTemplate.add("skillLevel", cfpSubmission.getSkillLevel().getName());
		stringTemplate.add("comments", cfpSubmission.getSlotPreference());
		stringTemplate.add("title", cfpSubmission.getTitle());
		stringTemplate.add("tshirtSize", cfpSubmission.getTshirtSize());
		stringTemplate.add("sessionRecordingApproved", cfpSubmission.isSessionRecordingApproved() ? "Yes" : "No");

		String renderedTemplate = stringTemplate.render();

		return renderedTemplate;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

}