package bomber.AI;

import java.awt.Point;
import java.security.SecureRandom;
import java.util.LinkedList;

import bomber.game.GameState;

/**
 * AI of easy difficulty.
 * 
 * @author Jokubas Liutkus
 */
public class EasyAI extends AITemplate {

  /**
   * Instantiates a new easy AI.
   *
   * @param ai
   *          the AI
   * @param gameState
   *          the game state
   */
  public EasyAI(GameAI ai, GameState gameState) {
    super(ai, gameState);
  }

  /*
   * (non-Javadoc)
   * 
   * @see bomber.AI.AITemplate#performMoves(java.util.LinkedList, boolean)
   */
  @Override
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
  @Override
  protected void performPlannedMoves(LinkedList<AIActions> moves) {

    AIActions action;
    while (moves != null && !moves.isEmpty() && gameAI.isAlive()) {
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
            if (safetyCh.inDanger())
              break;
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
  @Override
  protected void move() {
    LinkedList<AIActions> moves;
    SecureRandom random = new SecureRandom();

    while (gameAI.isAlive()) {

      // check if the game is paused
      pausedGame();

      // if AI is in danger then escape only with 60% possibility
      if (safetyCh.inDanger() && random.nextInt(100) > 40) {
        moves = finder.escapeFromExplotion((safetyCh.getTilesAffectedByBombs()));
        performMoves(moves, true);

      }

      // if enemy is in bomb range then place the bomb and go to the
      //// // safe location only with 30% possibility
      else if (safetyCh.isEnemyInBombRange() && random.nextInt(10) > 4) {
        gameAI.getKeyState().setBomb(true);
        moves = finder.escapeFromExplotion((safetyCh.getTilesAffectedByBombs()));
        performMoves(moves, true);
      }

      // if enemy is accessible(no boxes are blocking the path) then
      // find a route to it and make moves (50% possibility)
      else if ((moves = getMovesToEnemy()) != null && random.nextBoolean()) {
        performMoves(moves, false);
        gameAI.getKeyState().setBomb(true);
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
