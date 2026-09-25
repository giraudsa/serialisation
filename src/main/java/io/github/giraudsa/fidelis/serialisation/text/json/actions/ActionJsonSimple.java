package io.github.giraudsa.fidelis.serialisation.text.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import io.github.giraudsa.fidelis.exception.MarshallExeption;
import io.github.giraudsa.fidelis.exception.NotImplementedSerializeException;
import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.text.json.ActionJson;
import io.github.giraudsa.fidelis.utils.Constants;
import io.github.giraudsa.fidelis.utils.champ.Champ;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public abstract class ActionJsonSimple<T> extends ActionJson<T> {

	protected ActionJsonSimple() {
		super();
	}

	/**
	 * Une valeur simple n'empile aucun travail : on écrit directement clef,
	 * valeur et fermeture, dans le même ordre que les deux comportements empilés
	 * par {@link ActionJson#marshall}, sans les allouer.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void marshall(final Marshaller marshaller, final Object obj, final FieldInformations fieldInformations)
			throws MarshallExeption {
		final boolean typeDevinable = isTypeDevinable(marshaller, obj, fieldInformations);
		final boolean nePasEcrireType = writeType(marshaller) ? typeDevinable : true;
		try {
			if (fieldInformations instanceof Champ)
				ecritClef(marshaller, (Champ) fieldInformations);
			else
				ecritClef(marshaller, fieldInformations.getName());
			final boolean separateurAEcrire = commenceObject(marshaller, (T) obj, nePasEcrireType);
			ecritValeur(marshaller, (T) obj, fieldInformations, separateurAEcrire);
			clotureObject(marshaller, (T) obj, nePasEcrireType);
		} catch (IOException | IllegalAccessException | InstantiationException | InvocationTargetException
				| NoSuchMethodException | NotImplementedSerializeException e) {
			throw new MarshallExeption(e);
		}
	}

	@Override
	protected void clotureObject(final Marshaller marshaller, final T obj, final boolean typeDevinable)
			throws IOException {
		if (!typeDevinable)
			fermeAccolade(marshaller);
	}

	@Override
	protected boolean commenceObject(final Marshaller marshaller, final T obj, final boolean typeDevinable)
			throws IOException {
		if (!typeDevinable) {
			ouvreAccolade(marshaller);
			ecritType(marshaller, obj);
			writeSeparator(marshaller);
			ecritClef(marshaller, Constants.VALEUR);
			return false;
		}
		return true;
	}
}
