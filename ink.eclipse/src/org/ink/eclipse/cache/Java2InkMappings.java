package org.ink.eclipse.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Java2InkMappings {

	private static final Map<String, String> memory = new ConcurrentHashMap<String, String>(1000);


	public static final String get(String resource){
		return memory.get(resource);
	}


	public static final void put(String resource, String inkId){
		memory.put(resource, inkId);
	}

	public static final Iterator<Map.Entry<String, String>> iterate(){
		return memory.entrySet().iterator();
	}


}
