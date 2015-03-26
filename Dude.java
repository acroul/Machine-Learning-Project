
class Dude {
	
///////////////////////////
//FIELDS
///////////////////////////

	private String Name;
	private int MaxHP;
	private int HP;
	private int minDamage;
	private int maxDamage;
	private int Speed;
	private int Position;
	private int Readiness;
	private boolean Bleeding;
	private boolean Poisoned;
	private boolean Stunned;
        private int TurnsBleeding = 0;
        private int TurnsPoisoned = 0;
	private Attack Attack1;
	private Attack Attack2;
	private Attack Attack3;
	private Attack Attack4;
	
///////////////////////////
//CONSTRUCTOR
///////////////////////////

	public Dude(String name, int position){
		this.Name = name;
		name = name.toLowerCase();
		
		switch (name) {
			
		//Create Warrior
			case "warrior":
				this.MaxHP = 35;
				this.HP = 35;
				this.minDamage = 7;
				this.maxDamage = 13;
				this.Speed = 6;
				this.Position = position;
				this.Readiness = 0;
				this.Bleeding = false;
				this.Poisoned = false;
				this.Stunned = false;
				
				Attack1 = new Attack("Crushing Blow");
				Attack2 = new Attack("Smite");
				Attack3 = new Attack("Cleave");
				Attack4 = new Attack("Shield Bash");
				
				break;
			
		//Create Ranger
			case "ranger":
				this.MaxHP = 25;
				this.HP = 25;
				this.minDamage = 5;
				this.maxDamage = 8;
				this.Speed = 8;
				this.Position = position;
				this.Readiness = 0;
				this.Bleeding = false;
				this.Poisoned = false;
				this.Stunned = false;
				
				Attack1 = new Attack("Stab");
				Attack2 = new Attack("Ranged Shot");
				Attack3 = new Attack("Multishot");
				Attack4 = new Attack("Poison Bomb");
				
				break;
				
		//Create Mage		
			case "mage":
				this.MaxHP = 20;
				this.HP = 20;
				this.minDamage = 6;
				this.maxDamage = 9;
				this.Speed = 7;
				this.Position = position;
				this.Readiness = 0;
				this.Bleeding = false;
				this.Poisoned = false;
				this.Stunned = false;
				
				Attack1 = new Attack("Shake Foundation");
				Attack2 = new Attack("Lightning Strike");
				Attack3 = new Attack("Dark Deliverance");
				Attack4 = new Attack("Phlegethon Flame");
				
				break;
		
		//Create Priest		
			default:
			case "priest":
				this.MaxHP = 18;
				this.HP = 18;
				this.minDamage = 4;
				this.maxDamage = 7;
				this.Speed = 5;
				this.Position = position;
				this.Readiness = 0;
				this.Bleeding = false;
				this.Poisoned = false;
				this.Stunned = false;
				
				Attack1 = new Attack("Judgement");
				Attack2 = new Attack("Healing Ritual");
				Attack3 = new Attack("Solomon's Ring");
				Attack4 = new Attack("Mephisto's Rage");
				
				break;
		}
		
	}
	
///////////////////////////
//GETTERS
///////////////////////////

	public String getName(){
		return this.Name;
	}
	
	public int getMaxHP() {
		return this.MaxHP;
	}
	
	public int getHP(){
		return this.HP;
	}

	public int getMinDamage(){
		return this.minDamage;
	}
	
	public int getMaxDamage(){
		return this.maxDamage;
	}
	
	public int getSpeed(){
		return this.Speed;
	}
	
	public int getPosition(){
		return this.Position;
	}
	
	public int getReadiness() {
		return this.Readiness;
	}
	
	public boolean isBleeding(){
		return this.Bleeding;
	}
	
	public boolean isPoisoned(){
		return this.Poisoned;
	}
	
	public boolean isStunned(){
		return this.Stunned;
	}
	
	public Attack getAttack(int num){
		
		switch (num) {
			
			default:
			case 1:
				return Attack1;
			case 2:
				return Attack2;
			case 3:
				return Attack3;
			case 4:
				return Attack4;
				
		}
	}

///////////////////////////
//SETTERS
///////////////////////////	

	public void setPosition(int pos){
		this.Position = pos;
	}
	
	public void setHP(int hp) {
		this.HP = hp;
	}
	
	public void setReadiness(int readiness) {
		this.Readiness = readiness;
	}
	
	public void setBleeding(boolean HemophiliacInGymClass){
		this.Bleeding = HemophiliacInGymClass; //wow
	}
	
	public void setPoisoned(boolean ArbokUsePoisonSting){
		this.Poisoned = ArbokUsePoisonSting; //lol
	}
	
	public void setStunned(boolean StevenHawking){
		this.Stunned = StevenHawking; //lmao
	}
        

              
///////////////////////////
//INFLICT BLEEDING DAMAGE
///////////////////////////      
        public void BleedBabyBleed(){
            this.TurnsBleeding += 1;
            
            this.HP -= 2;                    //Bleeding will take 2 HP
            
            if(this.TurnsBleeding == 3){     //Bleeding will last 3 turns
                this.TurnsBleeding = 0;
                this.Bleeding = false;
            }
        }
        
	
        
///////////////////////////
//INFLICT POISON DAMAGE
///////////////////////////      
        public void FeelingVenomenal(){
            this.TurnsPoisoned += 1;
            
            this.HP -= this.TurnsPoisoned;   //Poison damage will increase each turn
            
            if(this.TurnsBleeding == 3){     //Poison will last 3 turns
                this.TurnsBleeding = 0;
                this.Bleeding = false;
            }
        }
       
        
        
///////////////////////////
//INFLICT STUN
//A man got into a car accident and was rushed to the hospital.
//His life was saved but the left side of his body was completely paralyzed.
//"He's going to be all right now," said the doctor.
///////////////////////////  
        
        public void WheelchairBound(){
            this.Stunned = false;  //Stun only lasts 1 turn
        }
        
     
        
///////////////////////////
//MAIN
///////////////////////////		
	public static void main(String[] args) {}

}