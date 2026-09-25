package bench;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.apache.fory.json.ForyJson;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import bench.model.Catalogue;
import io.github.giraudsa.fidelis.deserialisation.binary.BinaryUnmarshaller;
import io.github.giraudsa.fidelis.deserialisation.text.json.JsonUnmarshaller;
import io.github.giraudsa.fidelis.serialisation.binary.BinaryMarshaller;
import io.github.giraudsa.fidelis.serialisation.text.json.JsonMarshaller;

/** Adaptateur commun : chaque framework encode un {@link Catalogue} en String ou byte[] et le relit. */
public abstract class Codec {

	public abstract Object encode(Catalogue c) throws Exception;

	public abstract Catalogue decode(Object data) throws Exception;

	public static int taille(final Object data) {
		return data instanceof byte[] ? ((byte[]) data).length
				: ((String) data).getBytes(StandardCharsets.UTF_8).length;
	}

	public static final String[] NOMS = { "fidelis-json", "fidelis-binaire", "jackson-json",
			"gson", "fastjson2", "fastjson2-ref", "fory-json", "kryo", "fory", "java-natif" };

	static ObjectMapper jacksonChamps(final ObjectMapper m) {
		m.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
		m.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		return m;
	}

	public static Codec cree(final String nom) {
		switch (nom) {
		case "fidelis-json":
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
		case "fidelis-binaire":
			return new Codec() {
				// tampon réutilisé d'un appel à l'autre (comme Kryo et Fory) : seule la copie du résultat est payée
				private final ByteArrayOutputStream out = new ByteArrayOutputStream(4096);

				@Override
				public Object encode(final Catalogue c) throws Exception {
					out.reset();
					BinaryMarshaller.toCompleteBinary(c, out);
					return out.toByteArray();
				}

				@Override
				public Catalogue decode(final Object d) throws Exception {
					return BinaryUnmarshaller.fromBinary(new ByteArrayInputStream((byte[]) d));
				}
			};
		case "fastjson2":
		case "fastjson2-ref": {
			// par champs comme Jackson ; la variante -ref détecte aussi les références partagées ($ref)
			final JSONWriter.Feature[] ecriture = nom.endsWith("-ref")
					? new JSONWriter.Feature[] { JSONWriter.Feature.FieldBased, JSONWriter.Feature.ReferenceDetection }
					: new JSONWriter.Feature[] { JSONWriter.Feature.FieldBased };
			return new Codec() {
				@Override
				public Object encode(final Catalogue c) throws Exception {
					return JSON.toJSONString(c, ecriture);
				}

				@Override
				public Catalogue decode(final Object d) throws Exception {
					return JSON.parseObject((String) d, Catalogue.class, JSONReader.Feature.FieldBased);
				}
			};
		}
		case "fory-json": {
			final ForyJson j = ForyJson.builder().withFieldMode(true).build();
			return new Codec() {
				@Override
				public Object encode(final Catalogue c) throws Exception {
					return j.toJson(c);
				}

				@Override
				public Catalogue decode(final Object d) throws Exception {
					return j.fromJson((String) d, Catalogue.class);
				}
			};
		}
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
		case "kryo": {
			// références activées : même sémantique d'identité que Fidelis (cycles, partage)
			final Kryo k = new Kryo();
			k.setRegistrationRequired(false);
			k.setReferences(true);
			k.register(Catalogue.class);
			k.register(ArrayList.class);
			k.register(LinkedHashMap.class);
			return new Codec() {
				// tampon réutilisé d'un appel à l'autre, usage normal de Kryo
				private final Output out = new Output(4096, -1);

				@Override
				public Object encode(final Catalogue c) {
					out.reset();
					k.writeObject(out, c);
					return out.toBytes();
				}

				@Override
				public Catalogue decode(final Object d) {
					return k.readObject(new Input((byte[]) d), Catalogue.class);
				}
			};
		}
		case "fory": {
			// suivi des références activé : même sémantique d'identité que Fidelis et Kryo
			final org.apache.fory.ThreadSafeFory f = org.apache.fory.Fory.builder().withXlang(false).withRefTracking(true)
					.requireClassRegistration(false).withTypeChecker((resolveur, classe) -> true)
					.buildThreadSafeFory();
			return new Codec() {
				@Override
				public Object encode(final Catalogue c) {
					return f.serialize(c);
				}

				@Override
				public Catalogue decode(final Object d) {
					return (Catalogue) f.deserialize((byte[]) d);
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
