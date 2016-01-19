package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.serialisation.Marshaller;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActionBinaryAtomicBoolean extends ActionBinarySimple<AtomicBoolean> {

	
	public ActionBinaryAtomicBoolean() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, AtomicBoolean atomicBoolean, FieldInformations fieldInformations) throws IOException{
		writeBoolean(marshaller, atomicBoolean.get());
	}
}
