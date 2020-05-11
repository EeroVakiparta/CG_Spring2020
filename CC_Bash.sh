# Grab the pellets as fast as you can!

# width: size of the grid
# height: top left corner is (x=0, y=0)
read -r width height
for (( i=0; i<$height; i++ )); do
    # row: one line of the grid: space " " is floor, pound "#" is wall
    read -r row
done

# game loop
while true; do
    read -r myScore opponentScore
    # visiblePacCount: all your pacs and enemy pacs in sight
    read -r visiblePacCount
    for (( i=0; i<$visiblePacCount; i++ )); do
        # pacId: pac number (unique within a team)
        # mine: true if this pac is yours
        # x: position in the grid
        # y: position in the grid
        # typeId: unused in wood leagues
        # speedTurnsLeft: unused in wood leagues
        # abilityCooldown: unused in wood leagues
        read -r pacId mine x y typeId speedTurnsLeft abilityCooldown
    done
    # visiblePelletCount: all pellets in sight
    read -r visiblePelletCount
    for (( i=0; i<$visiblePelletCount; i++ )); do
        # value: amount of points this pellet is worth
        read -r x y value
    done

    # Write an action using echo
    # To debug: echo "Debug messages..." >&2

    echo "MOVE 0 "${x}" "${y} # MOVE <pacId> <x> <y>
done