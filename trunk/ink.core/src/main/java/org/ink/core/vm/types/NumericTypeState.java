package org.ink.core.vm.types;



/**
 * @author Lior Schachter
 */
public interface NumericTypeState extends PrimitiveTypeState{
	public class Data extends PrimitiveTypeState.Data implements NumericTypeState{
	}
}
