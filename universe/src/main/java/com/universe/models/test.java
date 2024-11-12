package com.universe.models;

import javax.swing.JFrame;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JLabel;

public class test {
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * @wbp.parser.entryPoint
	 */
	private void initialize() {
        // Set up the main frame
        JFrame frame = new JFrame("Sign Up or Welcome Page");
        frame.setBounds(500, 500, 700, 700);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.getContentPane().setLayout(null);
        
        textField = new JTextField();
        textField.setBounds(350, 468, 211, 32);
        frame.getContentPane().add(textField);
        textField.setColumns(10);
        
        textField_1 = new JTextField();
        textField_1.setColumns(10);
        textField_1.setBounds(350, 528, 211, 32);
        frame.getContentPane().add(textField_1);
        
        JButton btnNewButton = new JButton("Login");
        btnNewButton.setBounds(399, 585, 117, 29);
        frame.getContentPane().add(btnNewButton);
        
        JLabel lblNewLabel = new JLabel("Username or Email *");
        lblNewLabel.setBounds(174, 476, 133, 24);
        frame.getContentPane().add(lblNewLabel);
        
        JLabel lblNewLabel_1 = new JLabel("Password *");
        lblNewLabel_1.setBounds(229, 536, 97, 16);
        frame.getContentPane().add(lblNewLabel_1);
}
}