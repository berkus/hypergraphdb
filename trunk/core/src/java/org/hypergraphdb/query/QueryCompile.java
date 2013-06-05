package org.hypergraphdb.query;

import java.util.HashMap;
import org.hypergraphdb.HGException;
import org.hypergraphdb.HGQuery;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.query.cond2qry.ConditionToQuery;
import org.hypergraphdb.query.cond2qry.OrToParellelQuery;
import org.hypergraphdb.query.cond2qry.QueryMetaData;
import org.hypergraphdb.query.cond2qry.ToQueryMap;
import org.hypergraphdb.util.CallContextRef;
import org.hypergraphdb.util.DelegateMapResolver;
import org.hypergraphdb.util.Mapping;

/**
 * <p>
 * A controller-type of class that maintains context during the query compilation process.
 * </p>
 * 
 * <p>
 * Unless you are extending the querying API somehow, this class is normally not of 
 * interest to HyperGraphDB users.
 * </p>
 * 
 * @author Borislav Iordanov
 *
 */
public class QueryCompile
{
	static CallContextRef<DelegateMapResolver<Class<? extends HGQueryCondition>, ConditionToQuery<?>>> 
		translatorMap =
			new CallContextRef<DelegateMapResolver<Class<? extends HGQueryCondition>, ConditionToQuery<?>>>()
	{
		public DelegateMapResolver<Class<? extends HGQueryCondition>, ConditionToQuery<?>> compute()
		{
			return new DelegateMapResolver<Class<? extends HGQueryCondition>, ConditionToQuery<?>>(
	                ToQueryMap.getInstance(), 
	                new HashMap<Class<? extends HGQueryCondition>, ConditionToQuery<?>>());			
		}
	};
    
    /**
     * Start query compilation in the current thread. Make sure {@link finish} is called
     * in a <code>finally</code> block. 
     */
    public static void start()
    {
        translatorMap.push();
    }
    
    /**
     * Finish query compilation in this thread.
     */
    public static void finish()
    {
        translatorMap.pop();
    }
    
    // This is temporary, until we figure a better API/framework to manage different
    // compilations based on user options (either global or on-the-fly configuration)
    public static void parallel()
    {
        if (translatorMap.get() != null)
        {
            translatorMap.get().getMap().put(Or.class, new OrToParellelQuery());
        }
        else
            ToQueryMap.getInstance().put(Or.class, new OrToParellelQuery());
    }
        
    @SuppressWarnings("unchecked")
    public static <ResultType> ConditionToQuery<ResultType> translator(HyperGraph graph, Class<? extends HGQueryCondition> conditionType)
    {
        ConditionToQuery<ResultType> trans = graph.getConfig().getQueryConfiguration().compiler(conditionType);        
        return trans != null ? trans : (ConditionToQuery<ResultType>)translatorMap.get().resolve(conditionType);
    }
    
    public static HGQueryCondition transform(HyperGraph graph, HGQueryCondition condition)
    {
        for (Mapping<HGQueryCondition, HGQueryCondition> m : graph.getConfig().getQueryConfiguration().getTransforms())
            condition = m.eval(condition); 
        return condition;
    }
    
    public static <T> HGQuery<T> translate(HyperGraph graph, HGQueryCondition condition)
    {
        ConditionToQuery<T> trans = translator(graph, condition.getClass());
        if (trans == null)
            throw new HGException("The query condition '" + condition + 
                    "' could not be translated to an executable query either because it is not specific enough. " +
                    "Please try to contrain the query futher, for example by specifying the atom's types or " +
                    "incidence sets or some indexed property value.");
        else
        {
            HGQuery<T> q = (HGQuery<T>)trans.getQuery(graph, condition);
            if (q == null)
                throw new IllegalArgumentException("Unable to convert query condition " +
                        condition + " to a query. Try constraining further, e.g. by atom type.");
            q.setHyperGraph(graph);
            return q;
        }
    }
    
    public static QueryMetaData toMetaData(HyperGraph graph, HGQueryCondition condition)
    {
        ConditionToQuery<?> trans = translator(graph, condition.getClass());
        if (trans == null)
            throw new HGException("The query condition '" + condition + 
                    "' could not be translated to an executable query either because it is not specific enough. " +
                    "Please try to contrain the query futher, for example by specifying the atom's types or " +
                    "incidence sets or some indexed property value.");
        else
            return trans.getMetaData(graph, condition);
    }
}