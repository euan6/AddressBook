package address_book;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;

enum State {
	INSPECT, EXTEND;
}

public class AddressBook extends JFrame implements ActionListener{

	private State state = State.INSPECT;

	private final String[] ACTION_TEXT = {"New", "Add", "Change", "Delete", "Forward", "Back", "Quit"};
	private final String[] TEXT = {"Name", "Street", "Town", "Postcode", "Telephone", "Email"};
	private final int NEW = 0;
	private final int ADD = 1;
	private final int CHANGE = 2;
	private final int DELETE = 3;
	private final int FORWARD = 4;
	private final int BACK = 5;
	private final int QUIT = 6;
	private final int MAXEVENTS = ACTION_TEXT.length;
	private JButton[] actions;
	private JLabel[] headings;
	private JTextField[] details;
	private JPanel controls, entry;
	private List<Entry> entries;
	private int current;
	private String filename;

	public AddressBook(String filename) {
		entries = new LinkedList<>();
		current = 0;
		this.filename = filename;
		
		entry = new JPanel(new GridLayout(Entry.MAXDETAILS, 2));
		headings = new JLabel[Entry.MAXDETAILS];
		details = new JTextField[Entry.MAXDETAILS];
		for (int i = 0; i < Entry.MAXDETAILS; i++) {
			headings[i] = setupLabel(TEXT[i], entry);
			details[i] = setupTextField("", entry);
		}
		add(entry, BorderLayout.CENTER);

		controls = new JPanel(new GridLayout(MAXEVENTS, 1));
		actions = new JButton[MAXEVENTS];
		for (int i = 0; i < MAXEVENTS; i++) {
			actions[i] = setupButton(ACTION_TEXT[i], controls, this);
		}
		add(controls, BorderLayout.WEST);

		actions[ADD].setEnabled(false);
		
		readAddressBook();
	}
	
	private void readAddressBook() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			Entry e = new Entry();
			while (e.readEntry(in)) {
				entries.add(e);
				e = new Entry();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		current = 0;
		if (!entries.isEmpty()) {
			showEntry(entries.get(current));
		} else if (entries.isEmpty()) {
			actions[CHANGE].setEnabled(false);
			actions[DELETE].setEnabled(false);
			actions[FORWARD].setEnabled(false);
			actions[BACK].setEnabled(false);
		}
	}

	private JComponent setUp(JComponent comp, Container c) {
		comp.setFont(new Font("Sansserif", Font.PLAIN, 18));
		comp.setBackground(Color.white);
		c.add(comp);
		return comp;
	}

	private JTextField setupTextField(String text, Container c) {
		JTextField t = new JTextField(text);
		return (JTextField) setUp(t, c);
	}

	private JLabel setupLabel(String text, Container c) {
		JLabel l = new JLabel(text, JLabel.CENTER);
		l.setOpaque(true);
		return (JLabel) setUp(l, c);
	}

	private JButton setupButton(String text, Container c, ActionListener l) {
		JButton b = new JButton(text);
		b.addActionListener(l);
		return (JButton) setUp(b, c);
	}

	private void showEntry(Entry e) {
		for (int i = 0; i < Entry.MAXDETAILS; i++) {
			details[i].setText(e.getDetails(i));
		}
	}

	private void setAllButtonsEnabled(boolean b) {
		for (int i = 0; i < MAXEVENTS; i++) {
			actions[i].setEnabled(b);
		}
	}

	private void clearText() {
		for (int i = 0; i < Entry.MAXDETAILS; i++) {
			details[i].setText("");
		}
	}

	private void doNew() {
		clearText();
		setAllButtonsEnabled(false);
		actions[ADD].setEnabled(true);
	}

	private void doAdd() {
		String[] newDetails = new String[Entry.MAXDETAILS];
		for (int i = 0; i < Entry.MAXDETAILS; i++) {
			newDetails[i] = details[i].getText();
		}
		Entry e = new Entry(newDetails);
		boolean inserted = false;
		if (entries.isEmpty()) {
			entries.add(e);
			inserted = true;
		} else {
			for (int i = 0; i < entries.size(); i++) {
				if (entries.get(i).getDetails(0).compareTo(e.getDetails(0)) > 0) {
					entries.add(i, e);
					current = i;
					inserted = true;
					break;
				}
			}
		}
		if (!inserted) {
			entries.add(e);
		}
		
		setAllButtonsEnabled(true);
		actions[ADD].setEnabled(false);
	}
	
	private void doQuit() {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));
			for (Entry e : entries) {
				e.writeEntry(out);
			}
			out.close();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void doForward() {
		if (current < entries.size() - 1) {
			current++;
			showEntry(entries.get(current));
		}
	}
	
	private void doBack() {
		if (current > 0) {
			current--;
			showEntry(entries.get(current));
		}
	}
	
	private void doDelete() {
		if (entries.isEmpty()) {
			return;
		}
		entries.remove(current);
		current = current > 0 ? current - 1 : current;
		if (entries.isEmpty()) {
			clearText();
		} else {
			showEntry(entries.get(current));
		}
	}
	
	private void doChange() {
		if (entries.isEmpty()) {
			return;
		}
		entries.remove(current);
		doAdd();
	}

	public void actionPerformed(ActionEvent e) {
		switch(state) {
		case INSPECT:
			if (e.getSource() == actions[NEW]) {
				doNew();
				state = State.EXTEND;
			} else if (e.getSource() == actions[QUIT]) {
				doQuit();
			} else if (e.getSource() == actions[FORWARD]) {
				doForward();
			} else if (e.getSource() == actions[BACK]) {
				doBack();
			} else if (e.getSource() == actions[DELETE]) {
				doDelete();
			} else if (e.getSource() == actions[CHANGE]) {
				doChange();
			}
			break;
		case EXTEND:
			if (e.getSource() == actions[ADD]) {
				doAdd();
				state = State.INSPECT;
			}
			break;
		}
	}
}