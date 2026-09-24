package bench;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.thoughtworks.xstream.XStream;

import bench.model.Catalogue;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import giraudsa.marshall.serialisation.text.xml.XmlMarshaller;

/** Adaptateur commun : chaque framework encode un {@link Catalogue} en String ou byte[] et le relit. */
public abstract class Codec {

	public abstract Object encode(Catalogue c) throws Exception;

	public abstract Catalogue decode(Object data) throws Exception;

	public static int taille(final Object data) {
		return data instanceof byte[] ? ((byte[]) data).length
				: ((String) data).getBytes(StandardCharsets.UTF_8).length;
	}

	public static final String[] NOMS = { "giraudsa-json", "giraudsa-xml", "giraudsa-binaire", "jackson-json",
			"gson", "jackson-xml", "xstream", "kryo", "java-natif" };

	static ObjectMapper jacksonChamps(final ObjectMapper m) {
		m.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		m.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		return m;
	}

	public static Codec cree(final String nom) {
		switch (nom) {
		case "giraudsa-json":
			return new Codec() {
				@Override
				public Object encode(final Catalogue c) throws Exception {
					return JsonMarshaller.toCompleteJson(c);
				}

				@Override
				public Catalogue decode(final Object d) throws Exception {
					return JsonUnmarshaller.fromJson((String) d);
				}
			};
		case "giraudsa-xml":
			return new Codec() {
				@Override
				public Object encode(final Catalogue c) throws Exception {
					return XmlMarshaller.toCompleteXml(c);
				}

				@Override
				public Catalogue decode(final Object d) throws Exception {
					return XmlUnmarshaller.fromXml((String) d);
				}
			};
		case "giraudsa-binaire":
			return new Codec() {
				@Override
				public Object encode(final Catalogue c) throws Exception {
					final ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
					BinaryMarshaller.toCompleteBinary(c, out);
					return out.toByteArray();
				}

				@Override
				public Catalogue decode(final Object d) throws Exception {
					return BinaryUnmarshaller.fromBinary(new ByteArrayInputStream((byte[]) d));
				}
			};
		case "jackson-json": {
			final ObjectMapper m = jacksonChamps(new ObjectMapper());
			return new Codec() {
				@Override
				public Object encode(final Catalogue c) throws Exception {
					return m.writeValueAsString(c);
				}

				@Override
				public Catalogue decode(final Object d) throws Exception {
					return m.readValue((String) d, Catalogue.class);
				}
			};
		}
		case "jackson-xml": {
			final ObjectMapper m = jacksonChamps(new XmlMapper());
			return new Codec() {
				@Override
				public Object encode(final Catalogue c) throws Exception {
					return m.writeValueAsString(c);
				}

				@Override
				public Catalogue decode(final Object d) throws Exception {
					return m.readValue((String) d, Catalogue.class);
				}
			};
		}
		case "gson": {
			// par défaut Gson sérialise Date en texte localisé et perd les millisecondes
			final Gson g = new GsonBuilder()
					.registerTypeAdapter(Date.class,
							(JsonSerializer<Date>) (src, t, ctx) -> new JsonPrimitive(src.getTime()))
					.registerTypeAdapter(Date.class,
							(JsonDeserializer<Date>) (json, t, ctx) -> new Date(json.getAsLong()))
					.create();
			return new Codec() {
				@Override
				public Object encode(final Catalogue c) {
					return g.toJson(c);
				}

				@Override
				public Catalogue decode(final Object d) {
					return g.fromJson((String) d, Catalogue.class);
				}
			};
		}
		case "xstream": {
			final XStream x = new XStream();
			x.allowTypesByWildcard(new String[] { "bench.model.**" });
			return new Codec() {
				@Override
				public Object encode(final Catalogue c) {
					return x.toXML(c);
				}

				@Override
				public Catalogue decode(final Object d) {
					return (Catalogue) x.fromXML((String) d);
				}
			};
		}
		case "kryo": {
			// références activées : même sémantique d'identité que giraudsa (cycles, partage)
			final Kryo k = new Kryo();
			k.setRegistrationRequired(false);
			k.setReferences(true);
			k.register(Catalogue.class);
			k.register(ArrayList.class);
			k.register(LinkedHashMap.class);
			return new Codec() {
				@Override
				public Object encode(final Catalogue c) {
					final Output out = new Output(4096, -1);
					k.writeObject(out, c);
					return out.toBytes();
				}

				@Override
				public Catalogue decode(final Object d) {
					return k.readObject(new Input((byte[]) d), Catalogue.class);
				}
			};
		}
		case "java-natif":
			return new Codec() {
				@Override
				public Object encode(final Catalogue c) throws Exception {
					final ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);
					try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
						oos.writeObject(c);
					}
					return bos.toByteArray();
				}

				@Override
				public Catalogue decode(final Object d) throws Exception {
					try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream((byte[]) d))) {
						return (Catalogue) ois.readObject();
					}
				}
			};
		default:
			throw new IllegalArgumentException(nom);
		}
	}
}
