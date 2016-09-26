package game_GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import game_logic.Category;
import game_logic.QuestionAnswer;
import game_logic.TeamData;

public class MainPlay_Screen extends JFrame {

	private static final long serialVersionUID = 1;
	
	private JMenuBar jmb;
	private JMenu menu;
	private JMenuItem restartMenuItem, fileMenuItem, exitMenuItem;
	private JLabel jeopardyLabel, questionTeamNameLabel, questionPointValueLabel, 
			questionCategoryLabel, formatPromptLabel, finalJeopardyLabel;
	private JLabel [] categoryLabel, teamNameLabel, teamScoreLabel, finalTeamNameLabel,
			teamBetLabel;
	private JSlider [] teamBetsSlider;
	private String filename;
	private int numberOfTeams;
	private List<TeamData> teamData;
	private JTextArea gameProgressText, questionText, finalQuestionText;
	private JTextField answerText;
	private JTextField [] finalAnswerText;
	private int whoseTurn, incorrectFormatTimes, numberOfChosenQuestions;
	private String gameProgress;

	private gameButton [][] questionButton;
	private gameButton submitAnswerButton;
	private JButton [] betButtons;
	private JButton [] finalSubmitButton;
	
	private JPanel centerPanel, askQuestionPanel, finalJeopardyPanel;
	
	//contains questions with their answer and boolean flag as to whether they have been asked
	protected QuestionAnswer[][] questions;
	
	//maps from the point value/category to their index in the appropriate array
	protected Map<Integer, Integer> pointValuesMapToIndex;
	protected Map<String, Category> categoriesMap;
	protected Map<Integer, Integer> indexMapToPointValues;
	protected Map<JButton, Integer> buttonMapToIndex;
	protected Map<JSlider, Integer> sliderMapToIndex;
	
	//protected Map<JButton, QuestionAnswer> buttonMapToQuestion;
	
	protected String finalJeopardyQuestion;
	protected String finalJeopardyAnswer;
	
	private static Set<String> unmodifiableSetAnswerVerbs;
	private static Set<String> unmodifiableSetAnswerNouns;
	
	private String[] finalAnswer;
	boolean quickPlay;
	
	public MainPlay_Screen()
	{
		super("Jeopardy!!");
		
		
	}
	
	public void getData(String filename, int numberOfTeams, List<TeamData> teamData,
			Map<Integer, Integer> pointValuesMapToIndex, Map<String, Category> categoriesMap,
			QuestionAnswer[][] questions, String finalJeopardyQuestion, 
			String finalJeopardyAnswer, boolean quickPlay)
	{
		this.filename = filename;
		this.numberOfTeams = numberOfTeams;
		this.teamData = teamData;
		this.pointValuesMapToIndex = pointValuesMapToIndex;
		this.categoriesMap = categoriesMap;
		this.questions = questions;
		this.finalJeopardyQuestion = finalJeopardyQuestion;
		this.finalJeopardyAnswer = finalJeopardyAnswer;
		this.quickPlay = quickPlay;
		
		initializeComponents();
		createGUI();
		addEvents();
		
		initializeAnswerFormatSet();
		startGame();
	}
	
	private void initializeComponents()
	{
		centerPanel = new JPanel();
		askQuestionPanel = new JPanel();
		finalJeopardyPanel = new JPanel();
		indexMapToPointValues = new HashMap<>();
		Iterator<Integer> it0 = pointValuesMapToIndex.keySet().iterator();
		while (it0.hasNext())
		{
			int value = it0.next();
			int index = pointValuesMapToIndex.get(value);
			indexMapToPointValues.put(index, value);
		}
		
		jmb = new JMenuBar();
		menu = new JMenu("Menu");
		restartMenuItem = new JMenuItem("Restart This Game");
		fileMenuItem = new JMenuItem("Choose New Game File");
		exitMenuItem = new JMenuItem("Exit Game");
		jeopardyLabel = new JLabel("Jeopardy");
		categoryLabel = new JLabel[5];
		teamNameLabel = new JLabel[numberOfTeams];
		teamScoreLabel = new JLabel[numberOfTeams];
		gameProgressText = new JTextArea();
		
		Iterator<String> it = categoriesMap.keySet().iterator();
		int n = 0;
		while(it.hasNext())
		{
			categoryLabel[n] = new JLabel(categoriesMap.get(it.next()).getCategory());
			n++;
		}
		for (int i = 0; i < numberOfTeams; i ++)
		{
			teamNameLabel[i] = new JLabel(teamData.get(i).getTeamName());
			teamNameLabel[i].setForeground(Color.WHITE);
			teamScoreLabel[i] = new JLabel("$" + teamData.get(i).getPoints());
			teamScoreLabel[i].setBackground(Color.DARK_GRAY);
			teamScoreLabel[i].setForeground(Color.WHITE);
			Font font = new Font("Times New Roman", Font.BOLD, 14);
			teamNameLabel[i].setHorizontalAlignment(SwingConstants.CENTER);
			teamScoreLabel[i].setHorizontalAlignment(SwingConstants.CENTER);
			teamNameLabel[i].setFont(font);
			teamScoreLabel[i].setFont(font);
		}
		
		//buttonMapToQuestion = new HashMap<>();
		
		questionButton = new gameButton[5][5];
		for (int i = 0; i < 5; i++)
		{
			for (int k = 0; k < 5; k++)
			{
				questionButton[i][k] = new gameButton("$" + indexMapToPointValues.get(i).toString());
				questionButton[i][k].setOpaque(true);
				questionButton[i][k].setBorderPainted(false);
				questionButton[i][k].setBackground(Color.DARK_GRAY);
				questionButton[i][k].setForeground(Color.WHITE);
				questionButton[i][k].setCategory(categoriesMap.get(categoryLabel[k].getText().toLowerCase()));
				questionButton[i][k].setPointValue(indexMapToPointValues.get(i));
				int x = categoriesMap.get(categoryLabel[k].getText().toLowerCase()).getIndex();
				questionButton[i][k].setQuestion(questions[x][i]);
				//buttonMapToQuestion.put(questionButton[i][k], questions[k][i]);
			}
		}
		
		questionTeamNameLabel = new JLabel();
		questionCategoryLabel = new JLabel();
		questionPointValueLabel = new JLabel();
		formatPromptLabel = new JLabel();
		questionText = new JTextArea();
		answerText = new JTextField();
		submitAnswerButton = new gameButton("Submit Answer");
		numberOfChosenQuestions = 0;
		
		finalJeopardyLabel = new JLabel("Final Jeopardy Round");
		finalQuestionText = new JTextArea();
		
		teamBetsSlider = new JSlider[numberOfTeams];
		betButtons = new JButton[numberOfTeams];
		finalTeamNameLabel = new JLabel[numberOfTeams];
		teamBetLabel = new JLabel[numberOfTeams];
		finalAnswerText = new JTextField[numberOfTeams];
		finalSubmitButton = new JButton[numberOfTeams];
		buttonMapToIndex = new HashMap<>();
		sliderMapToIndex = new HashMap<>();
		
		for (int i = 0; i < numberOfTeams; i++)
		{
			teamBetsSlider[i] = new JSlider();
			betButtons[i] = new JButton("Set Bet");
			finalTeamNameLabel[i] = new JLabel(teamData.get(i).getTeamName() + "'s bet:");
			teamBetLabel[i] = new JLabel("$1");
			finalAnswerText[i] = new JTextField();
			finalSubmitButton[i] = new JButton("Submit Answer");
			finalSubmitButton[i].setEnabled(false);
			buttonMapToIndex.put(betButtons[i], i);
			buttonMapToIndex.put(finalSubmitButton[i], i);
			sliderMapToIndex.put(teamBetsSlider[i], i);
		}
		
		finalAnswer = new String[numberOfTeams];
	}
	
	private void createGUI()
	{
		setSize(1500, 1000);
		setLocation(300, 100);
		setLayout(new BorderLayout());
		
		//JPanel northPanel = new JPanel();
		menu.add(restartMenuItem);
		menu.add(fileMenuItem);
		menu.add(exitMenuItem);
		jmb.add(menu);
		setJMenuBar(jmb);
		//northPanel.setPreferredSize(new Dimension(600, 30));
		//northPanel.add(jmb);
		
		//centerPanel.setPreferredSize(new Dimension(450, 590));
		//centerPanel.setBackground(Color.DARK_GRAY);
		centerPanel.setLayout(new BorderLayout());
		
		JPanel jeopardyPanel = new JPanel();
		jeopardyPanel.setLayout(new GridLayout(1, 1));
		jeopardyPanel.setBackground(Color.CYAN);
		Font font = new Font("Times New Roman", Font.BOLD, 22);
		jeopardyLabel.setFont(font);
		jeopardyLabel.setHorizontalAlignment(SwingConstants.CENTER);
		jeopardyPanel.setSize(new Dimension(1050, 60));
		jeopardyPanel.add(jeopardyLabel);
		
		JPanel questionPanel = new JPanel();
		questionPanel.setLayout(new BorderLayout());
		
		JPanel categoryPanel = new JPanel();
		categoryPanel.setBackground(Color.DARK_GRAY);
		categoryPanel.setPreferredSize(new Dimension(1050, 80));
		categoryPanel.setLayout(new GridLayout(1, 5));
		for (int i = 0;i < 5; i++)
		{
			categoryLabel[i].setForeground(Color.WHITE);
			categoryLabel[i].setHorizontalAlignment(SwingConstants.CENTER);
			categoryPanel.add(categoryLabel[i]);
		}
		
		questionPanel.add(categoryPanel, BorderLayout.NORTH);
		
		JPanel valuePanel = new JPanel();
		questionPanel.add(valuePanel, BorderLayout.CENTER);
		
		valuePanel.setLayout(new GridLayout(5, 5));
		for (int i = 0; i < 5; i++)
		{
			for (int k = 0; k < 5; k++)
			{
				valuePanel.add(questionButton[i][k]);
			}
		}
		
		centerPanel.add(jeopardyPanel, BorderLayout.NORTH);
		centerPanel.add(questionPanel);
		
		JPanel eastPanel = new JPanel();
		eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
		eastPanel.setBackground(Color.DARK_GRAY);
		eastPanel.setPreferredSize(new Dimension(450, 1000));
		
		JPanel northeastPanel = new JPanel();
		northeastPanel.setLayout(new GridLayout(numberOfTeams, 2));
		northeastPanel.setBackground(Color.DARK_GRAY);
		northeastPanel.setPreferredSize(new Dimension(450, 400));
		for (int i = 0; i < numberOfTeams; i ++)
		{
			northeastPanel.add(teamNameLabel[i]);
			northeastPanel.add(teamScoreLabel[i]);
		}
		eastPanel.add(northeastPanel);
		
		JPanel southeastPanel = new JPanel();
		southeastPanel.setBackground(Color.CYAN);
		southeastPanel.setPreferredSize(new Dimension(450, 600));
		//southeastPanel.setMinimumSize(new Dimension(230, 250));
		//southeastPanel.setMaximumSize(new Dimension(230, 250));
		southeastPanel.setLayout(new BoxLayout(southeastPanel, BoxLayout.Y_AXIS));
		
		JLabel progressTitle = new JLabel("Game Progress");
		JPanel progressTitlePanel = new JPanel();
		progressTitlePanel.setPreferredSize(new Dimension(450, 60));
		progressTitlePanel.add(progressTitle);
		progressTitlePanel.setBackground(Color.CYAN);
		font = new Font("Times New Roman", Font.BOLD, 22);
		progressTitle.setFont(font);
		southeastPanel.add(progressTitlePanel);
		JPanel gameProgressPanel = new JPanel();
		gameProgressPanel.setForeground(Color.WHITE);
		gameProgressPanel.setPreferredSize(new Dimension(450, 540));
		gameProgressPanel.setLayout(new GridLayout(1, 1));
		
		gameProgressText.setEditable(false);
		gameProgressText.setOpaque(true);
		gameProgressText.setWrapStyleWord(true);
		gameProgressText.setLineWrap(true);
		gameProgressText.getScrollableTracksViewportHeight();
		//gameProgressText.setPreferredSize(new Dimension(230, 220));
		gameProgressText.setMinimumSize(new Dimension(450, 540));
		//gameProgressText.setMaximumSize(new Dimension(230, 220));
		gameProgressText.setForeground(Color.BLACK);
		//gameProgressText.setPreferredSize(new Dimension(230, 220));
		gameProgressText.setBackground(Color.CYAN);
		gameProgressPanel.add(gameProgressText);
		JScrollPane jsp = new JScrollPane(gameProgressText);
		jsp.setPreferredSize(new Dimension(450, 540));
		jsp.setBackground(Color.CYAN);
		gameProgressPanel.add(jsp);
		gameProgressPanel.setBackground(Color.CYAN);
		southeastPanel.add(gameProgressPanel);
		eastPanel.add(southeastPanel);
		
		//add(northPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(eastPanel, BorderLayout.EAST);
		
		JPanel questionNorthPanel = new JPanel();
		questionNorthPanel.setLayout(new GridLayout(1, 3));
		questionNorthPanel.setPreferredSize(new Dimension(1050, 70));
		questionNorthPanel.setBackground(Color.BLUE);
		questionNorthPanel.add(questionTeamNameLabel);
		questionNorthPanel.add(questionCategoryLabel);
		questionNorthPanel.add(questionPointValueLabel);
		
		JPanel questionCenterPanel = new JPanel();
		questionCenterPanel.setBackground(Color.DARK_GRAY);;
		questionCenterPanel.setLayout(new GridBagLayout());
		GridBagConstraints bag = new GridBagConstraints();
		questionText.setBackground(Color.CYAN);
		questionText.setEditable(false);
		questionText.setLineWrap(true);
		questionText.setWrapStyleWord(true);
		bag.insets = new Insets(10, 10, 10, 10);
		bag.weightx = 0.8;
		bag.weighty = 0.1;
		bag.gridx = 0;
		bag.gridy = 0;
		bag.gridwidth = 5;
		bag.fill = GridBagConstraints.BOTH;
		formatPromptLabel.setForeground(Color.WHITE);
		formatPromptLabel.setHorizontalAlignment(SwingConstants.CENTER);
		questionCenterPanel.add(formatPromptLabel, bag);
		bag.gridx = 0;
		bag.gridy = 1;
		bag.weightx = 0.8;
		bag.weighty = 0.8;
		bag.gridwidth = 5;
		bag.gridheight = 4;
		bag.fill = GridBagConstraints.BOTH;
		questionText.getScrollableTracksViewportHeight();
		questionCenterPanel.add(questionText, bag);
		bag.weightx = 0.7;
		bag.weighty = 0.1;
		bag.gridx = 0;
		bag.gridy = 5;
		bag.gridwidth = 3;
		bag.gridheight = 1;
		bag.fill = GridBagConstraints.BOTH;
		questionCenterPanel.add(answerText, bag);
		bag.gridx = 4;
		bag.gridy = 5;
		bag.weightx = 0.01;
		bag.weighty = 0.1;
		bag.gridheight = 1;
		bag.gridwidth = 1;
		bag.fill = GridBagConstraints.BOTH;
		questionCenterPanel.add(submitAnswerButton, bag);
		
		JScrollPane scroll = new JScrollPane(questionText);
		bag.gridx = 0;
		bag.gridy = 1;
		bag.weightx = 0.8;
		bag.weighty = 0.8;
		bag.gridwidth = 5;
		bag.gridheight = 4;
		questionCenterPanel.add(scroll, bag);
		
		askQuestionPanel.setBackground(Color.DARK_GRAY);
		//askQuestionPanel.setPreferredSize(new Dimension(600, 450));
		askQuestionPanel.setLayout(new BorderLayout());
		askQuestionPanel.add(questionNorthPanel, BorderLayout.NORTH);
		askQuestionPanel.add(questionCenterPanel, BorderLayout.CENTER);
		
		//create final jeopardy panel
		finalJeopardyPanel.setLayout(new GridBagLayout());
		
	}
	
	private void addEvents()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		exitMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
			
		});
		
		restartMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				resetData();
				startGame();
				remove(finalJeopardyPanel);
				validate();
				repaint();
				remove(centerPanel);
				validate();
				repaint();
				add(centerPanel);
				validate();
				repaint();
				for (int i = 0; i < numberOfTeams; i++)
				{
					teamScoreLabel[i].setText("$0");
				}
			}
		});
		
		fileMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Welcome_Screen welcomeScreen = new Welcome_Screen();
				welcomeScreen.setVisible(true);
				dispose();
			}
		});
		
		for (int i = 0; i < 5; i++)
		{
			for (int k = 0; k < 5; k++)
			{
				questionButton[i][k].addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						chooseQuestion((gameButton)e.getSource());
					}
				});
			}
		}
		
		submitAnswerButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				formatPromptLabel.setText("");
				
				gameButton button = (gameButton)e.getSource();
				String givenAnswer = answerText.getText();
				String expectedAnswer = button.getQuestion().getAnswer();
				if (!validAnswerFormat(givenAnswer.trim()))
				{
					incorrectFormatTimes++;
					if (incorrectFormatTimes < 2)
					{
						formatPromptLabel.setText("Remember to pose your answer as a question");
						return;
					}
					else
					{
						teamData.get(whoseTurn).deductPoints(button.getPointValue());
						gameProgress = gameProgress + '\n' + teamData.get(whoseTurn).getTeamName()
								+ ", you got the answer wrong. " + button.getPointValue().toString()
								 + " will be deducted from your score.";
					}
				}
				else if (givenAnswer.toLowerCase().endsWith(expectedAnswer.toLowerCase()))
				{
					teamData.get(whoseTurn).addPoints(button.getPointValue());
					gameProgress = gameProgress + '\n' + teamData.get(whoseTurn).getTeamName() +
							", you got the answer right! " + button.getPointValue().toString()
							 + " will be added to your score.";
				}
				else
				{
					teamData.get(whoseTurn).deductPoints(button.getPointValue());
					gameProgress = gameProgress + '\n' + teamData.get(whoseTurn).getTeamName() +
							", you got the answer wrong. " + button.getPointValue().toString()
							 + " will be deducted from your score." + '\n' + "The expected answer was: "
							 + expectedAnswer;
				}
				
				
				teamScoreLabel[whoseTurn].setText("$" + teamData.get(whoseTurn).getPoints());				
				
				
				whoseTurn = (whoseTurn + 1)%numberOfTeams;
				gameProgress = gameProgress + '\n' + "Now it's " + teamData.get(whoseTurn).getTeamName()
						 + "'s turn! Please choose a question.";
				gameProgressText.setText(gameProgress);
				
				remove(askQuestionPanel);
				validate();
				repaint();
				
				
				if ((numberOfChosenQuestions >= 5 && quickPlay == true) ||
						(numberOfChosenQuestions >= 25 && quickPlay == false))
				{
					gameProgress = gameProgress + '\n' + "Welcome to Final Jeopardy!";
					gameProgressText.setText(gameProgress);
					finalJeopardy();
				}
				else
				{
					add(centerPanel, BorderLayout.CENTER);
					validate();
					repaint();
				}
				
				
				
			}
		});
		
		for (int i = 0; i < numberOfTeams; i++)
		{
			betButtons[i].addActionListener(new ActionListener (){
				public void actionPerformed(ActionEvent e) {
					int index = buttonMapToIndex.get((JButton)e.getSource());
					JButton button = (JButton)e.getSource();
					if (teamData.get(index).getPoints() > 0)
					{
						int value = teamBetsSlider[index].getValue();
						if (value > 0)
						{
							gameProgress = gameProgress + '\n' + teamData.get(index).getTeamName()
									+ " bets" + value;
							button.setEnabled(false);
							teamBetsSlider[index].setEnabled(false);
							teamData.get(index).setBet(value);
							gameProgressText.setText(gameProgress);
							boolean allSet = true;
							for (int i = 0; i < numberOfTeams; i++)
							{
								if (teamData.get(i).getPoints() > 0)
								{
									if (betButtons[i].isEnabled())
									{
										allSet = false;
									}
								}
							}
							if (allSet == true)
							{
								finalQuestionText.setText(finalJeopardyQuestion);
								gameProgress = gameProgress + '\n' + "Here is the Final Jeopardy question:"
										 + finalJeopardyQuestion;
								for (int i = 0; i < numberOfTeams; i++)
								{
									finalSubmitButton[i].setEnabled(true);
								}
							}
						}
					}
				}
				
			});
		}
		
		for (int i = 0; i < numberOfTeams; i++)
		{
			finalSubmitButton[i].addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					JButton button = (JButton)e.getSource();
					int index = buttonMapToIndex.get(button);
					if (teamData.get(index).getPoints() > 0)
					{
						finalAnswer[index] = finalAnswerText[index].getText();
						button.setEnabled(false);
						boolean allAnswered = true;
						for (int i = 0; i < numberOfTeams; i++)
						{
							if (teamData.get(i).getPoints() > 0)
							{
								if (finalSubmitButton[i].isEnabled())
								{
									allAnswered = false;
								}
							}
						}
						if (allAnswered)
						{
							List<TeamData> finalTeams = new ArrayList<>();
							for (int i = 0; i < numberOfTeams; i++)
							{
								if (teamData.get(i).getPoints() > 0)
								{
									finalTeams.add(teamData.get(i));
									if(!validAnswerFormat(finalAnswer[i].trim()))
									{
										teamData.get(i).deductPoints(teamData.get(i).getBet());
										gameProgress = gameProgress + '\n' + teamData.get(i).getTeamName()
												  + " had incorrect answer format, " + teamData.get(i).getBet()
												  + "will be deducted from their total.";
									}
									else
									{
										if (finalAnswer[i].toLowerCase().endsWith(finalJeopardyAnswer))
										{
											teamData.get(i).addPoints(teamData.get(i).getBet());
											gameProgress = gameProgress + '\n' + teamData.get(i).getTeamName()
													  + " got the answer right! " + teamData.get(i).getBet()
													  + "will be added to their total.";
										}
										else
										{
											teamData.get(i).deductPoints(teamData.get(i).getBet());
											gameProgress = gameProgress + '\n' + teamData.get(i).getTeamName()
													  + " had incorrect answer format, " + teamData.get(i).getBet()
													  + "will be deducted from their total.";
										}
									}
									teamScoreLabel[i].setText("$" + teamData.get(i).getPoints());
								}
							}
							gameProgressText.setText(gameProgress);
							ArrayList<Integer> winners = getWinners(finalTeams);
							if (winners.size() == 0){
								JOptionPane.showMessageDialog(MainPlay_Screen.this, 
										"There were no winners!", "", JOptionPane.INFORMATION_MESSAGE);
							}
							
							else{
								String toDisplay = winners.size() > 1 ? "And the winners are " : "And the winner is ";
								toDisplay = toDisplay + '\n' + teamData.get(winners.get(0)).getTeamName();
								if (winners.size() > 1){
									
									for (int i = 1; i<winners.size(); i++){
										toDisplay = toDisplay + ", " + teamData.get(winners.get(i)).getTeamName();
									}
								}
								
								JOptionPane.showMessageDialog(MainPlay_Screen.this, toDisplay);
							}
						}
					}
					
				}
				
			});
		}
		
		for (int i = 0; i < numberOfTeams; i++)
		{
			teamBetsSlider[i].addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					JSlider slider = (JSlider)e.getSource();
					int index = sliderMapToIndex.get((JSlider)e.getSource());
					if (teamData.get(index).getPoints() > 0)
					{
						teamBetLabel[index].setText("$" + slider.getValue());
					}
				}
				
			});
		}

	}
	
	private void startGame()
	{
		Random rand = new Random();
		int firstTeam = rand.nextInt(numberOfTeams);
		gameProgress = " Welcome to Jeopardy!";
		gameProgress = gameProgress + '\n';
		gameProgress = gameProgress + "The team to go first will be ";
		gameProgress = gameProgress + teamData.get(firstTeam).getTeamName();
		gameProgressText.setText(gameProgress);
		whoseTurn = firstTeam;
	}
	
	private void initializeAnswerFormatSet()
	{
		Set<String> nounsModifiableSet = new HashSet<>();
		Set<String> verbsModifiableSet = new HashSet<>();
		nounsModifiableSet.add("who");
		nounsModifiableSet.add("where");
		nounsModifiableSet.add("when");
		nounsModifiableSet.add("what");
		verbsModifiableSet.add("is");
		verbsModifiableSet.add("are");
		
		unmodifiableSetAnswerNouns = Collections.unmodifiableSet(nounsModifiableSet);
		unmodifiableSetAnswerVerbs = Collections.unmodifiableSet(verbsModifiableSet);
	}
	
	private void chooseQuestion(gameButton button)
	{
		QuestionAnswer question = button.getQuestion();
		if (question.hasBeenAsked())
		{
			return;
		}
		numberOfChosenQuestions++;
		button.setBackground(Color.GRAY);
		question.setHasBeenAsked();
		revalidate();
		repaint();
		remove(centerPanel);
		
		Font font = new Font("Times New Roman", Font.BOLD, 24);
		
		questionTeamNameLabel.setText(teamData.get(whoseTurn).getTeamName());
		questionTeamNameLabel.setForeground(Color.WHITE);
		questionTeamNameLabel.setFont(font);
		questionTeamNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		questionCategoryLabel.setText(button.getCategory().getCategory());
		questionCategoryLabel.setForeground(Color.WHITE);
		questionCategoryLabel.setFont(font);
		questionCategoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		questionPointValueLabel.setText("$" + button.getPointValue().toString());
		questionPointValueLabel.setForeground(Color.WHITE);
		questionPointValueLabel.setFont(font);
		questionPointValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		questionText.setText(button.getQuestion().getQuestion());
		questionText.setFont(new Font("Times New Roman", Font.BOLD, 22));
		
		answerText.setText("");
		
		submitAnswerButton.setCategory(button.getCategory());
		submitAnswerButton.setPointValue(button.getPointValue());
		submitAnswerButton.setQuestion(button.getQuestion());
		
		add(askQuestionPanel, BorderLayout.CENTER);
		validate();
		repaint();
		
		gameProgress = gameProgress + '\n' + teamData.get(whoseTurn).getTeamName()
				+ " chose the question in " + button.getCategory().getCategory() +
				" worth $" + button.getPointValue().toString();
		
		gameProgressText.setText(gameProgress);
		formatPromptLabel.setText("");
		
		incorrectFormatTimes = 0;
	}
	
	private boolean validAnswerFormat(String answer){
		
		if (answer.length() < 1) return false;
		
		String[] splitAnswer = answer.trim().split("\\s+");
		
		if (splitAnswer.length < 2) return false;
		
		return unmodifiableSetAnswerVerbs.contains(splitAnswer[1].toLowerCase()) && unmodifiableSetAnswerNouns.contains(splitAnswer[0].toLowerCase());
	}
	
private ArrayList<Integer> getWinners(List<TeamData> finalTeams){
		
		//sorts the finalists in order of their total score
		Collections.sort(finalTeams, TeamData.getComparator());
		System.out.println(finalTeams.size());
		for (int i = 0; i < finalTeams.size(); i++)
		{
			System.out.println(finalTeams.get(i).getPoints() + finalTeams.get(i).getTeamName());
		}
		ArrayList<Integer> winners = new ArrayList<>();
		
		//the team at the end of the list must have the highest score and is definitely a winner
		TeamData definiteWinnerObject = finalTeams.get(finalTeams.size() - 1);
		int definiteWinner = definiteWinnerObject.getTeam();
		System.out.println(definiteWinner);
		long max = definiteWinnerObject.getPoints();
		//if the max score is 0, we know that no one won
		if (max == 0) return winners;
		
		winners.add(definiteWinner);
		
		//check to see if there are other winners
		if (finalTeams.size() > 1){
			
			for (int i = finalTeams.size() -2; i > -1; i--){
				
				if (finalTeams.get(i).getPoints() == max){
					winners.add(finalTeams.get(i).getTeam());
				}
			}	
		}
		
		return winners;
	}
	
	class gameButton extends JButton
	{
		private static final long serialVersionUID = 1L;
		private Category category;
		private int pointValue;
		private QuestionAnswer question;
		public gameButton(String name)
		{
			super(name);
		}
		
		public void setCategory(Category category)
		{
			this.category = category;
		}
		
		public void setPointValue(int pointValue)
		{
			this.pointValue = pointValue;
		}
		
		public void setQuestion(QuestionAnswer question)
		{
			this.question = question;
		}
		
		public Category getCategory()
		{
			return category;
		}
		
		public Integer getPointValue()
		{
			return pointValue;
		}
		
		public QuestionAnswer getQuestion()
		{
			return question;
		}
	}
	
	private void finalJeopardy()
	{
		finalJeopardyPanel.removeAll();
		finalJeopardyPanel.validate();
		finalJeopardyPanel.repaint();
		GridBagConstraints bag = new GridBagConstraints();
		JPanel jeopardyLabelPanel = new JPanel();
		jeopardyLabelPanel.setBackground(Color.BLUE);
		finalJeopardyLabel.setForeground(Color.WHITE);
		finalJeopardyPanel.setBackground(Color.DARK_GRAY);
		finalJeopardyLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
		//jeopardyLabelPanel.setPreferredSize(new Dimension(600, 30));
		jeopardyLabelPanel.add(finalJeopardyLabel);
		bag.insets = new Insets(5, 5, 5, 5);
		bag.gridx = 0;
		bag.gridy = 0;
		bag.ipadx = 2;
		bag.weightx = 0.5;
		bag.gridwidth = 4;
		bag.weighty = 0.1;
		bag.fill = GridBagConstraints.BOTH;
		finalJeopardyPanel.add(jeopardyLabelPanel, bag);
		
		int n = 0;
		for (int i = 0; i < numberOfTeams; i++)
		{
			if (teamData.get(i).getPoints() > 0)
			{
				long value = teamData.get(i).getPoints();
				teamBetsSlider[i].setMaximum((int)value);
				teamBetsSlider[i].setMinimum(0); 
				teamBetsSlider[i].setValue(1);
				teamBetsSlider[i].setPaintLabels(true);
				teamBetsSlider[i].setPaintTicks(true);
				teamBetsSlider[i].setEnabled(true);
				teamBetsSlider[i].setBackground(Color.DARK_GRAY);
				teamBetsSlider[i].setLabelTable(teamBetsSlider[n].createStandardLabels((int)value/10));
				finalTeamNameLabel[i].setForeground(Color.WHITE);
				finalTeamNameLabel[i].setHorizontalAlignment(SwingConstants.CENTER);
				teamBetLabel[i].setForeground(Color.WHITE);
				teamBetLabel[i].setHorizontalTextPosition(SwingConstants.CENTER);
				bag.gridx = 0;
				bag.gridy = n + 1;
				bag.gridwidth = 1;
				bag.fill = GridBagConstraints.BOTH;
				finalJeopardyPanel.add(finalTeamNameLabel[i], bag);
				bag.gridx = 1;
				bag.gridy = n + 1;
				bag.ipadx = 20;
				bag.weightx = 1;
				finalJeopardyPanel.add(teamBetsSlider[i], bag);
				bag.gridx = 2;
				bag.ipadx = n + 1;
				bag.ipadx = 0;
				bag.weightx = 0.2;
				finalJeopardyPanel.add(teamBetLabel[i], bag);
				bag.gridx = 3;
				bag.gridy = n + 1;
				bag.weightx = 0.1;
				bag.fill = GridBagConstraints.NONE;
				betButtons[i].setEnabled(true);
				finalJeopardyPanel.add(betButtons[i], bag);
				
				HintText hintText = new HintText(finalAnswerText[i], teamData.get(i).getTeamName()
									+ ", enter your answer");
				
				n++;
			}
			
		}
		
		bag.gridx = 0;
		bag.gridy = n + 1;
		bag.gridwidth = 4;
		bag.weighty = 0.3;
		bag.fill = GridBagConstraints.BOTH;
		finalQuestionText.setEditable(false);
		finalQuestionText.setBackground(Color.CYAN);
		finalQuestionText.setForeground(Color.WHITE);
		finalQuestionText.setWrapStyleWord(true);
		finalQuestionText.setLineWrap(true);
		finalQuestionText.setFont(new Font("Times New Roman", Font.BOLD, 20));
		finalQuestionText.setText("And the question is...");
		
		finalJeopardyPanel.add(finalQuestionText, bag);
		
		JPanel finalAnswerPanel = new JPanel();
		finalAnswerPanel.setLayout(new GridBagLayout());
		
		int count = 0;
		for (int i = 0; i < numberOfTeams; i++)
		{
			if (teamData.get(i).getPoints() > 0)
			{
				bag.gridx = (count%2)*2;
				bag.gridy = count/2;
				bag.weightx = 1;
				bag.gridwidth = 1;
				bag.gridheight = 1;
				bag.ipadx = 30;
				bag.fill = GridBagConstraints.HORIZONTAL;
				finalAnswerPanel.add(finalAnswerText[i], bag);
				bag.gridx = (count%2)*2 + 1;
				bag.gridy = count/2;
				bag.weightx = 0.01;
				bag.ipadx = 0;
				finalAnswerPanel.add(finalSubmitButton[i], bag);
				count++;
			}
		}
		
		bag.gridx = 0;
		bag.gridy = n + 2;
		bag.weighty = 0.8;
		bag.gridwidth = 4;
		bag.fill = GridBagConstraints.BOTH;
		finalJeopardyPanel.add(finalAnswerPanel, bag);
		
		
		add(finalJeopardyPanel, BorderLayout.CENTER);
	}
	
	private void resetData()
	{
		whoseTurn = -1;
		numberOfChosenQuestions = 0;
		//total points for each team, each TeamPoints object holds team index, team points, and team name
		for (TeamData team : teamData){
			team.setPoints(0);
		}
		
		for (int i = 0; i<5; i++){
			
			for (int j = 0; j<5; j++){
				questions[i][j].resetHasBeenAsked();
				questionButton[j][i].setEnabled(true);
				questionButton[j][i].setBackground(Color.DARK_GRAY);
			}
		}
	}
	
	class HintText implements FocusListener, DocumentListener, PropertyChangeListener{
		private String hint;
		private JTextField text;
		private boolean isEmpty;
		private Color foreground;
		
		public HintText(final JTextField text, String hint) {
            super();
            this.text = text;
            this.hint = hint;
            isEmpty = true;
            foreground = text.getForeground();
            text.addFocusListener(this);
            text.getDocument().addDocumentListener(this);
            text.addPropertyChangeListener("foreground", this);
            if (!this.text.hasFocus()) {
                focusLost(null);
            }
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (isEmpty) {
            	text.getDocument().removeDocumentListener(this);
                text.removePropertyChangeListener("foreground", this);
                text.setText("");
                text.setForeground(foreground);
            	text.getDocument().addDocumentListener(this);
                text.addPropertyChangeListener("foreground", this);
            }

        }

        @Override
        public void focusLost(FocusEvent e) {
            if (isEmpty) {
            	text.getDocument().removeDocumentListener(this);
                text.removePropertyChangeListener("foreground", this);
                text.setText(hint);
                text.setForeground(Color.GRAY);
                text.addFocusListener(this);
                text.getDocument().addDocumentListener(this);
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
        	update();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        	update();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
        	update();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
        	update();
        }
        
        private void update()
        {
        	if (!this.text.hasFocus()) {
                focusLost(null);
            }
        	isEmpty = (text.getText() == "");
        }
		
	}
	
}
