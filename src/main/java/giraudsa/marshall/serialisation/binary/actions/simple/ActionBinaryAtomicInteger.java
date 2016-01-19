package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import giraudsa.marshall.serialisation.Marshaller;
import utils.champ.FieldInformations;

public class ActionBinaryAtomicInteger  extends ActionBinarySimple<AtomicInteger>{

	public ActionBinaryAtomicInteger() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, AtomicInteger objetASerialiser, FieldInformations fieldInformations) throws IOException {
		writeInt(marshaller, objetASerialiser.get());
	}

}
