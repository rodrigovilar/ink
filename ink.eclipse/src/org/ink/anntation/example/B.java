package org.ink.anntation.example;

@Stam(o = @Stam2(ints = 4))
public class B extends A {
	public static void main(String[] args) {
		Stam s = A.class.getAnnotation(Stam.class);
		Stam2 ccs = s.o();
		System.out.println(ccs.ints2());// 6
		s = B.class.getAnnotation(Stam.class);
		ccs = s.o();
		System.out.println(ccs.ints2());// 44 (but with object inheritance it would be 6)
	}
}
