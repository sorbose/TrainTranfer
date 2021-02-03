package traintransfer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextPane;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.JTextArea;

public class WindowErrMsg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1537831677453453742L;
	private final JPanel contentPanel = new JPanel();
	JTextArea textArea;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			WindowErrMsg dialog = new WindowErrMsg();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public WindowErrMsg() {
		setBounds(100, 100, 450, 215);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		textArea = new JTextArea();
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
		textArea.setEditable(false);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setBackground(SystemColor.menu);
		textArea.setBounds(10, 10, 416, 125);
		contentPanel.add(textArea);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("È·¶¨");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
