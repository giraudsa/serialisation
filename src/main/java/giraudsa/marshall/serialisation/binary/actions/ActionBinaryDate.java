package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.util.Date;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;
import utils.headers.Header;
import utils.headers.HeaderTypeCourant;

public class ActionBinaryDate extends ActionBinary<Date> {

	public ActionBinaryDate() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Date date, final FieldInformations fieldInformations,
			final boolean isDejaVu) throws IOException {
		if (!isDejaVu) {
			setDejaTotalementSerialise(marshaller, date);
			writeLong(marshaller, date.getTime());
		}
	}

	@Override
	protected boolean writeHeaders(final Marshaller marshaller, final Date date,
			final FieldInformations fieldInformations) throws MarshallExeption, IOException {
		if (date.getClass() == Date.class) {
			final boolean isDejaVu = isDejaVuDate(marshaller, date);
			final int smallId = getSmallIdDateAndStockDate(marshaller, date);
			final HeaderTypeCourant<?> header = HeaderTypeCourant.getHeader(date, smallId);
			header.write(getOutput(marshaller), smallId);
			return isDejaVu;
		}
		final Class<?> typeObj = getTypeObjProblemeHibernate(date);
		final boolean isDejaVu = isDejaVu(marshaller, date);
		final boolean isTypeDevinable = isTypeDevinable(marshaller, date, fieldInformations);
		final boolean isDejaVuType = isDejaVuType(marshaller, typeObj);
		final int smallId = getSmallIdAndStockObj(marshaller, date);
		final short smallIdType = getSmallIdTypeAndStockType(marshaller, typeObj);
		final Header<?> header = Header.getHeader(isDejaVu, isTypeDevinable, smallId, smallIdType);
		header.write(getOutput(marshaller), smallId, smallIdType, isDejaVuType, typeObj);
		return isDejaVu;
	}
}
