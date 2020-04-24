package soRec.LuceneQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import soRec.Utils.Properties;

public class SingleQuery {
	
	/*
	 * 
	 * 
	 * 
	 * boosted versions
	 * 
	 * 
	 * 
	 */
	public String getImportDeclarationBoosted(HashMap<String, String> token, String query, boolean firstGuard, boolean overallGuard, String conj, HashMap<String, Double> entropies)
	{
		if(overallGuard==true)
		{
			String value = (String) token.get("ImportDeclaration");
			if(value!=null)
			{
				if(firstGuard==true)
				{
					if(entropies.get(token.get("ImportDeclaration"))!=null)
					{
						
						query +="ImportDeclaration: "+value+"^"+entropies.get(token.get("ImportDeclaration"));
					}
				}
				else
				{
					if(entropies.get(token.get("ImportDeclaration"))!=null)
					{
						query += " "+conj+" ImportDeclaration: "+value+"^"+entropies.get(token.get("ImportDeclaration"));
					}
				}
				
				
			}
		}
		
		return query;
	}
	
	public String getMethodDeclarationBoosted(HashMap<String, String> token, String query, boolean firstGuard, boolean overallGuard, String conj, HashMap<String, Double> entropies)
	{
		if(overallGuard==true)
		{
			String value = (String) token.get("MethodDeclaration");
			if(value!=null)
			{
				if(firstGuard==true)
				{
					if(entropies.get(token.get("MethodDeclaration"))!=null)
					{
						
						query +="MethodDeclaration: "+value+"^"+entropies.get(token.get("MethodDeclaration"));
					}
				}
				else
				{
					if(entropies.get(token.get("MethodDeclaration"))!=null)
					{
						query +=" "+conj+" MethodDeclaration: "+value+"^"+entropies.get(token.get("MethodDeclaration"));
					}
				}
				
			}
		}
		
		return query;
	}
	
	public String getMethodInvocationBoosted(HashMap<String, String> token, String query, boolean firstGuard, boolean overallGuard, String conj, HashMap<String, Double> entropies)
	{
		if(overallGuard==true)
		{
			String value = (String) token.get("MethodInvocation");
			if(value!=null)
			{
				if(firstGuard==true)
				{
					if(entropies.get(token.get("MethodInvocation"))!=null)
					{
						
						query +="MethodInvocation: "+value+"^"+entropies.get(token.get("MethodInvocation"));
					}
				}
				else
				{
					if(entropies.get(token.get("MethodInvocation"))!=null)
					{
						query +=" "+conj+" MethodInvocation: "+value+"^"+entropies.get(token.get("MethodInvocation"));
					}
				}
				
			}
		}
		
		return query;
	}
	
	public String getVariableDeclarationTypeBoosted(HashMap<String, String> token, String query, boolean firstGuard, boolean overallGuard, String conj, ArrayList<String> imports, HashMap<String, Double> entropies)
	{
		if(overallGuard==true)
		{
			String value = (String) token.get("VariableDeclarationType");
			
			if(value!=null)
			{
				/*
				 * non possiamo distinguere tra dichiarazioni di classi proprio e metodi quindi controlliamo se la dichiarazione sta tra gli import
				 */
				String imp = "";
				for(int i=0; i<imports.size(); i+=1)
				{
					imp = imports.get(i);
					
					if((imp!=null&&imp.contains(value))||(imp!=null&&value.contains(imp)))
					{
						if(firstGuard==true)
						{
							if(entropies.get(token.get("VariableDeclarationType"))!=null)
							{
								
								query +="VariableDeclarationType: "+value+"^"+entropies.get(token.get("VariableDeclarationType"));
								
							}
						}
						else
						{
							if(entropies.get(token.get("VariableDeclarationType"))!=null)
							{
								query +=" "+conj+" VariableDeclarationType: "+value+"^"+entropies.get(token.get("VariableDeclarationType"));
							}
						}
						break;
					}
				}

			}
			

		}
		
		return query;
	}
	
	public String getVariableDeclarationBoosted(HashMap<String, String> token, String query, boolean firstGuard, boolean overallGuard, String conj, HashMap<String, Double> entropies)
	{
		if(overallGuard==true)
		{
			String value = (String) token.get("VariableDeclaration");
			if(value!=null)
			{
				if(firstGuard==true)
				{
					if(entropies.get(token.get("VariableDeclaration"))!=null)
					{
						
						query +="VariableDeclaration: "+value+"^"+entropies.get(token.get("VariableDeclaration"));
						//query +="VariableDeclaration: "+value;
					}
				}
				else
				{
					if(entropies.get(token.get("VariableDeclaration"))!=null)
					{
						query += " "+conj+" VariableDeclaration: "+value+"^"+entropies.get(token.get("VariableDeclaration"));
						//query += " "+conj+" VariableDeclaration: "+value;
					}
				}
				
			}
		}
		
		return query;
	}
	
	public String getClassInstanceBoosted(HashMap<String, String> token, String query, boolean firstGuard, boolean overallGuard, String conj, HashMap<String, Double> entropies)
	{
		if(overallGuard==true)
		{
			String value = (String) token.get("ClassInstance");
			if(value!=null)
			{
				if(firstGuard==true)
				{
					if(entropies.get(token.get("ClassInstance"))!=null)
					{
						
						query +="ClassInstance: "+value+"^"+entropies.get(token.get("ClassInstance"));
					}
				}
				else
				{
					if(entropies.get(token.get("ClassInstance"))!=null)
					{
						query += " "+conj+" ClassInstance: "+value+"^"+entropies.get(token.get("ClassInstance"));
					}
				}
				
			}
		}
		
		return query;
	}

	public String getTitle(HashMap<String, Integer> vars, String query, boolean overallGuard, String conj, Properties prop) 
	{
		if(overallGuard==true)
		{
			Iterator it = vars.entrySet().iterator();
		    while (it.hasNext()) 
		    {
		    	Map.Entry pair = (Map.Entry)it.next();
		    	if(pair.getKey().toString().toLowerCase().equals("java")!=true)
		    	{
		    		if((Integer)pair.getValue()>4)
		    		{
		    			query +=" "+conj+" Title: "+pair.getKey()+"^"+prop.getTitleBoostValue();
		    		}
		    		else
		    		{
		    			query +=" "+conj+" Title: "+pair.getKey()+"^"+pair.getValue();
		    		}
		    		
		    	}
		    }
		    
		}
		
		return query;
	}
	
	public String getAnswer(HashMap<String, Integer> vars, String query, boolean overallGuard, String conj, Properties prop) 
	{
		if(overallGuard==true)
		{
			Iterator it = vars.entrySet().iterator();
		    while (it.hasNext()) 
		    {
		    	Map.Entry pair = (Map.Entry)it.next();
		    	if(pair.getKey().toString().toLowerCase().equals("java")!=true)
		    	{
		    		if((Integer)pair.getValue()>4)
		    		{
		    			query +=" "+conj+" Answer: "+pair.getKey()+"^"+prop.getAnswerBoostValue();
		    		}
		    		else
		    		{
		    			query +=" "+conj+" Answer: "+pair.getKey()+"^"+Double.valueOf((Integer)pair.getValue())/3;
		    		}
		    		
		    	}
		    }
		}
		
		return query;
	}
	
	public String getQuestion(HashMap<String, Integer> vars, String query, boolean overallGuard, String conj, Properties prop) 
	{
		if(overallGuard==true)
		{
			Iterator it = vars.entrySet().iterator();
		    while (it.hasNext()) 
		    {
		    	Map.Entry pair = (Map.Entry)it.next();
		    	if(pair.getKey().toString().toLowerCase().equals("java")!=true)
		    	{
		    		if((Integer)pair.getValue()>4)
		    		{
		    			query +=" "+conj+" Question: "+pair.getKey()+"^"+prop.getQuestionBoostValue();
		    		}
		    		else
		    		{
		    			query +=" "+conj+" Question: "+pair.getKey()+"^"+Double.valueOf((Integer)pair.getValue())/3;
		    		}
		    		
		    	}
		    }
		}
		
		return query;
	}
}
