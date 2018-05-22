package giraudsa.marshall.deserialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.Array;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SetValueException;
import giraudsa.marshall.exception.UnmarshallExeption;
import utils.champ.FakeChamp;

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
		if (!deserialisationFini)
			litObject(fakeChamp);
		else
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
			tailleCollection = readInt();
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
		Array.set(obj, index++, objet);
		deserialisationFini = index >= tailleCollection;
		if (deserialisationFini)
			exporteObject();
	}
}
