package org.hypergraphdb.peer.serializer;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import org.hypergraphdb.handle.UUIDPersistentHandle;
import org.hypergraphdb.peer.protocol.SerializerManager;
import org.hypergraphdb.util.Pair;

public class DefaultSerializerManager implements SerializerManager
{
	public static final Integer NULL_SERIALIZER_ID = 0;
	
	public static final Integer PERSISTENT_HANDLE_SERIALIZER_ID = 100;

	public static final Integer SUBGRAPH_SERIALIZER_ID = 200;

	private static HashMap<String, HGSerializer> wellKnownSerializers = new HashMap<String, HGSerializer>();
	private static HashMap<String, Integer> wellKnownSerializerIds = new HashMap<String, Integer>();
	private static HashMap<Integer, HGSerializer> invertedWellKnownSerializerIds = new HashMap<Integer, HGSerializer>();
		
	private static LinkedList<Pair<SerializerMapper, Integer>> serializerMappers = new LinkedList<Pair<SerializerMapper,Integer>>();
	private static HashMap<Integer, SerializerMapper> invertedSerializerMappers = new HashMap<Integer, SerializerMapper>(); 
	
	
	public DefaultSerializerManager() 
	{
		addWellknownSerializer("nullSerializer", new NullSerializer(), NULL_SERIALIZER_ID);
		
		addWellknownSerializer(UUIDPersistentHandle.class.getName(), new PersistentHandlerSerializer(), PERSISTENT_HANDLE_SERIALIZER_ID);
		
		addSerializerMapper(new SubgraphSerializer(), SUBGRAPH_SERIALIZER_ID, null);
	}
	
	public static void addWellknownSerializer(String name, HGSerializer serializer, Integer id)
	{
		wellKnownSerializers.put(name, serializer);
		wellKnownSerializerIds.put(name, id);
		invertedWellKnownSerializerIds.put(id, serializer);
	}

	public static void addSerializerMapper(SerializerMapper mapper, Integer id, SerializerMapper addAfter)
	{
		if (addAfter == null)
		{
			serializerMappers.addFirst(new Pair<SerializerMapper, Integer>(mapper, id));
		}else{
			int index = serializerMappers.indexOf(addAfter);
			if (index >= 0)
			{
				serializerMappers.add(index + 1, new Pair<SerializerMapper, Integer>(mapper, id));
			}else{
				serializerMappers.add(new Pair<SerializerMapper, Integer>(mapper, id));
			}
		}
		
		invertedSerializerMappers.put(id, mapper);
	}
	
	//interface functions
	public HGSerializer getSerializer(InputStream in)
	{
		Integer serializerId = SerializationUtils.deserializeInt(in);
		return getSerializerById(serializerId); 

	}
	public HGSerializer getSerializer(Object data){
		if (data == null) return wellKnownSerializers.get("nullSerializer");
		else return getSerializerByType(data.getClass());
	}
	
	public HGSerializer getSerializerByType(Class<?> clazz){
		HGSerializer serializer = null;
		
		//first try with well known serializers
		serializer = getSerializerByTypeName(clazz.getName());
		if (serializer == null)
		{
			//try existing mappers
			ListIterator<Pair<SerializerMapper, Integer>> iterator = serializerMappers.listIterator();
			while ((serializer == null) && (iterator.hasNext()))
			{
				serializer = iterator.next().getFirst().accept(clazz);
			}
		}

		//should be worry about this being null?
		return serializer;
	}
	

	private HGSerializer getSerializerByTypeName(String typeName){
		HGSerializer serializer = null;

		serializer = wellKnownSerializers.get(typeName);
		
		if (serializer == null){
			if (serializer == null){
				// TODO check if it is actually a bean
				serializer = wellKnownSerializers.get("beanSerializer");
			}
		}
		
		return serializer;
	}

	public static HGSerializer getSerializerById(Integer serializerId)
	{
		//first try well known
		HGSerializer serializer = invertedWellKnownSerializerIds.get(serializerId);
		
		if (serializer == null)
		{
			SerializerMapper mapper = invertedSerializerMappers.get(serializerId);
			
			if (mapper != null)
			{
				serializer = mapper.getSerializer();
			}
		}
		
		//should be worry about this being null?
		return serializer;
	}
}
