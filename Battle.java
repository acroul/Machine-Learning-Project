import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

public class Battle {
	private JPanel GamePanel;	

	public Dude[] Heroes;
	public Dude[] Foes;
	private ArrayList<Dude> readyDudes;
	
	public Battle() {
		Heroes = new Dude[] { new Dude("Warrior", 0), new Dude("Ranger", 1), new Dude("Mage", 2), new Dude("Priest", 3) };
		Foes = new Dude[] { new Dude("Warrior", 0), new Dude("Ranger", 1), new Dude("Mage", 2), new Dude("Priest", 3) };
		readyDudes = new ArrayList<Dude>();
		
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
	/// Returns: True if the player has won, false if lost	
	public boolean BattleLoop() {
		do {
			
		}while(HeroesLive() && FoesLive());
		
		return HeroesLive(); 
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
