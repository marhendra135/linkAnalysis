package data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Email {
	/*
	 * This class is the object representation of Enron's Document.
	 * This class is used within search activities.
	 * */
	private String mId;
	private Date date;
	private String dateFrom;
	private String dateTo;
	private String senderEmails;
	private String senderName;
	private String senderStatus;
	private String recEmail;
	private String recName;
	private String recStatus;
	private String subject;
	private String body;
	private String type;
	private double bVal = 1.0;
	
	public Email(){
	}
	
	public Email(String mId, String dateFrom, String dateTo, String senderEmails, String senderName,
			String senderStatus, String recEmail, String recName,
			String recStatus, String subject, String body, String type) {

		this.setmId(mId.trim());
		this.setDateFrom(dateFrom.trim());
		this.setDateTo(dateTo.trim());
		this.setSenderEmails(senderEmails.trim());
		this.setSenderName(senderName.trim());
		this.setSenderStatus(senderStatus.trim());
		this.setRecEmail(recEmail.trim());
		this.setRecName(recName.trim());
		this.setRecStatus(recStatus.trim());
		this.setSubject(subject.trim());
		this.setBody(body.trim());
		this.setType(type.trim());

	}
	
	public Email(String mId, Date date, String senderEmails, String senderName,
			String senderStatus, String recEmail, String recName,
			String recStatus, String subject, String body, String type) {

		this.setmId(mId.trim());
		this.setDate(date);
		this.setSenderEmails(senderEmails.trim());
		this.setSenderName(senderName.trim());
		this.setSenderStatus(senderStatus.trim());
		this.setRecEmail(recEmail.trim());
		this.setRecName(recName.trim());
		this.setRecStatus(recStatus.trim());
		this.setSubject(subject.trim());
		this.setBody(body.trim());
		this.setType(type.trim());

	}
	
	public Email(String mId, Date date, String senderEmails, String senderName,
			String senderStatus, String recEmail, String recName,
			String recStatus, String subject, String body, String type, double bVal) {

		this.setmId(mId.trim());
		this.setDate(date);
		this.setSenderEmails(senderEmails.trim());
		this.setSenderName(senderName.trim());
		this.setSenderStatus(senderStatus.trim());
		this.setRecEmail(recEmail.trim());
		this.setRecName(recName.trim());
		this.setRecStatus(recStatus.trim());
		this.setSubject(subject.trim());
		this.setBody(body.trim());
		this.setType(type.trim());
		this.setbVal(bVal);

	}
	public double getbVal() {
		return bVal;
	}

	public void setbVal(double bVal) {
		this.bVal = bVal;
	}

	@Override
	public String toString() {
		return "Email [mId=" + mId + ", dateFrom=" + dateFrom + ", dateTo=" + dateTo + ", senderEmails="
				+ senderEmails + ", senderName=" + senderName
				+ ", senderStatus=" + senderStatus + ", recEmail=" + recEmail
				+ ", recName=" + recName + ", recStatus=" + recStatus
				+ ", subject=" + subject + ", body=" + body + ", type=" + type
				+ "]";
	}

	public String getmId() {
		return mId;
	}
	public void setmId(String txtInp) {
		txtInp = cleanTermForQuery(txtInp);
		this.mId = txtInp;
	}
	public Date getDate() {
		return date;
	}

	public String getStrDate() {
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		return df.format(date);
	}
	

	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getSenderEmails() {
		return senderEmails;
	}
	public void setSenderEmails(String txtInp) {
		txtInp = cleanTermForQuery(txtInp);
		this.senderEmails = txtInp;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String txtInp) {
		txtInp = cleanTermForQuery(txtInp);
		this.senderName = txtInp;
	}
	public String getSenderStatus() {
		return senderStatus;
	}
	public void setSenderStatus(String txtInp) {
		txtInp = cleanTermForQuery(txtInp);
		this.senderStatus = txtInp;
	}
	public String getRecEmail() {
		return recEmail;
	}
	public void setRecEmail(String txtInp) {
		txtInp = cleanTermForQuery(txtInp);
		this.recEmail = txtInp;
	}
	public String getRecName() {
		return recName;
	}
	public void setRecName(String txtInp) {
		txtInp = cleanTermForQuery(txtInp);
		this.recName = txtInp;
	}
	public String getRecStatus() {
		return recStatus;
	}
	public void setRecStatus(String txtInp) {
		txtInp = cleanTermForQuery(txtInp);
		this.recStatus = txtInp;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String txtInp) {
		txtInp = cleanTermForQuery(txtInp);
		this.subject = txtInp;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String txtInp) {
		txtInp = cleanTermForQuery(txtInp);
		this.body = txtInp;
	}

	public String getType() {
		return type;
	}

	public void setType(String txtInp) {
		txtInp = cleanTermForQuery(txtInp);
		this.type = txtInp;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String txtInp) {
		txtInp = cleanTermForQuery(txtInp);
		this.dateTo = txtInp;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String txtInp) {
		txtInp = cleanTermForQuery(txtInp);
		this.dateFrom = txtInp;
	}
	
	private String cleanTermForQuery(String txtInp){
		while (!txtInp.equals("") && (txtInp.substring(0, 1).equals("*") || txtInp.substring(0, 1).equals("?"))) {
			txtInp = txtInp.substring(1);
		}
		return txtInp;
	}

	
}
