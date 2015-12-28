package giraudsa.marshall.deserialisation.text.json;

import giraudsa.marshall.deserialisation.EntityManager;
import giraudsa.marshall.deserialisation.text.ActionText;
import giraudsa.marshall.deserialisation.text.TextUnmarshaller;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonCollectionType;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonDate;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonDictionaryType;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonEnum;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.xml.sax.SAXException;

import utils.Constants;

public class JsonUnmarshaller<T> extends TextUnmarshaller<T> {
	/////ATTRIBUTS
	private boolean waitingForType;
	private boolean waitingForAction;
	private String clefEnCours;

	public static <U> U fromJson(Reader reader, EntityManager entity) throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, JsonHandlerException, ParseException{
		JsonUnmarshaller<U> w = new JsonUnmarshaller<U>(reader, entity, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")){};
		return w.parse();
	}

	public static <U> U fromJson(Reader reader) throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, JsonHandlerException, ParseException{
		return fromJson(reader, null, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
	}

	public static <U> U fromJson(String stringToUnmarshall)  throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, JsonHandlerException, ParseException{
		if(stringToUnmarshall == null || stringToUnmarshall.length() == 0) return null;
		try(StringReader sr = new StringReader(stringToUnmarshall)){
			return fromJson(sr, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		}
	}

	public static  <U> U fromJson(String stringToUnmarshall, EntityManager entity)  throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, JsonHandlerException, ParseException{
		if(stringToUnmarshall == null || stringToUnmarshall.length() == 0) return null;
		try(StringReader sr = new StringReader(stringToUnmarshall)){
			return fromJson(sr, entity);
		}
	}
	
	public static <U> U fromJson(Reader reader, EntityManager entity, DateFormat df) throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, JsonHandlerException, ParseException{
		JsonUnmarshaller<U> w = new JsonUnmarshaller<U>(reader, entity, df){};
		return w.parse();
	}

	public static <U> U fromJson(Reader reader, DateFormat df) throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, JsonHandlerException, ParseException{
		return fromJson(reader, null, df);
	}

	public static <U> U fromJson(String stringToUnmarshall, DateFormat df)  throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, JsonHandlerException, ParseException{
		if(stringToUnmarshall == null || stringToUnmarshall.length() == 0) return null;
		try(StringReader sr = new StringReader(stringToUnmarshall)){
			return fromJson(sr, null, df);
		}
	}

	public static  <U> U fromJson(String stringToUnmarshall, EntityManager entity, DateFormat df)  throws IOException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, JsonHandlerException, ParseException{
		if(stringToUnmarshall == null || stringToUnmarshall.length() == 0) return null;
		try(StringReader sr = new StringReader(stringToUnmarshall)){
			return fromJson(sr, entity, df);
		}
	}

	private JsonUnmarshaller(Reader reader, EntityManager entity, DateFormat df) throws ClassNotFoundException {
		super(reader, entity, df);
		initDico();
	}

	private void initDico() {
		actions.put(Date.class, ActionJsonDate.getInstance(this));
		actions.put(Collection.class, ActionJsonCollectionType.getInstance(this));
		actions.put(Map.class, ActionJsonDictionaryType.getInstance(this));
		actions.put(Object.class, ActionJsonObject.getInstance(this));
		actions.put(void.class, ActionJsonVoid.getInstance(this));
		actions.put(UUID.class, ActionJsonUUID.getInstance(this));
		actions.put(Enum.class, ActionJsonEnum.getInstance(this));
		actions.put(Void.class, ActionJsonVoid.getInstance(this));
		
		actions.put(String.class, ActionJsonSimpleComportement.getInstance(String.class, this));
		actions.put(Boolean.class, ActionJsonSimpleComportement.getInstance(Boolean.class, this));
		actions.put(Byte.class, ActionJsonSimpleComportement.getInstance(Byte.class, this));
		actions.put(Float.class, ActionJsonSimpleComportement.getInstance(Float.class, this));
		actions.put(Integer.class, ActionJsonSimpleComportement.getInstance(Integer.class, this));
		actions.put(Double.class, ActionJsonSimpleComportement.getInstance(Double.class, this));
		actions.put(Long.class, ActionJsonSimpleComportement.getInstance(Long.class, this));
		actions.put(Short.class, ActionJsonSimpleComportement.getInstance(Short.class, this));
		actions.put(Character.class, ActionJsonSimpleComportement.getInstance(Character.class,this));
	}

	private T parse() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, NotImplementedSerializeException, JsonHandlerException, ParseException {
		JsonUnmarshallerHandler handler = new JsonUnmarshallerHandler(this);
		handler.parse(reader);
		return obj;
	}

	void setClef(String clef) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NotImplementedSerializeException {
		if(clef.equals(Constants.CLEF_TYPE)){
			waitingForType = true;
		}else if(!pileAction.isEmpty()){
			if(waitingForAction){
				pileAction.push(getAction(getType((ActionJson<?>)getActionEnCours(), clefEnCours)));
				setNom((ActionText<?>) getActionEnCours(), clefEnCours);
				waitingForAction = false;
			}
			clefEnCours = clef;			
		}
	}

	void setValeur(String valeur, Class<?> type) throws NotImplementedSerializeException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
	ParseException, IllegalArgumentException, SecurityException, ClassNotFoundException, IOException {
		Class<?> type2 = null;
		if(waitingForType){
			type2 = getType(valeur);
		}else{
			type2 = getType((ActionJson<?>)getActionEnCours(), clefEnCours);
		}
		if(type2 != null && !type2.isAssignableFrom(type)){
			type = type2;
		}
		ActionJson<?> action = (ActionJson<?>) getAction(type);
		setNom(action, clefEnCours);
		clefEnCours = null;
		pileAction.push(action);
		if(!waitingForType){
			rempliData(getActionEnCours(),valeur);
			integreObject();
		}
		waitingForType = false;
		waitingForAction = false;
	}

	void fermeAccolade() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
		integreObject();
	}


	void ouvreChrochet() throws NotImplementedSerializeException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?> type = getType((ActionJson<?>)getActionEnCours(), clefEnCours);
		if(type == null) type = ArrayList.class;
		ActionJson<?> action = (ActionJson<?>) getAction(type);
		setNom(action, clefEnCours);
		clefEnCours = null;
		pileAction.push(action);
	}

	void fermeCrocher() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
		integreObject();
	}

	private Class<?> getType(String smallNameType) throws ClassNotFoundException {
		return Class.forName(Constants.getNameType(Constants.getNameType(smallNameType)));
	}

	@SuppressWarnings("unchecked")
	private void integreObject() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
		construitObjet(getActionEnCours());
		ActionJson<?> actionATraiter = (ActionJson<?>) pileAction.pop();
		if(pileAction.isEmpty()){
			obj = (T)getObjet(actionATraiter);
		}else{
			String nom = getNom(actionATraiter);
			Object objet = getObjet(actionATraiter);
			integreObjet(getActionEnCours(), nom, objet);
		}
	}

	public void ouvreAccolade() {
		waitingForAction = true;
	}
}
