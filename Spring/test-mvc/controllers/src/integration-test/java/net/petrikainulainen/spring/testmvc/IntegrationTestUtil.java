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

        Map<String, Object> values = mapper.convertValue(object, Map.class);

        Set<String> keys = values.keySet();
        Iterator<String> keyIter = keys.iterator();

        StringBuilder formUrlEncoded = new StringBuilder();

        for (int index=0; index < keys.size(); index++) {
            String currentKey = keyIter.next();
            Object currentValue = values.get(currentKey);

            formUrlEncoded.append(currentKey);
            formUrlEncoded.append("=");
            formUrlEncoded.append(currentValue);

            if (keyIter.hasNext()) {
                formUrlEncoded.append("&");
            }
        }

        return formUrlEncoded.toString().getBytes();
    }
}
