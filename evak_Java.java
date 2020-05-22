import java.util.*;

class Player {

	public static void main(String args[]) {
		List<Pak> pakList = new ArrayList<Pak>();
		List<String> rows = new ArrayList<>();

		Scanner in = new Scanner(System.in);
		int width = in.nextInt(); // size of the grid
		int height = in.nextInt(); // top left corner is (x=0, y=0)
		//char[][] areaOfPlay = new char[width][height];
		if (in.hasNextLine()) {
			in.nextLine();
		}
		for (int i = 0; i < height; i++) {
			String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
			rows.add(row);
		}

		char[][] areaOfPlay = new char[ rows.size() ][];
		for ( int i = 0; i < rows.size(); i++ ) {
			areaOfPlay[i] = rows.get( i ).toCharArray();
		}

		// Make map with correct size

		int[][] areaOfPellets = new int[width][height];
		Mappi mappi = new Mappi(areaOfPlay,areaOfPellets);
		int oldScore = 0;
		int vuoroIlmanScore = 0;
		boolean random = false;
		// game loop
		while (true) {
			areaOfPellets = new int[width][height];
			mappi = new Mappi(areaOfPlay,areaOfPellets);
			pakList = new ArrayList<Pak>();
			int myScore = in.nextInt();
			int opponentScore = in.nextInt();
			int visiblePacCount = in.nextInt(); // all your pacs and enemy pacs in sight
			for (int i = 0; i < visiblePacCount; i++) {
				int pacId = in.nextInt(); // pac number (unique within a team)
				boolean mine = in.nextInt() != 0; // true if this pac is yours
				int x = in.nextInt(); // position in the grid
				int y = in.nextInt(); // position in the grid
				String typeId = in.next(); // unused in wood leagues
				int speedTurnsLeft = in.nextInt(); // unused in wood leagues
				int abilityCooldown = in.nextInt(); // unused in wood leagues
				if (mine) {
					pakList.add( PakMaker(pacId,x,y,typeId,speedTurnsLeft,abilityCooldown));
				}
			}

			for (int[] row: mappi.pellets){
				Arrays.fill(row, 0);
			}

			int visiblePelletCount = in.nextInt(); // all pellets in sight
			for (int i = 0; i < visiblePelletCount; i++) {
				int x = in.nextInt();
				int y = in.nextInt();
				int value = in.nextInt(); // amount of points this pellet is worth
				mappi.pellets[x][y] = value;
				//System.err.println(value + " in" + x +"-" + y);
			}

			Set<Command> commands = new HashSet<>();
			if(random){
				commands = RandomCommander(mappi, pakList);
			}else{
				commands = PakCommander(mappi, pakList);
			}
			try{
				//System.err.println(commands);
				//System.err.println(pakList);
				printCommands(commands,pakList);
			}catch (Error eero){
				System.out.println("MOVE 0 0 0");
			}
			if(oldScore < myScore){
				vuoroIlmanScore = 0;
			}else{
				vuoroIlmanScore++;
			}
			if(vuoroIlmanScore > 4){
				//	random = true;
			}else{
				random = false;
			}
			oldScore = myScore;
		}
	}

	public static Set<Command> printCommands(Set<Command> commands, List<Pak> pakList) {
		for (Pak p : pakList) {
			for (Command c : commands) {
				if (p.Id == c.who) {
					p.setCommand(c);
				}
			}
		}

		String print = "";
		for (Pak p : pakList) {
			if (p != null){
				if (p.coolDown == 0) {
					print += "SPEED " + p.Id + " | ";
				}else{
					print += "MOVE " + p.Id + " " + p.command.toX + " " + p.command.toY + " | ";
				}
			}
		}
		System.out.println(print.substring(0,print.length() -2));
		return commands;
	}


	public static Pak PakMaker(int pakID, int x, int y,String type,int speed, int cd){
		return new Pak(pakID, x, y,type,speed,cd);
	}

	private static Set<Command> RandomCommander(Mappi m, List<Pak> pl) {
		Set<Command> commands = new HashSet<>();
		int[][] food = m.pellets;
		for (Pak p : pl) {
			pew: for (int i = 0; i < food.length; i++) {
				for (int j = 0; j < food[i].length; j++) {
					int f = food[i][j];
					commands.add(new Command(p.Id, i, j));
				}
			}
		}
		return commands;
	}

	public static Set<Command> PakCommander(Mappi m, List<Pak> pl) {
		Set<Command> commands = new HashSet<>();
		List<Location> commandLocations = new ArrayList<>();
		int[][] food = m.pellets;
		for (Pak p : pl) {
			int tempDistToClosest = 100;
			found: for (int i = 0; i < food.length; i++) {
				for (int j = 0; j < food[i].length; j++) {
					boolean reserved = false;
					int f = food[i][j];
					if ( f > 0){
						Location tempLocation = new Location(i, j);
						for (Location l : commandLocations) {
							if (l.lx == i && l.ly == j) {
								reserved = true;
								break;
							}
						}
						if (reserved) {
							// System.err.println("reserved" + tempLocation);
						}else {
							if(f>1){
								Command c = new Command(p.Id, i, j);
								commandLocations.add(new Location(i, j));
								commands.remove(c);
								commands.add(c);
								break found;

							}	else{

								// System.err.println( p.Id +" attacking target= " + i + "-" + j);
								int distToFood = Math.abs(p.isX - i) + Math.abs(p.isY - j);
								if (distToFood < tempDistToClosest) {
									tempDistToClosest = distToFood;
									commandLocations.add(new Location(i, j));
									Command c = new Command(p.Id, i, j);
									commands.remove(c);
									commands.add(c);
								}
							}
						}
					}
				}
			}
		}
		return commands;
	}
}

class Location{
	int lx ;
	int ly;

	public Location(int lx, int ly) {
		this.lx = lx;
		this.ly = ly;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Location)) return false;
		Location location = (Location) o;
		return lx == location.lx &&
			ly == location.ly;
	}

	@Override
	public int hashCode() {
		return Objects.hash(lx, ly);
	}
}

class Command{

	int who;
	int toX;
	int toY;

	public Command(int who, int toX, int toY) {
		this.who = who;
		this.toX = toX;
		this.toY = toY;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Command)) return false;
		Command command = (Command) o;
		return who == command.who;
	}

	@Override
	public int hashCode() {
		return Objects.hash(who);
	}

	@Override
	public String toString() {
		return 	"Move=" + who +
			" to X=" + toX +
			" Y=" + toY;
	}
}

class Pak {

	int Id;
	int isX;
	int isY;
	String type;
	int speedLeft;
	int coolDown;
	Command command = new Command(Id, isX, isY);

	public Pak(int id, int isX, int isY, String type, int speedLeft, int coolDown) {
		Id = id;
		this.isX = isX;
		this.isY = isY;
		this.type = type;
		this.speedLeft = speedLeft;
		this.coolDown = coolDown;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getIsX() {
		return isX;
	}

	public void setIsX(int isX) {
		this.isX = isX;
	}

	public int getIsY() {
		return isY;
	}

	public void setIsY(int isY) {
		this.isY = isY;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSpeedLeft() {
		return speedLeft;
	}

	public void setSpeedLeft(int speedLeft) {
		this.speedLeft = speedLeft;
	}

	public int getCoolDown() {
		return coolDown;
	}

	public void setCoolDown(int coolDown) {
		this.coolDown = coolDown;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	@Override
	public String toString() {
		return "Pak{" +
			"Id=" + Id +
			", isX=" + isX +
			", isY=" + isY +
			", type='" + type + '\'' +
			", s=" + speedLeft +
			", cd=" + coolDown +
			", c=" + command.toX +"-"+ command.toY;
	}
}

class Mappi{

	char[][] arena;
	int[][] pellets;

	public Mappi(char[][] map, int[][] pellets) {
		this.arena = map;
		this.pellets = pellets;
	}
}