package net.petrikainulainen.spring.testmvc;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Petri Kainulainen
 */
public class IntegrationTestUtil {
    public static byte[] convertObjectToFormUrlEncodedBytes(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

        Map<String, Object> propertyValues = mapper.convertValue(object, Map.class);

        Set<String> propertyNames = propertyValues.keySet();
        Iterator<String> nameIter = propertyNames.iterator();

        StringBuilder formUrlEncoded = new StringBuilder();

        for (int index=0; index < propertyNames.size(); index++) {
            String currentKey = nameIter.next();
            Object currentValue = propertyValues.get(currentKey);

            formUrlEncoded.append(currentKey);
            formUrlEncoded.append("=");
            formUrlEncoded.append(currentValue);

            if (nameIter.hasNext()) {
                formUrlEncoded.append("&");
            }
        }

        return formUrlEncoded.toString().getBytes();
    }
}
