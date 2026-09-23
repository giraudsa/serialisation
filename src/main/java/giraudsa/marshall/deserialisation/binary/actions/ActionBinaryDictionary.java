package giraudsa.marshall.deserialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
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
		if (!deserialisationFini) {
			if (!clefLue)
				litObject(fakeChampKey);
			else
				litObject(fakeChampValue);
		} else
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
			tailleCollection = readInt();
			deserialisationFini = index >= tailleCollection;
		}
		final Type[] types = fieldInformations.getParametreType();
		Type parametreTypeKey = Object.class;
		Type parametreTypeValue = Object.class;
		if (types.length > 1) {
			parametreTypeKey = types[0];
			parametreTypeValue = types[1];
		}
		fakeChampKey = new FakeChamp(null, parametreTypeKey, fieldInformations.getRelation(),
				fieldInformations.getAnnotations());
		fakeChampValue = new FakeChamp(null, parametreTypeValue, fieldInformations.getRelation(),
				fieldInformations.getAnnotations());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void integreObjet(final String name, final Object objet) throws IllegalAccessException,
			EntityManagerImplementationException, InstanciationException, SetValueException {
		if (!clefLue) {
			clefTampon = objet;
			clefLue = true;
		} else {
			((Map) obj).put(clefTampon, objet);
			clefTampon = null;
			clefLue = false;
			deserialisationFini = ++index >= tailleCollection;
		}
		if (deserialisationFini)
			exporteObject();
	}

	private Object newInstance() throws UnmarshallExeption {
		Map objetADeserialiser = null;
		try {
			if (type == HashMap.class)
				objetADeserialiser = new HashMap<>();
			else if (type == LinkedHashMap.class)
				objetADeserialiser = new LinkedHashMap<>();
			else if (TypeExtension.isHibernate(type)) {
				if (fieldInformations.getValueType().isAssignableFrom(ConcurrentHashMap.class))
					objetADeserialiser = new ConcurrentHashMap<>();
				else if (fieldInformations.getValueType().isAssignableFrom(LinkedHashMap.class))
					objetADeserialiser = new LinkedHashMap<>();
				else if (fieldInformations.getValueType().isAssignableFrom(HashMap.class))
					objetADeserialiser = new HashMap<>();
				else
					throw new UnmarshallExeption("Probleme avec un type hibernate " + type.getName(),
							new InstantiationException());
			} else
				try {
					objetADeserialiser = type.getDeclaredConstructor().newInstance();
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