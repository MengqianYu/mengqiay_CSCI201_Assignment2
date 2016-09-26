package game_logic;

public class Category {
	private String category;
	private int index;
	
	public Category(String category, int index){
		this.category = category;
		this.index = index;
	}
	
	public String getCategory(){
		return category;
	}
	
	public int getIndex(){
		return index;
	}
}
