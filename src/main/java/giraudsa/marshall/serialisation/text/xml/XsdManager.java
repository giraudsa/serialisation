package giraudsa.marshall.serialisation.text.xml;

import java.net.URI;

public interface XsdManager {

public <U> URI getUriOfXsd(Class<U> clazz);
}
