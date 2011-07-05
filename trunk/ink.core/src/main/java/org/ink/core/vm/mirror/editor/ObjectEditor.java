package org.ink.core.vm.mirror.editor;

import org.ink.core.vm.lang.InkClassState;
import org.ink.core.vm.lang.InkObject;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Scope;
import org.ink.core.vm.lang.property.mirror.PropertyMirror;

/**
 * @author Lior Schachter
 */
public interface ObjectEditor extends InkObject{

	public ObjectEditor startEdit(InkObjectState object);
	public ObjectEditor startEdit(InkObjectState object, boolean transactional);
	public <T extends InkObjectState> T getEditedState();
	public ObjectEditor createDescendent(String	descendentId);
	public void setId(String id);
	public void setOwner(InkObjectState owner);
	public void setAbstract(boolean isAbstract);
	public void init(InkClassState cls);
	public void setSuperId(String id);
	public void setSuper(InkObjectState theSuperObject);
	public void setScope(Scope scope);
	public void setPropertyValue(String propertyName, Object value);
	public void setPropertyValue(byte index, Object value);
	public void setPropertyValue(String propertyName, Object value, boolean override);
	public void setPropertyValue(byte index, Object value, boolean override);
	public void setHoldingProperty(PropertyMirror propMirror, byte Index);
	public void setRoot(boolean isRoot);
	public void setLoadOnStartup(boolean loadOnStartup);
	public void save();
	public void compile();
}