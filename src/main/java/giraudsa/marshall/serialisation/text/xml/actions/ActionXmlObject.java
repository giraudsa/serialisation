package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.TypeExtension;
import utils.champ.Champ;
import utils.champ.FieldInformations;

public class ActionXmlObject extends ActionXml<Object> {



	public ActionXmlObject() {
		super();
	}


	@Override protected void ecritValeur(Marshaller marshaller, Object obj, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException, MarshallExeption{
		Class<?> typeObj = (Class<?>) obj.getClass();
		List<Champ> champs = TypeExtension.getSerializableFields(typeObj);
		Champ champId = TypeExtension.getChampId(typeObj);
		boolean serialiseTout = serialiseTout(marshaller, obj, fieldInformations);
		setDejaVu(marshaller, obj);
		if(!serialiseTout){
			pushComportement(marshaller, traiteChamp(marshaller, obj, champId));
			return;
		}
		setDejaTotalementSerialise(marshaller, obj);
		Deque<Comportement> tmp = new ArrayDeque<>();
		for (Champ champ : champs){
			Comportement comportement = traiteChamp(marshaller, obj, champ);
			if(comportement != null) 
				tmp.push(comportement);
		}
		pushComportements(marshaller, tmp);
	}
	
	@Override
	protected boolean pushComportementParticulier(Marshaller marshaller, Object obj, String nomBalise, FieldInformations fieldInformations){
		boolean isComportementIdDansBalise = !serialiseTout(marshaller, obj, fieldInformations);
		if (isComportementIdDansBalise){
			pushComportement(marshaller, new ComportementIdDansBalise(obj, nomBalise, fieldInformations));
		}
		return isComportementIdDansBalise;
	}
	
	protected class ComportementIdDansBalise extends Comportement{

		private Object obj;
		private FieldInformations fieldInformations;
		private String nomBalise;
		
		protected ComportementIdDansBalise(Object obj, String nomBalise ,FieldInformations fieldInformations) {
			super();
			this.obj = obj;
			this.fieldInformations = fieldInformations;
			this.nomBalise = nomBalise;
		}

		@Override
		protected void evalue(Marshaller marshaller) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, MarshallExeption{
			boolean typeDevinable = isTypeDevinable(marshaller, obj, fieldInformations);
			Class<?> typeObj = (Class<?>) obj.getClass();
			Champ champId = TypeExtension.getChampId(typeObj);
			Object id = champId.get(obj, getDicoObjToFakeId(marshaller));
			if(id == null)
				throw new MarshallExeption("l'objet de type " + typeObj.getName() + " a un id null");
			ouvreBaliseEcritIdFermeBalise(marshaller, obj, nomBalise, typeDevinable,id.toString());
			setDejaVu(marshaller, obj);
		}
	
	}

}
