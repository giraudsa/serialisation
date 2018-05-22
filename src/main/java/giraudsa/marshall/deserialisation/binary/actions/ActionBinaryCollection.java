package giraudsa.marshall.deserialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ActionBinaryCollection<C extends Collection> extends ActionBinary<C> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionBinaryCollection.class);

	public static ActionAbstrait<?> getInstance() { // NOSONAR
		return new ActionBinaryCollection<>(Collection.class, null);
	}

	private boolean deserialisationFini = false;
	private FakeChamp fakeChamp;
	private int index = 0;

	private int tailleCollection;

	private ActionBinaryCollection(final Class<C> type, final BinaryUnmarshaller<?> b) {
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
	public <U extends C> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionBinaryCollection<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws UnmarshallExeption, IOException {
		if (isDejaVu() && !isDejaTotalementDeSerialise() && strategieDeSerialiseTout()) {
			obj = getObjet();
			tailleCollection = ((Collection) obj).size();
			setDejaTotalementDeSerialise();
			deserialisationFini = index < tailleCollection;
		} else if (isDejaVu()) {
			deserialisationFini = true;
			obj = getObjet();
		} else {// !isDejaVu
			obj = newInstance();
			stockeObjetId();
			if (strategieDeSerialiseTout())
				setDejaTotalementDeSerialise();
			tailleCollection = readInt();
			deserialisationFini = index >= tailleCollection;
		}

		final Type[] types = fieldInformations.getParametreType();
		Type parametreType = Object.class;
		if (types.length > 0)
			parametreType = types[0];
		fakeChamp = new FakeChamp(null, parametreType, fieldInformations.getRelation(),
				fieldInformations.getAnnotations());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void integreObjet(final String nom, final Object objet) throws IllegalAccessException,
			EntityManagerImplementationException, InstanciationException, SetValueException {
		((Collection) obj).add(objet);
		deserialisationFini = ++index >= tailleCollection;
		if (deserialisationFini)
			exporteObject();
	}

	private Collection newInstance() throws UnmarshallExeption {
		Collection objetADeserialiser = null;
		try {
			if (type == ArrayList.class || type.getName().indexOf("ArrayList") != -1)
				objetADeserialiser = new ArrayList();
			else if (type == LinkedList.class)
				objetADeserialiser = new LinkedList();
			else if (type == HashSet.class)
				objetADeserialiser = new HashSet();
			else if (type.getName().toLowerCase().indexOf("hibernate") != -1) {
				if (fieldInformations.getValueType().isAssignableFrom(ArrayList.class))
					objetADeserialiser = new ArrayList();
				else if (fieldInformations.getValueType().isAssignableFrom(HashSet.class))
					objetADeserialiser = new HashSet();
				else
					throw new UnmarshallExeption("Probleme avec un type hibernate " + type.getName(),
							new InstantiationException());
			} else
				objetADeserialiser = type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			LOGGER.error("impossible d'instancier la collection " + type.getName(), e);
			throw new UnmarshallExeption("impossible d'instancier la collection " + type.getName(), e);
		}
		return objetADeserialiser;
	}
}
