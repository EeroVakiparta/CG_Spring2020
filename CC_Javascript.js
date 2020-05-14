/**
 * Added pieces of code marked with --. Everything else is competition "engine code", and should not be modified.
 **/

var inputs = readline().split(' ');
const width = parseInt(inputs[0]); // size of the grid
const height = parseInt(inputs[1]); // top left corner is (x=0, y=0)
for (let i = 0; i < height; i++) {
    const row = readline(); // one line of the grid: space " " is floor, pound "#" is wall
}

// game loop
while (true) {

    //-- Added code
    var pacmanCommandX;
    var pacmanCommandY;


    var inputs = readline().split(' ');
    const myScore = parseInt(inputs[0]);
    const opponentScore = parseInt(inputs[1]);
    const visiblePacCount = parseInt(readline()); // all your pacs and enemy pacs in sight
    for (let i = 0; i < visiblePacCount; i++) {
        var inputs = readline().split(' ');
        const pacId = parseInt(inputs[0]); // pac number (unique within a team)
        const mine = inputs[1] !== '0'; // true if this pac is yours
        const x = parseInt(inputs[2]); // position in the grid
        const y = parseInt(inputs[3]); // position in the grid
        const typeId = inputs[4]; // unused in wood leagues
        const speedTurnsLeft = parseInt(inputs[5]); // unused in wood leagues
        const abilityCooldown = parseInt(inputs[6]); // unused in wood leagues
    }
    const visiblePelletCount = parseInt(readline()); // all pellets in sight
    for (let i = 0; i < visiblePelletCount; i++) {
        var inputs = readline().split(' ');
        const x = parseInt(inputs[0]);
        const y = parseInt(inputs[1]);
        const value = parseInt(inputs[2]); // amount of points this pellet is worth

        //-- Added code
        pacmanCommandX = x;
        pacmanCommandY = y;
        // What happens is that from all pellets on map
        // the last pellet coordinates given by "game engine"
        // are set as target for the pacman to move to
        // Note: 10x point pellets are given in list last. 
        // Because of this the pacman will eat them first.

    }

    // Write an action using console.log()
    // To debug: console.error('Debug messages...');

    console.log('MOVE 0 ' + x + ' ' + y);     // MOVE <pacId> <x> <y>

}
