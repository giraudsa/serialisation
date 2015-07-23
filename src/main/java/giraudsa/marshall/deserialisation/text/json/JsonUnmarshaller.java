package giraudsa.marshall.deserialisation.text.json;

import giraudsa.marshall.deserialisation.EntityManager;
import giraudsa.marshall.deserialisation.text.TextUnmarshaller;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonCollectionType;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonDate;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonDictionaryType;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonObject;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonSimpleComportement;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonUUID;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonVoid;
import giraudsa.marshall.exception.JsonHandlerException;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.xml.sax.SAXException;

import utils.Constants;

public class JsonUnmarshaller<T> extends TextUnmarshaller<T> {
	/////ATTRIBUTS
	private boolean waitingForType;
	private String clefEnCours;
	private Stack<ActionJson<?>> pileAction = new Stack<ActionJson<?>>();
	private ActionJson<?> getActionEnCours(){
		return pileAction.peek();
	}

	public static <U> U fromJson(StringReader reader, EntityManager entity) throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, JsonHandlerException, ParseException{
		JsonUnmarshaller<U> w = new JsonUnmarshaller<U>(reader, entity){};
		return w.parse();
	}

	public static <U> U fromJson(StringReader reader) throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, JsonHandlerException, ParseException{
		return fromJson(reader, null);
	}

	public static <U> U fromXml(String stringToUnmarshall)  throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, JsonHandlerException, ParseException{
		if(stringToUnmarshall == null || stringToUnmarshall.length() == 0) return null;
		try(StringReader sr = new StringReader(stringToUnmarshall)){
			return fromJson(sr, null);
		}
	}

	public static  <U> U fromJson(String stringToUnmarshall, EntityManager entity)  throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, JsonHandlerException, ParseException{
		if(stringToUnmarshall == null || stringToUnmarshall.length() == 0) return null;
		try(StringReader sr = new StringReader(stringToUnmarshall)){
			return fromJson(sr, entity);
		}
	}

	private JsonUnmarshaller(Reader reader, EntityManager entity) throws ClassNotFoundException {
		super(reader, entity);
		typesAction.put(Date.class, ActionJsonDate.class);
		typesAction.put(Iterable.class, ActionJsonCollectionType.class);
		typesAction.put(Collection.class, ActionJsonCollectionType.class);
		typesAction.put(List.class, ActionJsonCollectionType.class);
		typesAction.put(Map.class, ActionJsonDictionaryType.class);
		typesAction.put(Object.class, ActionJsonObject.class);
		typesAction.put(void.class, ActionJsonVoid.class);
		typesAction.put(Boolean.class, ActionJsonSimpleComportement.class);
		typesAction.put(Enum.class, ActionJsonSimpleComportement.class);
		typesAction.put(UUID.class, ActionJsonUUID.class);
		typesAction.put(String.class, ActionJsonSimpleComportement.class);
		typesAction.put(Byte.class, ActionJsonSimpleComportement.class);
		typesAction.put(Float.class, ActionJsonSimpleComportement.class);
		typesAction.put(Integer.class, ActionJsonSimpleComportement.class);
		typesAction.put(Double.class, ActionJsonSimpleComportement.class);
		typesAction.put(Long.class, ActionJsonSimpleComportement.class);
		typesAction.put(Short.class, ActionJsonSimpleComportement.class);
	}

	private T parse() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, NotImplementedSerializeException, JsonHandlerException, ParseException {
		JsonUnmarshallerHandler handler = new JsonUnmarshallerHandler(this);
		handler.parse(reader);
		return obj;
	}

	void setClef(String clef) {
		if(clef.equals(Constants.CLEF_TYPE)){
			waitingForType = true;
		}else if(!pileAction.isEmpty()){
			clefEnCours = clef;			
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" }) void setValeur(String valeur, Class<?> type) throws NotImplementedSerializeException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
	ParseException, IllegalArgumentException, SecurityException, ClassNotFoundException {
		Class<?> type2 = null;
		if(waitingForType){
			type2 = getType(valeur);
		}else{
			type2 = getActionEnCours().getType(clefEnCours);
		}
		type = type2 != null ? type2 : type;
		Class<? extends ActionJson> behavior = (Class<? extends ActionJson>) getTypeAction(type);
		ActionJson<?> action = behavior.getConstructor(Class.class, String.class).newInstance(type, clefEnCours);
		clefEnCours = null;
		pileAction.push(action);
		if(!waitingForType){
			rempliData(getActionEnCours(),valeur);
			integreObject();
		}
		waitingForType = false;
	}

	void fermeAccolade() throws InstantiationException, IllegalAccessException {
		integreObject();
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	void ouvreChrochet() throws NotImplementedSerializeException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?> type = ArrayList.class;
		Class<? extends ActionJson> behavior = (Class<? extends ActionJson>) getTypeAction(type);
		ActionJson<?> action = behavior.getConstructor(Class.class, String.class).newInstance(type, clefEnCours);
		clefEnCours = null;
		pileAction.push(action);
	}

	void fermeCrocher() throws InstantiationException, IllegalAccessException {
		integreObject();
	}

	private Class<?> getType(String smallNameType) throws ClassNotFoundException {
		return Class.forName(Constants.getNameType(Constants.getNameType(smallNameType)));
	}

	@SuppressWarnings("unchecked")
	private void integreObject() throws InstantiationException, IllegalAccessException {
		construitObjet(getActionEnCours());
		ActionJson<?> actionATraiter = pileAction.pop();
		if(pileAction.isEmpty()){
			Object t = getObjet(actionATraiter);
			if(t instanceof Container) obj = (T) ((Container)t).get__Principal();
			else obj = (T)t;
		}else{
			String nom = getNom(actionATraiter);
			Object objet = getObjet(actionATraiter);
			integreObjet(getActionEnCours(), nom, objet);
		}
	}
}
