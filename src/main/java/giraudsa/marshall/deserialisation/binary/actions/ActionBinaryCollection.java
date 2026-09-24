package giraudsa.marshall.deserialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
import utils.champ.FieldInformations;
import utils.TypeExtension;

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
		while (!deserialisationFini) {
			final Object valeur = litValeur(fakeChamp);
			if (isEnAttente(valeur))
				return;
			ajoute(valeur);
		}
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
			tailleCollection = readVarInt();
			deserialisationFini = index >= tailleCollection;
		}

		fakeChamp = fieldInformations.getChampParametre(FieldInformations.ELEMENT);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void integreObjet(final String nom, final Object objet) throws IllegalAccessException,
			EntityManagerImplementationException, InstanciationException, SetValueException {
		ajoute(objet);
		if (deserialisationFini)
			exporteObject();
	}

	@SuppressWarnings("unchecked")
	private void ajoute(final Object objet) {
		((Collection) obj).add(objet);
		deserialisationFini = ++index >= tailleCollection;
	}

	private Collection newInstance() throws UnmarshallExeption {
		Collection objetADeserialiser = null;
		try {
			if (type == ArrayList.class)
				objetADeserialiser = new ArrayList();
			else if (type == LinkedList.class)
				objetADeserialiser = new LinkedList();
			else if (type == HashSet.class)
				objetADeserialiser = new HashSet();
			else if (TypeExtension.isHibernate(type)) {
				if (fieldInformations.getValueType().isAssignableFrom(ArrayList.class))
					objetADeserialiser = new ArrayList();
				else if (fieldInformations.getValueType().isAssignableFrom(HashSet.class))
					objetADeserialiser = new HashSet();
				else
					throw new UnmarshallExeption("Probleme avec un type hibernate " + type.getName(),
							new InstantiationException());
			} else
				try {
					objetADeserialiser = type.getDeclaredConstructor().newInstance();
				} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
					// liste sans constructeur accessible (Arrays$ArrayList...) : on se rabat
					// sur ArrayList comme historiquement
					if (type.getName().indexOf("ArrayList") == -1)
						throw new InstantiationException(e.toString());
					objetADeserialiser = new ArrayList();
				}
		} catch (final InstantiationException e) {
			LOGGER.error("impossible d'instancier la collection " + type.getName(), e);
			throw new UnmarshallExeption("impossible d'instancier la collection " + type.getName(), e);
		}
		return objetADeserialiser;
	}
}
