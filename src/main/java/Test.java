import java.util.Arrays;

import javax.mail.MessagingException;

import com.fathzer.mail.DefaultMailer;
import com.fathzer.mail.GoogleMailer;

public class Test {

	public static void main(String[] args) throws MessagingException {
//		Mailer log = new GoogleMailer("fathzer@gmail.com", "gtige9220");
		DefaultMailer log = new GoogleMailer("fathzer@gmail.com", "kczwlakrweintwib");
		log.sendMail(Arrays.asList("jm@astesana.net"), "test", "This is a test from Google");
	}

}
