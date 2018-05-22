package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;
import utils.headers.HeaderSimpleType;

public class ActionBinaryByte extends ActionBinary<Byte> {

	public ActionBinaryByte() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Byte objetASerialiser,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		// rien a faire
	}

	@Override
	protected boolean writeHeaders(final Marshaller marshaller, final Byte octet, final FieldInformations fi)
			throws IOException, MarshallExeption {
		if (!fi.getValueType().isPrimitive()) {
			final HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(octet);
			header.writeValue(getOutput(marshaller), octet);
		} else
			writeByte(marshaller, octet);
		return false;
	}
}
