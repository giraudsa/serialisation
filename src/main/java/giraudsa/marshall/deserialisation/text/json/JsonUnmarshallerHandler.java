package giraudsa.marshall.deserialisation.text.json;

import java.io.IOException;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.JsonHandlerException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SetValueException;
import giraudsa.marshall.exception.UnmarshallExeption;

public class JsonUnmarshallerHandler {
	private static final int FIN = -1;
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUnmarshallerHandler.class);
	private static final char QUOTE = '\"';
	private static final int TAILLE_BLOC = 8192;

	private static void erreurParsing(final String message, final Exception e) throws UnmarshallExeption {
		LOGGER.error(message, e);
		throw new UnmarshallExeption(message, e);
	}

	/** caractères accumulés depuis le dernier séparateur */
	private final StringBuilder buff = new StringBuilder();

	private boolean isBetweenQuote = false;

	private final JsonUnmarshaller<?> jsonUnmarshaller;

	// lecture par blocs : Reader.read() caractère par caractère est lent (et
	// synchronisé pour StringReader).
	private final char[] bloc = new char[TAILLE_BLOC];
	private int finBloc = 0;
	private int positionBloc = 0;
	private Reader reader;

	protected JsonUnmarshallerHandler(final JsonUnmarshaller<?> jsonUnmarshaller) {
		this.jsonUnmarshaller = jsonUnmarshaller;
	}

	private void comportement(final char c)
			throws ClassNotFoundException, EntityManagerImplementationException, InstanciationException,
			NotImplementedSerializeException, JsonHandlerException, IllegalAccessException, SetValueException,
			UnmarshallExeption {
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
			buff.append(c);
			break;
		case '\\':
			buff.append(readEscapeCharacter());
			break;
		default:
			buff.append(c);
		}
	}

	private void deuxPoints() throws NotImplementedSerializeException, JsonHandlerException {
		if (!isBetweenQuote)
			setClef();
		else
			buff.append(':');
	}

	/**
	 * Enlève les espaces et les sauts de ligne d'une valeur non quotée. Comme
	 * l'implémentation historique : tous les ' ' et '\n', et autant de '\r' (les
	 * premiers) qu'il y a de '\n'. Les tabulations sont conservées.
	 */
	private void enleveEspaceEtSautDeLigne() {
		int nbSautsDeLigne = 0;
		final int taille = buff.length();
		for (int i = 0; i < taille; i++)
			if (buff.charAt(i) == '\n')
				nbSautsDeLigne++;
		int ecriture = 0;
		for (int i = 0; i < taille; i++) {
			final char c = buff.charAt(i);
			if (c == ' ' || c == '\n')
				continue;
			if (c == '\r' && nbSautsDeLigne > 0) {
				nbSautsDeLigne--;
				continue;
			}
			buff.setCharAt(ecriture++, c);
		}
		buff.setLength(ecriture);
	}

	/**
	 * Garde ce qui est entre le premier et le dernier guillemet.
	 *
	 * @return false s'il n'y a pas deux guillemets distincts.
	 */
	private boolean enleveGuillemets() {
		final int firstQuote = buff.indexOf("\"");
		final int lastQuote = buff.lastIndexOf("\"");
		if (firstQuote != -1 && lastQuote != firstQuote) {
			buff.setLength(lastQuote);
			buff.delete(0, firstQuote + 1);
			return true;
		}
		return false;
	}

	private char escapeCharactere() throws UnmarshallExeption {
		char result = 0;
		final char[] tmp = new char[4];
		for (int i = 0; i < 4; i++)
			tmp[i] = (char) lit();
		for (final char c : tmp) {
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

	private void fermeAccolade() throws EntityManagerImplementationException, InstanciationException,
			ClassNotFoundException, NotImplementedSerializeException, IllegalAccessException, SetValueException {
		if (!isBetweenQuote) {
			if (buff.length() > 0)
				setValeur();
			jsonUnmarshaller.fermeAccolade();
		} else
			buff.append('}');
	}

	private void fermeCrochet() throws ClassNotFoundException, EntityManagerImplementationException,
			InstanciationException, NotImplementedSerializeException, IllegalAccessException, SetValueException {
		if (!isBetweenQuote) {
			if (buff.length() > 0)
				setValeur();
			jsonUnmarshaller.fermeCrocher();
		} else
			buff.append(']');
	}

	private String getString() {
		final String s = buff.toString();
		buff.setLength(0);
		return s;
	}

	private Class<?> guessType() {
		switch (buff.charAt(0)) {
		case 't':
		case 'f':
			return Boolean.class;
		case 'n':
			return Void.class;
		default:
			return Integer.class;
		}
	}

	/** @return le caractère suivant, ou FIN. */
	private int lit() throws UnmarshallExeption {
		if (positionBloc == finBloc) {
			try {
				int lu = reader.read(bloc, 0, TAILLE_BLOC);
				while (lu == 0)
					lu = reader.read(bloc, 0, TAILLE_BLOC);
				if (lu == FIN)
					return FIN;
				finBloc = lu;
				positionBloc = 0;
			} catch (final IOException e) {
				erreurParsing("message tronqué", e);
			}
		}
		return bloc[positionBloc++];
	}

	private void ouvreAccolade() {
		if (!isBetweenQuote) {
			buff.setLength(0);
			jsonUnmarshaller.ouvreAccolade();
		} else
			buff.append('{');
	}

	private void ouvreCrochet() throws NotImplementedSerializeException {
		if (!isBetweenQuote)
			jsonUnmarshaller.ouvreChrochet();
		else
			buff.append('[');
	}

	protected void parse(final Reader reader) throws IOException, ClassNotFoundException,
			EntityManagerImplementationException, InstanciationException, NotImplementedSerializeException,
			JsonHandlerException, UnmarshallExeption, IllegalAccessException, SetValueException {
		this.reader = reader;
		int t = lit();
		while (t != FIN) {
			comportement((char) t);
			t = lit();
		}
	}

	private void quote() {
		isBetweenQuote = !isBetweenQuote;
	}

	private char readEscapeCharacter() throws UnmarshallExeption {
		final char escaped = (char) lit();
		switch (escaped) {
		case 'u':
			return escapeCharactere();
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
		default:
			return escaped;
		}
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
		if (buff.length() > 0) {
			Class<?> typeGuess = String.class;
			if (!enleveGuillemets()) {
				enleveEspaceEtSautDeLigne();
				if (buff.length() == 0)
					return;
				typeGuess = guessType();
			}
			jsonUnmarshaller.setValeur(getString(), typeGuess);
		}
	}

	private void virgule() throws ClassNotFoundException, EntityManagerImplementationException, InstanciationException,
			NotImplementedSerializeException, IllegalAccessException, SetValueException {
		if (!isBetweenQuote)
			setValeur();
		else
			buff.append(',');
	}
}
