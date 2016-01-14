package giraudsa.marshall.serialisation.text.json;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.TextMarshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonCollectionType;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonDictionary;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonObject;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithoutQuote;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonBoolean;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonDate;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonInteger;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonString;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonVoid;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.ConfigurationMarshalling;
import utils.Constants;

public class JsonMarshaller extends TextMarshaller {
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonMarshaller.class);
	private final String clefType;
	private boolean isFirst = true;
	
	// ///CONSTRUCTEUR
	private JsonMarshaller(Writer output, boolean isCompleteSerialisation) throws IOException {
		super(output, isCompleteSerialisation, ConfigurationMarshalling.getDatFormatJson());
		clefType = ConfigurationMarshalling.getEstIdUniversel() ? Constants.CLEF_TYPE_ID_UNIVERSEL : Constants.CLEF_TYPE;
	}

	// /////METHODES PUBLIQUES STATIQUES
	public static <U> void toJson(U obj, Writer output) throws MarshallExeption {
		try {
			JsonMarshaller v = new JsonMarshaller(output, false);
			v.marshall(obj);
		} catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NotImplementedSerializeException e) {
			LOGGER.debug("probleme de sérialisation json de " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
	}

	public static <U> String toJson(U obj) throws MarshallExeption{
		try (StringWriter sw = new StringWriter()) {
			toJson(obj, sw);
			return sw.toString();
		} catch (IOException e) {
			LOGGER.debug("Problème à la création d'un StringWriter", e);
			throw new MarshallExeption(e);
		}
	}
	public static <U> void toCompleteJson(U obj, Writer output) throws MarshallExeption{
		try {
			JsonMarshaller v = new JsonMarshaller(output, true);
			v.marshall(obj);
		} catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NotImplementedSerializeException e) {
			LOGGER.debug("probleme de sérialisation complète en json de " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
	}


	public static <U> String toCompleteJson(U obj) throws MarshallExeption{
		try(StringWriter sw = new StringWriter()){
			toCompleteJson(obj, sw);
			return sw.toString();
		} catch (IOException e) {
			LOGGER.debug("Problème à la création d'un StringWriter", e);
			throw new MarshallExeption(e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void initialiseDico() {
		dicoTypeToAction.put(Date.class, new ActionJsonDate(this));
		dicoTypeToAction.put(Boolean.class, new ActionJsonBoolean(this));
		dicoTypeToAction.put(Collection.class, new ActionJsonCollectionType(this));
		dicoTypeToAction.put(Map.class, new ActionJsonDictionary(this));
		dicoTypeToAction.put(Object.class, new ActionJsonObject(this));
		dicoTypeToAction.put(void.class, new ActionJsonVoid(this));
		dicoTypeToAction.put(Integer.class, new ActionJsonInteger(this));
		dicoTypeToAction.put(Enum.class, new ActionJsonSimpleWithQuote<Enum>(this));
		dicoTypeToAction.put(UUID.class, new ActionJsonSimpleWithQuote<UUID>(this));
		dicoTypeToAction.put(String.class, new ActionJsonString(this));
		dicoTypeToAction.put(Byte.class, new ActionJsonSimpleWithoutQuote<Byte>(this));
		dicoTypeToAction.put(Float.class, new ActionJsonSimpleWithoutQuote<Float>(this));
		dicoTypeToAction.put(Double.class, new ActionJsonSimpleWithoutQuote<Double>(this));
		dicoTypeToAction.put(Long.class, new ActionJsonSimpleWithoutQuote<Long>(this));
		dicoTypeToAction.put(Short.class, new ActionJsonSimpleWithoutQuote<Short>(this));
	}

	// ///ME
	
	protected void ecritClef(String nomClef) throws IOException {
		if(isPrettyPrint()){
			aLaLigne();
		}
		if(nomClef != null){
			writer.write("\"" + nomClef + "\":");
		}
	}

	protected void ecritType(Class<?> type) throws IOException {
		ecritClef(clefType);
		String stringType = Constants.getSmallNameType(type);
		writeWithQuote(stringType);
	}

	protected void writeWithQuote(String string) throws IOException {
		writer.write("\"" + string + "\"");
	}

	protected void writeSeparator() throws IOException {
		writer.write(",");
	}

	protected void ouvreAccolade() throws IOException {
		++niveau;
		writer.write("{");
	}

	protected void fermeAccolade() throws IOException {
		--niveau;
		if(isPrettyPrint()){
			aLaLigne();
		}
		writer.write("}");
	}

	protected void ouvreCrochet() throws IOException {
		++niveau;
		writer.write("[");
	}

	protected void fermeCrochet() throws IOException {
		niveau--;
		if(isPrettyPrint()){
			aLaLigne();
		}
		writer.write("]");
	}

	///prettyPrint methode
	private void aLaLigne() throws IOException {
		if(isFirst ){
			isFirst = false;
			return;
		}
		writer.write(System.lineSeparator());
		for(int j = 0; j < niveau; j++){
			writer.write("   ");
		}
	}
}
