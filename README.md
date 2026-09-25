# Fidelis

[![CI](https://github.com/giraudsa/fidelis/actions/workflows/ci.yml/badge.svg)](https://github.com/giraudsa/fidelis/actions/workflows/ci.yml)
![Java 11+](https://img.shields.io/badge/Java-11%2B-blue)
[![Release](https://img.shields.io/github/v/release/giraudsa/fidelis)](https://github.com/giraudsa/fidelis/releases/latest)
[![Licence MIT](https://img.shields.io/badge/licence-MIT-green)](LICENSE)

**Fidelis** est une bibliothèque Java de sérialisation *fidèle au graphe d'objets* : ce qui est relu est exactement le graphe écrit — mêmes objets partagés, mêmes cycles, mêmes sous-classes — en **JSON** ou dans un **format binaire** compact, sans annotation obligatoire, sans constructeur requis et sans code généré dans votre projet.

```java
String json = JsonMarshaller.toCompleteJson(commande);
Commande relue = JsonUnmarshaller.fromJson(json);   // même graphe : partages, cycles et sous-classes compris
```

## Sommaire

- [Pourquoi Fidelis](#pourquoi-fidelis)
- [Installation](#installation)
- [Démarrage rapide](#démarrage-rapide)
- [Identité, cycles et polymorphisme](#identité-cycles-et-polymorphisme)
- [Stratégies de sérialisation](#stratégies-de-sérialisation)
- [API](#api)
- [Configuration](#configuration)
- [Format binaire](#format-binaire)
- [Performances](#performances)
- [Compatibilité et version 2.0](#compatibilité-et-version-20)
- [Licence](#licence)

## Pourquoi Fidelis

| | Fidelis | Jackson, Gson, fastjson2, Fory JSON |
|---|---|---|
| Objet partagé | un seul objet à la relecture | dupliqué |
| Cycle (`enfant.parent = racine`) | conservé | erreur ou boucle infinie |
| Sous-classe dans un champ de type parent | relue avec son vrai type | type déclaré, sauf annotations |
| Code métier à modifier | rien (annotations facultatives) | annotations ou modules pour les cas avancés |

- **Aucune génération de code source** : pour chaque classe, un lecteur et un écrivain de ses champs sont générés *en mémoire, à l'exécution* (classes cachées du JDK 15+). Rien n'apparaît dans votre projet ; sur un JDK plus ancien, la réflexion prend le relais.
- **Pas de constructeur requis** : les objets sont créés sans appeler de constructeur.
- **Profondeur maîtrisée** : une stratégie décide, champ par champ, d'écrire l'objet en entier ou seulement sa référence (voir [Stratégies](#stratégies-de-sérialisation)).
- **Intégration à une couche de persistance** : un `EntityManager` permet de relier les objets relus aux instances existantes.
- **Rapide** : le format binaire est le plus compact et relit plus vite que Kryo et Fory ; le JSON écrit plus vite que Jackson, Gson et fastjson2 (voir [Performances](#performances)).

## Installation

JDK 11 ou plus (15+ recommandé : lecteurs et écrivains générés).

**Sans Maven** : téléchargez `fidelis-<version>.jar` sur la [page des releases](https://github.com/giraudsa/fidelis/releases/latest) et ajoutez-le au classpath (`fidelis-<version>-sources.jar` contient les sources, pour l'IDE).

**Avec Maven** : la bibliothèque n'est pas encore publiée sur Maven Central ; construisez-la depuis les sources,

```sh
git clone https://github.com/giraudsa/fidelis.git
cd fidelis && mvn install
```

puis ajoutez la dépendance :

```xml
<dependency>
  <groupId>io.github.giraudsa</groupId>
  <artifactId>fidelis</artifactId>
  <version>2.0.0</version>
</dependency>
```

Fidelis n'a **aucune dépendance** : seul le JDK est nécessaire. Les erreurs sont journalisées par `java.lang.System.Logger` (par défaut vers `java.util.logging` ; une application sous SLF4J les récupère avec `slf4j-jdk-platform-logging`).

## Démarrage rapide

```java
import io.github.giraudsa.fidelis.serialisation.text.json.JsonMarshaller;
import io.github.giraudsa.fidelis.deserialisation.text.json.JsonUnmarshaller;
import io.github.giraudsa.fidelis.serialisation.binary.BinaryMarshaller;
import io.github.giraudsa.fidelis.deserialisation.binary.BinaryUnmarshaller;

// JSON
String json = JsonMarshaller.toCompleteJson(commande);
Commande c1 = JsonUnmarshaller.fromJson(json);

// binaire
ByteArrayOutputStream out = new ByteArrayOutputStream();
BinaryMarshaller.toCompleteBinary(commande, out);
Commande c2 = BinaryUnmarshaller.fromBinary(new ByteArrayInputStream(out.toByteArray()));
```

Toutes les méthodes existent aussi en flux (`Writer`, `Reader`, `OutputStream`, `InputStream`), pour écrire vers le réseau ou un fichier sans chaîne intermédiaire.

## Identité, cycles et polymorphisme

Chaque objet est identifié par son champ `id`. Une classe sans champ `id` reçoit un identifiant généré (un UUID), invisible dans le modèle. Un objet déjà écrit n'est plus écrit qu'en référence, par son id ; le type n'est écrit que s'il ne se déduit pas du champ.

Avec ce modèle :

```java
class Client     { String id; String nom; }
class ClientPro extends Client { String siret; }
class Commande   { String id; Client client; @Relation(type = TypeRelation.COMPOSITION) List<Ligne> lignes; }
class Ligne      { String id; String produit; int quantite; Commande commande; }
```

`JsonMarshaller.toCompleteJson(commande)` produit :

```json
{"__type":"exemple.Commande","id":"c1",
 "client":{"__type":"exemple.ClientPro","id":"k1","nom":"Durand","siret":"123"},
 "lignes":[{"id":"l1","produit":"stylo","quantite":3,"commande":{"id":"c1"}}]}
```

- `client` est un `ClientPro` dans un champ `Client` : son type est écrit, il est relu en `ClientPro` ;
- la ligne référence sa commande par `{"id":"c1"}` : à la relecture, `ligne.commande == commande` (le cycle est reconstruit) ;
- le type des lignes se déduit de `List<Ligne>` : il n'est pas écrit.

## Stratégies de sérialisation

La relation entre deux objets est précisée par l'annotation `@Relation` (en son absence : association) :

```java
@Relation(type = TypeRelation.COMPOSITION) private List<Ligne> lignes;   // « je suis composé de »
@Relation(type = TypeRelation.AGGREGATION) private Adresse adresse;      // « j'ai »
private Client client;                                                    // association : « je connais »
```

La stratégie décide, pour chaque champ, d'écrire l'objet en entier ou seulement son id :

| Stratégie | Objets écrits en entier | Utilisée par |
|---|---|---|
| `StrategieSerialisationComplete` | tous | `toCompleteJson`, `toCompleteBinary` |
| `StrategieParComposition` | la racine et les compositions | `toJson`, `toBinary` |
| `StrategieParCompositionOuAgregationEtClasseConcrete` | compositions, et agrégations vers une classe concrète | à passer explicitement |

Avec `JsonMarshaller.toJson(commande)`, le client (association) n'est écrit que par sa référence : `"client":{"__type":"exemple.ClientPro","id":"k1"}`. Une stratégie personnalisée hérite de `StrategieDeSerialisation` :

```java
JsonMarshaller.toJson(commande, writer, new MaStrategie(), true);
```

## API

**JSON** (`io.github.giraudsa.fidelis.serialisation.text.json.JsonMarshaller`, `...deserialisation.text.json.JsonUnmarshaller`)

```java
String toCompleteJson(U obj)
void   toCompleteJson(U obj, Writer out, EntityManager em)
String toJson(U obj)
String toJson(U obj, EntityManager em)
String toJson(U obj, StrategieDeSerialisation strategie, EntityManager em, boolean ecrireLesTypes)
void   toJson(U obj, Writer out, EntityManager em)
void   toJson(U obj, Writer out, StrategieDeSerialisation strategie, EntityManager em, boolean ecrireLesTypes)

U fromJson(String json)            U fromJson(String json, EntityManager em)
U fromJson(Reader reader)          U fromJson(Reader reader, EntityManager em)
```

**Binaire** (`...serialisation.binary.BinaryMarshaller`, `...deserialisation.binary.BinaryUnmarshaller`)

```java
void toCompleteBinary(U obj, OutputStream out)
void toBinary(U obj, OutputStream out)
void toBinary(U obj, OutputStream out, StrategieDeSerialisation strategie)

U fromBinary(InputStream in)       U fromBinary(InputStream in, EntityManager em)
```

**EntityManager** : à la relecture, `findObjectOrCreate(String id, Class<U> type)` fournit l'instance correspondant à un id (existante ou à créer) ; à l'écriture, `getId(Object)` donne l'id d'un objet qui n'en porte pas.

## Configuration

Tout se règle par `io.github.giraudsa.fidelis.utils.ConfigurationMarshalling` et deux annotations facultatives :

| Besoin | Réglage |
|---|---|
| Exclure un champ | `@IgnoreSerialise`, ou votre propre annotation : `setAnnotationIgnoreSerialise(Transient.class)` |
| Renommer un champ dans la sortie | `@MarshallAsAttribute(name = "nomPublic")` |
| Format des dates JSON (par défaut ISO 8601 en UTC, `2026-09-25T08:30:00.000Z`) | `setDateFormatJson(new SimpleDateFormat(...))` |
| JSON mis en forme | `setPrettyPrint()` |
| Ids uniques dans tout le graphe (UUID...) : type non répété pour un objet déjà vu | `setIdUniversel()` (détecté automatiquement à la relecture) |
| Tolérer des champs ou classes inconnus à la relecture JSON | `setContrainteModel(false)` |
| JSON sans types (lisible par d'autres outils, mais plus relisible par Fidelis) | dernier argument de `toJson(..., false)` |

Le format de date doit être le même à l'écriture et à la relecture.

## Format binaire

Le format binaire conserve toutes les garanties (identité, cycles, polymorphisme, stratégies) et reste plus compact que les formats binaires usuels :

- identifiants implicites : un objet, une chaîne, une date ou un UUID vu pour la première fois ne porte pas de numéro ; seules les références arrière en portent un ;
- chaînes dédupliquées de façon adaptative (un champ dont les valeurs ne se répètent pas n'est plus cherché dans la table) ;
- tailles en varint, primitifs sans en-tête (entiers en varint zigzag), `BigDecimal` en binaire ;
- type écrit seulement s'il ne se déduit pas du champ ; les collections et maps courantes du JDK ont un numéro fixe ;
- aucune limite de taille ; caractères nuls et demi-caractères UTF-16 isolés transportés.

Aucune méthode d'accès mémoire de `sun.misc.Unsafe` n'est utilisée (elles sont signalées par un avertissement depuis le JDK 24) ; seule `Unsafe.allocateInstance`, non dépréciée, sert à créer les objets sans constructeur. Les graphes très profonds (longues listes chaînées) ne provoquent pas de `StackOverflowError` : au-delà d'une profondeur, le parcours passe par une pile explicite.

## Performances

JMH, JDK 25, Linux arm64, catalogue de commandes : « petit » = 1 commande et 10 lignes (13 objets), « gros » = 1 000 commandes (13 000 objets).

| Format | écriture petit (µs) | écriture gros (ms) | lecture petit (µs) | lecture gros (ms) | taille gros (Ko) |
|---|---:|---:|---:|---:|---:|
| **Fidelis binaire** | 1,11 | 1,26 | **1,29** | **1,02** | **393** |
| Fory 1.7 | **0,88** | **1,07** | 1,78 | 1,55 | 604 |
| Kryo 5.6 | 1,85 | 4,43 | 2,31 | 1,93 | 508 |
| Java natif | 7,31 | 8,13 | 36,8 | 8,20 | 1 174 |
| Fidelis JSON | 2,20 | 1,98 | 2,96 | 3,25 | 1 519 |
| **Fory JSON 1.7** | **0,83** | **0,98** | **1,13** | **1,19** | 1 458 |
| fastjson2 2.0 | 2,71 | 2,21 | 2,15 | 1,83 | 1 466 |
| Jackson 2.17 | 3,33 | 3,74 | 7,72 | 7,58 | 1 458 |
| Gson 2.10 | 7,73 | 7,52 | 8,09 | 7,40 | 1 458 |

En gras : le meilleur de sa famille. Kryo et Fory binaire ont le suivi des références activé (même sémantique d'identité que Fidelis). Les autres JSON ne conservent ni identité, ni cycles, ni polymorphisme : le catalogue mesuré est un arbre qu'ils relisent correctement, mais ils ne relisent pas un graphe partagé ou cyclique. Fory lit et écrit l'intérieur des chaînes par `sun.misc.Unsafe`, au prix de l'avertissement du JDK 24+.

Pour refaire les mesures (module `benchmark/`) :

```sh
cd benchmark && mvn package -DskipTests
java -jar target/benchmarks.jar SerialisationBenchmark
java -jar target/benchmarks.jar SerialisationBenchmark -p framework=fidelis-binaire,kryo,fory
```

## Compatibilité et version 2.0

La version 2.0 (anciennement « Marshalling », paquetages `giraudsa.marshall`) est incompatible avec la 1.x :

- **paquetages renommés** : `giraudsa.marshall.*` → `io.github.giraudsa.fidelis.*` et `utils.*` → `io.github.giraudsa.fidelis.utils.*` ; artefact Maven `io.github.giraudsa:fidelis` ;
- **format binaire** nouveau : un flux écrit en 1.x ne se relit pas en 2.0, et inversement ;
- **format XML retiré** (`XmlMarshaller`, `XmlUnmarshaller`, format de date XML) ;
- le **format JSON ne change pas** : un JSON écrit en 1.x se relit en 2.0.

## Licence

[MIT](LICENSE) — © giraudsa
