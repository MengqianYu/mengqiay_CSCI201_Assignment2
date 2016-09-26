package main;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import game_GUI.MainPlay_Screen;
import game_GUI.Welcome_Screen;

public class Main {
	
	public static void main (String [] args){

		//new GamePlay(args[0]);
		Welcome_Screen welcome = new Welcome_Screen();
		welcome.setVisible(true);
		//MainPlay_Screen mainplay = new MainPlay_Screen();
		//mainplay.setVisible(true);
	}
}
