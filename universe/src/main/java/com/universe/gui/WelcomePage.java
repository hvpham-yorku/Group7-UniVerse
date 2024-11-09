package com.universe.gui;

import javax.swing.*;
import java.awt.*;

public class WelcomePage {

    private JFrame frame;
    private JTextField textField;
    private JTextField textField_1;
    private JTextField textField_2;
//    private Choice choice_1;

    /**
     * Launch the Welcome Page.
     */
    public WelcomePage(String userName) {
        initialize(userName);
        frame.setVisible(true);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize(String userName) {
        // Set up the main frame
        frame = new JFrame();
        frame.setBounds(500, 500, 700, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        
        textField = new JTextField();
        textField.setBounds(237, 147, 229, 26);
        frame.getContentPane().add(textField);
        textField.setColumns(10);
        
        textField_1 = new JTextField();
        textField_1.setColumns(10);
        textField_1.setBounds(237, 197, 229, 26);
        frame.getContentPane().add(textField_1);
        
        textField_2 = new JTextField();
        textField_2.setColumns(10);
        textField_2.setBounds(237, 242, 229, 26);
        frame.getContentPane().add(textField_2);
    
        
        Choice choice = new Choice();
        choice.setBackground(new Color(255, 83, 34));
        choice.setBounds(208, 315, 150, 27);

        String[] citiesInOntario = {
            "Toronto", "Ottawa", "Mississauga", "Brampton", "Hamilton", 
            "London", "Markham", "Vaughan", "Kitchener", "Windsor", 
            "Richmond Hill", "Oakville", "Burlington", "Sudbury", "Oshawa", 
            "St. Catharines", "Barrie", "Cambridge", "Kingston", "Guelph",
            "Thunder Bay", "Waterloo", "Pickering", "Niagara Falls", "Whitby"
        };

        for (String city : citiesInOntario) {
            choice.add(city);
        }
        frame.getContentPane().add(choice);

        Choice choice_1 = new Choice();
        choice_1.setBackground(new Color(255, 83, 34));
        choice_1.setBounds(398, 315, 150, 27);
        String[] universitiesInOntario = {
            "University of Toronto", "York University", "McMaster University", 
            "University of Waterloo", "Western University", "Queen's University", 
            "University of Ottawa", "Carleton University", "University of Guelph", 
            "Lakehead University", "Trent University", "Wilfrid Laurier University", 
            "Brock University", "Ryerson University", "Laurentian University", 
            "Nipissing University", "Ontario Tech University", "Algoma University"
        };

        for (String university : universitiesInOntario) {
            choice_1.add(university);
        }
        frame.getContentPane().add(choice_1);


    }
}
