package giraudsa.marshall.deserialisation.text.json;

import giraudsa.marshall.deserialisation.EntityManager;
import giraudsa.marshall.deserialisation.text.ActionText;
import giraudsa.marshall.deserialisation.text.TextUnmarshaller;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonArrayType;
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
import giraudsa.marshall.exception.UnmarshallExeption;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigurationMarshalling;
import utils.Constants;
import utils.TypeExtension;

public class JsonUnmarshaller<T> extends TextUnmarshaller<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUnmarshaller.class);
	/////ATTRIBUTS
	private boolean waitingForType;
	private boolean waitingForAction;
	private String clefEnCours;
	private String clefType;
	
	///////methodes public de désérialisation

	private JsonUnmarshaller(Reader reader, EntityManager entity) throws ClassNotFoundException, IOException {
		super(reader, entity, ConfigurationMarshalling.getDatFormatJson());
	}

	public static <U> U fromJson(Reader reader, EntityManager entity) throws UnmarshallExeption{
		try {
			JsonUnmarshaller<U> w = new JsonUnmarshaller<>(reader, entity);
			return w.parse();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException | IOException
				| NotImplementedSerializeException | JsonHandlerException | ParseException e) {
			LOGGER.error("probleme dans la désérialisation JSON", e);
			throw new UnmarshallExeption(e);
		}
	}

	public static <U> U fromJson(Reader reader) throws UnmarshallExeption{
		return fromJson(reader, null);
	}

	public static <U> U fromJson(String stringToUnmarshall) throws UnmarshallExeption{
		if(stringToUnmarshall == null || stringToUnmarshall.length() == 0) 
			return null;
		try(StringReader sr = new StringReader(stringToUnmarshall)){
			return fromJson(sr);
		}
	}

	public static  <U> U fromJson(String stringToUnmarshall, EntityManager entity) throws UnmarshallExeption{
		if(stringToUnmarshall == null || stringToUnmarshall.length() == 0)
			return null;
		try(StringReader sr = new StringReader(stringToUnmarshall)){
			return fromJson(sr, entity);
		}
	}


	@Override
	protected void initialiseActions() throws IOException {
		actions.put(Date.class, ActionJsonDate.getInstance(this));
		actions.put(Collection.class, ActionJsonCollectionType.getInstance(this));
		actions.put(Array.class, ActionJsonArrayType.getInstance(this));
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

	private T parse() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, NotImplementedSerializeException, JsonHandlerException, ParseException, UnmarshallExeption {
		JsonUnmarshallerHandler handler = new JsonUnmarshallerHandler(this);
		handler.parse(reader);
		return obj;
	}

	protected void setClef(String clef) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NotImplementedSerializeException {
		if(isClefType(clef)){
			waitingForType = true;
		}else if(!pileAction.isEmpty()){
			if(waitingForAction){
				Class<?> typeToUnmarshall = getType(clefEnCours);
				typeToUnmarshall = TypeExtension.getTypeEnveloppe(typeToUnmarshall);
				ActionText<?> action = (ActionText<?>) getAction(typeToUnmarshall);
				setNom(action, clefEnCours);
				setFieldInformation(action);
				pileAction.push(action);
				waitingForAction = false;
			}
			clefEnCours = clef;
		}
	}

	private boolean isClefType(String clef) {
		if(clef.equals(clefType) || clef.equals(Constants.CLEF_TYPE) || clef.equals(Constants.CLEF_TYPE_ID_UNIVERSEL)){
			if(clefType == null){
				//récuperation de la configuration idUniversel de celui qui a encodé
				boolean isIdUniversel = clef.equals(Constants.CLEF_TYPE_ID_UNIVERSEL) ? true : false;
				clefType = clef;
				setCache(isIdUniversel);
			}
			return true;
		}
		return false;
	}


	protected void setValeur(String valeur, Class<?> type) throws NotImplementedSerializeException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
	ParseException, ClassNotFoundException, IOException {
		Class<?> type2;
		if(waitingForType){
			type2 = getTypeDepuisNom(valeur);
		}else{
			type2 = getType(clefEnCours);
		}
		Class<?> typeAction = type;
		if(type2 != null && !type2.isAssignableFrom(type)){
			typeAction = type2;
		}
		ActionJson<?> action = (ActionJson<?>) getAction(typeAction);
		setNom(action, clefEnCours);
		setFieldInformation(action);
		clefEnCours = null;
		pileAction.push(action);
		if(!waitingForType){
			rempliData(getActionEnCours(),valeur);
			integreObject();
		}
		waitingForType = false;
		waitingForAction = false;
	}

	protected void fermeAccolade() throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IOException, NotImplementedSerializeException {
		integreObject();
	}


	protected void ouvreChrochet() throws NotImplementedSerializeException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Class<?> type = getType(clefEnCours);
		if(type == null)
			type = ArrayList.class;
		ActionJson<?> action = (ActionJson<?>) getAction(type);
		setNom(action, clefEnCours);
		setFieldInformation(action);
		clefEnCours = null;
		pileAction.push(action);
	}

	protected void fermeCrocher() throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IOException, NotImplementedSerializeException {
		integreObject();
	}

	@SuppressWarnings("unchecked")
	private void integreObject() throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IOException, NotImplementedSerializeException {
		construitObjet(getActionEnCours());
		ActionJson<?> actionATraiter = (ActionJson<?>) pileAction.pop();
		if(pileAction.isEmpty())
			obj = (T)getObjet(actionATraiter);
		else{
			String nom = getNom(actionATraiter);
			Object objet = getObjet(actionATraiter);
			integreObjet(getActionEnCours(), nom, objet);
		}
	}

	protected void ouvreAccolade() {
		waitingForAction = true;
	}
}
