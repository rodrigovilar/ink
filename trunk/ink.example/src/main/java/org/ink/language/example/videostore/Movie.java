package org.ink.language.example.videostore;

import org.ink.core.vm.lang.InkClass;

public interface Movie extends InkClass {

	public String getTitle();

	public String getRating();
}