package com.example.benny.icalculation.swingGui;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainUi extends JFrame {
    private JPanel mainPanel;
    private JButton loadICalButton;
    private JButton generateTextButton;
    private JPanel gridPanel;

    static void main() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        new MainUi();
    }

    MainUi() {
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.pack();

        this.setVisible(true);
        loadICalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        gridPanel = new JPanel();
        GridLayout constraints = new GridLayout(4, 2);
        gridPanel.setLayout(constraints);

        JLabel icalURLLabel = new JLabel("iCal URL");
        gridPanel.add(icalURLLabel);
        JTextField icalURLInput = new JTextField();
        gridPanel.add(icalURLInput);

        String[] months = { "---", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "September", "November", "December" };

        JLabel stopAfterLabel = new JLabel("Stop after Month");
        gridPanel.add(stopAfterLabel);

        JComboBox<String> stopAfter = new JComboBox<String>(months);
        gridPanel.add(stopAfter);

        JLabel ignorePastLabel = new JLabel("Ignore Past");
        gridPanel.add(ignorePastLabel);

        JCheckBox ignorePast = new JCheckBox();
        gridPanel.add(ignorePast);

        JLabel outputTypeLabel = new JLabel("Output Type");
        gridPanel.add(outputTypeLabel);

        JPanel outputTypePanel = new JPanel();
        gridPanel.add(outputTypePanel);

        GridLayout layout = new GridLayout(2, 1);
        gridPanel.setLayout(layout);

        JRadioButton outputTypeClipboard = new JRadioButton("Clipboard");
        outputTypePanel.add(outputTypeClipboard);

        JRadioButton outputTypeFile = new JRadioButton("File");
        outputTypePanel.add(outputTypeFile);

        ButtonGroup outputTypeGroup = new ButtonGroup();
        outputTypeGroup.add(outputTypeClipboard);
        outputTypeGroup.add(outputTypeFile);
    }
}
