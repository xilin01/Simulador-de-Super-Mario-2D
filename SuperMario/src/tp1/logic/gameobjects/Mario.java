// Grupo 13: XiangLin - MarioRosellGarcia

package tp1.logic.gameobjects;

import tp1.exceptions.ObjectParseException;
import tp1.exceptions.OffBoardException;

import tp1.logic.Position;
import tp1.logic.Action;
import tp1.logic.ActionList;
import tp1.logic.GameWorld;

import tp1.view.Messages;

public class Mario extends MovingObject {
	private static final String NAME = Messages.GAMEOBJECT_MARIO_NAME;
	private static final String SHORTCUT = Messages.GAMEOBJECT_MARIO_SHORTCUT;
	
	private boolean big;
	private boolean collidedUp;
	private ActionList actionList;
	
	private Mario(Position position, GameWorld game, Action direction, boolean big) {
		super(position, NAME, SHORTCUT, game, direction);
		this.big = big;
		this.collidedUp = false;
		this.actionList = new ActionList();
	}
	
	public Mario(Position position, GameWorld game) {
		this(position, game, Action.RIGHT, true);
	}
	
	Mario() {
		this(null, null, null, true);
	}

	@Override
	protected Position headPosition() {
		Position position = super.nextPosition(Action.STOP);
		
		if(this.big) position = super.nextPosition(Action.UP);
	return position;
	}
	
	@Override
	protected void collidedUp() {
		this.collidedUp = true;
	}
	
	@Override
	public boolean isInPosition(Position position) {
	return super.isInPosition(position) || (this.isAlive() && this.headPosition().equals(position));
	}
	
	@Override
	public void update() {
		Position position = super.nextPosition(Action.STOP);

		this.playerMovement();
		if(super.nextPosition(Action.STOP).equals(position)) {
			super.update();
			if(!super.isValidPosition()) this.game.marioDead();
		}
	}
	
	@Override
	public String getIcon() {
		StringBuilder stringBuilder = new StringBuilder();
		
		if(this.isInDirection(Action.STOP)) stringBuilder.append(Messages.MARIO_STOP);
		else if(this.isInDirection(Action.LEFT)) stringBuilder.append(Messages.MARIO_LEFT);
		else stringBuilder.append(Messages.MARIO_RIGHT);
	return stringBuilder.toString(); 
	}

	private void playerMovement() {
		boolean startGrounded = super.isSolidPosition(Action.DOWN);
		
		for(Action action: this.actionList) {
			if(action == Action.DOWN) this.down();
			else if(action == Action.UP) this.up(startGrounded);
			else if (action == Action.STOP) super.stop();
			else super.doAction(action);
		}
		this.actionList.clear();
	}
	
	private void down() {
		if(super.isSolidPosition(Action.DOWN)) super.stop();
		else {
			while(super.freeFalling()) {
				if(!super.isAlive()) this.game.marioDead();
			}
		}
	}
	
	private void up(boolean startGrounded) {
		if(startGrounded) {
			super.up();
			this.collidedUp = false;
		}
	}
	
	public void addAction(Action action) {
		this.actionList.addLast(action);
	}

	@Override
	public boolean receiveInteraction(ExitDoor exitDoor) {
	return true;
	}

	@Override
	public boolean receiveInteraction(Goomba goomba) {
		if(this.big) {
			if(!super.isFalling()) this.big = false;
		}
		else {
			if(!super.isFalling()) {
				super.dead();
				this.game.marioDead();
			}
		}
	return true;
	}
	
	@Override
	public boolean receiveInteraction(Mushroom mushroom) {
		if(!this.big) this.big = true;
	return true;
	}
	
	@Override
	public boolean receiveInteraction(Box box) {
	return this.collidedUp;
	}	

	@Override
	Mario newInstance(Position position, GameWorld game) {
	return new Mario(position, game, Action.RIGHT, true);
	}
	
	@Override
	protected boolean validDirection(Action direction) {
	return super.validDirection(direction) || direction == Action.STOP;
	}
	
	private boolean validSize(String size) {
	return size.equalsIgnoreCase(Messages.MARIO_BIG) || size.equalsIgnoreCase(Messages.MARIO_B) || size.equalsIgnoreCase(Messages.MARIO_SMALL) || size.equalsIgnoreCase(Messages.MARIO_S);
	}
	
	private void updateSize(String size) {
		if(size.equalsIgnoreCase(Messages.MARIO_SMALL) || size.equalsIgnoreCase(Messages.MARIO_S)) this.big = false;
	}
	
	@Override
	public Mario parse(String[] objectWords, GameWorld game) throws OffBoardException, ObjectParseException {
		Mario mario = (Mario) super.parse(objectWords, game);
		
		if(mario != null && 4 <= objectWords.length) {
			if(objectWords.length == 4) {
				if(this.validSize(objectWords[3])) mario.updateSize(objectWords[3]);
				else throw new ObjectParseException(Messages.INVALID_MARIO_SIZE.formatted(String.join(" ", objectWords)));
			}
			else throw new ObjectParseException(Messages.OBJECT_TOO_MUCH_ARGS.formatted(String.join(" ", objectWords)));
		}
	return mario;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(super.toString()).append(Messages.SPACE);
		if(this.big) stringBuilder.append(Messages.MARIO_BIG);
		else stringBuilder.append(Messages.MARIO_SMALL);
	return stringBuilder.toString();
	}
	
	@Override
	public Mario newCopy() {
		Mario mario = (Mario) super.newCopy();
		
		mario.big = this.big;
		mario.collidedUp = this.collidedUp;
	return mario;
	}
}
