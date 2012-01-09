package org.ink.eclipse.editors.partitioner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class InkPartitionScanner extends RuleBasedPartitionScanner {

	public final static String SDL_DEFAULT = "__ink_default";
	public final static String INK_COMMENT = "__ink_comment";
	public final static String INK_STRING = "__ink_string";
	public final static String INK_START_OBJECT = "__ink_start_object";

	public InkPartitionScanner() {
		IToken inkComment = new Token(INK_COMMENT);
		IToken inkString = new Token(INK_STRING);
		// IToken startObject = new Token(INK_START_OBJECT);
		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();
		rules.add(new EndOfLineRule("//", inkComment));
		rules.add(new MultiLineRule("/*", "*/", inkComment));
		rules.add(new SingleLineRule("\"", "\"", inkString, '\\'));
		// rules.add(new StartObjectRule(startObject));

		setPredicateRules(rules.toArray(new IPredicateRule[] {}));
	}
}