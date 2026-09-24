package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.util.Date;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;
import utils.headers.HeaderTypeCourant;

public class ActionBinaryDate extends ActionBinary<Date> {

	public ActionBinaryDate() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Date date, final FieldInformations fieldInformations,
			final boolean isDejaVu) throws IOException {
		// feuille : l'indicateur « totalement sérialisé » n'est jamais relu. Le poser coûtait cher pour une Date,
		// sans smallId d'objet (dédupliquée par valeur) : table d'identité générique qui grossit à chaque date
		if (!isDejaVu)
			writeLong(marshaller, date.getTime());
	}

	@Override
	protected boolean writeHeaders(final Marshaller marshaller, final Date date,
			final FieldInformations fieldInformations) throws IOException {
		if (date.getClass() == Date.class) {
			final int id = smallIdDate(marshaller, date);
			final boolean isDejaVu = id > 0;
			final int smallId = isDejaVu ? id : -id;
			HeaderTypeCourant.getHeader(date, smallId, isDejaVu).write(getOutput(marshaller), smallId);
			return isDejaVu;
		}
		return writeHeadersObjet(marshaller, date, fieldInformations);
	}
}
