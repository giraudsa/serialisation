package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;

public class ActionBinaryAtomicInteger  extends ActionBinary<AtomicInteger>{

	public ActionBinaryAtomicInteger() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, AtomicInteger atomicInteger, FieldInformations fieldInformations, boolean isDejaVu) throws IOException {
		if(!isDejaVu){
			setDejaTotalementSerialise(marshaller, atomicInteger);
			writeInt(marshaller, atomicInteger.get());
		}
	}

}
