package com.actemium.marshall.deserialisation.json;

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

import com.actemium.marshall.deserialisation.EntityManager;
import com.actemium.marshall.deserialisation.Unmarshaller;
import com.actemium.marshall.deserialisation.json.actions.ActionJsonCollectionType;
import com.actemium.marshall.deserialisation.json.actions.ActionJsonDate;
import com.actemium.marshall.deserialisation.json.actions.ActionJsonDictionaryType;
import com.actemium.marshall.deserialisation.json.actions.ActionJsonObject;
import com.actemium.marshall.deserialisation.json.actions.ActionJsonSimpleComportement;
import com.actemium.marshall.deserialisation.json.actions.ActionJsonUUID;
import com.actemium.marshall.deserialisation.json.actions.ActionJsonVoid;
import com.actemium.marshall.exception.JsonHandlerException;
import com.actemium.marshall.exception.NotImplementedSerializeException;

public class JsonUnmarshaller<T> extends Unmarshaller<T> {
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
		behaviors.put(Date.class, ActionJsonDate.class);
		behaviors.put(Iterable.class, ActionJsonCollectionType.class);
		behaviors.put(Collection.class, ActionJsonCollectionType.class);
		behaviors.put(List.class, ActionJsonCollectionType.class);
		behaviors.put(Map.class, ActionJsonDictionaryType.class);
		behaviors.put(Object.class, ActionJsonObject.class);
		behaviors.put(void.class, ActionJsonVoid.class);
		behaviors.put(Boolean.class, ActionJsonSimpleComportement.class);
		behaviors.put(Enum.class, ActionJsonSimpleComportement.class);
		behaviors.put(UUID.class, ActionJsonUUID.class);
		behaviors.put(String.class, ActionJsonSimpleComportement.class);
		behaviors.put(Byte.class, ActionJsonSimpleComportement.class);
		behaviors.put(Float.class, ActionJsonSimpleComportement.class);
		behaviors.put(Integer.class, ActionJsonSimpleComportement.class);
		behaviors.put(Double.class, ActionJsonSimpleComportement.class);
		behaviors.put(Long.class, ActionJsonSimpleComportement.class);
		behaviors.put(Short.class, ActionJsonSimpleComportement.class);
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
		Class<? extends ActionJson> behavior = (Class<? extends ActionJson>) getBehavior(type);
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
		Class<? extends ActionJson> behavior = (Class<? extends ActionJson>) getBehavior(type);
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
