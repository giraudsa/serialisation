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
A la désérialisation, il faut pouvoir éventuellement s'interfacer avec les objets déjà existant afin d'affecter les bonnes instances en fonction des id. Pour cette raison, il est possible de passer au désérialiseur un EntityManager, c'est-à-dire une classe qui implémente l'interface EntityManager. Elle offre donc deux méthodes :

	U findObject(String, Class<U>)
	<U> metEnCache(String, U)

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


##4 - Format Binaire
------------------
il y a 2 méthodes public static à la sérialisation en binaire et 2 pour la désérialisation.
	
###4.1 - Sérialisation

	BinaryMarshaller.toBinary(U, OutputStream)
	BinaryMarshaller.toCompleteBinary(U, OutputStream)

###4.2 - Désérialisation

	BinaryUnmarshaller.fromBinary(InputStream, EntityManager)
	BinaryUnmarshaller.fromBinary(InputStream)
	
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