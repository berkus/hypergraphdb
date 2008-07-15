package hgtest;

import java.util.List;

import org.hypergraphdb.HGSearchResult;

@SuppressWarnings("unchecked")
public class T
{
	/**
	 * Return a random integer between 0 (inclusive) and i (exclusive).
	 *  
	 * @param i
	 * @return
	 */
	public static int random(int i)
	{
		return random(0, i);
	}
	
	/**
	 * Return a random integer between i (inclusive) and j (exclusive).
	 *  
	 * @param i
	 * @return
	 */
	public static int random(int i, int j)
	{
		return i + (int)(Math.random()*(j-i));
	}	
	
	public static void swap(Object [] A, int i, int j)
	{
		Object x = A[i];
		A[i] = A[j];
		A[j] = x; 
	}
	
	public static void swap(List L, int i, int j)
	{
		Object x = L.get(i);
		L.set(i, L.get(j));
		L.set(j, x);
	}
	
	public static void shuffle(List L)
	{
		for (int i = 0; i < L.size(); i++)
			swap(L, random(i), random(i, L.size()));
	}
	
	/**
	 * Move the result set forward maxSteps if possible. Return the number
	 * of successful forward moves.
	 */
	public static int forward(HGSearchResult<?> rs, int maxSteps)
	{
		int i = 0;
		for (; i < maxSteps && rs.hasNext(); i++)
			rs.next();
		return i;
	}

	/**
	 * Move the result set forward maxSteps if possible. Return the number
	 * of successful forward moves.
	 */	
	public static int back(HGSearchResult<?> rs, int maxSteps)
	{
		int i = 0;
		for (; i < maxSteps && rs.hasPrev(); i++)
			rs.prev();
		return i;		
	}
	
	/**
	 * Go back and forth on a result set an 'iteration' number of times. Assume
	 * the result set is not empty and it already has a current position. The windowSize
	 * parameters controls how far we are going to move - a random number of steps
	 * b/w 0 and windowSize is actually used.
	 */
	public static void backAndForth(HGSearchResult<?> rs, int windowSize, int iteration)
	{
		boolean advance = true;
		for (int i = 0; i < iteration; i++)
		{
			Object x = rs.current();
			int steps = random(windowSize);
			steps = forward(rs, steps);
			if (back(rs, steps) != steps)
				throw new RuntimeException("Moved " + steps + " forward, but not backward.");
			if (!x.equals(rs.current()))
				throw new RuntimeException("Moving " + steps + " steps forward and backward missed current " + x);
			if (advance)
			{
				forward(rs, random(windowSize));
				if (!rs.hasNext())
				{
					back(rs, random(windowSize));
					advance = false;
				}				
			}
			else 
			{
				back(rs, random(windowSize));
				if (!rs.hasPrev())
				{
					forward(rs, random(windowSize));
					advance = true;
				}
			}
				
		}
	}	
}
