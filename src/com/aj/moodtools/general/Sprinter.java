package com.aj.moodtools.general;

public class Sprinter extends Thread {
	private String[] stuff;  //array to avoid passing parameters by value
	
	public Sprinter(String []stuff) {this.stuff=stuff;}
	
	public String[] getCooked(){return stuff;}
	
	@Override public void run() {}
}
