package address_book;
import javax.swing.JFrame;

public class RunAddressBook {

	public static void main(String[] args) {
		AddressBook a = new AddressBook("addressbook.txt");
		a.setSize(600, 400);
		a.setTitle("Address Book");
		a.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		a.setVisible(true);
	}
}