import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.awt.*;

/* Run this one
 * Battle Procedure:
 * 1. BattleLoop determines which hero's turn it is
 * 2. BattleLoop calls StartTurn, which tells the GamePanel who's turn it is
 * 3. In the GamePanel, User selects their attack and who they're targeting
 * 4. After clicking the Attack! button, GamePanel calls Battle.PerformAttack
 * 5. Battle.CheckBattle() checks if anybody has died or if the battle is over
 * 6. go to step 1
 */

public class Battle {
	private GamePanel gamePanel;	

	public Dude[] Heroes;
	public Dude[] Foes;
	private int readinessThreshold = 20;
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
		this.gamePanel = new GamePanel(this);
		this.gamePanel.setSize(800, 600);
		window.add(gamePanel);
		window.setResizable(false);
		window.setVisible(true);
		
		BattleLoop();
	}
	
	/// The main game loop
	/// The goal of this method is to select which hero or foe's turn it is to attack	
	public void BattleLoop() {
		if(readyDudes.size() == 0) { // Nobody is ready to attack
			System.out.println("===============");
			for(int i = 0; i < 4; i++) {
				Heroes[i].setReadiness(Heroes[i].getReadiness() + Heroes[i].getSpeed());
				System.out.println(Heroes[i].getName() + ": " + Heroes[i].getReadiness());
				if(Heroes[i].getReadiness() > readinessThreshold) {
					readyDudes.add(i);
				}
				
				Foes[i].setReadiness(Foes[i].getReadiness() + Foes[i].getSpeed());
				System.out.println("Foe " + Heroes[i].getName() + ": " + Heroes[i].getReadiness());
				if(Foes[i].getReadiness() > readinessThreshold) {
					readyDudes.add(i * -1);	// Need a way to distinguish heroes and foes
				}				
			}
			BattleLoop();
		}
		else if(readyDudes.size() == 1) {
			currentDudesTurn = readyDudes.get(0);
			readyDudes = new ArrayList<Integer>();
			StartTurn();
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
						System.out.println("Dude's Turn: " + currentDudesTurn);
						readyDudes.remove(new Integer(currentDudesTurn));
						StartTurn();
						return;
					}
				}
				currentDudesTurn = speediestIndex;
				readyDudes.remove(new Integer(speediestIndex));
				StartTurn();
			}
			else {
				currentDudesTurn = readiestIndex;
				readyDudes.remove(new Integer(readiestIndex));
				StartTurn();
			}
		}
	}
	
	public void StartTurn() {
		if(currentDudesTurn > 0) {
			gamePanel.setHeroTurn(currentDudesTurn);
		}
		else {
			gamePanel.setFoeTurn(currentDudesTurn * -1);
		}
	}
	
	public void ProcessAttack(int selectedTarget, int selectedAttack) {
		Dude attacker, target;
		Attack attack;
		if(currentDudesTurn > -1) {
			attacker = Heroes[currentDudesTurn];
			target = Foes[selectedTarget];
		}
		else {
			attacker = Foes[currentDudesTurn * -1];
			target = Heroes[selectedTarget];
		}
		
		attack = attacker.getAttack(selectedAttack);
		if(attack.targetsAll()) {
			for(int i = 0; i < Foes.length; i++) {
				if(attack.isValidTarget(i) && Foes[i] != null) {
					PerformAttack(i, attacker, Foes[i], attack);
				}
			}
		}
		else {
			PerformAttack(selectedTarget, attacker, target, attack);
		}
		
		gamePanel.updateUI();
		CheckBattle();
	}
	
	public void PerformAttack(int selectedTarget, Dude attacker, Dude target, Attack attack) {
		if(!doesAttackHit(attack.getAccuracy())) {
			System.out.println("The attack missed!");
		}
		else {
			int minAttackDamage = (int) Math.ceil(attacker.getMinDamage() * (attack.getDamage() / 100.0));
			int maxAttackDamage = (int) Math.ceil(attacker.getMaxDamage() * (attack.getDamage() / 100.0));
			int damageDealt = randomInRange(minAttackDamage, maxAttackDamage);
			
			if(attack.Heals()) {
				target.setHP(target.getHP() + damageDealt);
				System.out.println(attack.getName() + " healed " + damageDealt + " HP.");
				if(target.getHP() > target.getMaxHP()) { target.setHP(target.getMaxHP()); }
			}
			else {
				target.setHP(target.getHP() - damageDealt);
				System.out.println(attack.getName() + " hit " + target.getName() + " and dealt " + damageDealt + " damage.");
				if(target.getHP() < 0) { target.setHP(0); }
			}
			
			if(attack.causesBleeding()) { target.setBleeding(true); }
			if(attack.Stuns()) { target.setStunned(true); }
			if(attack.Poisons()) { target.setPoisoned(true); }
			if(attack.knocksBack()) { 
				int squaresToMove = attack.getKnockBackSpaces();
				while(squaresToMove > 0) {
					if(selectedTarget > -1) {
						Dude temp = Heroes[selectedTarget + 1];
						Heroes[selectedTarget + 1] = Heroes[selectedTarget];
						Heroes[selectedTarget] = temp;
					}
					else {
						Dude temp = Foes[selectedTarget + 1];
						Foes[selectedTarget - 1] = Foes[selectedTarget];
						Foes[selectedTarget] = temp;
					}
				}
			}		
		}
	}
	
	public void CheckBattle() {
		if(!(HeroesLive() && FoesLive())) {
			if(HeroesLive()) {
				System.out.println("You've won!");
				return;
			}
			else {
				System.out.println("You lose, nerd");
				return;
			}
		}
		
		for(int i = 0; i < Heroes.length; i++) {
			if(Heroes[i] != null && Heroes[i].getHP() == 0) {
				int j;
				for(j = i; j <= 3; j++) {
					Heroes[j] = Heroes[j + 1];
				}
				Heroes[3] = null;				
			}
		}
		
		
		for(int i = 0; i < Foes.length; i++) {
			if(Foes[i] != null && Foes[i].getHP() == 0) {
				for(int j = i; j < 3; j++) {
					Foes[j] = Foes[j + 1];
				}
				Foes[3] = null;
			}			
		}
		BattleLoop();
	}
	
	// Returns true if there are any heroes still alive. 
	// Used to determine whether the battle is over, and who has won.
	public boolean HeroesLive() {
		for(int i = 0; i < 4; i++) {
			if(Heroes[i] != null) {
				return true;
			}
		}
		return false;
	}
	
	// Returns true if there are any foes still alive. 
	// Used to determine whether the battle is over, and who has won.
	public boolean FoesLive() {
		for(int i = 0; i < 4; i++) {
			if(Foes[i] != null) {
				return true;
			}
		}
		return false;
	}
	
	public boolean doesAttackHit(int accuracy) {
		Random random = new Random(System.currentTimeMillis() * (accuracy));
		int accuracyResult = randomInRange(0, 100);
		if(accuracyResult <= accuracy) {
			return true;
		}
		return false;
	}
	
	public int randomInRange(int min, int max) {
		Random random = new Random(System.currentTimeMillis() * (max + 5)); // Trying a good random seed... I hope
		return (random.nextInt((max - min) + 1) + min);
	}
	
	public static void main(String args[]) {
		Battle battle = new Battle();
	}
}
