package org.ink.core.vm.lang.operation;

import java.util.List;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.lang.internal.annotations.CoreListField;
import org.ink.core.vm.lang.operation.interceptors.OperationInterceptor;
import org.ink.core.vm.lang.operation.interceptors.OperationInterceptorState;

/**
 * @author Lior Schachter
 */
public interface OperationState extends InkObjectState{

	@CoreField(mandatory=true)
	public static final byte p_name = 0;

	@CoreListField(itemName="interceptor")
	public static final byte p_interceptors = p_name + 1;

	public String getName();
	public void setName(String value);

	public List<? extends OperationInterceptor> getInterceptors();
	public void setInterceptors(List<? extends OperationInterceptorState> value);

	public class Data extends InkObjectState.Data implements OperationState{
		@Override
		public String getName() {
			return (String)getValue(p_name);
		}

		@Override
		public void setName(String value) {
			setValue(p_name, value);
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<? extends OperationInterceptor> getInterceptors() {
			return (List<? extends OperationInterceptor>) getValue(p_interceptors);
		}

		@Override
		public void setInterceptors(
				List<? extends OperationInterceptorState> value) {
			setValue(p_interceptors, value);
		}
	}

}
