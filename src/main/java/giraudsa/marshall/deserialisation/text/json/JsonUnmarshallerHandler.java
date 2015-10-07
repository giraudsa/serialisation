package giraudsa.marshall.deserialisation.text.json;

import giraudsa.marshall.exception.JsonHandlerException;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;

public class JsonUnmarshallerHandler {
	
	private ArrayList<Character> buff = new ArrayList<>();
	private boolean ignoreNextchar = false;
	private boolean isBetweenQuote = false;
	
	private static final char quote = '\"';
	
	public JsonUnmarshaller<?> jsonUnmarshaller;
	
	JsonUnmarshallerHandler(JsonUnmarshaller<?> jsonUnmarshaller) {
		this.jsonUnmarshaller = jsonUnmarshaller;
	}
	
	void parse(Reader reader) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, NotImplementedSerializeException, JsonHandlerException, ParseException{
		int t = reader.read();
		while (t != -1){
			traiteCaractere(t);
			t = reader.read();
		}
		
	}

	private void traiteCaractere(int t) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, NotImplementedSerializeException, JsonHandlerException, ParseException {
		if(!ignoreNextchar){
			char c = (char)t;
			comportement(c);
		}else{
			buff.add((char)t);
			ignoreNextchar = false;
		}
	}
	
	private void deuxPoints() throws JsonHandlerException{
		if(!isBetweenQuote) setClef();
		else buff.add(':');
	}
	
	private void virgule() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, NotImplementedSerializeException, ParseException {
		if(!isBetweenQuote)	setValeur();
		else buff.add(',');
	}
	
	private void quote() {
		isBetweenQuote = !isBetweenQuote;
	}
	
	private void ouvreAccolade(){
		if(!isBetweenQuote) buff.clear();
	}

	private void fermeAccolade() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, NotImplementedSerializeException, ParseException{
		if(!isBetweenQuote){
			if(!buff.isEmpty()){
				setValeur();
			}
			jsonUnmarshaller.fermeAccolade();
		}
	}
	private void ouvreCrochet() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException{
		if(!isBetweenQuote) jsonUnmarshaller.ouvreChrochet();
	}
	
	private void fermeCrochet() throws InstantiationException, IllegalAccessException{
		if(!isBetweenQuote) jsonUnmarshaller.fermeCrocher();
	}

	private void comportement(char c) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, JsonHandlerException, ClassNotFoundException, ParseException {
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
	

	private void setValeur() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, NotImplementedSerializeException, ParseException{
		if(!buff.isEmpty()){
			Class<?> type = null;
			if(!enleveGuillemets()){
				type = guessType(); 
			}
			jsonUnmarshaller.setValeur(getString(), type);
		}
	}

	private boolean enleveGuillemets() {
		if(!buff.isEmpty() && buff.get(0) == quote && buff.get(buff.size()-1) == quote){
			buff.remove(0);
			buff.remove(buff.size()-1);
			return true;
		}
		return false;
	}
	
	private String getString() {
		String s = new String(ArrayUtils.toPrimitive(buff.toArray(new Character[buff.size()])), 0, buff.size());
		buff.clear();
		return StringEscapeUtils.unescapeJson(s);
	}
	
	private Class<?> guessType() {
		switch (buff.get(0)){
		case 't':
		case 'f':
			return Boolean.class;
		default:
			return Integer.class;
		}
	}
	
	private void setClef() throws JsonHandlerException {
		if(enleveGuillemets()){
			String clef = getString();
			jsonUnmarshaller.setClef(clef);	
		}else{
			throw new JsonHandlerException("la clef n'a pas de guillemets");
		}
		
	}
}
