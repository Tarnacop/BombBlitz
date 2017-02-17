package bomber.game;

public enum Block {

	BLANK, SOLID, SOFT, BLAST, PLUS_BOMB, MINUS_BOMB, PLUS_RANGE, MINUS_RANGE, PLUS_SPEED, MINUS_SPEED	//possible block types for each grid square of the map: 
	//BLANK - nothing
	//SOLID - indestructible block
	//SOFT - destructible block
	//BLAST - explosion blast
	
	//PLUS_BOMB - +1 bomb powerup
	//MINUS_BOMB - -1 bomb powerdown
	//PLUS_RANGE - +1 bomb range powerup
	//MINUS_RANGE - -1 bomb range powerdown
	//PLUS_SPEED - increase speed powerup
	//MINUS_SPEED - decrease speed powerdown
}
