package org.ink.eclipse.editors.highlight;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.ink.eclipse.editors.utils.ColorManager;
import org.ink.eclipse.editors.utils.InkColorConstants;
import org.ink.eclipse.editors.utils.WhitespaceDetector;

public class InkTagScanner extends RuleBasedScanner {

	public InkTagScanner(ColorManager manager) {
		IToken string = new Token(new TextAttribute(manager.getColor(InkColorConstants.STRING)));

		IRule[] rules = new IRule[3];

		// Add rule for double quotes
		rules[0] = new SingleLineRule("\"", "\"", string, '\\');
		// Add a rule for single quotes
		rules[1] = new SingleLineRule("'", "'", string, '\\');
		// Add generic whitespace rule.
		rules[2] = new WhitespaceRule(new WhitespaceDetector());

		setRules(rules);
	}
}
