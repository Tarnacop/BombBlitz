package bomber.game;

/**
 *
 * @author Owen Jenkins
 * @version 1.4
 * @since 2017-03-23
 * 
 *        Block class for "Bomb Blitz" Game Application (2017 Year 2 Team
 *        Project, Team B1). Contains enums for each block type in the game.
 */
public enum Block {

	BLANK, SOLID, SOFT, BLAST, PLUS_BOMB, MINUS_BOMB, PLUS_RANGE, MINUS_RANGE, PLUS_SPEED, MINUS_SPEED, HOLE
	// BLANK - nothing
	// SOLID - indestructible block
	// SOFT - destructible block
	// BLAST - explosion blast

	// PLUS_BOMB - +1 bomb powerup
	// MINUS_BOMB - -1 bomb powerdown
	// PLUS_RANGE - +1 bomb range powerup
	// MINUS_RANGE - -1 bomb range powerdown
	// PLUS_SPEED - increase speed powerup
	// MINUS_SPEED - decrease speed powerdown

	// HOLE - die
}
