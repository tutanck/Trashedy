package com.aj.tools.jr;

public class Node<T> {
	
	private T yellow;
	private T white;
	
	public Node(
			T left,
			T right
			) {
		this.yellow=left;
		this.white=right;
	}

	public T yellow() {return yellow;}

	public T white() {return white;}
}
