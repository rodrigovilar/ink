package org.ink.example.http;

public class InterviewExam {

	
	public static void main(String[] args) {
		System.out.println("InterviewExam starting.");
		boolean[] x = new boolean[101];
		int i = 0;
		int i1 = 0;
		for (i = 1;i < 101;i++) {
			for (i1 = 0;i1 < 100; i1 = i1+i) {
				x[i1] = !x[i1];
			}
		}
		
		for (i = 1; i < 101; i++) {
			if (x[i]) {
				System.out.print(i + " ");
			}
		}
		
		System.out.println("\nInterviewExam done.");
	}
}
