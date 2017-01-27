package bomber.AI;

public interface AIState {
	public void updateState();
	
	public void toEscapeState();
	
	public void toFindState();
	
	public void toAttackState();
	
	public void writeMove();
}
