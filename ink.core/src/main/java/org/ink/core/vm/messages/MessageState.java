package org.ink.core.vm.messages;

import static org.ink.core.vm.factory.internal.CoreNotations.Ids.COMPONENT_TYPE_VIOLATION;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.ENUM_ILLEGAL_VALUE;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.JAVA_MAPPING_ERROR;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.MAX_VALUE_VIOLATION;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.MIN_VALUE_VIOLATION;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.MISSING_FIELD_DATA;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.REFINEMENT_VIOLATION;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.REG_EXP_SYNATX_ERROR;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.STRING_MAX_LENGTH_VIOLATION;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.STRING_MIN_LENGTH_VIOLATION;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.STRING_REG_EXP_VIOLATION;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.VALUES_COMPARISON_VIOLATION;
import static org.ink.core.vm.factory.internal.CoreNotations.Ids.WRONG_VALUE_TYPE;

import org.ink.core.vm.lang.InkObjectState;
import org.ink.core.vm.lang.internal.annotations.CoreClassSpec;
import org.ink.core.vm.lang.internal.annotations.CoreField;
import org.ink.core.vm.lang.internal.annotations.CoreInstanceSpec;
import org.ink.core.vm.lang.internal.annotations.CoreInstanceValues;
import org.ink.core.vm.lang.internal.annotations.CoreInstanceValuesLocation;
/**
 * @author Lior Schachter
 */
@CoreClassSpec(metaclass=MessageClassState.class)
@CoreInstanceSpec(
		ids={MISSING_FIELD_DATA,
				WRONG_VALUE_TYPE,
				REFINEMENT_VIOLATION,
				COMPONENT_TYPE_VIOLATION,
				STRING_MIN_LENGTH_VIOLATION,
				STRING_MAX_LENGTH_VIOLATION,
				STRING_REG_EXP_VIOLATION,
				MIN_VALUE_VIOLATION,
				MAX_VALUE_VIOLATION,
				REG_EXP_SYNATX_ERROR,
				VALUES_COMPARISON_VIOLATION,
				ENUM_ILLEGAL_VALUE,
				JAVA_MAPPING_ERROR
		},
		locations = {@CoreInstanceValuesLocation(indexes = { MessageState.p_text }),
				@CoreInstanceValuesLocation(indexes = { MessageState.p_text }),
				@CoreInstanceValuesLocation(indexes = { MessageState.p_text }),
				@CoreInstanceValuesLocation(indexes = { MessageState.p_text }),
				@CoreInstanceValuesLocation(indexes = { MessageState.p_text }),
				@CoreInstanceValuesLocation(indexes = { MessageState.p_text }),
				@CoreInstanceValuesLocation(indexes = { MessageState.p_text }),
				@CoreInstanceValuesLocation(indexes = { MessageState.p_text }),
				@CoreInstanceValuesLocation(indexes = { MessageState.p_text }),
				@CoreInstanceValuesLocation(indexes = { MessageState.p_text }),
				@CoreInstanceValuesLocation(indexes = { MessageState.p_text }),
				@CoreInstanceValuesLocation(indexes = { MessageState.p_text }),
				@CoreInstanceValuesLocation(indexes = { MessageState.p_text })},
				values={@CoreInstanceValues(values={"The field ''{0}'' should not be empty."}),
				@CoreInstanceValues(values={"The field ''{0}'' is of wrong type; expected ''{1}'', actual ''{2}''."}),
				@CoreInstanceValues(values={"The object ''{0}'' should refine matching super field; expected ''{1}'', actual ''{2}''."}),
				@CoreInstanceValues(values={"The object ''{0}'' is defined as ''{1}'' component."}),
				@CoreInstanceValues(values={"The value length of field ''{0}'' should be at least {1} characters."}),
				@CoreInstanceValues(values={"The value length of field ''{0}'' should be at most {1} characters."}),
				@CoreInstanceValues(values={"The field ''{0}'' value should match the regular expression ''{1}''."}),
				@CoreInstanceValues(values={"The field ''{0}'' value should be equals or greater than {1}."}),
				@CoreInstanceValues(values={"The field ''{0}'' value should be equals or smaller than {1}."}),
				@CoreInstanceValues(values={"The field ''{0}'' value is not a vaild regular expression."}),
				@CoreInstanceValues(values={"The field ''{0}'' value ({1}) should be greater or equals to field ''{2}'' value ({3})."}),
				@CoreInstanceValues(values={"The value ''{0}'' is not part of ''{1}'' enumeration. Please select one of the following: [{2}]."}),
				@CoreInstanceValues(values={"Java-Mapping Error:{0}"}),
		})
		public interface MessageState extends InkObjectState{

	@CoreField(mandatory=true)
	public static final byte p_text = 0;

	public String getText();
	public void setText(String value);

	public class Data extends InkObjectState.Data implements MessageState{

		@Override
		public String getText() {
			return (String)getValue(p_text);
		}

		@Override
		public void setText(String value) {
			setValue(p_text, value);
		}
	}
}
