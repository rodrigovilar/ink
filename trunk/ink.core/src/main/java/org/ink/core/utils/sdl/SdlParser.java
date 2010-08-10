package org.ink.core.utils.sdl;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.ikayzo.sdl.SDLParseException;
import org.ikayzo.sdl.Tag;

/**
 * @author Lior Schachter
 */

public class SdlParser{
	

	private static final String ROOT = "root";

	public static Tag parse(File f) throws IOException, SDLParseException{
		return new Tag(ROOT).read(f);
	}
	
	public static Tag parse(URL url) throws IOException, SDLParseException{
		return new Tag(ROOT).read(url);
	}
	
	public static Tag parse(String data) throws SDLParseException{
		return new Tag(ROOT).read(data);
	}
	
}
