package jp.co.tis.tc.command.slave.lib;

import java.util.concurrent.CopyOnWriteArrayList;

public class Observable {
	private CopyOnWriteArrayList<Observer> observers = new CopyOnWriteArrayList<Observer>();
	
	protected void notifyObservers(Command command) {
		for (Observer o : observers) {
			o.notifyCommand(command);
		}
	}
	public void addObserver(Observer o) {
		observers.add(o);
	}
	public void removeObserver(Observer o) {
		if (observers.contains(o)) {
			observers.remove(o);
		}
	}
}
