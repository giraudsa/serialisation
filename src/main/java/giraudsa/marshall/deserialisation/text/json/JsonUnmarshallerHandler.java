package giraudsa.marshall.deserialisation.text.json;

import giraudsa.marshall.exception.JsonHandlerException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.UnmarshallExeption;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUnmarshallerHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUnmarshallerHandler.class);
	
	private ArrayList<Character> buff = new ArrayList<Character>();
	private boolean ignoreNextchar = false;
	private boolean isBetweenQuote = false;
	
	private static final char QUOTE = '\"';
	private static final char ESPACE = ' ';
	
	private JsonUnmarshaller<?> jsonUnmarshaller;
	
	protected JsonUnmarshallerHandler(JsonUnmarshaller<?> jsonUnmarshaller) {
		this.jsonUnmarshaller = jsonUnmarshaller;
	}
	
	protected void parse(Reader reader) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, NotImplementedSerializeException, JsonHandlerException, ParseException, UnmarshallExeption{
		int t = reader.read();
		while (t != -1){
			traiteCaractere(t, reader);
			if(!ignoreNextchar)
				t = reader.read();
		}
		
	}

	private void traiteCaractere(int t, Reader reader) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, NotImplementedSerializeException, JsonHandlerException, ParseException, UnmarshallExeption {
		if(!ignoreNextchar){
			char c = (char)t;
			comportement(c);
		}else{
			buff.add(readEscapeCharacter(reader));
			ignoreNextchar = false;
		}
	}
	
	private void deuxPoints() throws JsonHandlerException, InstantiationException, IllegalAccessException, ClassNotFoundException, NotImplementedSerializeException{
		if(!isBetweenQuote) 
			setClef();
		else buff.add(':');
	}
	
	private void virgule() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, NotImplementedSerializeException, ParseException, IOException, UnmarshallExeption {
		if(!isBetweenQuote)
			setValeur();
		else buff.add(',');
	}
	
	private void quote() {
		isBetweenQuote = !isBetweenQuote;
	}
	
	private void ouvreAccolade(){
		if(!isBetweenQuote){
			buff.clear();
			jsonUnmarshaller.ouvreAccolade();
		}
		else buff.add('{');
	}

	private void fermeAccolade() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, NotImplementedSerializeException, ParseException, IOException, UnmarshallExeption{
		if(!isBetweenQuote){
			if(!buff.isEmpty()){
				setValeur();
			}
			jsonUnmarshaller.fermeAccolade();
		}
		else buff.add('}');
	}
	private void ouvreCrochet() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException{
		if(!isBetweenQuote) 
			jsonUnmarshaller.ouvreChrochet();
		else buff.add('[');
	}
	
	private void fermeCrochet() throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IOException, NotImplementedSerializeException, ParseException, UnmarshallExeption{
		if(!isBetweenQuote){
			if(!buff.isEmpty()){
				setValeur();
			}
			jsonUnmarshaller.fermeCrocher();
		}else buff.add(']');
	}

	private void comportement(char c) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, JsonHandlerException, ClassNotFoundException, ParseException, UnmarshallExeption {
		switch (c) {
		case '{':
			ouvreAccolade();
			break;
		case '}':
			fermeAccolade();
			break;
		case '[':
			ouvreCrochet();
			break;
		case ']':
			fermeCrochet();
			break;
		case ':':
			deuxPoints();
			break;
		case ',':
			virgule();
			break;
		case '"':
			quote();
			buff.add(c);
			break;
		case '\\':
			ignoreNextchar = true;
			break;
		case '/':
		case ';':
		case '#':
		case '=':
		case ' ':
		case '\t':
		case '\f':
		case '\r':
		case '\n':
		default:
			buff.add(c);
		}
	}
	

	private void setValeur() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, NotImplementedSerializeException, ParseException, IOException, UnmarshallExeption{
		if(!buff.isEmpty()){
			Class<?> type = String.class;
			if(!enleveGuillemets()){
				enleveEspaceEtSautDeLigne();
				if(buff.isEmpty())
					return;
				type = guessType(); 
			}
			jsonUnmarshaller.setValeur(getString(), type);
		}
	}

	private void enleveEspaceEtSautDeLigne() {
		while(buff.indexOf(ESPACE) != -1){
			buff.remove(buff.indexOf(ESPACE));
		}
		while(buff.indexOf('\r') != -1){
			buff.remove(buff.indexOf('\r'));
			buff.remove(buff.indexOf('\n'));
		}
	}

	private boolean enleveGuillemets() {
		int firstQuote = buff.indexOf(QUOTE);
		int lastQuote = buff.lastIndexOf(QUOTE);
		int size = buff.size();
		if(!buff.isEmpty() && firstQuote != -1 && lastQuote != firstQuote){
			for(int i = 0; i <= firstQuote; i++){
				buff.remove(0);
			}
			for(int i = 0; i < size - lastQuote; i++){
				buff.remove(buff.size()-1);
			}
			return true;
		}
		return false;
	}
	
	private String getString() {
		StringBuilder sb = new StringBuilder();
		for(Character character: buff){
			sb.append(character);
		}
		buff.clear();
		return sb.toString();
	}
	
	private Class<?> guessType() {
		switch (buff.get(0)){
		case 't':
		case 'f':
			return Boolean.class;
		case 'n':
			return Void.class;
		default:
			return Integer.class;
		}
	}
	
	private void setClef() throws JsonHandlerException, InstantiationException, IllegalAccessException, ClassNotFoundException, NotImplementedSerializeException {
		if(enleveGuillemets()){
			String clef = getString();
			jsonUnmarshaller.setClef(clef);	
		}else{
			throw new JsonHandlerException("la clef n'a pas de guillemets");
		}
		
	}
	
	
	private static char readEscapeCharacter(Reader reader) throws UnmarshallExeption {
		char escaped = 0;
		try {
			escaped = (char) reader.read();
		} catch (IOException e) {
			erreurParsing("message tronqué", e);
		}
	    switch (escaped) {
	    case 'u':
			return escapeCharactereU(reader);
	    case 't':
	      return '\t';
	    case 'b':
	      return '\b';
	    case 'n':
	      return '\n';
	    case 'r':
	      return '\r';
	    case 'f':
	      return '\f';
	    case '\n':
	    case '\'':
	    case '"':
	    case '\\':
	    default:
	      return escaped;
	    }
	  }

	private static char escapeCharactereU(Reader reader) throws UnmarshallExeption{
		char result = 0;
		char[] tmp = new char[4];
		try {
			reader.read(tmp);
		} catch (IOException e) {
			erreurParsing("message tronqué", e);
		}
		for (int i = 0, end = 4; i < end; i++) {
			char c = tmp[i];
			result <<= 4;
			if (c >= '0' && c <= '9') {
				result += (c - '0');
			} else if (c >= 'a' && c <= 'f') {
				result += (c - 'a' + 10);
			} else if (c >= 'A' && c <= 'F') {
				result += (c - 'A' + 10);
			} else {
				throw new NumberFormatException("\\u" + new String(tmp));
			}
		}
		return result;
	}

	private static void erreurParsing(String message, Exception e) throws UnmarshallExeption {
		LOGGER.error(message, e);
		throw new UnmarshallExeption(message, e);
	}
}
