# serialisation-désérialisation multi-format (json - xml)

petite bibliothèque de sérialisation - deserialisation orienté simplicité d'utilisation et supportant le polymorphisme.

Elle propose un outil de sérialisation vers le format xml et json (bientot en binaire):

Marshaller XML: export uniquement d'un graphe d'objets en une représentation XML
il y a 4 méthodes public static à la sérialisation en xml et 2 pour la désérialisation.
Sérialisation :
- XmlMarshaller.toXml(U obj, StringWriter output) qui sérialise un objet et ses liens par références en xml dans un writer
- XmlMarshaller.ToXml(U obj) : la meme chose, mais renvoi un string
- XmlMarshaller.toCompleteXml(U obj, StringWriter output) : qui sérialise toute la grappe d'objet tenue par obj en xml dans un writer
- XmlMarshaller.toCompleteXml(U obj) : la meme chose, mais renvoi un string
pour json :
- JsonMarshaller.toJson(U obj, StringWriter output) qui sérialise un objet et ses liens par références en json dans un writer
- JsonMarshaller.ToJson(U obj) : la meme chose, mais renvoi un string
- JsonMarshaller.toCompleteJson(U obj, StringWriter output) : qui sérialise toute la grappe d'objet tenue par obj en json dans un writer
- JsonMarshaller.toCompleteJson(U obj) : la meme chose, mais renvoi un string

D'une manière générale, le polymorphisme (classe dérivée) et les cycles sont autorisés dans les graphes d'objets sérialisés. L'identification des objets est implémentée de la manière suivante: 
	- chaque objet sérialisé doit avoir un identifiant unique (attribut "id") renseigné et unique à travers le graphe
	- si un objet est instance d'une classe qui ne dispose pas de cet attribut "id", un UUID lui est automatiquement affecté (création d'un pseudo-attribut "id")
	- toute référence ultérieure à un objet est remplacée par son UUID pour éviter un débordement de pile et permettre une représentation hiérarchique d'un graphe
	Un attribut peut être exclu de la sérialisation en le préfixant de @IgnoreSerialise
	Le nom d'un attribut tel qu'il apparaîtra dans le format de sortie peut être adapté par @MarshallAs("NomPublicIci")
	Par défaut, le marshaller sérialise par référence. Pour obtenir sérialisé de manière imbriqué, il faut décorer les attributs de la classe
	avec le préfixe @relation(type=TypeRelation.COMPOSITION).

En XML, le schémat de sérialisation est le suivant : <nomBalise type="type.objet">liste des attributs suivant le meme schémat</nomBalise>
En Json, le schéma de sérialisation est le suivant : {"__type"="type.objet",attributs...}

pour la désérialisation, il y a 4 méthodes publiques static par type.
XmlUnmarshaller.fromXml(StringReader reader, EntityManager entity)
XmlUnmarshaller.fromXml(StringReader reader)
XmlUnmarshaller.fromXml(String stringToUnmarshall)
XmlUnmarshaller.fromXml(String stringToUnmarshall, EntityManager entity)
L'entity est un objet implémentant une interface ayant une méthode findObject(String id, Class<?> type)

idem pour le json :

JsonUnmarshaller.fromJson(StringReader reader, EntityManager entity)
JsonUnmarshaller.fromJson(StringReader reader)
JsonUnmarshaller.fromJson(String stringToUnmarshall)
JsonUnmarshaller.fromJson(String stringToUnmarshall, EntityManager entity)

En termes d'interface de programmation, il y a deux niveaux d'utilisation: 
1. API triviale:
	Supposons un objet "obj", d'un type quelconque (par exemple MonObjet)
	String xml = XmlMarshaller.ToXml(obj) // produit la représentation XML de cet objet sous forme d'une string.
	String json = JsonMarshaller.ToJson(obj)
	on peut aisni désérialiser :
	MonObjet monObjetFromXml = XmlUnmarshaller.fromXml(xml)
	MonObjet monObjetFromJson = JsonUnmarshaller.fromJson(json)
	 
	
2. API fine: offre les mêmes fonctionnalités, mais travaille sur des flux binaires/textuels
	Cela permet les cas d'utilisation suivants: 
	- export XML/JSON vers un flux réseau sans construire de string intermédiaire (indispensable sur un serveur de service ou pour un graphe volumineux)
