package org.owasp.appsensor.storage.elasticsearch.mapping;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.owasp.appsensor.core.geolocation.GeoLocation;

import java.io.IOException;

/**
 * Jackson Serializer that deserializes GeoLocations serialized to JSON by {@link GeoLocationJacksonSerializer}.
 *
 * @author Maik Jäkel(m.jaekel@xsite.de) http://www.xsite.de
 */
public class GeoLocationJacksonDeserializer extends StdDeserializer<GeoLocation> {
    public GeoLocationJacksonDeserializer() {
        super(GeoLocation.class);
    }

    @Override
    public GeoLocation deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        JsonToken jsonToken;

        Double lat = null;
        Double lon = null;

        while ((jsonToken = jsonParser.nextValue()) != null) {

            switch (jsonToken) {
                case VALUE_NUMBER_FLOAT:
                    if ("lat".equals(jsonParser.getCurrentName())) {
                        lat = jsonParser.getDoubleValue();
                        break;
                    } else if ("lon".equals(jsonParser.getCurrentName())) {
                        lon = jsonParser.getDoubleValue();
                        break;
                    }
                default:
                    break;
            }
        }

        if (lat == null || lon == null) {
            return null;
        }

        return new GeoLocation(lat, lon);
    }


}
