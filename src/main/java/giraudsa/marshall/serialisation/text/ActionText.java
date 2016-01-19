package giraudsa.marshall.serialisation.text;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.ActionAbstrait;
import giraudsa.marshall.serialisation.Marshaller;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.util.Map;

public abstract class ActionText<T> extends ActionAbstrait<T> {
	protected ActionText() {
		super();
	}

	protected DateFormat getDateFormat(Marshaller marshaller){
		return getTextMarshaller(marshaller).df;
	}
	
	@Override
	protected boolean isUniversalId(Marshaller marshaller) {
		return getTextMarshaller(marshaller).isUniversalId;
	}

	private TextMarshaller getTextMarshaller(Marshaller marshaller){
		return (TextMarshaller)marshaller;
	}
	
	protected void write(Marshaller marshaller, String s) throws IOException {
		getTextMarshaller(marshaller).write(s);
	}
	
	protected void write(Marshaller marshaller, char s) 
			throws IOException {
		getTextMarshaller(marshaller).write(s);
	}
	
	protected boolean serialiseTout(Marshaller marshaller, Object obj, FieldInformations fieldInformations) {
		if (isCompleteMarshalling(marshaller) && ! isDejaVu(marshaller, obj))
			return true;
		return !isCompleteMarshalling(marshaller) && fieldInformations.getRelation() == TypeRelation.COMPOSITION && !isDejaTotalementSerialise(marshaller, obj);
	}

	protected abstract Map<Character, String> getRemplacementChar();
	protected void writeEscape(Marshaller marshaller, String toBeEscaped) throws IOException{
		if (toBeEscaped.isEmpty())
			write(marshaller, toBeEscaped);
		StringReader sr = new StringReader(toBeEscaped);
		int i = sr.read();
		while(i != -1){
			String r = getRemplacementChar().get((char)i);
			if(r == null)
				write(marshaller, (char)i);
			else
				write(marshaller, r);
			i = sr.read();
		}
	}

}
