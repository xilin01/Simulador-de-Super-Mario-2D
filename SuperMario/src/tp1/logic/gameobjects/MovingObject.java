// Grupo 13: XiangLin - MarioRosellGarcia

package tp1.logic.gameobjects;

import tp1.exceptions.ActionParseException;
import tp1.exceptions.ObjectParseException;
import tp1.exceptions.OffBoardException;

import tp1.logic.Action;
import tp1.logic.GameWorld;
import tp1.logic.Position;

import tp1.view.Messages;

public abstract class MovingObject extends GameObject {
	private Action direction;
	private boolean isFalling;
	
	public MovingObject(Position position, String name, String shortcut, GameWorld game, Action direction) {
		super(position, name, shortcut, game);
		this.direction = direction;
		this.isFalling = false;
	}
	
	@Override
	public boolean isSolid() {
	return false;
	}
	
	protected boolean isInDirection(Action direction) {
	return this.direction == direction;
	}

	@Override
	public void update() {
		if(super.isSolidPosition(Action.DOWN)) this.doAction(this.direction);
		else this.freeFalling();	
	}
	
	protected void doAction(Action action) {
		this.isFalling = false;
		if(super.isSolidPosition(action) || super.isSolidPosition(headPosition().nextPosition(action)) || !super.isValidPosition(action)) this.direction = Action.opposite(action);
		else {
			this.direction = action;
			super.move(action);
		}
	}

	protected boolean freeFalling() {
		boolean freeFalling = false;
		
		if(!super.isSolidPosition(Action.DOWN) && super.isValidPosition()) {
			freeFalling = true;
			this.isFalling = true;
			super.move(Action.DOWN);
			if(!super.isValidPosition()) super.dead();
		}
	return freeFalling;
	}
	
	protected void up() {
		this.isFalling = false;
		if(!super.isSolidPosition(headPosition().nextPosition(Action.UP)) && headPosition().nextPosition(Action.UP).isValid()) this.move(Action.UP);
		else {
			collidedUp();
			super.move(Action.STOP);
		}
	}
	
	protected void stop() {
		this.isFalling = false;
		this.direction = Action.STOP;
	}
	
	protected boolean isFalling() {
	return this.isFalling;
	}
	
	protected boolean validDirection(Action direction) {
	return direction == Action.LEFT || direction == Action.RIGHT;
	}
	
	@Override
	public MovingObject parse(String[] objectWords, GameWorld game) throws OffBoardException, ObjectParseException {
		try {
			MovingObject movingObject = (MovingObject) super.parse(objectWords, game);
			
			if(movingObject != null && 3 <= objectWords.length) {
				Action direction = Action.parseAction(objectWords[2]);
				
				if(validDirection(direction)) movingObject.direction = direction;
				else throw new ObjectParseException(Messages.INVALID_MOVING_OBJECT_DIRECTION.formatted(String.join(" ", objectWords)));
			}
			return movingObject;
		}
		catch(ActionParseException ape){
			throw new ObjectParseException(Messages.UNKNOWN_MOVING_OBJECT_DIRECTION.formatted(String.join(" ", objectWords)), ape);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(super.toString()).append(Messages.SPACE).append(this.direction.toString());
	return stringBuilder.toString();
	};
	
	@Override
	public MovingObject newCopy() {
		MovingObject movingObject = (MovingObject) super.newCopy();
		
		movingObject.direction = this.direction;
		movingObject.isFalling = this.isFalling;
	return movingObject;
	}
	
	protected abstract Position headPosition();
	protected abstract void collidedUp();
}
