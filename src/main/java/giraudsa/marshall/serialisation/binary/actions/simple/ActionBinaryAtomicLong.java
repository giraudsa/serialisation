package giraudsa.marshall.serialisation.binary.actions.simple;


import utils.champ.FieldInformations;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import giraudsa.marshall.serialisation.Marshaller;

public class ActionBinaryAtomicLong  extends ActionBinarySimple<AtomicLong>{

	public ActionBinaryAtomicLong() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, AtomicLong objetASerialiser, FieldInformations fieldInformations) throws IOException {
		writeLong(marshaller, objetASerialiser.get());
	}

}
