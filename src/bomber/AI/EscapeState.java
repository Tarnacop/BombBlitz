package bomber.AI;

public class EscapeState implements AIState{

	private AIManager mainAI;
	
	public EscapeState( AIManager mainAI)
	{
		this.mainAI = mainAI;
	}
	
	@Override
	public void updateState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toEscapeState() {
		//empty current state
	}

	@Override
	public void toFindState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toAttackState() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeMove() {
		// TODO Auto-generated method stub
		
	}

}
