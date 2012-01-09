package org.ink.core.vm.factory.internal;

/**
 * @author Lior Schachter
 */
public class FileSystemObjectDescriptor implements ObjectDescriptor {

	private String classId;
	private String id;
	private Class<?> stateClass;

	public FileSystemObjectDescriptor(String id, String classId, Class<?> stateClass) {
		super();
		this.classId = classId;
		this.id = id;
		this.stateClass = stateClass;
	}

	@Override
	public String getClassId() {
		return classId;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Class<?> getStateClass() {
		return stateClass;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setStateClass(Class<?> stateClass) {
		this.stateClass = stateClass;
	}

}
