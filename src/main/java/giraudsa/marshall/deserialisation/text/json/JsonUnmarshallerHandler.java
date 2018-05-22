package giraudsa.marshall.deserialisation.text.json;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.JsonHandlerException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SetValueException;
import giraudsa.marshall.exception.UnmarshallExeption;

public class JsonUnmarshallerHandler {
	private static final char ESPACE = ' ';

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUnmarshallerHandler.class);
	private static final char QUOTE = '\"';

	private static void erreurParsing(final String message, final Exception e) throws UnmarshallExeption {
		LOGGER.error(message, e);
		throw new UnmarshallExeption(message, e);
	}

	private static char escapeCharactere(final Reader reader) throws UnmarshallExeption {
		char result = 0;
		final char[] tmp = new char[4];
		try {
			tmp[0] = (char) reader.read();
			tmp[1] = (char) reader.read();
			tmp[2] = (char) reader.read();
			tmp[3] = (char) reader.read();
		} catch (final IOException e) {
			erreurParsing("message tronqué", e);
		}
		for (int i = 0, end = 4; i < end; i++) {
			final char c = tmp[i];
			result <<= 4;
			if (c >= '0' && c <= '9')
				result += c - '0';
			else if (c >= 'a' && c <= 'f')
				result += c - 'a' + 10;
			else if (c >= 'A' && c <= 'F')
				result += c - 'A' + 10;
			else
				throw new NumberFormatException("\\u" + new String(tmp));
		}
		return result;
	}

	private static char readEscapeCharacter(final Reader reader) throws UnmarshallExeption {
		char escaped = 0;
		try {
			escaped = (char) reader.read();
		} catch (final IOException e) {
			erreurParsing("message tronqué", e);
		}
		switch (escaped) {
		case 'u':
			return escapeCharactere(reader);
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

	private final ArrayList<Character> buff = new ArrayList<>();

	private boolean ignoreNextchar = false;

	private boolean isBetweenQuote = false;

	private final JsonUnmarshaller<?> jsonUnmarshaller;

	protected JsonUnmarshallerHandler(final JsonUnmarshaller<?> jsonUnmarshaller) {
		this.jsonUnmarshaller = jsonUnmarshaller;
	}

	private void comportement(final char c)
			throws ClassNotFoundException, EntityManagerImplementationException, InstanciationException,
			NotImplementedSerializeException, JsonHandlerException, IllegalAccessException, SetValueException {
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

	private void deuxPoints() throws NotImplementedSerializeException, JsonHandlerException {
		if (!isBetweenQuote)
			setClef();
		else
			buff.add(':');
	}

	private void enleveEspaceEtSautDeLigne() {
		while (buff.indexOf(ESPACE) != -1)
			buff.remove(buff.indexOf(ESPACE));
		while (buff.indexOf('\n') != -1) {
			if (buff.indexOf('\r') != -1)
				buff.remove(buff.indexOf('\r'));
			buff.remove(buff.indexOf('\n'));
		}
	}

	private boolean enleveGuillemets() {
		final int firstQuote = buff.indexOf(QUOTE);
		final int lastQuote = buff.lastIndexOf(QUOTE);
		final int size = buff.size();
		if (!buff.isEmpty() && firstQuote != -1 && lastQuote != firstQuote) {
			for (int i = 0; i <= firstQuote; i++)
				buff.remove(0);
			for (int i = 0; i < size - lastQuote; i++)
				buff.remove(buff.size() - 1);
			return true;
		}
		return false;
	}

	private void fermeAccolade() throws EntityManagerImplementationException, InstanciationException,
			ClassNotFoundException, NotImplementedSerializeException, IllegalAccessException, SetValueException {
		if (!isBetweenQuote) {
			if (!buff.isEmpty())
				setValeur();
			jsonUnmarshaller.fermeAccolade();
		} else
			buff.add('}');
	}

	private void fermeCrochet() throws ClassNotFoundException, EntityManagerImplementationException,
			InstanciationException, NotImplementedSerializeException, IllegalAccessException, SetValueException {
		if (!isBetweenQuote) {
			if (!buff.isEmpty())
				setValeur();
			jsonUnmarshaller.fermeCrocher();
		} else
			buff.add(']');
	}

	private String getString() {
		final StringBuilder sb = new StringBuilder();
		for (final Character character : buff)
			sb.append(character);
		buff.clear();
		return sb.toString();
	}

	private Class<?> guessType() {
		switch (buff.get(0)) {
		case 't':
		case 'f':
			return Boolean.class;
		case 'n':
			return Void.class;
		default:
			return Integer.class;
		}
	}

	private void ouvreAccolade() {
		if (!isBetweenQuote) {
			buff.clear();
			jsonUnmarshaller.ouvreAccolade();
		} else
			buff.add('{');
	}

	private void ouvreCrochet() throws NotImplementedSerializeException {
		if (!isBetweenQuote)
			jsonUnmarshaller.ouvreChrochet();
		else
			buff.add('[');
	}

	protected void parse(final Reader reader) throws IOException, ClassNotFoundException,
			EntityManagerImplementationException, InstanciationException, NotImplementedSerializeException,
			JsonHandlerException, UnmarshallExeption, IllegalAccessException, SetValueException {
		int t = reader.read();
		while (t != -1) {
			traiteCaractere(t, reader);
			if (!ignoreNextchar)
				t = reader.read();
		}

	}

	private void quote() {
		isBetweenQuote = !isBetweenQuote;
	}

	private void setClef() throws NotImplementedSerializeException, JsonHandlerException {
		if (enleveGuillemets()) {
			final String clef = getString();
			jsonUnmarshaller.setClef(clef);
		} else
			throw new JsonHandlerException("la clef n'a pas de guillemets");

	}

	private void setValeur() throws ClassNotFoundException, EntityManagerImplementationException,
			InstanciationException, NotImplementedSerializeException, IllegalAccessException, SetValueException {
		if (!buff.isEmpty()) {
			Class<?> typeGuess = String.class;
			if (!enleveGuillemets()) {
				enleveEspaceEtSautDeLigne();
				if (buff.isEmpty())
					return;
				typeGuess = guessType();
			}
			jsonUnmarshaller.setValeur(getString(), typeGuess);
		}
	}

	private void traiteCaractere(final int t, final Reader reader) throws ClassNotFoundException,
			EntityManagerImplementationException, InstanciationException, NotImplementedSerializeException,
			JsonHandlerException, UnmarshallExeption, IllegalAccessException, SetValueException {
		if (!ignoreNextchar) {
			final char c = (char) t;
			comportement(c);
		} else {
			buff.add(readEscapeCharacter(reader));
			ignoreNextchar = false;
		}
	}

	private void virgule() throws ClassNotFoundException, EntityManagerImplementationException, InstanciationException,
			NotImplementedSerializeException, IllegalAccessException, SetValueException {
		if (!isBetweenQuote)
			setValeur();
		else
			buff.add(',');
	}
}
