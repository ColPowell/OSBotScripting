package nezz.org.chocolatebuyer.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

public class GUI extends JFrame {

	private JPanel contentPane;

	JCheckBox chckbxDifferentPaths = new JCheckBox("Different paths");
	JCheckBox chckbxVariedSleeps = new JCheckBox("Varied Sleeps");
	JCheckBox chckbxRandomRightClickinghovering = new JCheckBox("Random Right Clicking/Hovering");
	JCheckBox chckbxRotateCamera = new JCheckBox("Rotate Camera");
	JCheckBox chckbxChangeMouseSpeed = new JCheckBox("Change mouse speed");
	JCheckBox chckbxIdleToLogout = new JCheckBox("Idle to logout (VERY RARE)");	

	/**
	 * Create the frame.
	 */
	public GUI(final Settings settings) {
		setTitle("TzHaar Cooker v1.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		

		JButton btnBegin = new JButton("Start");
		btnBegin.setFont(new Font("Vani", Font.PLAIN, 13));
		btnBegin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				settings.started=true;
				settings.idleToLogout = chckbxIdleToLogout.isSelected();
				settings.changeMouseSpeed = chckbxChangeMouseSpeed.isSelected();
				settings.rightClickHover = chckbxRandomRightClickinghovering.isSelected();
				settings.variedSleep = chckbxVariedSleeps.isSelected();
				settings.rotateCamera = chckbxRotateCamera.isSelected();
				settings.randomPaths = chckbxDifferentPaths.isSelected();
				setVisible(false);
			}
		});
		btnBegin.setBounds(281, 226, 143, 24);
		contentPane.add(btnBegin);
		
		JLabel lblAntibanFeatures = new JLabel("Anti-Ban features");
		lblAntibanFeatures.setBounds(10, 11, 111, 14);
		contentPane.add(lblAntibanFeatures);
		
		chckbxDifferentPaths.setBounds(20, 32, 128, 23);
		contentPane.add(chckbxDifferentPaths);
		
		chckbxVariedSleeps.setBounds(20, 58, 128, 23);
		contentPane.add(chckbxVariedSleeps);
		
		chckbxRandomRightClickinghovering.setBounds(20, 84, 208, 23);
		contentPane.add(chckbxRandomRightClickinghovering);
		
		chckbxRotateCamera.setBounds(20, 110, 128, 23);
		contentPane.add(chckbxRotateCamera);
		
		chckbxChangeMouseSpeed.setBounds(20, 135, 143, 23);
		contentPane.add(chckbxChangeMouseSpeed);
		
		chckbxIdleToLogout.setBounds(20, 162, 190, 23);
		contentPane.add(chckbxIdleToLogout);
	}
}
