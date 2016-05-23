package de.beuth.sp.screbo.database;

import java.io.Serializable;

import org.ektorp.CouchDbConnector;
import org.ektorp.impl.ObjectMapperFactory;
import org.ektorp.impl.jackson.EktorpJacksonModule;
import org.ektorp.util.Assert;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Modified org.ektorp.impl.StdObjectMapperFactory to be able to de-/serialize new java.time objects.
 * 
 * @author volker.gronau
 *
 */
@SuppressWarnings("serial")
public class ScreboObjectMapper implements ObjectMapperFactory, Serializable {
	public static ScreboObjectMapper instance = new ScreboObjectMapper();

	/**
	 * Use as singleton.
	 * 
	 * @return
	 */
	public static ScreboObjectMapper getInstance() {
		return instance;
	}

	private ObjectMapper objectMapperInstance;
	private boolean writeDatesAsTimestamps = false;

	@Override
	public synchronized ObjectMapper createObjectMapper() {
		if (objectMapperInstance == null) {
			objectMapperInstance = new ObjectMapper();
			applyDefaultConfiguration(objectMapperInstance);
		}
		return objectMapperInstance;
	}

	@Override
	public ObjectMapper createObjectMapper(CouchDbConnector connector) {
		ObjectMapper objectMapper = new ObjectMapper();
		applyDefaultConfiguration(objectMapper);
		objectMapper.registerModule(new EktorpJacksonModule(connector, objectMapper));
		return objectMapper;
	}

	public synchronized void setObjectMapper(ObjectMapper om) {
		Assert.notNull(om, "ObjectMapper may not be null");
		this.objectMapperInstance = om;
	}

	public void setWriteDatesAsTimestamps(boolean b) {
		this.writeDatesAsTimestamps = b;
	}

	private void applyDefaultConfiguration(ObjectMapper om) {
		om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, this.writeDatesAsTimestamps);
		om.registerModule(new JavaTimeModule());

		// Use fields instead of getters and setters
		om.setVisibility(om.getVisibilityChecker().withFieldVisibility(JsonAutoDetect.Visibility.ANY).withGetterVisibility(JsonAutoDetect.Visibility.NONE).withSetterVisibility(JsonAutoDetect.Visibility.NONE));
	}
}
