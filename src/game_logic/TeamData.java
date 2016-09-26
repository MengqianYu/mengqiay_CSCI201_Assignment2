package game_logic;

import java.util.Comparator;

public class TeamData {
	private Integer team;
	private Long totalPoints;
	private String teamName;
	private Long bet;
	
	public TeamData(Integer team, Long totalPoints, String teamName){
		this.team = team;
		this.totalPoints = totalPoints;
		this.teamName = teamName;
	}
	
	public void addPoints(long points){
		totalPoints += points;
	}
	
	public void deductPoints(long points){
		totalPoints -= points;
	}
	
	public void setBet(long bet){
		this.bet = bet;
	}
	
	public Long getBet(){
		return bet;
	}
	
	public String getTeamName(){
		return teamName;
	}
	
	public Integer getTeam(){
		return team;
	}
	
	public Long getPoints(){
		return totalPoints;
	}
	
	public void setPoints(long points){
		this.totalPoints = points;
	}
	
	public static PointComparator getComparator(){
		return new PointComparator();
	}
	
	private static class PointComparator implements Comparator<TeamData>{

		@Override
		public int compare(TeamData teamOne, TeamData teamTwo) {
			return teamOne.getPoints().compareTo(teamTwo.getPoints());
		}
		
	}
}
