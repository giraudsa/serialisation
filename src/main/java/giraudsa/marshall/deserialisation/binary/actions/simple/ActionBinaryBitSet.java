package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.util.BitSet;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

@SuppressWarnings("rawtypes")
public class ActionBinaryBitSet extends ActionBinarySimple<BitSet> {
	public static ActionAbstrait<BitSet> getInstance() { // NOSONAR
		return new ActionBinaryBitSet(BitSet.class, null);
	}

	private ActionBinaryBitSet(final Class<BitSet> type, final BinaryUnmarshaller<?> b) {
		super(type, b);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends BitSet> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryBitSet(BitSet.class, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		if (isDejaVu())
			obj = getObjet();
		else {
			final int taille = readInt();
			obj = new BitSet(taille);
			for (int i = 0; i < taille; i++)
				((BitSet) obj).set(i, readBoolean());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
