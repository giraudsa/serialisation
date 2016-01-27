package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

import utils.champ.FieldInformations;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.BitSet;

public class ActionBinaryBitSet extends ActionBinary<BitSet> {


	public ActionBinaryBitSet() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, BitSet bitSet, FieldInformations fieldInformations, boolean isDejaVu) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException{
		if (!isDejaVu){
			setDejaTotalementSerialise(marshaller, bitSet);
			int size = bitSet.length();
			writeInt(marshaller, size);
			for (int i = 0; i < size; i++) {
				writeBoolean(marshaller, bitSet.get(i));
			}
		}
	}
}
