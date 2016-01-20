package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import utils.champ.FieldInformations;

public class ActionBinaryVoid extends ActionBinarySimple<Void> {

	public ActionBinaryVoid() {
		super();
	}
	
	@Override
	protected byte[] calculHeaders(Marshaller marshaller, Void objetASerialiser, boolean typeDevinable, boolean isDejaVu) {
		return getHeaderConstant(getType(objetASerialiser), typeDevinable);
	}
	@Override
	protected void ecritValeur(Marshaller marshaller, Void objetASerialiser, FieldInformations fieldInformations) throws IOException {
		//rien à écrire si l'objet est null.
	}
}
