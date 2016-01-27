package giraudsa.marshall.serialisation.binary.actions.simple;

import utils.champ.FieldInformations;
import utils.headers.HeaderSimpleType;
import java.io.IOException;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;





public class ActionBinaryBoolean extends ActionBinary<Boolean> {
	
	public ActionBinaryBoolean() {
		super();
	}

	@Override
	protected boolean writeHeaders(Marshaller marshaller, Boolean bool, FieldInformations fi) throws IOException, MarshallExeption {
		HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(bool);
		header.writeValue(getOutput(marshaller), bool);
		return false;
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, Boolean objetASerialiser, FieldInformations fieldInformations, boolean isDejaVu) throws IOException{
		//rien à faire l'information est dans le header
	}

}
