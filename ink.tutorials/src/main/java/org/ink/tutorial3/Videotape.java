package org.ink.tutorial3;

import org.ink.core.vm.lang.InkObject;

public interface Videotape extends InkObject {

	public boolean isRented();

	public boolean canRent(CustomerState customer);
}