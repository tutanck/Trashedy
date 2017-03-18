package com.aj.jeez.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Anagbla Joan */
@RunWith(Parameterized.class)
public class ExpectedResultTest {


	private final JSONObject result;
	private final Set<String> epnOut;

	public ExpectedResultTest(
			final JSONObject result, 
			final Set<String> epnOut
			) {
		this.result = result;
		this.epnOut = epnOut;
	}


	@Parameters
	public static Collection<Object[]> params(
			//			JSONObject result,
			//			Set<String> epnOut
			) {
		ArrayList<Object[]> paramsList =new ArrayList<>();
		paramsList.add(
				new Object[] {
						new JSONObject(), 
						new HashSet<>(Arrays.asList(new String[]{"lol"}))
				}
				);
		//{ result, epnOut});
		return paramsList;
	}

	//TODO passer a une map pour typer les key du result et anisi verifier le typage static 
	@Test
	public void ResultShouldContainExpected(){
		for(String expected : epnOut){
			Assert.assertTrue(
					"{result} should at least contain all keys in {epnOut}",
					resultWellFormed(expected)
					);
		}
	}


	//TODO verifier le typage des key du resultat 
	private boolean resultWellFormed(
			String expected
			){
		return result.has(expected);
	}
}