package soRec.Utils;

import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.classic.ParseException;



 
public class Entropy {
	
	public HashMap<String, Double> calculate(String target, String snippet, HashMap<String, Double> entropies, int tokens) throws ParseException, IOException
	{	
		int tot = tokens;
		
		double p;
		double np;
		
		double count = StringUtils.countMatches(snippet, target);
		
		p = count/(double)tot;
		np = 1-p;
		
		double ent = -p*(Math.log(p)/Math.log(2))-np*(Math.log(np)/Math.log(2));
		entropies.put(target, ent);
		return entropies;
		
	}
 
	public ArrayList<String> aggregateTokens(ArrayList<ArrayList<HashMap<String,String>>> projectTokens)
	{
	
		ArrayList<String> results = new ArrayList<String>();
		
		for(ArrayList<HashMap<String, String>> tokensFile:projectTokens)
		{
			for(HashMap<String, String> tokens:tokensFile)
			{
				Collection coll = tokens.values();
				results.add(coll.toArray()[0].toString());
			}
		}
		
		return results;
	}
	

	
	public HashMap<String, Double> assignBoostLarge(HashMap<String, Double> entropies)
	{
		LinkedHashMap<String, Double> entropiesOrdered = new LinkedHashMap<String, Double>();
		int size = entropies.size();
		
		entropies.entrySet().stream()
        .sorted(Map.Entry.<String, Double>comparingByValue().reversed()) 
        .limit(size).forEachOrdered(x -> entropiesOrdered.put(x.getKey(), x.getValue()));
		
		int count = 0;
		HashMap<String, Double> entropiesNormalized = new HashMap<String, Double>();
		
		Set<String> keys = entropiesOrdered.keySet();
		
		for(String singleKey:keys)
		{
			Double val = entropies.get(singleKey);
			if(count<=size*0.25)
			{
				entropiesNormalized.put(singleKey, 4.0);
				count += 1;
				continue;
			}
			if(count>=(size*0.25)&&count<(size*0.50))
			{
				entropiesNormalized.put(singleKey, 3.0); 
				count += 1;
				continue;
			}
			if(count>=(size*0.50)&&count<(size*0.75))
			{
				entropiesNormalized.put(singleKey, 2.0);
				count += 1;
				continue;
			}
			if(count>=(size*0.75)&&count<size)
			{
				entropiesNormalized.put(singleKey, 1.0);
				count += 1;
				continue;
			}
		}
		return entropiesNormalized;
	}
	
	public HashMap<String, Double> assignFlatBoost(HashMap<String, Double> entropies)
	{
		Set<String> keys = entropies.keySet();
		HashMap<String, Double> entropiesNormalized = new HashMap<String, Double>();
		
		for(String singleKey:keys)
		{
			entropiesNormalized.put(singleKey, 1.0);
		}
		return entropiesNormalized;
	}
}

