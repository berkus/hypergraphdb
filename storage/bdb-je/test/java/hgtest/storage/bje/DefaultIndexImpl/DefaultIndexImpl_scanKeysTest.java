package hgtest.storage.bje.DefaultIndexImpl;

import org.hypergraphdb.HGException;
import org.hypergraphdb.HGRandomAccessResult;
import org.hypergraphdb.storage.bje.DefaultIndexImpl;
import org.powermock.api.easymock.PowerMock;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static hgtest.storage.bje.TestUtils.assertExceptions;
import static hgtest.storage.bje.TestUtils.list;
import static org.testng.Assert.assertEquals;

/**
 * @author Yuriy Sechko
 */
public class DefaultIndexImpl_scanKeysTest extends DefaultIndexImplTestBasis
{
	@Test
	public void indexIsNotOpened() throws Exception
	{
		final Exception expected = new HGException(
				"Attempting to operate on index 'sample_index' while the index is being closed.");

		PowerMock.replayAll();
		final DefaultIndexImpl<Integer, String> index = new DefaultIndexImpl<Integer, String>(
				INDEX_NAME, storage, transactionManager, keyConverter,
				valueConverter, comparator);

		try
		{
			index.scanKeys();
		}
		catch (Exception occurred)
		{
			assertExceptions(occurred, expected);
		}
	}

	@Test
	public void thereAreNotAddedEntries() throws Exception
	{
		final List<Integer> expected = Collections.emptyList();

		startupIndex();
		PowerMock.replayAll();

		final HGRandomAccessResult<Integer> result = index.scanKeys();
		final List<Integer> actual = list(result);

		assertEquals(actual, expected);
		result.close();
		index.close();
	}

	@Test
	public void thereIsOneAddedEntry() throws Exception
	{
		final List<Integer> expected = new ArrayList<Integer>();
		expected.add(11);

		startupIndex();
		PowerMock.replayAll();
		index.addEntry(11, "eleven");

		final HGRandomAccessResult<Integer> result = index.scanKeys();
		final List<Integer> actual = list(result);

		assertEquals(actual, expected);
		result.close();
		index.close();
	}

	@Test
	public void thereAreSeveralAddedEntries() throws Exception
	{
		final List<Integer> expected = new ArrayList<Integer>();
		expected.add(1);
		expected.add(2);
		expected.add(3);

		startupIndex();
		PowerMock.replayAll();
		index.addEntry(1, "one");
		index.addEntry(2, "two");
		index.addEntry(3, "three");

		final HGRandomAccessResult<Integer> result = index.scanKeys();
		final List<Integer> actual = list(result);

		assertEquals(actual, expected);
		result.close();
		index.close();
	}

	@Test
	public void transactionManagerThrowsException() throws Exception
	{
		final Exception expected = new HGException(
				"Failed to lookup index 'sample_index': java.lang.IllegalStateException: This exception is thrown by fake transaction manager.");

		startupIndexWithFakeTransactionManager();
		try
		{
			index.scanKeys();
		}
		catch (Exception occurred)
		{
			assertEquals(occurred.getClass(), expected.getClass());
			assertEquals(occurred.getMessage(), expected.getMessage());
		}
		finally
		{
			index.close();
		}
	}
}
