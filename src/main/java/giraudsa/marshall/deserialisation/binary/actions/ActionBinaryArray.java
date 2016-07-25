package giraudsa.marshall.deserialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.UnmarshallExeption;
import utils.champ.FakeChamp;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

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
	public void deserialisePariellement() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IOException, NotImplementedSerializeException, UnmarshallExeption {
		if(!deserialisationFini){
			litObject(fakeChamp);
		}else{
			exporteObject();
		}
	}

	@Override
	protected void integreObjet(String nom, Object objet) throws IllegalAccessException, UnmarshallExeption {
		Array.set(obj, index++, objet);
		deserialisationFini = index >= tailleCollection;
		if(deserialisationFini)
			exporteObject();
	}

	@Override
	protected void initialise() throws IOException {
		componentType = fieldInformations.getValueType().getComponentType();
		fakeChamp = new FakeChamp(null, componentType, fieldInformations.getRelation());
		if (isDejaVu() && !isDeserialisationComplete() && fieldInformations.getRelation() == TypeRelation.COMPOSITION){
			obj = getObjet();
			tailleCollection = Array.getLength(obj);
			setDejaTotalementDeSerialise();
			deserialisationFini = index >= tailleCollection;
		}else if(isDejaVu()){
			deserialisationFini = true;
			obj = getObjet();
		}else{ //!dejavu
			tailleCollection = readInt();
			obj = Array.newInstance(componentType, tailleCollection);
			stockeObjetId();
			if(isDeserialisationComplete() || fieldInformations.getRelation() == TypeRelation.COMPOSITION)
				setDejaTotalementDeSerialise();
			deserialisationFini = index >= tailleCollection;
		}
	}
}
