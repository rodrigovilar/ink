package org.ink.core.vm.traits;

import java.util.List;

import org.ink.core.vm.exceptions.WeaveException;
import org.ink.core.vm.lang.InkObjectImpl;
import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.Property;
import org.ink.core.vm.mirror.ClassMirror;
import org.ink.core.vm.mirror.Mirror;
import org.ink.core.vm.mirror.TraitMirror;
import org.ink.core.vm.mirror.editor.ClassEditor;

/**
 * @author Lior Schachter
 */
public class PersonalityImpl<B extends PersonalityState> extends InkObjectImpl<B> implements Personality{

	@Override
	public byte getTraitsCount() {
		return reflect().getPropertiesCount();
	}
	
	@Override
	public boolean hasRole(String role) {
		return reflect().getClassMirror().getClassPropertiesIndexes().containsKey(role);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Trait> T adapt(byte index, InkObjectState state, ClassMirror cMirror) {
		T result = null;
		Mirror m = reflect();
		Trait trait;
		if(index >= m.getPropertiesCount()){
			trait = cMirror.getRole(index);
		}else{
			trait = (Trait) reflect().getPropertyValue(index);
		}
		if (trait != null) {
			boolean canBeCached = trait.reflect().getClassMirror().canCacheBehaviorInstance();
			if(canBeCached){
				if((result=cMirror.getTrait(index, state))==null){
					synchronized(state){
						if((result=cMirror.getTrait(index, state))==null){
							result = (T)produceTrait(trait, state);
							cMirror.cacheTrait(index, state, result);
						}
					}
				}
			}else{
				result = (T)produceTrait(trait, state);
			}
		}
		return result;	
	}
	
	private<T extends Trait> T produceTrait(T trait, InkObjectState state) {
		return ((TraitMirror)trait.reflect()).adapt(state);
	}

	@Override
	public void bind(ClassMirror cls) throws WeaveException {
		Mirror reflection = reflect();
		byte count = reflection.getPropertiesCount();
		ClassEditor editor = cls.edit();
		Trait t;
		TraitClass tClass;
		List<? extends Property> injectedProps;
		for(byte i=0;i<count;i++){
			t = (Trait) reflection.getPropertyValue(i);
			if(t!=null){
				tClass= (TraitClass) t.getMeta();
				injectedProps = tClass.getInjectedTargetProperties();
				if(injectedProps!=null && injectedProps.size()>0){
					editor.weaveStructuralTrait(reflection.getPropertyMirror(i).getName(), tClass);
				}
			}
		}
	}
	
	@Override
	public Trait getTrait(byte index) {
		Mirror m = reflect();
		if(index < m.getPropertiesCount()){
			return (Trait) m.getPropertyValue(index);
		}
		return null;
	}

}
