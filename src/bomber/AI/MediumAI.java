
package bomber.AI;

import java.awt.Point;
import java.security.SecureRandom;
import java.util.LinkedList;

import bomber.game.GameState;

/**
 * AI of medium difficulty.
 * 
 * @author Jokubas Liutkus
 */
public class MediumAI extends AITemplate {

  /**
   * Instantiates a new medium AI.
   *
   * @param ai
   *          the ai
   * @param gameState
   *          the game state
   */
  public MediumAI(GameAI ai, GameState gameState) {
    super(ai, gameState);
  }

  /*
   * (non-Javadoc)
   * 
   * @see bomber.AI.AITemplate#performMoves(java.util.LinkedList, boolean)
   */
  protected void performMoves(LinkedList<AIActions> moves, boolean inDanger) {
    // if in player is in danger then perform moves without any checks to
    // get into safe location
    if (inDanger) {
      while (moves != null && !moves.isEmpty() && gameAI.isAlive()) {
        pausedGame();
        makeSingleMove(moves.removeFirst());
      }
    } 
    
    // else move until the enemy is reachable, or the AI is in danger
    else {
      while (moves != null && !moves.isEmpty() && !safetyCh.inDanger()
          && safetyCh.checkMoveSafety(moves.peek()) && !safetyCh.isEnemyInBombRange()
          && gameAI.isAlive()) {
        pausedGame();
        makeSingleMove(moves.removeFirst());
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see bomber.AI.AITemplate#performPlannedMoves(java.util.LinkedList)
   */
  protected void performPlannedMoves(LinkedList<AIActions> moves) {
    AIActions action;

    while (moves != null && !moves.isEmpty() && getMovesToEnemy() == null && gameAI.isAlive()) {
      pausedGame();
      action = moves.removeFirst();
      // if actions is bomb place it
      if (action == AIActions.BOMB) {
        gameAI.getKeyState().setBomb(true);
        try {
          sleep(100);
        } catch (InterruptedException e) {

        }
        gameAI.getKeyState().setBomb(false);
      }
      // if action is none wait until the next move is safe
      else if (action == AIActions.NONE) {
        if (moves != null) {
          while (!safetyCh.checkMoveSafety(moves.peek()) && gameAI.isAlive()) {
            pausedGame();
          }
        }
      }
      // otherwise make a standard move
      else {
        makeSingleMove(action);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see bomber.AI.AITemplate#move()
   */
  protected void move() {
    LinkedList<AIActions> moves;
    SecureRandom random = new SecureRandom();
    while (gameAI.isAlive()) {
      pausedGame();

      // if AI is in danger then find the escape route
      if (safetyCh.inDanger() && random.nextBoolean()) {
        moves = finder.escapeFromExplotion((safetyCh.getTilesAffectedByBombs()));
        performMoves(moves, true);

      }

      // else if there is an upgrade find the moves to it
      else if (random.nextBoolean() && (moves = finder.findRouteToUpgrade()) != null) {

        performMoves(moves, false);
      }

      // if enemy is in bomb range then place the bomb and go to the
      // safe location
      else if (safetyCh.isEnemyInBombRange() && random.nextBoolean()) {
        gameAI.getKeyState().setBomb(true);
        moves = finder.escapeFromExplotion((safetyCh.getTilesAffectedByBombs()));
        performMoves(moves, true);
      }

      // if enemy is accessible(no boxes are blocking the path) then
      // find a route to it and make moves
      else if ((moves = getMovesToEnemy()) != null) {
        performMoves(moves, false);
      }
      // if enemy is not in the range get the plan how to reach enemy and
      // fullfill it
      else if ((moves = finder.getPlanToEnemy(gameAI.getGridPos(),
          finder.getNearestEnemy())) != null && random.nextBoolean()) {
        performPlannedMoves(moves);
      }

      // otherwise just generate a random goal and start fulfilling it
      else {
        int x = random.nextInt(gameState.getMap().getGridMap().length);
        int y = random.nextInt(gameState.getMap().getGridMap()[0].length);
        moves = finder.getPlanToEnemy(gameAI.getGridPos(), new Point(x, y));
        performPlannedMoves(moves);
      }

      gameAI.getKeyState().setBomb(false);
    }
  }

}
