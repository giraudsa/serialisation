package giraudsa.marshall.serialisation.binary.actions.simple;


import utils.champ.FieldInformations;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

public class ActionBinaryAtomicLong  extends ActionBinary<AtomicLong>{

	public ActionBinaryAtomicLong() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, AtomicLong atomicLong, FieldInformations fieldInformations, boolean isDejaVu) throws IOException {
		if(!isDejaVu){
			setDejaTotalementSerialise(marshaller, atomicLong);
			writeLong(marshaller, atomicLong.get());
		}
	}

}
