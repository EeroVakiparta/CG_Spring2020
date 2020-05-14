
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Grab the pellets as fast as you can!
 **/
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
					pakList.add(pacId, PakMaker(pacId,x,y));
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
				mappi.pellets[x][y] = (char) value;
			}
			Set<Command> commands = new HashSet<>();
			if(random){
				commands = RandomCommander(mappi, pakList);
			}else{
				commands = PakCommander(mappi, pakList);
			}
			
			// Write an action using System.out.println()
			// To debug: System.err.println("Debug messages...");

			printCommands(commands);

			if(oldScore < myScore){
				vuoroIlmanScore = 0;

			}else{
				vuoroIlmanScore++;
			}
			if(vuoroIlmanScore > 4){
				random = true;
			}else{
				random = false;
			}
			oldScore = myScore;
		}
	}

	public static Set<Command> printCommands(Set<Command> commands) {
		String print = "";
		for (Command c : commands) {
			print += "MOVE " + c.who + " " + c.toX + " " + c.toY + " | ";
		}

		System.out.println(print.substring(0,print.length() - 3));
		return commands;
	}


	public static Pak PakMaker(int pakID, int x, int y){
		return new Pak(pakID, x, y);
	}

	private static Set<Command> RandomCommander(Mappi m, List<Pak> pl) {
		Set<Command> commands = new HashSet<>();
		int[][] food = m.pellets;
		for (Pak p : pl) {
			pew: for (int i = 0; i < food.length; i++) {
				for (int j = 0; j < food[i].length; j++) {
					int f = food[i][j];
					if (f > 1) {
						commands.add(new Command(p.Id, i, j));
						break pew;
					}else if (f == 1){
						commands.add(new Command(p.Id, i, j));
					}
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
						if (f > 1) {
							//System.err.println( p.Id +" attacking fat target= " + i + "-" + j);
							commands.add(new Command(p.Id, i, j));
							commandLocations.add(new Location(i, j));
							break found;
						} else if (f == 1) {
							// System.err.println( p.Id +" attacking target= " + i + "-" + j);
							int distToFood = Math.abs(p.isX - i) + Math.abs(p.isY - j);
							if (distToFood < tempDistToClosest) {
								tempDistToClosest = distToFood;
								commandLocations.add(new Location(i, j));
								commands.add( new Command(p.Id, i, j));
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
}


class Pak {

	int Id;
	int isX;
	int isY;

	public Pak(int id, int isX, int isY) {
		Id = id;
		this.isX = isX;
		this.isY = isY;
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