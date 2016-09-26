package game_GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import game_logic.Category;
import game_logic.QuestionAnswer;
import game_logic.TeamData;

public class Welcome_Screen extends JFrame{
	public static final long serialVersionUID = 1;
	private JLabel welcome, instruction1, instruction2, instruction3, fileChosen;
	private JLabel[] nameLabel;
	private JSlider slider;
	private JTextField[] nameText;
	private JButton chooseFile, start, clear, exit;
	private JFileChooser filechooser;
	private String filename;
	private int numberOfTeams;
	private List<TeamData> teamData;
	private JPanel teamJP[];
	private JCheckBox quickPlayCheck;
	
	protected Map<Integer, Integer> pointValuesMapToIndex;
	protected Map<String, Category> categoriesMap;
	
	protected String finalJeopardyQuestion;
	protected String finalJeopardyAnswer;
	
	protected BufferedReader br;
	private FileReader fr;
	//contains questions with their answer and boolean flag as to whether they have been asked
	protected QuestionAnswer[][] questions;
	
	private boolean parseSuccess;
	private boolean quickPlay;
	
	public Welcome_Screen(){
		initializeComponents();
		createGUI();
		addEvents();
	}
	
	private void initializeComponents(){
		quickPlay = false;
		quickPlayCheck = new JCheckBox("Quick Play");
		
		welcome = new JLabel("Welcome to Jeopardy!");
		instruction1 = new JLabel("Choose the game file, number of teams, and team names before starting the game.");
		instruction2 = new JLabel("Please choose a game file.");
		instruction3 = new JLabel("Please choose the number of teams that will be playing on the slider below");
		fileChosen = new JLabel();
		
		nameText= new JTextField[4];
		nameLabel = new JLabel[4];
		teamJP = new JPanel[4];
		
		for (int i = 0; i < 4; i++){
			int n = i + 1;
			nameLabel[i] = new JLabel("Please name Team " + n);
			teamJP[i] = new JPanel();
			teamJP[i].setBackground(Color.BLUE);
			nameText[i] = new JTextField();
		}
		
		
		chooseFile = new JButton("Choose File");
		
		slider = new JSlider(1, 4, 1);
		
		filechooser = new JFileChooser("Open");
		
		start = new JButton("Start Jeopardy");
		clear = new JButton("Clear");
		exit = new JButton("Exit");
		
		numberOfTeams = 1;
	}
	
	private void createGUI(){
		setSize(1000, 600);
		setLocation(100, 100);
		setLayout(new BorderLayout());
		
		JPanel northJP = new JPanel();
		northJP.setPreferredSize(new Dimension(1000, 80));
		northJP.setLayout(new GridLayout(2, 1));
		//northJP.setLayout(new BoxLayout(northJP, BoxLayout.Y_AXIS));
		northJP.setBackground(Color.CYAN);
		Font font1 = new Font("Times New Roman", Font.BOLD, 34);
		welcome.setFont(font1);
		welcome.setHorizontalAlignment(SwingConstants.CENTER);
		Font font2 = new Font("Times New Roman", Font.BOLD, 16);
		instruction1.setFont(font2);
		instruction1.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel welcomePanel = new JPanel();
		welcomePanel.setBackground(Color.CYAN);
		welcomePanel.add(welcome);
		welcomePanel.add(quickPlayCheck);
		northJP.add(welcomePanel);
		northJP.add(instruction1);
		
		JPanel centerJP = new JPanel();
		centerJP.setLayout(new BorderLayout());
		
		JPanel jpChooseFile = new JPanel();
		jpChooseFile.setPreferredSize(new Dimension(1000, 60));
		jpChooseFile.setLayout(new GridBagLayout());
		GridBagConstraints bc = new GridBagConstraints();
		bc.gridx = 0;
		bc.gridy = 0;
		//bc.weightx = 0.3;
		Font font3 = new Font("Times New Roman", Font.BOLD, 17);
		instruction2.setFont(font3);
		instruction2.setPreferredSize(new Dimension(500, 40));
		jpChooseFile.add(instruction2, bc);
		//bc.weightx = 0.3;
		bc.gridx = 1;
		bc.gridy = 0;
		chooseFile.setPreferredSize(new Dimension(250, 40));
		jpChooseFile.add(chooseFile, bc);
		//bc.weightx = 1;
		bc.gridx = 3;
		bc.gridy = 0;
		fileChosen.setFont(font3);
		fileChosen.setPreferredSize(new Dimension(250, 40));
		jpChooseFile.add(fileChosen, bc);
		
		instruction2.setHorizontalAlignment(SwingConstants.CENTER);
		fileChosen.setHorizontalAlignment(SwingConstants.CENTER);
		instruction2.setForeground(Color.WHITE);
		fileChosen.setForeground(Color.WHITE);
		chooseFile.setBackground(Color.DARK_GRAY);
		chooseFile.setForeground(Color.WHITE);
		chooseFile.setOpaque(true);
		chooseFile.setBorderPainted(false);
		chooseFile.setMaximumSize(new Dimension(150, 10));
		//jpChooseFile.add(chooseFile);
		//jpChooseFile.add(fileChosen);
		jpChooseFile.setBackground(Color.BLUE);
		
		centerJP.add(jpChooseFile, BorderLayout.NORTH);
		
		JPanel jpChooseTeam = new JPanel();
		jpChooseTeam.setLayout(new BorderLayout());
		jpChooseTeam.setBackground(Color.BLUE);
		
		JPanel jpSlider = new JPanel();
		jpSlider.setBackground(Color.BLUE);
		jpSlider.setLayout(new BoxLayout(jpSlider, BoxLayout.Y_AXIS));
		instruction3.setForeground(Color.WHITE);
		instruction3.setFont(font2);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setBackground(Color.DARK_GRAY);
		slider.setLabelTable(slider.createStandardLabels(1));
		jpSlider.add(instruction3);
		jpSlider.add(slider);
		
		JPanel jpTeamName = new JPanel();
		jpTeamName.setBackground(Color.BLUE);
		jpTeamName.setLayout(new GridBagLayout());
		GridBagConstraints bag = new GridBagConstraints();
		bag.gridx = 0;
		bag.gridy = 0;
		teamJP[0].setPreferredSize(new Dimension(400, 100));
		jpTeamName.add(teamJP[0], bag);
		bag.gridx = 2;
		bag.gridy = 0;
		bag.weightx = 0.5;
		teamJP[1].setPreferredSize(new Dimension(400, 100));
		jpTeamName.add(teamJP[1], bag);
		bag.gridx = 0;
		bag.gridy = 2;
		bag.weightx = 0.5;
		teamJP[2].setPreferredSize(new Dimension(400, 100));
		jpTeamName.add(teamJP[2], bag);
		bag.gridx = 2;
		bag.gridy = 2;
		bag.weightx = 0.5;
		teamJP[3].setPreferredSize(new Dimension(400, 100));
		jpTeamName.add(teamJP[3], bag);
		
		jpChooseTeam.add(jpSlider, BorderLayout.NORTH);
		jpChooseTeam.add(jpTeamName, BorderLayout.CENTER);
		
		centerJP.add(jpChooseFile, BorderLayout.NORTH);
		centerJP.add(jpChooseTeam, BorderLayout.CENTER);
		
		
		
		JPanel southJP = new JPanel();
		southJP.setLayout(new FlowLayout());
		start.setOpaque(true);
		start.setBorderPainted(false);
		start.setBackground(Color.DARK_GRAY);
		start.setForeground(Color.WHITE);
		clear.setOpaque(true);
		clear.setBorderPainted(false);
		clear.setBackground(Color.DARK_GRAY);
		clear.setForeground(Color.WHITE);
		exit.setOpaque(true);
		exit.setBorderPainted(false);
		exit.setBackground(Color.DARK_GRAY);
		exit.setForeground(Color.WHITE);
		southJP.setPreferredSize(new Dimension(1000, 60));
		southJP.add(start);
		southJP.add(clear);
		southJP.add(exit);
		southJP.setBackground(Color.BLUE);
		
		add(northJP, BorderLayout.NORTH);
		add(centerJP, BorderLayout.CENTER);
		add(southJP, BorderLayout.SOUTH);
		showTeamOption(0);
	}
	
	private void addEvents(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		quickPlayCheck.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				quickPlay = quickPlayCheck.isSelected();
			}
			
		});
		
		chooseFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt)
			{
				filechooser.setDialogTitle("Open");
				filechooser.setFileFilter(new FileNameExtensionFilter("text files(*.txt)", "txt"));
				filechooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = filechooser.showOpenDialog(new JFrame());
				if (returnVal == JFileChooser.APPROVE_OPTION){
					filename = filechooser.getSelectedFile().getName();
					fileChosen.setText(filechooser.getSelectedFile().getName());
				}
			}
		});
		
		slider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent evt){
				numberOfTeams = slider.getValue();
				for (int i = 1; i < 4; i ++){
					if (i >= numberOfTeams)
					{
						hideTeamOption(i);
					}
					else
					{
						showTeamOption(i);
					}
				}
			}
		});
		
		start.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt)
			{
				numberOfTeams = slider.getValue();
				teamData = new ArrayList<>(numberOfTeams);

				Set<String> duplicateTeamNamesCheck = new HashSet<>();

				boolean whitespace = false;
				boolean duplicate = false;
				//choose team names
				for (int i = 0; i < numberOfTeams; i++){
						
					String input = nameText[i].getText();
					
					if (duplicateTeamNamesCheck.contains(input.toLowerCase())){
						duplicate = true;
					}
					else if (input.trim().equals("")){
						System.out.println(i);
						whitespace = true;
					}
					else{
						teamData.add(new TeamData(i, 0L, input));
						duplicateTeamNamesCheck.add(input.toLowerCase());
					}
				}
				
				if (whitespace == true && duplicate == true)
				{
					JOptionPane.showMessageDialog(Welcome_Screen.this, 
							"Error: Your team name cannot be white space, "
							+ "and you cannot have duplicate team names.",
							"Error", JOptionPane.ERROR_MESSAGE);
					System.out.println("joption");
				}
				else if (whitespace == true){
					JOptionPane.showMessageDialog(Welcome_Screen.this, 
							"Error: Your team name cannot be white space.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
				else if (duplicate == true){
					JOptionPane.showMessageDialog(Welcome_Screen.this,
							"Error: You cannot have duplicate names.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
				
				if (filename == null)
				{
					JOptionPane.showMessageDialog(Welcome_Screen.this,
							"Error: You must choose a game file.", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
				
				if (whitespace == false && duplicate == false && filename != null)
				{
					parseFile();
					if (parseSuccess)
					{
						dispose();
						MainPlay_Screen mainPlay = new MainPlay_Screen();
						mainPlay.getData(filename, numberOfTeams, teamData, pointValuesMapToIndex, 
								categoriesMap, questions, finalJeopardyQuestion, finalJeopardyAnswer,
								quickPlay);
						mainPlay.setVisible(true);
					}
				}
				
		}});
		
		clear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				slider.setValue(1);
				nameText[0].setText("");
			}
		});
		
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
		});
	}
	
	private void showTeamOption(int number){
		hideTeamOption(number);
		teamJP[number].setLayout(new GridBagLayout());
		teamJP[number].setBackground(Color.BLUE);
		JPanel jpLabel = new JPanel();
		JPanel jpText = new JPanel();
	
		String text = "";
		if (nameText[number] != null)
		{
			text = nameText[number].getText();
		}
		else
		{
			nameText[number] = new JTextField();
		}
		
		
		nameText[number].setBackground(Color.CYAN);
		
		nameText[number].setText(text);
		//JPanel jp = new JPanel();
		jpLabel.setLayout(new GridLayout(1, 1));
		jpText.setLayout(new GridLayout(1, 1));
		//nameText[number].setSize(jpText.getSize());
		jpLabel.add(nameLabel[number]);
		jpText.add(nameText[number]);
		jpLabel.setBackground(Color.DARK_GRAY);
		jpText.setBackground(Color.BLUE);
		jpLabel.setPreferredSize(new Dimension(200, 40));
		jpText.setPreferredSize(new Dimension(200, 40));
		Font font = new Font("Times New Roman", Font.BOLD, 18);
		nameLabel[number].setFont(font);
		nameLabel[number].setHorizontalAlignment(SwingConstants.CENTER);
		nameLabel[number].setForeground(Color.WHITE);
		nameText[number].setFont(font);
		/*jp.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		jp.add(jpLabel, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.ipadx = 150;
	
		
		nameText[number].setBackground(Color.DARK_GRAY);
		jp.add(jpText, gbc);*/
		GridBagConstraints bag = new GridBagConstraints();
		bag.gridx = 0;
		bag.gridy = 0;
		teamJP[number].add(jpLabel, bag);
		bag.gridx = 0;
		bag.gridy = 1;
		teamJP[number].add(jpText, bag);
		teamJP[number].setBackground(Color.BLUE);
		
	}
	
	private void hideTeamOption(int number){
		if (teamJP[number] == null || nameText[number] == null)
			return;
		teamJP[number].revalidate();
		teamJP[number].repaint();
		teamJP[number].removeAll();
		nameText[number].setText("");    //??
		teamJP[number].setBackground(Color.BLUE);
	}
	
	private void parseFile()
	{
		parseSuccess = true;
		pointValuesMapToIndex = new HashMap<>();
		categoriesMap = new HashMap<>();
		questions = new QuestionAnswer[5][5];
		
		try {
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			
			parseCategoriesAndPoints();
			parseQuestions();
		}
		catch (FileNotFoundException e) {

		}
		catch (IOException e) {
			
		}
	}
	
public void parseCategoriesAndPoints() throws IOException{
		
		String categories = br.readLine();
		String[] parsedCategories = categories.split("::");
		
		if (parsedCategories.length != 5){
			exit("Too many or too few categories provided.");
		}
		
		for (String str : parsedCategories){
			
			if (str.trim().equals("")){
				exit("One of the categories is whitespace.");
			}
		}
		
		String pointValues = br.readLine();
		String[] parsedPointValues = pointValues.split("::");
		
		if (parsedPointValues.length != 5){
			exit( "Too many or too few dollar values provided.");
		}
		
		for (int i = 0; i<5; i++){
			categoriesMap.put(parsedCategories[i].toLowerCase().trim(), new Category(parsedCategories[i].trim(), i));
			
			try{
				pointValuesMapToIndex.put(Integer.parseInt(parsedPointValues[i].trim()), i);
			}
			catch (NumberFormatException nfe){
				exit("One of the point values is a string.");
			}
		}
	}

	public void parseQuestions() throws IOException{
	
		String templine = "";
		String fullData = "";
		int questionCount = 0;
		boolean haveFinalJeopardy = false;
	
		while(questionCount != 26){
			
			templine = br.readLine();
			if (templine == null){
				exit("Not enough questions in the file");
			}
			
			if (!templine.startsWith("::")){
				fullData += templine;
			}
			else{
				
				//parsePrevious question
				if (questionCount != 0){
					haveFinalJeopardy = parseQuestions(fullData, haveFinalJeopardy);
				}
				
				fullData = templine.substring(2);
				questionCount++;
			}
			
		}
		
		haveFinalJeopardy = parseQuestions(fullData, haveFinalJeopardy);
		
		if (br.readLine() != null){
			exit("Two many questions provided.");
		}
		
		if (!haveFinalJeopardy){
			exit("This game file does not have a final jeopardy question.");
		}
}
	
private Boolean parseQuestions(String line, Boolean haveFinalJeopardy){
		
		Boolean finalJeopardy = haveFinalJeopardy;
		
		if(line.toLowerCase().startsWith("fj")){
			
			if (haveFinalJeopardy){
				exit("Cannot have more than one final jeopardy question.");
			}
			else{
				
				parseFinalJeopardy(line);
				finalJeopardy = true;
			}
			
		}
		else{
			parseQuestionString(line);
		}
		return finalJeopardy;
	}
	
	private void parseFinalJeopardy(String finalJeopardyString){
		
		String [] questionData = finalJeopardyString.split("::");
		
		if (questionData.length != 3) exit("Too much or not enough data provided for the final jeopardy question.");
		
		if (questionData[1].trim().equals("")) exit("The Final Jeopardy question cannot be whitespace");
		
		if (questionData[2].trim().equals("")) exit("The Final Jeopardy answer cannot be whitespace");
		
		finalJeopardyQuestion = questionData[1].trim();
		finalJeopardyAnswer = questionData[2].trim();

	}
	
	//does not check whether there is a duplicate category/point value question
	private void parseQuestionString(String question){
		
		String [] questionData = question.split("::");
		
		if (questionData.length != 4){
			exit("Too much or not enough data provided for this question");
		}
		else{
			
			String category = questionData[0].trim();
			
			if (!categoriesMap.containsKey(category.toLowerCase())) exit("This category does not exist: "+category);
			
			Integer pointValue = -1;
			
			try{
				pointValue = Integer.parseInt(questionData[1].trim());	
			}
			catch (NumberFormatException nfe){
				exit("The point value cannot be a String.");
			}
			
			if (!pointValuesMapToIndex.containsKey(pointValue)) exit("This point value does not exist: "+pointValue);
			
			int indexX = categoriesMap.get(category.toLowerCase().trim()).getIndex();
			int indexY = pointValuesMapToIndex.get(pointValue);
			
			if (questionData[2].trim().equals("")) exit("The question cannot be whitespace.");
			
			if (questionData[3].trim().equals("")) exit("The answer cannot be whitespace.");
			
			questions[indexX][indexY] = new QuestionAnswer(questionData[2].trim(), questionData[3].trim());
		}
	}
	
	

	
	private void exit(String message)
	{
		JOptionPane.showMessageDialog(Welcome_Screen.this, 
				message, "Error",
				JOptionPane.ERROR_MESSAGE);
		parseSuccess = false;
	}
	
	
}
