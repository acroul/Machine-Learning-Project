import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class GamePanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = 1L; // idfk, Eclipse said that I should have one of these
	private Rectangle[] heroSquares;
	private Rectangle[] foeSquares;
	private Rectangle[] attackSquares;
	private Rectangle attackButton;
	private Rectangle highlightedHeroSquare;
	private Rectangle highlightedFoeSquare;
	private int selectedDude;
	private int selectedAttack;
	private int selectedFoe;
	private int foesTarget;
	private boolean attackButtonClick = false;
	private boolean isHerosTurn = false;
	private boolean isFoesTurn = false;
	
	private Battle battle;

	public GamePanel(Battle battle) {
		this.battle = battle;
		this.addMouseListener(this);
		selectedDude = -1;
		selectedAttack = -1;
	}
	
	public void updateUI() {
		repaint();
	}
	
//==========================
//BATTLE FLOW LOGIC
//==========================
	
	public void setHeroTurn(int dudeIndex) {
		this.isHerosTurn = true;
		this.isFoesTurn = false;
		this.selectedFoe = -1;
		this.selectedDude = dudeIndex;
		repaint();
	}
	
	public void setFoeTurn(int dudeIndex) {
		this.isFoesTurn = true;
		this.isHerosTurn = false;
		this.selectedFoe = dudeIndex;
		this.selectedDude = -1;
		repaint();
	}
	
	public void setFoesTarget(int dudeIndex) {
		this.foesTarget = dudeIndex;
		repaint();
	}
	
//==========================
//DRAW LOGIC
//==========================
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.fillRect(0, 0, 800, 600);
		g.setColor(Color.WHITE);
		Graphics2D g2 = (Graphics2D)g;
		
		// Draw Dude Squares
		drawDudes(g2);
		if(selectedDude != -1 || selectedFoe != -1) {
			selectDude(g2);
			drawAttacks(g2);
			if(selectedAttack != -1) {
				selectAttack(g2);
			}
		}
		
		// Draw Attack Button
		if(attackButtonClick) {
			g2.setColor(Color.RED);
		}
		else {
			g2.setColor(Color.WHITE);
		}
		attackButton = new Rectangle(300, 500, 200, 50);
		g2.drawRect(300, 500, 200, 50);
		g2.setFont(new Font("Arial", Font.BOLD, 16));
		g2.drawString("Attack!", 375, 530);
	}
	
	public void selectDude(Graphics2D g2) {
		if(selectedDude != -1) {
			g2.setColor(Color.GREEN);
			g2.draw(this.heroSquares[selectedDude]);
		}
		if(selectedFoe != -1) {
			g2.setColor(Color.RED);
			g2.draw(this.foeSquares[selectedFoe]);
		}
		g2.setColor(Color.WHITE);
	}
	
	public void selectAttack(Graphics2D g2) {
		Attack attack = battle.Heroes[selectedDude].getAttack(selectedAttack + 1);
		int startX = heroSquares[3].x;
		int startY = heroSquares[0].y + heroSquares[0].height + 15;
		int highlightRectWidth = foeSquares[0].width;
		int highlightRectHeight = 20;
		int highlightRectPadding = 10;
		Color color = g2.getColor();
		
		g2.setColor(Color.RED);
		g2.draw(attackSquares[selectedAttack]);
		
		g2.setColor(Color.GREEN);
		for(int i = 3; i >= 0; i--) {
			if(attack.isValidAttackPosition(i)) {
				g2.drawRect(startX, startY, highlightRectWidth, highlightRectHeight);
			}
			startX += highlightRectWidth + highlightRectPadding;
		}
		
		if(attack.targetsTeam()) {			
			startX = heroSquares[3].x;
			g2.setColor(Color.GREEN);
		} 
		else {
			startX = foeSquares[0].x;
			g2.setColor(Color.RED);
		}
		
		for(int i = 0; i < 4; i++) {
			if(attack.isValidTarget(i) || attack.targetsTeam()) {
				g2.fillRect(startX, startY, highlightRectWidth, highlightRectHeight);
				if(attack.targetsAll()) {
					g2.draw(foeSquares[i]);
					if(i < 3 && attack.isValidTarget(i + 1)) {
						g2.fillRect(startX + highlightRectWidth, startY + 8, 10, 4);
					}
				}
			}
			startX += highlightRectWidth + highlightRectPadding;
		}
		g2.setColor(color);
	}
	
	public void drawDudes(Graphics2D g2) {
		// Draw the heroes
		int startX = 20;
		int startY = 20;
		int dudeInfoBoxSize = 80; // length and width
		int dudeInfoBoxPadding = 10;
		g2.setStroke(new BasicStroke(2));
		heroSquares = new Rectangle[4];
		
		Dude[] heroes = battle.Heroes;
		for(int i = 3; i >= 0; i--) {
			if(heroes[i].getHP() > 0) {
				heroSquares[i] = new Rectangle(startX, startY, dudeInfoBoxSize, dudeInfoBoxSize);
				g2.drawRect(startX, startY, dudeInfoBoxSize, dudeInfoBoxSize);
				
				g2.setFont(new Font("Arial", Font.PLAIN, 16));
				g2.drawString(heroes[i].getName(), startX + 5, startY + 15);
				
				g2.setFont(new Font("Arial", Font.PLAIN, 12));				
				g2.drawString("HP: " + heroes[i].getHP() + "/" + heroes[i].getMaxHP(), startX + 10, startY + 30);
				g2.drawString("DMG: " + heroes[i].getMinDamage() + "-" + heroes[i].getMaxDamage(), startX + 10, startY + 45);
				g2.drawString("SPD: " + heroes[i].getSpeed(), startX + 10, startY + 60);
			}
			startX += dudeInfoBoxSize + dudeInfoBoxPadding;
		}
		
		startX += 40;
		Dude[] foes = battle.Foes;
		foeSquares = new Rectangle[4];
		for(int i = 0; i < 4; i++) {
			if(foes[i].getHP() > 0) {
				foeSquares[i] = new Rectangle(startX, startY, dudeInfoBoxSize, dudeInfoBoxSize);
				g2.drawRect(startX, startY, dudeInfoBoxSize, dudeInfoBoxSize);
				g2.setFont(new Font("Arial", Font.PLAIN, 16));
				g2.drawString(foes[i].getName(), startX + 5, startY + 15);
				g2.setFont(new Font("Arial", Font.PLAIN, 12));
				g2.drawString("HP: " + foes[i].getHP() + "/" + heroes[i].getMaxHP(), startX + 10, startY + 30);
				g2.drawString("DMG: " + foes[i].getMinDamage() + "-" + foes[i].getMaxDamage(), startX + 10, startY + 45);
				g2.drawString("SPD: " + foes[i].getSpeed(), startX + 10, startY + 60);
			}
			startX += dudeInfoBoxSize + dudeInfoBoxPadding;
		}
	}
	
	public void drawAttacks(Graphics2D g2) {
		int startX = 20;
		int dudeIndex;
		Dude dude;
		if(selectedDude != -1) {
			dude = battle.Heroes[selectedDude];
			dudeIndex = selectedDude;
			startX = 20;
		}
		else {
			dude = battle.Foes[selectedFoe];
			dudeIndex = selectedFoe;
			startX = foeSquares[0].x;
		}
		int startY = 160;
		int attackBoxWidth = 170;
		int attackBoxHeight = 60;
		int attackBoxPadding = 10;
		
		this.attackSquares = new Rectangle[4];
		
		for(int i = 0; i < 4; i++) {
			Attack attack = dude.getAttack(i + 1);
			Rectangle rect = new Rectangle(startX, startY, attackBoxWidth, attackBoxHeight);
			attackSquares[i] = rect;
			
			if(attack.isValidAttackPosition(dudeIndex)) {
				g2.setColor(Color.WHITE);
			}
			else {
				g2.setColor(Color.GRAY);
			}
			
			g2.draw(rect);
			
			g2.drawString(attack.getName(), startX + 5, startY + 15);
			g2.drawString("DMG: " + attack.getDamage() + "%", startX + 10, startY + 30);
			g2.drawString("ACC: " + attack.getAccuracy() + "%", startX + 80, startY + 30);
			
			Color color = g2.getColor();
			if(attack.causesBleeding()) {
				g2.setColor(Color.RED);
				g2.drawString("BLD", startX + 10, startY + 45);
				g2.setColor(color);
			}
			if(attack.Poisons()) {
				g2.setColor(Color.GREEN);
				g2.drawString("PSN", startX + 35, startY + 45);
				g2.setColor(color);
			}
			if(attack.Stuns()) {
				g2.setColor(Color.YELLOW);
				g2.drawString("STUN", startX + 70, startY + 45);
				g2.setColor(color);
			}
			if(attack.knocksBack()) {
				g2.setColor(Color.CYAN);
				g2.drawString("KB " + attack.getKnockBackSpaces(), startX + 110, startY + 45);
				g2.setColor(color);
			}
			
			// Increment pixels			
			if(i == 1) {
				startY += attackBoxHeight + attackBoxPadding;
				startX -= attackBoxWidth + attackBoxPadding;
			}
			else {
				startX += attackBoxWidth + attackBoxPadding;
			}
			g2.setColor(Color.WHITE);
		}
	}
	
	public void mouseClicked(MouseEvent e) {

	}
	
	public void mouseEntered(MouseEvent e) {
		
	}
	
	public void mouseExited(MouseEvent e) {}
	
	public void mousePressed(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		Point mouseLoc = new Point(mouseX, mouseY);
		
		if(attackButton.contains(mouseLoc)) {
			attackButtonClick = true;
			repaint();
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();
		Point mouseLoc = new Point(mouseX, mouseY);
		
		/*for(int i = 0; i < heroSquares.length; i++) {
			if(heroSquares[i].contains(mouseLoc)) {
				this.highlightedHeroSquare = heroSquares[i];
				this.selectedDude = i;
				repaint();
			}
		}*/
		
		if(selectedDude != -1 && selectedAttack != -1) {
			for(int i = 0; i < foeSquares.length; i++) {
				if(foeSquares[i].contains(mouseLoc)) {
					this.selectedFoe = i;
					this.highlightedFoeSquare = foeSquares[i];
					repaint();
				}
			}
		}
		
		if(selectedDude != -1) {	// Can only select attack squares if it's your turn
			for(int i = 0; i < 4; i++) {
				if(attackSquares != null && attackSquares[i].contains(mouseLoc)) {
					selectedAttack = i;
					selectedFoe = -1;
					repaint();
				}
			}
		}
		
		if(attackButton.contains(mouseLoc)) {
			if(attackButtonClick) {
				
			}
			attackButtonClick = false;
			repaint();
		}
		
	}
}