package giraudsa.marshall.deserialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SetValueException;
import giraudsa.marshall.exception.UnmarshallExeption;
import utils.TypeExtension;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

@SuppressWarnings("rawtypes")
public class ActionBinaryDictionary<D extends Map> extends ActionBinary<D> {

	public static ActionAbstrait<Map> getInstance() {
		return new ActionBinaryDictionary<>(Map.class, null);
	}

	private boolean clefLue = false;
	private Object clefTampon;
	private boolean deserialisationFini = false;
	private FakeChamp fakeChampKey;
	private FakeChamp fakeChampValue;
	private int index = 0;

	private int tailleCollection;

	private ActionBinaryDictionary(final Class<D> type, final BinaryUnmarshaller<?> b) {
		super(type, b);
	}

	@Override
	public void deserialisePariellement()
			throws ClassNotFoundException, NotImplementedSerializeException, IOException, UnmarshallExeption,
			InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		while (!deserialisationFini) {
			final Object valeur = litValeur(clefLue ? fakeChampValue : fakeChampKey);
			if (isEnAttente(valeur))
				return;
			ajoute(valeur);
		}
		exporteObject();
	}

	@Override
	public <U extends D> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionBinaryDictionary<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws UnmarshallExeption, IOException {
		if (isDejaVu() && !isDejaTotalementDeSerialise() && strategieDeSerialiseTout()) {
			obj = getObjet();
			setDejaTotalementDeSerialise();
			tailleCollection = ((Map) obj).size();
			deserialisationFini = index < tailleCollection;
		} else if (isDejaVu()) {
			deserialisationFini = true;
			obj = getObjet();
		} else if (!isDejaVu()) {
			obj = newInstance();
			stockeObjetId();
			if (strategieDeSerialiseTout())
				setDejaTotalementDeSerialise();
			tailleCollection = readVarInt();
			deserialisationFini = index >= tailleCollection;
		}
		fakeChampKey = fieldInformations.getChampParametre(FieldInformations.CLE);
		fakeChampValue = fieldInformations.getChampParametre(FieldInformations.VALEUR);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void integreObjet(final String name, final Object objet) throws IllegalAccessException,
			EntityManagerImplementationException, InstanciationException, SetValueException {
		ajoute(objet);
		if (deserialisationFini)
			exporteObject();
	}

	@SuppressWarnings("unchecked")
	private void ajoute(final Object objet) {
		if (!clefLue) {
			clefTampon = objet;
			clefLue = true;
		} else {
			((Map) obj).put(clefTampon, objet);
			clefTampon = null;
			clefLue = false;
			deserialisationFini = ++index >= tailleCollection;
		}
	}

	private Object newInstance() throws UnmarshallExeption {
		return nouvelleMap(type, fieldInformations);
	}

	/** Instancie la map de type donné (lecture directe ou par action). */
	@SuppressWarnings("rawtypes")
	public static Object nouvelleMap(final Class<?> type, final FieldInformations fi) throws UnmarshallExeption {
		Map objetADeserialiser = null;
		try {
			if (type == HashMap.class)
				objetADeserialiser = new HashMap<>();
			else if (type == LinkedHashMap.class)
				objetADeserialiser = new LinkedHashMap<>();
			else if (TypeExtension.isHibernate(type)) {
				if (fi.getValueType().isAssignableFrom(ConcurrentHashMap.class))
					objetADeserialiser = new ConcurrentHashMap<>();
				else if (fi.getValueType().isAssignableFrom(LinkedHashMap.class))
					objetADeserialiser = new LinkedHashMap<>();
				else if (fi.getValueType().isAssignableFrom(HashMap.class))
					objetADeserialiser = new HashMap<>();
				else
					throw new UnmarshallExeption("Probleme avec un type hibernate " + type.getName(),
							new InstantiationException());
			} else
				try {
					objetADeserialiser = (Map) type.getDeclaredConstructor().newInstance();
				} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
					// map sans constructeur accessible : on se rabat sur HashMap comme
					// historiquement
					if (type.getName().indexOf("HashMap") == -1)
						throw new InstantiationException(e.toString());
					objetADeserialiser = new HashMap<>();
				}
		} catch (final InstantiationException e) {
			throw new UnmarshallExeption("impossible d'instancier la collection " + type.getName(), e);
		}
		return objetADeserialiser;
	}

}