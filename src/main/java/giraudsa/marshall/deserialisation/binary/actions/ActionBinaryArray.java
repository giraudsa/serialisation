package giraudsa.marshall.deserialisation.binary.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.UnmarshallExeption;
import utils.champ.FakeChamp;

import java.io.IOException;
import java.lang.reflect.Array;

@SuppressWarnings("rawtypes")
public class ActionBinaryArray<T> extends ActionBinary<T> {
	private boolean deserialisationFini = false;
	private int tailleCollection;
	private int index = 0;
	private FakeChamp fakeChamp;
	private Class<?> componentType;
	
	private ActionBinaryArray(Class<T> type, BinaryUnmarshaller<?> b){
		super(type, b);
	}

	public static ActionAbstrait<Object> getInstance(){ // NOSONAR
		return new ActionBinaryArray<>(Object.class, null);
	}
	
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionBinaryArray<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}
	@Override
	public void deserialisePariellement() throws ClassNotFoundException, NotImplementedSerializeException, IOException, UnmarshallExeption, InstanciationException, IllegalAccessException, EntityManagerImplementationException {
		if(!deserialisationFini){
			litObject(fakeChamp);
		}else{
			exporteObject();
		}
	}

	@Override
	protected void integreObjet(String nom, Object objet) throws IllegalAccessException, EntityManagerImplementationException, InstanciationException {
		Array.set(obj, index++, objet);
		deserialisationFini = index >= tailleCollection;
		if(deserialisationFini)
			exporteObject();
	}

	@Override
	protected void initialise() throws IOException {
		componentType = fieldInformations.getValueType().getComponentType();
		fakeChamp = new FakeChamp(null, componentType, fieldInformations.getRelation());
		if (isDejaVu() && !isDejaTotalementDeSerialise() && strategieDeSerialiseTout()){
			obj = getObjet();
			tailleCollection = Array.getLength(obj);
			setDejaTotalementDeSerialise();
			deserialisationFini = index >= tailleCollection;
		}else if(isDejaVu()){ //il est soit déjà totalement désérialisé, soit il n'est pas à désérialiser
			deserialisationFini = true;
			obj = getObjet();
		}else{ //!dejavu
			tailleCollection = readInt();
			obj = Array.newInstance(componentType, tailleCollection);
			stockeObjetId();
			if(strategieDeSerialiseTout())
				setDejaTotalementDeSerialise();
			deserialisationFini = index >= tailleCollection;
		}
	}
}
