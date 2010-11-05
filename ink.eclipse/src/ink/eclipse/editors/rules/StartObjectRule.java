package ink.eclipse.editors.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

public class StartObjectRule extends MultiLineRule
{

	public StartObjectRule(IToken token)
	{
		this(token, false);
	}	
	
	protected StartObjectRule(IToken token, boolean endAsWell)
	{
		super("\n", "\n", token);
	}

	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed)
	{
		int c = scanner.read();
		while(c=='\n' || c == ICharacterScanner.EOF){
			c = scanner.read();
		}
		if(c == ICharacterScanner.EOF){
			return false;
		}
		scanner.unread();
		return super.sequenceDetected(scanner, sequence, eofAllowed);
//		int c = scanner.read();
//		if (sequence[0] == '<')
//		{
//			if (c == '?')
//			{
//				// processing instruction - abort
//				scanner.unread();
//				return false;
//			}
//			if (c == '!')
//			{
//				scanner.unread();
//				// comment - abort
//				return false;
//			}
//		}
//		else if (sequence[0] == '>')
//		{
//			scanner.unread();
//		}
	}
}