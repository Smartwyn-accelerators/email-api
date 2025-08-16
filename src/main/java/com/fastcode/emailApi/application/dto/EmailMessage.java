package com.fastcode.emailApi.application.dto;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class EmailMessage {
	@NotNull(message = "to should not be null")
	private String to;
	private String cc;
	private String bcc;
	private String from;
	private String replyTo;
	@NotNull(message = "subject should not be null")
	private String subject;
	@NotNull(message = "content should not be null")
	private String content;
	private Boolean isHtml;
	private List<File> inlineImages;
	private List<File> attachments;
	private Map<Long,byte[]> imageDataSourceMap;
	private LocalDateTime scheduledTime;
	private String defaultEmailProvider;

	public EmailMessage() {
	}

	public @NotNull(message = "to should not be null") String getTo() {
		return to;
	}

	public void setTo(@NotNull(message = "to should not be null") String to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public @NotNull(message = "subject should not be null") String getSubject() {
		return subject;
	}

	public void setSubject(@NotNull(message = "subject should not be null") String subject) {
		this.subject = subject;
	}

	public @NotNull(message = "content should not be null") String getContent() {
		return content;
	}

	public void setContent(@NotNull(message = "content should not be null") String content) {
		this.content = content;
	}

	public boolean isHtml() {
		return isHtml;
	}

	public void setHtml(boolean html) {
		isHtml = html;
	}

	public List<File> getInlineImages() {
		return inlineImages;
	}

	public void setInlineImages(List<File> inlineImages) {
		this.inlineImages = inlineImages;
	}

	public List<File> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<File> attachments) {
		this.attachments = attachments;
	}

	public Map<Long, byte[]> getImageDataSourceMap() {
		return imageDataSourceMap;
	}

	public void setImageDataSourceMap(Map<Long, byte[]> imageDataSourceMap) {
		this.imageDataSourceMap = imageDataSourceMap;
	}

	public LocalDateTime getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(LocalDateTime scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public String getDefaultEmailProvider() {
		return defaultEmailProvider;
	}

	public void setDefaultEmailProvider(String defaultEmailProvider) {
		this.defaultEmailProvider = defaultEmailProvider;
	}
}

