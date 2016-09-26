package game_logic;

import java.util.List;

public class Output {
	//print to the console the scores in a pretty format
	public static void printScores(int numberOfTeams, List<TeamData> teamData){
		
		System.out.println("Here are the updated scores: ");
		System.out.println();
		
		for (int i = 0; i<numberOfTeams; i++){
			
			System.out.print(teamData.get(i).getTeamName());
			System.out.print(" : ");
			System.out.println(teamData.get(i).getPoints());
		}
		
		System.out.println();
	}
		
}
