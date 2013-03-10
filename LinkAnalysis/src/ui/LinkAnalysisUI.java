package ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTextField;

import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

import data.Email;
import work.LuceneSearch;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JScrollPane;
public class LinkAnalysisUI {

	private JFrame frmInGroup;
	private JTextField textSearchStd;
	private JTextField textFrom;
	private JTextField textTo;
	private JTextField textSubject;
	private JTextField textBodyHas;
	private JTextField textBodyDoesNot;
	private JTextField textFromPos;
	private JTextField textToPos;
	private LuceneSearch luceneSearch = null;
	private Email email = null;
	private ArrayList<Document> listResult = null;
	private String dateFrom = "";
	private String dateTo = "";
	private String[] columNames = { "From", "Subject", "Date", "Body"};
	private String[]  dateFilters = { "Anytime", "Today", "This Month", "This Year" , "Year 2001"};
	private JTable tableStd;
	private JTable tableAdv;
	private DateFormat df;
	private DefaultTableModel modelStd;
	private DefaultTableModel modelAdv;
	private boolean forceDB = true;
	JFrame frmHelp =null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LinkAnalysisUI window = new LinkAnalysisUI();
					window.frmInGroup.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LinkAnalysisUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		luceneSearch = new LuceneSearch();
		email = new Email();
		listResult = new ArrayList<Document>();
		
		df = new SimpleDateFormat("yyyyMMddHHmmss");
		
		frmInGroup = new JFrame();
		frmInGroup.setResizable(false);
		frmInGroup.setTitle("IN4325 - Group 11 - Lucene Search with Link Analysis");
		frmInGroup.setBounds(100, 100, 450, 300);
		frmInGroup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmInGroup.setSize(800,600);
		
		JMenuBar menuBar = new JMenuBar();
		frmInGroup.setJMenuBar(menuBar);
		
		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);
		
		JMenuItem mntmIndexNoAnalysis = new JMenuItem("Build Index-No Analysis");


		mnTools.add(mntmIndexNoAnalysis);
		
		JMenuItem mntmClose = new JMenuItem("Close");
		mntmClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmInGroup.dispose();
			}
		});
		
		JMenuItem mntmIndexBackLink = new JMenuItem("Build Index-BackLink");
		mnTools.add(mntmIndexBackLink);
		
		JMenuItem mntmIndexPageRank = new JMenuItem("Build Index-PageRank");
		mnTools.add(mntmIndexPageRank);
		
		JMenuItem mntmIndexHITS = new JMenuItem("BuildMenu-HITS");
		mnTools.add(mntmIndexHITS);
		mnTools.add(mntmClose);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmHelp = new JMenuItem("Help");
		mntmHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				popupHelpUI();
		
			}
		});
		mnHelp.add(mntmHelp);
		final JLabel foundStdLbl = new JLabel("Found : 0 result(s)");
		JTabbedPane searchTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frmInGroup.getContentPane().add(searchTabbedPane, BorderLayout.CENTER);
		
		JPanel stdPanel = new JPanel();
		searchTabbedPane.addTab("Standard", null, stdPanel, null);
		
		textSearchStd = new JTextField();
		textSearchStd.setColumns(25);
		
		final JButton btnSearchStd = new JButton("Search");
		btnSearchStd.setEnabled(false);
		btnSearchStd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Search standard will be performed");
				listResult = null;
				frmInGroup.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				try {
					listResult = luceneSearch.standardQuery(textSearchStd.getText());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				if (listResult==null)
					foundStdLbl.setText("Found : 0 result(s)");
				else
					foundStdLbl.setText("Found : "+ listResult.size()+" result(s)");
				try {
					viewResult(listResult, tableStd, modelStd);
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				frmInGroup.setCursor(Cursor.getDefaultCursor());
			}
		});

		
		final JLabel lblLucene = new JLabel("Lucene !");
		lblLucene.setHorizontalAlignment(SwingConstants.CENTER);
		lblLucene.setFont(new Font("Tahoma", Font.PLAIN, 16));
		

		
		JScrollPane scrollPane = new JScrollPane();
		
		
		
		GroupLayout gl_stdPanel = new GroupLayout(stdPanel);
		gl_stdPanel.setHorizontalGroup(
			gl_stdPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_stdPanel.createSequentialGroup()
					.addGroup(gl_stdPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_stdPanel.createSequentialGroup()
							.addGap(70)
							.addGroup(gl_stdPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(foundStdLbl, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 648, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_stdPanel.createSequentialGroup()
							.addGap(83)
							.addGroup(gl_stdPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_stdPanel.createSequentialGroup()
									.addGap(279)
									.addComponent(btnSearchStd))
								.addComponent(textSearchStd, GroupLayout.PREFERRED_SIZE, 623, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_stdPanel.createSequentialGroup()
							.addGap(266)
							.addComponent(lblLucene, GroupLayout.PREFERRED_SIZE, 256, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(71, Short.MAX_VALUE))
		);
		gl_stdPanel.setVerticalGroup(
			gl_stdPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_stdPanel.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(lblLucene)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_stdPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_stdPanel.createSequentialGroup()
							.addComponent(textSearchStd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(11)
							.addComponent(btnSearchStd)
							.addGap(18)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 304, GroupLayout.PREFERRED_SIZE)
							.addGap(200))
						.addGroup(gl_stdPanel.createSequentialGroup()
							.addComponent(foundStdLbl)
							.addGap(515))))
		);

		//tableStd = new JTable(data, columNames);
		modelStd = new DefaultTableModel();
		tableStd = new JTable();
		tableStd.setModel(modelStd);
		modelStd.setColumnIdentifiers(columNames);
		tableStd.setEnabled(false);
		scrollPane.setViewportView(tableStd);
		stdPanel.setLayout(gl_stdPanel);
		
		JPanel advPanel = new JPanel();
		searchTabbedPane.addTab("Advanced", null, advPanel, null);
		


		
		textFrom = new JTextField();
		textFrom.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("From");
		
		JLabel lblToccbcc = new JLabel("TO/CC/BCC");
		
		textTo = new JTextField();
		textTo.setColumns(10);
		
		JLabel lblSubject = new JLabel("Subject");
		
		textSubject = new JTextField();
		textSubject.setColumns(10);
		
		JLabel lblHasThe = new JLabel("Has the words");
		
		JLabel lblDoesntTheWords = new JLabel("Doesn't have");
		
		textBodyHas = new JTextField();
		textBodyHas.setColumns(10);
		
		textBodyDoesNot = new JTextField();
		textBodyDoesNot.setColumns(10);
		
		JLabel lblDate = new JLabel("Date");
		
		final JComboBox comboBoxDate = new JComboBox(dateFilters);
		comboBoxDate.setSelectedIndex(0);
		comboBoxDate.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				Calendar cal = Calendar.getInstance();
				Date dt = null;
				Date dtNow = Calendar.getInstance().getTime();
				dateTo = df.format(dtNow);

				
				switch (comboBoxDate.getSelectedIndex()) {
					case 0: //anytime
						dateFrom="";
						dateTo="";
						break;
					case 1: //today
						dateFrom = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
						dateFrom += "000000";
						break;
					case 2: //thismonth

						dateFrom = new SimpleDateFormat("yyyyMM").format(Calendar.getInstance().getTime());
						dateFrom += "01000000";
						break;
					case 3: //thisyear
						dateFrom = new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime());
						dateFrom += "0101000000";
						break;
					case 4 ://y 2001
						cal.set(2001,0,1,0,0,0);
						dt = cal.getTime();
						dateFrom = df.format(dt);
						cal.set(2001,11,31,0,0,0);
						dt = cal.getTime();
						dateTo = df.format(dt);
						break;
					
				}
				System.out.println(comboBoxDate.getSelectedIndex()+" -Date to : " + dateTo);
				System.out.println("Date from : " + dateFrom);
			}
		});

		
		JLabel lblFromPos = new JLabel("From Pos");
		
		textFromPos = new JTextField();
		textFromPos.setColumns(10);
		
		textToPos = new JTextField();
		textToPos.setColumns(10);
		final JLabel foundAdvLbl = new JLabel("Found : 0 result(s)");
		final JButton btnSearchAdv = new JButton("Search");
		btnSearchAdv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Search advanced will be performed");
				frmInGroup.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				listResult = null;
				
				try {
					String strBody = "";
					strBody = textBodyHas.getText();
					if (!textBodyDoesNot.getText().equals(""))
						strBody = strBody + " AND (NOT " + textBodyDoesNot.getText() + ")";
					email = new Email("", dateFrom, dateTo, "", textFrom.getText(), textFromPos.getText(), 
							"", textTo.getText(), textToPos.getText(), textSubject.getText()
							, strBody , "");
					listResult = luceneSearch.assistedQuery(email);
					if (listResult==null)
						foundAdvLbl.setText("Found : 0 result(s)");
					else
						foundAdvLbl.setText("Found : "+listResult.size()+" result(s)");
					viewResult(listResult, tableAdv, modelAdv);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				frmInGroup.setCursor(Cursor.getDefaultCursor());
			}
		});
		btnSearchAdv.setEnabled(false);

		mntmIndexNoAnalysis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmInGroup.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				try {
					luceneSearch.setAnalysisType(0);
					luceneSearch.buildIndex(forceDB);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				frmInGroup.setCursor(Cursor.getDefaultCursor());
				lblLucene.setText("Lucene Search - No Link Analysis");
				if (forceDB){ //first time
					forceDB = false;
					btnSearchStd.setEnabled(true);
					btnSearchAdv.setEnabled(true);
				}
			}
		});
		mntmIndexBackLink.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmInGroup.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				try {
					luceneSearch.setAnalysisType(1);
					luceneSearch.buildIndex(forceDB);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				frmInGroup.setCursor(Cursor.getDefaultCursor());
				lblLucene.setText("Lucene Search - BackLink Analysis");
				if (forceDB){
					forceDB = false;
					btnSearchStd.setEnabled(true);
					btnSearchAdv.setEnabled(true);
				}
			}
		});
		
		mntmIndexPageRank.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmInGroup.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				try {
					luceneSearch.setAnalysisType(2);
					luceneSearch.buildIndex(forceDB);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				frmInGroup.setCursor(Cursor.getDefaultCursor());
				lblLucene.setText("Lucene Search - Page Rank Analysis");
				if (forceDB){
					forceDB = false;
					btnSearchStd.setEnabled(true);
					btnSearchAdv.setEnabled(true);
				}
			}
		});
		

		mntmIndexHITS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frmInGroup.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				try {
					luceneSearch.setAnalysisType(3);
					luceneSearch.buildIndex(forceDB);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				frmInGroup.setCursor(Cursor.getDefaultCursor());
				lblLucene.setText("Lucene Search - HITS Analysis");
				if (forceDB){
					forceDB = false;
					btnSearchStd.setEnabled(true);
					btnSearchAdv.setEnabled(true);
				}
			}
		});
		JLabel lblToPos = new JLabel("TO Pos");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		
		GroupLayout gl_advPanel = new GroupLayout(advPanel);
		gl_advPanel.setHorizontalGroup(
			gl_advPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_advPanel.createSequentialGroup()
					.addGap(57)
					.addGroup(gl_advPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_advPanel.createParallelGroup(Alignment.TRAILING, false)
							.addGroup(gl_advPanel.createSequentialGroup()
								.addComponent(foundAdvLbl, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnSearchAdv))
							.addGroup(gl_advPanel.createSequentialGroup()
								.addGroup(gl_advPanel.createParallelGroup(Alignment.LEADING)
									.addGroup(gl_advPanel.createSequentialGroup()
										.addGroup(gl_advPanel.createParallelGroup(Alignment.TRAILING)
											.addGroup(gl_advPanel.createSequentialGroup()
												.addGroup(gl_advPanel.createParallelGroup(Alignment.TRAILING)
													.addGroup(gl_advPanel.createParallelGroup(Alignment.LEADING)
														.addComponent(lblToccbcc, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
														.addComponent(lblNewLabel))
													.addComponent(lblSubject, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE))
												.addGap(35))
											.addGroup(gl_advPanel.createSequentialGroup()
												.addComponent(lblHasThe, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
												.addGap(18)))
										.addGroup(gl_advPanel.createParallelGroup(Alignment.LEADING)
											.addComponent(textBodyHas, GroupLayout.PREFERRED_SIZE, 309, GroupLayout.PREFERRED_SIZE)
											.addGroup(gl_advPanel.createSequentialGroup()
												.addGroup(gl_advPanel.createParallelGroup(Alignment.LEADING)
													.addComponent(textSubject, GroupLayout.PREFERRED_SIZE, 309, GroupLayout.PREFERRED_SIZE)
													.addGroup(gl_advPanel.createParallelGroup(Alignment.TRAILING, false)
														.addComponent(textFrom, Alignment.LEADING)
														.addComponent(textTo, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)))
												.addGap(42)
												.addGroup(gl_advPanel.createParallelGroup(Alignment.LEADING, false)
													.addComponent(lblFromPos)
													.addComponent(lblToPos, GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
													.addComponent(lblDate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
												.addGap(40)
												.addGroup(gl_advPanel.createParallelGroup(Alignment.LEADING, false)
													.addComponent(textFromPos, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
													.addComponent(comboBoxDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(textToPos)))))
									.addGroup(gl_advPanel.createSequentialGroup()
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(lblDoesntTheWords, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
										.addGap(18)
										.addComponent(textBodyDoesNot, GroupLayout.PREFERRED_SIZE, 309, GroupLayout.PREFERRED_SIZE)))
								.addPreferredGap(ComponentPlacement.RELATED)))
						.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 674, GroupLayout.PREFERRED_SIZE))
					.addGap(182))
		);
		gl_advPanel.setVerticalGroup(
			gl_advPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_advPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_advPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_advPanel.createSequentialGroup()
							.addGroup(gl_advPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_advPanel.createSequentialGroup()
									.addGroup(gl_advPanel.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblNewLabel)
										.addComponent(textFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addGap(12)
									.addGroup(gl_advPanel.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblToccbcc)
										.addComponent(textTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addGap(3)
									.addGroup(gl_advPanel.createParallelGroup(Alignment.TRAILING)
										.addGroup(gl_advPanel.createSequentialGroup()
											.addGroup(gl_advPanel.createParallelGroup(Alignment.BASELINE)
												.addComponent(textSubject, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(lblDate))
											.addGap(6)
											.addGroup(gl_advPanel.createParallelGroup(Alignment.BASELINE)
												.addComponent(textBodyHas, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(lblHasThe))
											.addGap(12))
										.addGroup(gl_advPanel.createSequentialGroup()
											.addComponent(lblSubject)
											.addGap(41)))
									.addGroup(gl_advPanel.createParallelGroup(Alignment.BASELINE)
										.addComponent(textBodyDoesNot, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblDoesntTheWords))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnSearchAdv))
								.addGroup(gl_advPanel.createSequentialGroup()
									.addGroup(gl_advPanel.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblFromPos)
										.addComponent(textFromPos, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_advPanel.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblToPos)
										.addComponent(textToPos, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(comboBoxDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
							.addGap(18)
							.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
							.addGap(27))
						.addGroup(Alignment.TRAILING, gl_advPanel.createSequentialGroup()
							.addComponent(foundAdvLbl)
							.addGap(338))))
		);
		modelAdv = new DefaultTableModel();
		tableAdv = new JTable();
		tableAdv.setModel(modelAdv);
		modelAdv.setColumnIdentifiers(columNames);
		//tableAdv = new JTable(data,columNames);
		tableAdv.setEnabled(false);
		scrollPane_1.setViewportView(tableAdv);
		advPanel.setLayout(gl_advPanel);
		

	}

	private void viewResult(ArrayList<Document> listResult, JTable table, DefaultTableModel  model) throws java.text.ParseException{
		String[][] data = {
			    {"", "", "", ""}
			};	
		model = new DefaultTableModel();
		table.setModel(model);
		model.setColumnIdentifiers(columNames);
		
		
		SimpleDateFormat dfIn = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat dfOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		if (listResult!=null){
			Iterator<Document> iter = listResult.iterator();
	
			
			while (iter.hasNext()){
				Document doc = iter.next();
				
				String name = doc.get("senderName") + " : " + doc.get("senderEmails");
				String subject = doc.get("subject");
				Date strDate = dfIn.parse(doc.get("date"));
				String date = dfOut.format(strDate);
				String body = doc.get("body");
				model.addRow(new String[] {name, subject, date, body});
			
			}
			
		}
			
		
			
	}
	
	private void popupHelpUI(){
		if (frmHelp==null){
			frmHelp = new JFrame();
			frmHelp.setTitle("Help");
			frmHelp.setResizable(false);
			frmHelp.setBounds(100, 100, 450, 300);
			frmHelp.setDefaultCloseOperation(frmHelp.DISPOSE_ON_CLOSE);
			
			JPanel panel = new JPanel();
			frmHelp.getContentPane().add(panel, BorderLayout.CENTER);
			
			JLabel lblNewLabel = new JLabel("<html>\r\n--------------------------------------<br>\r\nLucene Search Application<br>\r\nGroup 11 - IN4325<br>\r\n--------------------------------------<br>\r\nHow to use :<br>\r\n1. Build index : Tools -> Build Index-[Analysis type]<br>\r\n2. Search :<br>\r\n    a. Standard Search<br>\r\n    b. Advanced Search<br>\r\n<br>\r\n--------------------------------------<br>\r\nNugroho Dwi P         :4256786<br>\r\nMarhendra Lidiansa : 4256360<br>\r\nNidhi Singh               : 4242246<br>\r\n--------------------------------------<br>\r\n</html>");
			lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
			GroupLayout gl_panel = new GroupLayout(panel);
			gl_panel.setHorizontalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel.createSequentialGroup()
						.addGap(25)
						.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 363, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(56, Short.MAX_VALUE))
			);
			gl_panel.setVerticalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(35, Short.MAX_VALUE))
			);
			panel.setLayout(gl_panel);
			frmHelp.setVisible(true);
		}
		frmHelp.show();
	}
	
}
