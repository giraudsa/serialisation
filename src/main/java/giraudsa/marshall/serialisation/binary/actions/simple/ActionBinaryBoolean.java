package giraudsa.marshall.serialisation.binary.actions.simple;

import utils.Constants;
import utils.champ.FieldInformations;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;





public class ActionBinaryBoolean extends ActionBinarySimple<Boolean> {

	private byte[] headerTrue = new byte[]{Constants.BoolValue.TRUE};
	private byte[] headerFalse = new byte[]{Constants.BoolValue.FALSE};
	
	public ActionBinaryBoolean() {
		super();
	}

	@Override
	protected byte[] calculHeaders(Marshaller marshaller, Boolean objetASerialiser, boolean typeDevinable, boolean isDejaVu) {
		return objetASerialiser ? headerTrue : headerFalse;
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, Boolean objetASerialiser, FieldInformations fieldInformations) throws IOException{
		//rien à faire l'information est dans le header
	}


}
