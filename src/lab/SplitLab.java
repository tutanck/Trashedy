package lab;

import java.util.Arrays;

public class SplitLab {

	public static void main(String[] args) {
		System.out.println(Arrays.asList("nom".split("\\:")));
		System.out.println(Arrays.asList("nom:".split("\\:")));
		System.out.println(Arrays.asList("nom: ".split("\\:")));
		System.out.println(Arrays.asList("nom:string".split("\\:")));
	}
}
