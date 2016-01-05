# serialisation-désérialisation multi-format
#JSON - XML - Binaire

Petite bibliothèque de sérialisation - deserialisation orienté simplicité d'utilisation et supportant le polymorphisme.

Elle propose un outil de sérialisation vers le format xml, json et en binaire

0 - Exemple
-----------

Un exemple simple :
	
	Toto toto = new Toto(parametres);
	String jsonToto = JsonMarshaller.toJson(toto);
	Toto toto2 = JsonUnmarshaller.fromJson(toto);


1 - Préalable
-------------
D'une manière générale, le polymorphisme (classe dérivée) et les cycles sont autorisés dans les graphes d'objets sérialisés. L'identification des objets est implémentée de la manière suivante: 
	- chaque objet sérialisé doit avoir un identifiant unique (attribut "id") renseigné et unique à travers le graphe
	- si un objet est instance d'une classe qui ne dispose pas de cet attribut "id", un UUID lui est automatiquement affecté (création d'un pseudo-attribut "id").

Il y a deux méthode de sérialisation :
	- complete
	- au niveau de l'objet

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
	
1.1 - sérialisation complète
----------------------------

La sérialisation complète parcours la grappe d'objet complètement et sérialise l'objet en profondeur. Il faut cependant faire attention avec le comportement attendu car si la connexité est grande, l'objet sérialisé peut être d'une taille très importante.
Toute référence ultérieure à un objet est remplacée par son id pour éviter un débordement de pile et permettre une représentation hiérarchique d'un graphe.
	
1.2 - sérialisation au niveau de l'objet
----------------------------------------

La méthodologie de sérialisation est la suivante : en UML un objet est en "relation" avec un autre objet de 3 façons différentes : 
	- par Composition ("je suis composé de ...")
	- par Agrégation ("j'ai ...")
	- par Association ("je connais ...")
Le langage Java ne fait pas la différence entre ces liens. La volonté de cette librairie est de définir des comportement automatique de sérialisation en fonction de la sémantique. Pour cette raison la librairie défini l'annotation "TypeRelation" qui permet de définir si le lien est parmi {AGGREGATION, COMPOSITION, ASSOCIATION}

exemple :

	@Relation(type=TypeRelation.COMPOSITION)
	private Etat etatEnComposition

Si aucune annotation n'est indiqué, le comportement est celui d'une association.

Dans le processus de sérialisation au niveau de l'objet :
	les attributs d'un objet sont sérialisés.
	Un objet en composition est sérialisé
	Les objets en Association ou en agrégation sont juste référencés par leur id.

1.3 - EntityManager
-------------------

A la désérialisation, il faut pouvoir éventuellement s'interfacer avec les objets déjà existant afin d'affecter les bonnes instances en fonction des id. Pour cette raison, il est possible de passer au désérialiseur un EntityManager, c'est-à-dire une classe qui implémente l'interface EntityManager. Elle offre donc deux méthodes :

	U findObject(String, Class<U>)
	<U> metEnCache(String, U)

Ces deux méthodes permettent au désérialiseur de trouver un objet préexistant et de mettre en cache un objet qu'il aurait lui même créé.

1.4	- Divers
-----------
		
Un attribut peut être exclu de la sérialisation en le préfixant de @IgnoreSerialise
Le nom d'un attribut tel qu'il apparaîtra dans le format de sortie peut être adapté par @MarshallAs("NomPublicIci")

2 - Format XML
------------------

Le schéma de sérialisation est le suivant : <nomBalise type="type.objet">liste des attributs suivant le meme schémat</nomBalise>
Le type est mis optionnellement par le sérialiseur si celui ci est ambigu.
il y a 8 méthodes public static à la sérialisation en xml et 8 pour la désérialisation.

2.1 - Sérialisation
-------------------

	XmlMarshaller.toXml(U, Writer)
	XmlMarshaller.toXml(U)
	XmlMarshaller.toXml(U, Writer, DateFormat)
	XmlMarshaller.toXml(U, DateFormat)
	XmlMarshaller.toCompleteXml(U, Writer)
	XmlMarshaller.toCompleteXml(U)
	XmlMarshaller.toCompleteXml(U, StringWriter, DateFormat)
	XmlMarshaller.toCompleteXml(U, DateFormat)

2.2 - Désérialisation
---------------------
		
	XmlUnmarshaller.fromXml(Reader, EntityManager)
	XmlUnmarshaller.fromXml(Reader)
	XmlUnmarshaller.fromXml(String)
	XmlUnmarshaller.fromXml(String, EntityManager)
	XmlUnmarshaller.fromXml(Reader, EntityManager, DateFormat)
	XmlUnmarshaller.fromXml(Reader, DateFormat)
	XmlUnmarshaller.fromXml(String, DateFormat)
	XmlUnmarshaller.fromXml(String, EntityManager, DateFormat)


3 - Format JSON
------------------

le schéma de sérialisation est le suivant : {"__type"="type.objet",attributs...}
le type est mis optionnellement si celui-ci est ambigu.
il y a 8 méthodes public static à la sérialisation en json et 8 pour la désérialisation.

3.1 - Sérialisation
-------------------

	JsonMarshaller.toJson(U, Writer)
	JsonMarshaller.toJson(U)
	JsonMarshaller.toJson(U, Writer, DateFormat)
	JsonMarshaller.toJson(U, DateFormat)
	JsonMarshaller.toCompleteJson(U, Writer)
	JsonMarshaller.toCompleteJson(U)
	JsonMarshaller.toCompleteJson(U, Writer, DateFormat)
	JsonMarshaller.toCompleteJson(U, DateFormat)
	
3.2 - Désérialisation
---------------------
	
	JsonUnmarshaller.fromJson(Reader, EntityManager)
	JsonUnmarshaller.fromJson(Reader)
	JsonUnmarshaller.fromJson(String)
	JsonUnmarshaller.fromJson(String, EntityManager)
	JsonUnmarshaller.fromJson(Reader, EntityManager, DateFormat)
	JsonUnmarshaller.fromJson(Reader, DateFormat)
	JsonUnmarshaller.fromJson(String, DateFormat)
	JsonUnmarshaller.fromJson(String, EntityManager, DateFormat)


4 - Format Binaire
------------------
il y a 2 méthodes public static à la sérialisation en binaire et 2 pour la désérialisation.
	
4.1 - Sérialisation
-------------------

	BinaryMarshaller.toBinary(U, OutputStream)
	BinaryMarshaller.toCompleteBinary(U, OutputStream)

4.2 - Désérialisation
---------------------

	BinaryUnmarshaller.fromBinary(InputStream, EntityManager)
BinaryUnmarshaller.fromBinary(InputStream)
