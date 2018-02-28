package frame;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import utility.Utils;

public class MainView extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3912022311360148945L;
	JTextArea log;
	JFileChooser fc;
	JButton addPOFolder;
	JButton addPropertiesFolder;
	JButton fillButton;
	JButton addManualTranslation;
	JButton manualLoad;
	File dirPO;
	File dirProp;
	File dirManual;
	JTextField inputProp;
	JTextField inputPO;
	JTextField inputManual;

	 public void createMainView() { 
		 //Create file chooser
		 fc = new JFileChooser();
		 fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		 
		 //Create frame
		 JFrame frame = new MainView();
	     frame.setTitle("PO FILLER");
	     frame.setSize(200,200);
	     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     ImageIcon imgIcon = Utils.createImageIcon(MainView.class,"images/exchange.png");
	     frame.setIconImage(imgIcon.getImage());
	    
	     //Input PO
	     inputPO = new JTextField(15);
	     inputPO.addActionListener(this);
	     inputPO.requestFocus(); 
	     inputPO.setSize(100,100);
	     inputPO.disable();
	     JLabel labelPO = new JLabel("PO");
	     labelPO.setLabelFor(inputPO);
	     //Button Add Pot folder
	     addPOFolder = new JButton("Add...", Utils.createImageIcon(MainView.class,"images/folder.png"));
	     addPOFolder.addActionListener(this);
	     addPOFolder.setSize(10, 10);
	     
	     //Input Properties
	     inputProp = new JTextField(15);
	     inputProp.addActionListener(this);
	     inputProp.requestFocus(); 
	     inputProp.disable();
	     JLabel labelProp = new JLabel("Properties");
	     labelProp.setLabelFor(inputProp);
	     //Button Add Pot folder
	     ImageIcon icon = Utils.createImageIcon(MainView.class,"images/folder.png");
	     addPropertiesFolder = new JButton("Add...", Utils.createImageIcon(MainView.class,"images/folder.png"));
	     addPropertiesFolder.addActionListener(this);
	     addPropertiesFolder.setSize(20, 20);
	     
	     JPanel buttonPanelPO = new JPanel();
	     buttonPanelPO.add(labelPO);
	     buttonPanelPO.add(inputPO);
	     buttonPanelPO.add(addPOFolder);
	     
	     JPanel buttonPanelProp = new JPanel();
	     buttonPanelProp.add(labelProp);
	     buttonPanelProp.add(inputProp);
	     buttonPanelProp.add(addPropertiesFolder);
	     
	     //Fill button
	     JPanel buttonPanelFill = new JPanel();
	     fillButton = new JButton("Fill PO", Utils.createImageIcon(MainView.class,"images/exchange.png"));
	     fillButton.addActionListener(this);
	     fillButton.setSize(20, 20);
	     buttonPanelFill.add(fillButton);
	     
	     //Manual load
	     inputManual = new JTextField(15);
	     inputManual.addActionListener(this);
	     inputManual.requestFocus(); 
	     inputManual.disable();
	     JLabel labelManual = new JLabel("Manual Translations");
	     labelManual.setLabelFor(inputManual);
	     //Button Add Pot folder
	     addManualTranslation = new JButton("Add...", Utils.createImageIcon(MainView.class,"images/folder.png"));
	     addManualTranslation.addActionListener(this);
	     addManualTranslation.setSize(20, 20);
	     //manual button
	     JPanel buttonPanelLoadManual = new JPanel();
	     buttonPanelLoadManual.add(labelManual);
	     buttonPanelLoadManual.add(inputManual);
	     buttonPanelLoadManual.add(addManualTranslation);
	     manualLoad = new JButton("Load Manual Translations", Utils.createImageIcon(MainView.class,"images/load.png"));
	     manualLoad.addActionListener(this);
	     manualLoad.setSize(20, 20);
	     buttonPanelLoadManual.add(manualLoad);
	     
	     JPanel buttonPanel = new JPanel();
	     buttonPanel.add(buttonPanelPO);
	     buttonPanel.add(buttonPanelProp);
	     buttonPanel.add(buttonPanelFill);
	     //Add the buttons and the log to this panel.
	     
	      frame.add(buttonPanel, BorderLayout.PAGE_START);
	      frame.paint(getGraphics());
	      frame.add(buttonPanelLoadManual, BorderLayout.CENTER);
	      
	      log = new JTextArea(20,50);
	      log.setMargin(new Insets(5,5,5,5));
	      log.setEditable(false);
	      JScrollPane logScrollPane = new JScrollPane(log);
	      
	      frame.add(logScrollPane, BorderLayout.PAGE_END);
	      
	      frame.pack();
	      frame.setVisible(true);
	 }
	 
	 	
	 /**
	  * 
	  */
	 public void loadConf() {
		 try {
			Utils.loadProperties(dirProp,log);
		} catch (IOException ex) {
			log.append(Utils.getTimestamp() + " " + ex.getMessage());
		}
	 }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addPOFolder) {
			addPOFolderAction();
		}
		if (e.getSource() == addPropertiesFolder) {
			addPropertiesFolderAction();
		} else if (e.getSource() == fillButton) {
			fillAction();
		} else if(e.getSource() == addManualTranslation) {
			addManualTranslationFolderAction();
		}
		else if(e.getSource() == manualLoad) {
			manualLoadAction();
		}
	}


	/**
	 * 
	 */
	private void manualLoadAction() {
		if(dirManual != null && dirPO != null) {
			Utils.loadManualTranslations(dirManual, dirPO,log);
			log.append(Utils.getTimestamp() + " " + "Manual translations loaded." + Utils.NEWLINE);
		}
		else {
			log.append(Utils.getTimestamp() + " "
						+ " Before it's necessary insert a valid Manual Translation folder and PO folder" + Utils.NEWLINE);
			}
	}


	/**
	 * 
	 */
	private void addManualTranslationFolderAction() {
		int returnVal = fc.showOpenDialog(MainView.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			dirManual = fc.getSelectedFile();
			inputManual.setText(dirManual.getPath());
		} else {
			log.append(Utils.getTimestamp() + " " + " Add command cancelled by user." + Utils.NEWLINE);
		}
	}



	/**
	 * 
	 */
	private void fillAction() {
		if (dirPO != null && dirProp != null) {

			for (final File filePO : dirPO.listFiles()) {
				try {
					String extension = filePO.getName().substring(filePO.getName().lastIndexOf(Utils.DOT)+1, filePO.getName().length());
					if(extension.equals("po")) {
						Utils.fillPO(filePO);
						log.append(Utils.getTimestamp() + " File PO " + filePO.getName() + " filled." + Utils.NEWLINE);
					}
				} catch (IOException ex) {
					log.append(Utils.getTimestamp() + " " + ex.getMessage());
				}
			}
		} else {
			log.append(Utils.getTimestamp() + " "
					+ " Before it's necessary insert a valid POT and properties folder path." + Utils.NEWLINE);
		}
	}



	/**
	 * 
	 */
	private void addPropertiesFolderAction() {
		int returnVal = fc.showOpenDialog(MainView.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			log.setText("");
			dirProp = fc.getSelectedFile();
			inputProp.setText(dirProp.getPath());
			loadConf();
		} else {
			log.append(Utils.getTimestamp() + " " + " Add command cancelled by user." + Utils.NEWLINE);
		}
	}



	/**
	 * 
	 */
	private void addPOFolderAction() {
		int returnVal = fc.showOpenDialog(MainView.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			dirPO = fc.getSelectedFile();
			inputPO.setText(dirPO.getPath());
		} else {
			log.append(Utils.getTimestamp() + " " + " Add command cancelled by user." + Utils.NEWLINE);
		}
	}
}
