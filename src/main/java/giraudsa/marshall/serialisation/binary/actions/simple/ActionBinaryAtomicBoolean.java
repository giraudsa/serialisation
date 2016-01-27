package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActionBinaryAtomicBoolean extends ActionBinary<AtomicBoolean> {

	
	public ActionBinaryAtomicBoolean() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, AtomicBoolean atomicBoolean, FieldInformations fieldInformations, boolean isDejaVu) throws IOException{
		if(!isDejaVu){
			setDejaTotalementSerialise(marshaller, atomicBoolean);
			writeBoolean(marshaller, atomicBoolean.get());
		}
	}
}
