package game_logic;

public class QuestionAnswer {
	
	private String question;
	private String answer;
	private Boolean asked;
	
	public QuestionAnswer(String question, String answer){
		this.question = question;
		this.answer = answer;
		asked = false;
	}
	
	public String getQuestion(){
		return question;
	}
	
	public Boolean hasBeenAsked(){
		return asked;
	}
	
	public void setHasBeenAsked(){
		asked = true;
	}
	
	public void resetHasBeenAsked(){
		asked = false;
	}
	
	public String getAnswer(){
		return answer;
	}

}
