package giraudsa.marshall.deserialisation.text.json;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.EntityManager;
import giraudsa.marshall.deserialisation.text.ActionText;
import giraudsa.marshall.deserialisation.text.TextUnmarshaller;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonArrayType;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonAtomicIntegerArray;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonAtomicLongArray;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonBitSet;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonCalendar;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonCollectionType;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonCurrency;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonDate;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonDictionaryType;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonEnum;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonInetAddress;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonLocale;
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigurationMarshalling;
import utils.Constants;
import utils.TypeExtension;

public class JsonUnmarshaller<T> extends TextUnmarshaller<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUnmarshaller.class);
	private static final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = Collections.synchronizedMap(new HashMap<Class<?>, ActionAbstrait<?>>());
	static{
		dicoTypeToAction.put(Date.class, ActionJsonDate.getInstance());
		dicoTypeToAction.put(Collection.class, ActionJsonCollectionType.getInstance());
		dicoTypeToAction.put(Array.class, ActionJsonArrayType.getInstance());
		dicoTypeToAction.put(Map.class, ActionJsonDictionaryType.getInstance());
		dicoTypeToAction.put(Object.class, ActionJsonObject.getInstance());
		dicoTypeToAction.put(UUID.class, ActionJsonUUID.getInstance());
		dicoTypeToAction.put(Enum.class, ActionJsonEnum.getInstance());
		dicoTypeToAction.put(void.class, ActionJsonVoid.getInstance());
		dicoTypeToAction.put(Void.class, ActionJsonVoid.getInstance());
		
		dicoTypeToAction.put(String.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Boolean.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Byte.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Float.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Integer.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Double.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Long.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Short.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Character.class, ActionJsonSimpleComportement.getInstance());
		
		dicoTypeToAction.put(AtomicBoolean.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(AtomicInteger.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(AtomicLong.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(AtomicIntegerArray.class, ActionJsonAtomicIntegerArray.getInstance());
		dicoTypeToAction.put(AtomicLongArray.class, ActionJsonAtomicLongArray.getInstance());
		dicoTypeToAction.put(BigDecimal.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(BigInteger.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(URI.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(URL.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Currency.class, ActionJsonCurrency.getInstance());
		dicoTypeToAction.put(Locale.class, ActionJsonLocale.getInstance());
		dicoTypeToAction.put(InetAddress.class, ActionJsonInetAddress.getInstance());
		dicoTypeToAction.put(BitSet.class, ActionJsonBitSet.getInstance());
		dicoTypeToAction.put(Calendar.class, ActionJsonCalendar.getInstance());
		dicoTypeToAction.put(StringBuilder.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(StringBuffer.class, ActionJsonSimpleComportement.getInstance());
	}

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
	ParseException, ClassNotFoundException, IOException, UnmarshallExeption {
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

	protected void fermeAccolade() throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IOException, NotImplementedSerializeException, UnmarshallExeption {
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

	protected void fermeCrocher() throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IOException, NotImplementedSerializeException, UnmarshallExeption {
		integreObject();
	}

	@SuppressWarnings("unchecked")
	private void integreObject() throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IOException, NotImplementedSerializeException, UnmarshallExeption {
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

	@Override
	protected Map<Class<?>, ActionAbstrait<?>> getdicoTypeToAction() {
		return dicoTypeToAction;
	}
}
