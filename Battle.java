import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        private JFrame window;
	private GamePanel gamePanel;
	private Timer timer;

	public Dude[] Heroes;
	public Dude[] Foes;
	private int readinessThreshold = 20;
	private ArrayList<Integer> readyDudes;
	private int currentDudesTurn; //Which dude's turn it is

	private int foesSelectedAttack;
	private int foesTarget;
	private String notification;
	
	public Battle() {
		Heroes = new Dude[] { new Dude("Warrior", 0), new Dude("Ranger", 1), new Dude("Mage", 2), new Dude("Priest", 3) };
		Foes = new Dude[] { new Dude("Warrior", 0), new Dude("Ranger", 1), new Dude("Mage", 2), new Dude("Priest", 3) };
		readyDudes = new ArrayList<Integer>();
		
		window = new JFrame("Dude Battle");
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
			for(int i = 0; i < 4; i++) {
				if(Heroes[i] != null) {
					Heroes[i].setReadiness(Heroes[i].getReadiness() + Heroes[i].getSpeed());
					if(Heroes[i].getReadiness() > readinessThreshold) {
						readyDudes.add(i);
					}
				}
				if(Foes[i] != null) {
					Foes[i].setReadiness(Foes[i].getReadiness() + Foes[i].getSpeed());
					if(Foes[i].getReadiness() > readinessThreshold) {
						readyDudes.add(i + 4);	// Need a way to distinguish heroes and foes
					}				
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
				if(i < 4) {
					dude = Heroes[i];
				}
				else {
					dude = Foes[i - 4];
				}			
				
				if(dude != null && dude.getReadiness() > largestReadiness) {
					largestReadiness = dude.getReadiness();
					readiestIndex = i;
					if(evenReadierDudes.size() > 0) {
						evenReadierDudes = new ArrayList<Integer>();
					}
				}
				else if(dude != null && dude.getReadiness() == largestReadiness) {
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
					if(i < 4) {
						dude = Heroes[i];
					}
					else {
						dude = Foes[i - 4];
					}
					
					if(dude != null && dude.getSpeed() > largestSpeed) {
						largestSpeed = dude.getSpeed();
						speediestIndex = i;
					}
					else if(dude != null && dude.getSpeed() == largestSpeed) { // by this point, I wanna die
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
		if(currentDudesTurn < 4) {
                    Heroes[currentDudesTurn].setReadiness(Heroes[currentDudesTurn].getReadiness() - readinessThreshold);

                    boolean stun = checkStun();
                    boolean blood = checkBlood();
                    boolean poison = checkPoison();

                    if(stun || blood || poison)
                        drawStatusNotifications(stun, blood, poison);

                    
                //Prevent Hero from attacking if stunned    
                    if(stun){
                        BattleLoop();
                    }else{
                        gamePanel.setHeroTurn(currentDudesTurn);
                    }
		}
		else {
                    Foes[currentDudesTurn - 4].setReadiness(Foes[currentDudesTurn - 4].getReadiness() - readinessThreshold);

                    boolean stun = checkStun();
                    boolean blood = checkBlood();
                    boolean poison = checkPoison();

                    if(stun || blood || poison)
                        drawStatusNotifications(stun, blood, poison);

                    
                //Prevent Foe from attacking if stunned    
                    if(stun){
                        BattleLoop();
                    }else{
                        PrepareFoesTurn();
                        selectFoe();
                    }
		}
	}

	/// Step one of foe's turn
	/// Sets up the foe's attack
	public void PrepareFoesTurn() {
		int strongestAttack = 0;
		int strongestAttackIndex = 0;
		Dude attacker = Foes[currentDudesTurn - 4];

		for(int i = 1; i <= 4; i++) {
			if(attacker.getAttack(i).getDamage() > strongestAttack) {
				strongestAttack = attacker.getAttack(i).getDamage();
				strongestAttackIndex = i;
			}
		}

		foesSelectedAttack = strongestAttackIndex;
	}

	/// Step two of the foe's turn
	/// Waits one second, then selects the foe who will attack
	public void selectFoe() {
		ActionListener listener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				gamePanel.setFoeTurn(currentDudesTurn - 4);
				setFoesAttack();
			}
		};
		timer = new Timer(1000, listener);
		timer.setRepeats(false);
		timer.start();
	}

	/// Step three of the foe's turn
	/// Waits one second, then selects the foe's attack it will use
	public void setFoesAttack() {
		ActionListener listener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				System.out.println("===============\nSelecting attack #" + foesSelectedAttack + "\n=======================");
				gamePanel.setFoesAttack(foesSelectedAttack - 1);
				setFoesTarget();
			}
		};
		timer = new Timer(1000, listener);
		timer.setRepeats(false);
		timer.start();
	}

	/// Step four of the foe's turn
	/// Waits one second, then selects the foe's target of its attack
	public void setFoesTarget() {
		ActionListener listener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				gamePanel.setFoesTarget(0);
				performFoesAttack();
			}
		};
		timer = new Timer(1000, listener);
		timer.setRepeats(false);
		timer.start();
	}

	/// Step five of the foe's turn
	/// Waits one second, then performs the attack
	public void performFoesAttack() {
		ActionListener listener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				ProcessAttack(0, foesSelectedAttack);
			}
		};
		timer = new Timer(1000, listener);
		timer.setRepeats(false);
		timer.start();
	}

	/// Step six of foe's turn (Step five is performing the attack)
	/// Waits two seconds, then checks the battle and proceeds
	public void drawNotifications() {
		gamePanel.setNotification(notification);
		ActionListener listener = new ActionListener(){
			public void actionPerformed(ActionEvent event){
				CheckBattle();
			}
		};
		timer = new Timer(2000, listener);
		timer.setRepeats(false);
		timer.start();
	}
        
        ///Take care of status conditions
        public void drawStatusNotifications(boolean Stun, boolean Blood, boolean Poison){
            final StringBuffer message = new StringBuffer();
            if(Stun)
                message.append("Stunned!\n");
            if(Blood)
                message.append("Bleeding!\n");
            if(Poison)
                message.append("Poisoned!");
            
            ActionListener listener = new ActionListener(){
                public void actionPerformed(ActionEvent event){
                    gamePanel.statusUpdate = true;
                    gamePanel.setStatusNotification(message.toString(), currentDudesTurn);
                    gamePanel.statusUpdate = false;
                }
            };
            timer = new Timer(2000, listener);
            timer.setRepeats(false);
            timer.start();
        }
	
	public void ProcessAttack(int selectedTarget, int selectedAttack) {
		Dude attacker, target;
		Attack attack;
		if(currentDudesTurn < 4) {
			attacker = Heroes[currentDudesTurn];
			target = Foes[selectedTarget];
		}
		else {
			attacker = Foes[currentDudesTurn - 4];
			target = Heroes[selectedTarget];
		}
		
                
                //Initiate Attack
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
                
		this.drawNotifications();
	}
	
	public void PerformAttack(int selectedTarget, Dude attacker, Dude target, Attack attack) {
		if(!doesAttackHit(attack.getAccuracy())) {
			if(currentDudesTurn < 4) {
				System.out.println("Your " + attacker.getName() + "\'s " + attack.getName() + " missed!");
			}
			else {
				System.out.println("Enemy " + attacker.getName() + "\'s " + attack.getName() + " missed!");
			}
			this.notification = "Missed!";
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
				if(currentDudesTurn< 4) {
					System.out.println("Your " + attacker.getName() + "\'s " + attack.getName() + " hit " + target.getName() + " and dealt " + damageDealt + " damage.");
				}
				else {
					System.out.println("Enemy " + attacker.getName() + "\'s " + attack.getName() + " hit " + target.getName() + " and dealt " + damageDealt + " damage.");
				}
				if(target.getHP() < 0) { target.setHP(0); }
				this.notification = "Hit!\n" + damageDealt + "dmg";
			}
			
			if(attack.causesBleeding()) { target.setBleeding(true); }
			if(attack.Stuns()) { target.setStunned(true); }
			if(attack.Poisons()) { target.setPoisoned(true); }
			if(attack.knocksBack()) { 
				int squaresToMove = attack.getKnockBackSpaces();
				while(squaresToMove > 0) {
					if(selectedTarget < 4) {
						Dude temp = Heroes[selectedTarget + 1];
						Heroes[selectedTarget + 1] = Heroes[selectedTarget];
						Heroes[selectedTarget] = temp;
					}
					else {
						Dude temp = Foes[selectedTarget - 4 + 1];
						Foes[selectedTarget - 4 - 1] = Foes[selectedTarget];
						Foes[selectedTarget] = temp;
					}
					squaresToMove--;
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
			if(Heroes[i] != null && Heroes[i].getHP() <= 0) {
				readyDudes.remove(new Integer(i));
				for(int j = i; j < 3; j++) {
					for(int k = 0; k < readyDudes.size(); k++) {
						if(j  == readyDudes.get(k)) {
							readyDudes.remove(new Integer(j));
							readyDudes.add(j - 1);
						}
					}
					Heroes[j] = Heroes[j + 1];
				}
				Heroes[3] = null;				
			}
		}		
		
		for(int i = 0; i < Foes.length; i++) {
			if(Foes[i] != null && Foes[i].getHP() <= 0) {
				readyDudes.remove(new Integer(i + 4));
				for(int j = i; j < 3; j++) {
					for(int k = 0; k < readyDudes.size(); k++) {
						if(j + 4 == readyDudes.get(k)) {
							readyDudes.remove(new Integer(j + 4));
							readyDudes.add(j + 3);
						}
					}
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
		int accuracyResult = randomInRange(0, 100);
		if(accuracyResult <= accuracy) {
			return true;
		}
		return false;
	}
        
        public boolean checkBlood(){
        //Figure out whether Hero or Enemy is attacking
            String attackerLoyalty = (currentDudesTurn < 4)?("Your "):("Enemy ");
            Dude attacker = (currentDudesTurn < 4)?(Heroes[currentDudesTurn]):(Foes[currentDudesTurn - 4]);
            
        //Inflict Bleeding Damage
            if(attacker.isBleeding()){
                    attacker.BleedBabyBleed();

                    String message = attackerLoyalty + attacker.getName() + " has lost blood!";
                    System.out.println(message);
                    return true;
            }
            return false;
        }
        
        public boolean checkPoison(){
            //Figure out whether Hero or Enemy is attacking
            String attackerLoyalty = (currentDudesTurn < 4)?("Your "):("Enemy ");
            Dude attacker = (currentDudesTurn < 4)?(Heroes[currentDudesTurn]):(Foes[currentDudesTurn - 4]);
            
            //Inflict Poison Damage
            if(attacker.isPoisoned()){
                    attacker.FeelingVenomenal();

                    String message = attackerLoyalty + attacker.getName() + " is hurt by poison!";
                    System.out.println(message);
                    return true;
            }
            return false;
        }
        
        public boolean checkStun(){
            //Figure out whether Hero or Enemy is attacking
            String attackerLoyalty = (currentDudesTurn < 4)?("Your "):("Enemy ");
            Dude attacker = (currentDudesTurn < 4)?(Heroes[currentDudesTurn]):(Foes[currentDudesTurn - 4]);
            
            //Skip turn
            if(attacker.isStunned()){
                    attacker.WheelchairBound();

                    String message = attackerLoyalty + attacker.getName() + " is stunned and cannot move!";
                    System.out.println(message);
                    return true;
            }
            return false;
        }
	
	public int randomInRange(int min, int max) {
		HighQualityRandom random = new HighQualityRandom(System.currentTimeMillis() * (max + 5));
		return (random.next(25) % (max - min + 1) + min);
	}
	
	public static void main(String args[]) {
		Battle battle = new Battle();
	}
}
