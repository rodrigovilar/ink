package org.ink.eclipse.editors.highlight;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.ink.eclipse.editors.utils.ColorManager;
import org.ink.eclipse.editors.utils.WhitespaceDetector;

public class InkScanner extends RuleBasedScanner {

	public InkScanner(ColorManager manager) {
		// IToken procInstr =
		// new Token(
		// new TextAttribute(
		// manager.getColor(InkColorConstants.PROC_INSTR)));
		// IToken docType =
		// new Token(
		// new TextAttribute(
		// manager.getColor(InkColorConstants.DOCTYPE)));

		IRule[] rules = new IRule[1];
		// Add rule for processing instructions and doctype
		// rules[0] = new MultiLineRule("<?", "?>", procInstr);
		// rules[1] = new MultiLineRule("<!DOCTYPE", ">", docType);
		// Add generic whitespace rule.
		rules[0] = new WhitespaceRule(new WhitespaceDetector());

		setRules(rules);
	}
}
