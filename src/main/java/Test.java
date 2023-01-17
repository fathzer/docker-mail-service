import java.util.Arrays;

import javax.mail.MessagingException;

import com.fathzer.mail.Mailer;
import com.fathzer.mail.MailerBuilder;

public class Test {

	public static void main(String[] args) throws MessagingException {
		Mailer log = new MailerBuilder("smtp.gmail.com").withAuthentication("fathzer@gmail.com", "kczwlakrweintwib").build();
		log.sendMail(Arrays.asList("jm@astesana.net"), "test", "This is a test from Google", Mailer.TEXT_UTF8);
	}

}
