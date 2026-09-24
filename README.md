# serialisation-désérialisation  Java multi-format

Bibliothèque Java de sérialisation - deserialisation orienté supportant le polymorphisme. Les formats supportés sont xml, json et  binaire

##0 - Exemple
-----------

Un exemple simple :
	
	Toto toto = new Toto(parametres);
	String jsonToto = JsonMarshaller.toJson(toto);
	Toto toto2 = JsonUnmarshaller.fromJson(toto);


##1 - Préalable
-------------
D'une manière générale, le polymorphisme (classe dérivée) et les cycles sont autorisés dans les graphes d'objets sérialisés. L'identification des objets est implémentée de la manière suivante: 
	* chaque objet sérialisé doit avoir un identifiant unique (attribut "id") renseigné et unique à travers le graphe
	* si un objet est instance d'une classe qui ne dispose pas de cet attribut "id", un UUID lui est automatiquement affecté (création d'un pseudo-attribut "id").
Pour la désérialisation, il n'est pas nécessaire d'avoir un constructeur de classe.

Il y a deux méthodes de sérialisation :
	* complete
	* au niveau de l'objet

En termes d'interface de programmation, il y a deux niveaux d'utilisation: 

a) API triviale
Supposons un objet "obj", d'un type quelconque (par exemple MonObjet)
	
	String xml = XmlMarshaller.ToXml(obj) // produit la représentation XML de cet objet sous forme d'une string.
	String json = JsonMarshaller.ToJson(obj)
	on peut aisni désérialiser :
	MonObjet monObjetFromXml = XmlUnmarshaller.fromXml(xml)
	MonObjet monObjetFromJson = JsonUnmarshaller.fromJson(json)
	 
	
b) API fine: offre les mêmes fonctionnalités, mais travaille sur des flux binaires/textuels
Cela permet par exemple le cas d'utilisation d'export XML/JSON/Binaire vers un flux réseau sans construire de string intermédiaire (indispensable sur un serveur de service ou pour un graphe volumineux).
	
###1.1 - sérialisation complète
La sérialisation complète parcours la grappe d'objet complètement et sérialise l'objet en profondeur. Il faut cependant faire attention avec le comportement attendu car si la connexité est grande, l'objet sérialisé peut être d'une taille très importante.
Toute référence ultérieure à un objet est remplacée par son id pour éviter un débordement de pile et permettre une représentation hiérarchique d'un graphe.
	
###1.2 - sérialisation au niveau de l'objet
La méthodologie de sérialisation est la suivante : en UML un objet est en "relation" avec un autre objet de 3 façons différentes : 
	- par Composition ("je suis composé de ...")
	- par Agrégation ("j'ai ...")
	- par Association ("je connais ...")
Le langage Java ne fait pas la différence entre ces liens. La volonté de cette librairie est de définir des comportements automatiques de sérialisation en fonction de la sémantique. Pour cette raison la librairie défini l'annotation "TypeRelation" qui permet de spécifier si le lien est parmi {AGGREGATION, COMPOSITION, ASSOCIATION}

exemple :

	@Relation(type=TypeRelation.COMPOSITION)
	private Etat etatEnComposition

Si aucune annotation n'est indiquée, le comportement est celui d'une association.

Dans le processus de sérialisation au niveau de l'objet :
	les attributs d'un objet sont sérialisés.
	Un objet en composition est sérialisé
	Les objets en Association ou en agrégation sont simplement référencés par leurs id.

###1.3 - EntityManager
A la désérialisation, il faut pouvoir éventuellement s'interfacer avec les objets déjà existant afin d'affecter les bonnes instances en fonction des id. Pour cette raison, il est possible de passer au désérialiseur un EntityManager, c'est-à-dire une classe qui implémente l'interface EntityManager. Elle offre une méthode :

	U findObjectOrCreate(String, Class<U>)

Ces deux méthodes permettent au désérialiseur de trouver un objet préexistant et de mettre en cache un objet qu'il aurait lui-même créé. 

###1.4	- Divers
Un attribut peut être exclu de la sérialisation en le préfixant de @IgnoreSerialise
Le nom d'un attribut tel qu'il apparaîtra dans le format de sortie peut être adapté par @MarshallAs("NomPublicIci")

##2 - Format XML
------------------

Le schéma de sérialisation est le suivant : <nomBalise type="type.objet">liste des attributs suivant le meme schémat</nomBalise>
Le type est mis optionnellement par le sérialiseur si celui ci est ambigu.
il y a 4 méthodes public static à la sérialisation en xml et 4 pour la désérialisation.

###2.1 - Sérialisation
	XmlMarshaller.toXml(U, Writer)
	XmlMarshaller.toXml(U)
	XmlMarshaller.toCompleteXml(U, Writer)
	XmlMarshaller.toCompleteXml(U)

###2.2 - Désérialisation
	XmlUnmarshaller.fromXml(Reader, EntityManager)
	XmlUnmarshaller.fromXml(Reader)
	XmlUnmarshaller.fromXml(String)
	XmlUnmarshaller.fromXml(String, EntityManager)


##3 - Format JSON
------------------

le schéma de sérialisation est le suivant : {"__type"="type.objet",attributs...}
le type est mis optionnellement si celui-ci est ambigu.
il y a 4 méthodes public static à la sérialisation en json et 4 pour la désérialisation.

###3.1 - Sérialisation

	JsonMarshaller.toJson(U, Writer)
	JsonMarshaller.toJson(U)
	JsonMarshaller.toCompleteJson(U, Writer)
	JsonMarshaller.toCompleteJson(U)
	
###3.2 - Désérialisation
	
	JsonUnmarshaller.fromJson(Reader, EntityManager)
	JsonUnmarshaller.fromJson(Reader)
	JsonUnmarshaller.fromJson(String)
	JsonUnmarshaller.fromJson(String, EntityManager)

###3.3 - Performances

Sans EntityManager, la lecture analyse le texte d'un bloc et construit les objets au fil de l'eau, sans événements intermédiaires : clés retrouvées d'après leurs octets (la clé suivante d'une classe est prévue), champ résolu une fois par classe, nombres lus sans chaîne intermédiaire (décimaux par un algorithme exact, identique au bit près à `Double.parseDouble`). Un texte hors des cas courants (JSON non strict, tabulations, types particuliers...) est relu par le lecteur historique : tout ce qui était accepté l'est toujours, avec le même résultat. Avec un EntityManager, le lecteur historique est utilisé.

L'écriture prépare une fois par champ et par classe les clés et les noms de type, écrit les champs primitifs sans boxing et accumule le texte en octets Latin-1 tant qu'il le permet. Le texte produit est inchangé.

Les mesures sont au paragraphe 4.5.


##4 - Format Binaire
------------------
il y a 2 méthodes public static à la sérialisation en binaire et 2 pour la désérialisation.
	
###4.1 - Sérialisation

	BinaryMarshaller.toBinary(U, OutputStream)
	BinaryMarshaller.toCompleteBinary(U, OutputStream)

###4.2 - Désérialisation

	BinaryUnmarshaller.fromBinary(InputStream, EntityManager)
	BinaryUnmarshaller.fromBinary(InputStream)

###4.3 - Compatibilité (version 1.1)

**Le format binaire de la version 1.1 est incompatible avec celui des versions précédentes** : un flux écrit par une version 1.0.x ne peut pas être relu par la 1.1, et inversement. Les formats XML et JSON ne changent pas.

###4.4 - Caractéristiques du format

Le format binaire conserve toutes les garanties de la bibliothèque (polymorphisme, cycles, identité des objets, stratégies de sérialisation) et est plus compact que les formats binaires usuels :

* identifiants implicites : un objet, une chaîne, une date ou un UUID vu pour la première fois ne porte pas de numéro, le lecteur les numérote dans l'ordre de lecture ; seules les références arrière portent un numéro ;
* chaînes dédupliquées (une chaîne déjà écrite n'est plus qu'une référence), de façon adaptative : un champ dont les valeurs ne se répètent pas (identifiants, adresses...) n'est plus cherché dans la table ;
* longueurs et tailles en varint, champs de type primitif écrits sans en-tête (entiers en varint zigzag), BigDecimal en binaire ;
* type écrit seulement s'il n'est pas déductible du champ ; les collections et maps courantes du JDK (ArrayList, HashMap, LinkedHashMap...) ont un numéro fixe, leur nom n'est jamais écrit ;
* pas de limite de taille pour les chaînes, caractères nuls et surrogates isolés transportés.

###4.5 - Performances et prérequis

Le moteur binaire est conçu pour la vitesse, sans rien exiger du code métier (ni annotation, ni génération de code source) :

* pour chaque classe, un écrivain et un lecteur de ses champs sont générés **en mémoire, à l'exécution** (classes cachées, JDK 15 et plus) : accès direct aux champs, même privés. Sur un JDK plus ancien, pour un champ final ou si la génération échoue, la bibliothèque se replie sur la réflexion ;
* aucune méthode d'accès mémoire de `sun.misc.Unsafe` n'est utilisée (elles sont dépréciées et signalées par un avertissement depuis le JDK 24) ; seule `Unsafe.allocateInstance`, non dépréciée, sert à créer les objets sans appeler leur constructeur (repli sur le constructeur de sérialisation) ;
* les tables et tampons sont réutilisés d'un appel à l'autre sur un même thread ;
* les graphes sont parcourus par récursion jusqu'à 200 niveaux, puis par une pile explicite : un graphe très profond (longue liste chaînée...) ne provoque pas de `StackOverflowError`.

Mesures (JMH, JDK 25, Linux arm64, 10 itérations de 2 s) sur un catalogue de commandes : « petit » = 1 commande et 10 lignes (13 objets), « gros » = 1 000 commandes (13 000 objets). Kryo et Fory ont le suivi des références activé, pour la même sémantique d'identité ; Jackson et Gson ne gèrent ni les cycles, ni l'identité, ni le polymorphisme.

| Format | écriture petit (µs) | écriture gros (ms) | lecture petit (µs) | lecture gros (ms) | taille gros (Ko) |
|---|---:|---:|---:|---:|---:|
| **giraudsa binaire** | **1,07** | **1,16** | **1,41** | **0,98** | **393** |
| Fory 0.12 | 1,08 (± 0,19) | 1,28 | 1,74 | 1,45 | 602 |
| Kryo 5.6 | 1,75 | 3,76 | 2,27 | 1,96 | 508 |
| Java natif | 7,12 | 7,53 | 36,5 | 8,87 | 1 174 |
| **giraudsa JSON** | **3,01** | **3,03** | **4,33** | **4,51** | 1 519 |
| Jackson JSON 2.17 | 3,19 | 3,54 | 7,63 | 6,84 | 1 458 |
| Gson 2.10 | 7,48 | 7,27 | 8,01 | 7,33 | 1 458 |
| Jackson XML 2.17 | 6,45 | 6,94 | 15,1 | 13,4 | 2 116 |
| giraudsa XML | 7,73 | 7,40 | 32,6 | 26,3 | 2 213 |
| XStream 1.4 | 24,3 | 25,2 | 49,1 | 50,5 | 3 534 |

Parmi les formats JSON, giraudsa est le plus rapide en écriture comme en lecture (en gras : meilleur de sa famille de formats).

Fory copie le contenu des chaînes par `sun.misc.Unsafe`, ce qui le place à égalité en écriture sur les petits graphes, au prix de l'avertissement du JDK 24+ au démarrage.

Le module `benchmark/` (JMH) permet de refaire ces mesures :

	cd benchmark && mvn package -DskipTests
	java -jar target/benchmarks.jar SerialisationBenchmark
	java -jar target/benchmarks.jar SerialisationBenchmark -p framework=giraudsa-binaire,kryo,fory
	
##5 - Customisation
-------------------

Il est possible de personnaliser le pattern des Dates et de racourcir les flux xml et json en précisant si les id sont universels. Cela permet en effet de ne pas écrire à nouveau le type si l'id a déjà été vu au cours de la sérialisation. Par ailleurs, il est possible d'utiliser une annotation différente pour indiquer les attributs à ne pas sérialiser. Par défaut, @IgnoreSerialise est utilisé.

###5.1 - Format de date
Par défaut, le format de date respecte la norme RFC822 avec la TimeZone UTC. Cependant, il est possible de modifier en utilisant la configuration suivante :

	ConfigurationMarshalling.setDateFormatJson(new SimpleDateFormat());
	ConfigurationMarshalling.setDateFormatXml(new SimpleDateFormat());
Attention, il convient bien d'utiliser le même format entre la sérialisation et la désérialisation qui peuvent être sur des serveurs différents.

###5.2 - le type d'id
Les id sont souvent des incrémentations automatiques de base de données donc ne sont pas universel mais spécifique à une classe. Il est donc nécéssaire d'avoir l'information du type lorsqu'il n'est pas devinable. Cependant, lorsque les id utilisés sont de type UUID par exemple, lorsqu'un objet a déjà été vu lors de la sérialisation et qu'il est fait à nouveau référence à lui, il n'y a pas besoin d'indiquer à nouveau son type d'où une amélioration de la performance. Pour indiquer à la librairie qu'il s'agit d'un id de type universel :

	ConfigurationMarshalling.setIdUniversel();

Le désérialiseur détecte automatiquement la configuration utilisée via le sérialiseur par une balise dans le premier TAG du xml et via la manière de sérialiser le type dans le Json

###5.3 - Changement d'annotation Transcient
Pour éviter d'annoter un attribut d'une classe de plusieurs librairies (JAXB, hibernate...), il est possible de redéfinir l'annotation qui permet de ne pas sérialiser un attribut.

	ConfigurationMarshalling.setAnnotationIgnoreSerialise(@Transcient.class);
	
###5.4 - Formattage des XML
Il est possible de formatter les XML et les JSON pour une lecture humaine.

	ConfigurationMarshalling.setPrettyPrint();
	
###5.5 - Masquer les types dans les JSON générés.

Attention, il ne sera alors plus possible de désérialiser les JSON produits. Il suffit d'utiliser les deux méthodes suivantes selon le besoin en utilisant false comme dernier argument (writeType = false):

	JsonMarshaller.toJson(myObject, myWriter, myStrategieDeSerialisation, false);
	String json = JsonMarshaller.toJson(myobject, myStrategieDeSerialisation, boolean);
	
###5.6 - Stratégie de sérialisation

La stratégie de sérialisation permet de définir la profondeur de sérialisation en fonction du critère de profondeur et du modèle objet. Par défaut, il existe 3 stratégies possibles mais il est possible d'en définir d'autre en implémentant l'interface StrategieDeSerialisation. Cette interface possède une méthode qui défini si un objet doit être completement sérialisé ou uniquement par référence en fonction de sa profondeur et de ses informations de champs. Par exemple :

	JsonMarshaller.toJson(myObject, myWriter, new StrategieParCompositionOuAgregationEtClasseConcrete(), true);
	
###5.7 - Contrainte au modèle

Lors des évolutions de modèle par ajout d'attribut, il se peut que des classes ou des attributs apparaissent dans le XML qui ne soit pas dans le classloader en local. Par défaut le comportement lance une exception. Cependant, on peut vouloir le désérialiser et ne pas s'en occuper.

	 ConfigurationMarshalling.setContrainteModel(false);
Remarque : cette configuration ne fonctionne pas pour la désérialisation binaire.