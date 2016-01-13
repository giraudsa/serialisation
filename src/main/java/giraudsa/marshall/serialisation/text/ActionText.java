package giraudsa.marshall.serialisation.text;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.ActionAbstrait;
import utils.ConfigurationMarshalling;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.text.DateFormat;

public abstract class ActionText<T> extends ActionAbstrait<T> {
	
	protected ActionText(TextMarshaller textM) {
		super(textM);
	}

	protected DateFormat getDateFormat(){
		return getTextMarshaller().df;
	}
	
	@Override
	protected boolean isUniversalId() {
		return getTextMarshaller().isUniversalId;
	}

	protected TextMarshaller getTextMarshaller(){
		return (TextMarshaller)marshaller;
	}
	
	protected void write(String s) throws IOException {
		getTextMarshaller().write(s);
	}
	
	protected boolean serialiseTout(Object obj, FieldInformations fieldInformations) {
		if (isCompleteMarshalling() && ! isDejaVu(obj))
			return true;
		return !isCompleteMarshalling() && fieldInformations.getRelation() == TypeRelation.COMPOSITION && !isDejaTotalementSerialise(obj);
	}

}
