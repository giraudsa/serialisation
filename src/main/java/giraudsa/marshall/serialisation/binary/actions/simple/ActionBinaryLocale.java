package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;
import java.util.Locale;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;

public class ActionBinaryLocale extends ActionBinary<Locale> {

	@Override
	protected void ecritValeur(Marshaller marshaller, Locale locale, FieldInformations fieldInformation, boolean isDejaVu) throws IOException{
		if(!isDejaVu){
			setDejaTotalementSerialise(marshaller, locale);
			writeUTF(marshaller, locale.toString());
		}
	}

}
