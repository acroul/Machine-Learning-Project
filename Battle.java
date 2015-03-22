import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.awt.*;

// Run this one
public class Battle {
	private JPanel GamePanel;	

	public Dude[] Heroes;
	public Dude[] Foes;
	private int readinessThreshold = 100;
	private ArrayList<Integer> readyDudes;
	private int currentDudesTurn; //Which dude's turn it is
	
	public Battle() {
		Heroes = new Dude[] { new Dude("Warrior", 0), new Dude("Ranger", 1), new Dude("Mage", 2), new Dude("Priest", 3) };
		Foes = new Dude[] { new Dude("Warrior", 0), new Dude("Ranger", 1), new Dude("Mage", 2), new Dude("Priest", 3) };
		readyDudes = new ArrayList<Integer>();
		
		JFrame window = new JFrame("Dude Battle");
		window.setBounds(0, 0, 800, 600);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.GamePanel = new GamePanel(this);
		this.GamePanel.setSize(800, 600);
		window.add(GamePanel);
		window.setResizable(false);
		window.setVisible(true);
	}
	
	/// The main game loop
	/// The goal of this method is to select which hero or foe's turn it is to attack	
	public void BattleLoop() {
		if(readyDudes.size() == 0) { // Nobody is ready to attack
			for(int i = 0; i < 4; i++) {
				Heroes[i].setReadiness(Heroes[i].getReadiness() + Heroes[i].getSpeed());
				if(Heroes[i].getReadiness() > readinessThreshold) {
					readyDudes.add(i);
				}
				
				Foes[i].setReadiness(Foes[i].getReadiness() + Foes[i].getSpeed());
				if(Foes[i].getReadiness() > readinessThreshold) {
					readyDudes.add(i * -1);	// Need a way to distinguish heroes and foes
				}				
			}
			BattleLoop();
		}
		else if(readyDudes.size() == 1) {
			currentDudesTurn = readyDudes.get(0);
			readyDudes = new ArrayList<Integer>();
		}
		else {
			ArrayList<Integer> evenReadierDudes = new ArrayList<Integer>(); // for the cases where two or more dudes have the same readiness
			int largestReadiness = -1;
			int readiestIndex = 0;
			Dude dude;
			for(int i : readyDudes) {
				if(i >= 0) {
					dude = Heroes[i];
				}
				else {
					dude = Foes[i * -1];
				}			
				
				if(dude.getReadiness() > largestReadiness) {
					largestReadiness = dude.getReadiness();
					readiestIndex = i;
					if(evenReadierDudes.size() > 0) {
						evenReadierDudes = new ArrayList<Integer>();
					}
				}
				else if(dude.getReadiness() == largestReadiness) {
					if(!evenReadierDudes.contains(readiestIndex)) {
						evenReadierDudes.add(readiestIndex);
						evenReadierDudes.add(i);
					}
				}
			}
			if(evenReadierDudes.size() > 0) {
				int largestSpeed = 0;
				int speediestIndex = 0;
				for(int i : evenReadierDudes) {
					if(i >= 0) {
						dude = Heroes[i];
					}
					else {
						dude = Foes[i * -1];
					}
					
					if(dude.getSpeed() > largestSpeed) {
						largestSpeed = dude.getSpeed();
						speediestIndex = i;
					}
					else if(dude.getSpeed() == largestSpeed) { // by this point, I wanna die
						Random random = new Random(System.currentTimeMillis());
						currentDudesTurn = evenReadierDudes.get(random.nextInt((evenReadierDudes.size() - 1) + 1));
						readyDudes.remove(new Integer(currentDudesTurn));
					}
				}
			}
			else {
				currentDudesTurn = readiestIndex;
				readyDudes.remove(new Integer(readiestIndex));
			}
		}
	}
	
	// Returns true if there are any heroes still alive. 
	// Used to determine whether the battle is over, and who has won.
	public boolean HeroesLive() {
		for(int i = 0; i < 4; i++) {
			if(Heroes[i].getHP() > 0) {
				return true;
			}
		}
		return false;
	}
	
	// Returns true if there are any foes still alive. 
	// Used to determine whether the battle is over, and who has won.
	public boolean FoesLive() {
		for(int i = 0; i < 4; i++) {
			if(Foes[i].getHP() > 0) {
				return true;
			}
		}
		return false;
	}
	
	public static void main(String args[]) {
		Battle battle = new Battle();
	}
}
