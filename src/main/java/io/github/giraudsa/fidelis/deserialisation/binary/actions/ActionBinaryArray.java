package io.github.giraudsa.fidelis.deserialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.Array;

import io.github.giraudsa.fidelis.deserialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.deserialisation.Unmarshaller;
import io.github.giraudsa.fidelis.deserialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.deserialisation.binary.BinaryUnmarshaller;
import io.github.giraudsa.fidelis.exception.EntityManagerImplementationException;
import io.github.giraudsa.fidelis.exception.InstanciationException;
import io.github.giraudsa.fidelis.exception.NotImplementedSerializeException;
import io.github.giraudsa.fidelis.exception.SetValueException;
import io.github.giraudsa.fidelis.exception.UnmarshallExeption;
import io.github.giraudsa.fidelis.utils.champ.FakeChamp;

@SuppressWarnings("rawtypes")
public class ActionBinaryArray<T> extends ActionBinary<T> {
	public static ActionAbstrait<Object> getInstance() { // NOSONAR
		return new ActionBinaryArray<>(Object.class, null);
	}

	private boolean deserialisationFini = false;
	private FakeChamp fakeChamp;
	private int index = 0;

	private int tailleCollection;

	private ActionBinaryArray(final Class<T> type, final BinaryUnmarshaller<?> b) {
		super(type, b);
	}

	@Override
	public void deserialisePariellement()
			throws ClassNotFoundException, NotImplementedSerializeException, IOException, UnmarshallExeption,
			InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		while (!deserialisationFini) {
			final Object valeur = litValeur(fakeChamp);
			if (isEnAttente(valeur))
				return;
			ajoute(valeur);
		}
		exporteObject();
	}

	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionBinaryArray<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {

		final Class<?> componentType = fieldInformations.getValueType().getComponentType();
		fakeChamp = new FakeChamp(null, componentType, fieldInformations.getRelation(),
				fieldInformations.getAnnotations());
		if (isDejaVu() && !isDejaTotalementDeSerialise() && strategieDeSerialiseTout()) {
			obj = getObjet();
			tailleCollection = Array.getLength(obj);
			setDejaTotalementDeSerialise();
			deserialisationFini = index >= tailleCollection;
		} else if (isDejaVu()) { // il est soit déjà totalement désérialisé, soit il n'est pas à désérialiser
			deserialisationFini = true;
			obj = getObjet();
		} else { // !dejavu
			tailleCollection = readVarInt();
			obj = Array.newInstance(componentType, tailleCollection);
			stockeObjetId();
			if (strategieDeSerialiseTout())
				setDejaTotalementDeSerialise();
			deserialisationFini = index >= tailleCollection;
		}
	}

	@Override
	protected void integreObjet(final String nom, final Object objet) throws IllegalAccessException,
			EntityManagerImplementationException, InstanciationException, SetValueException {
		ajoute(objet);
		if (deserialisationFini)
			exporteObject();
	}

	private void ajoute(final Object objet) {
		Array.set(obj, index++, objet);
		deserialisationFini = index >= tailleCollection;
	}
}
