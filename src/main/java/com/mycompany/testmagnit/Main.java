package com.mycompany.testmagnit;
public class Main 
{

    /**
     *	args[0] - Url
     *  args[1] - login
     *  args[2] - password
     *  args[3] - database
     *  args[4] - N
     *  args[5] - path XML first
     *  args[6] - path XML second
     */
    public static void main(String[] args)
    {
    	String url,login,password,database,path1,path2,pathXSLT;
    	int N = 0;
    	url = args[0];
    	login = args[1];
    	password= args[2];
    	database = args[3];
    	path1 = args[5];
    	path2 = args[6];
    	pathXSLT = "1.xsl";
    	try 
    	{
    		N = Integer.valueOf(args[4]);
    	}
    	catch(NumberFormatException e)
    	{
    		e.printStackTrace();
    	}
        GenerateFiles db = new GenerateFiles(url,login,password,database,N);
        int[] fieldsFromDB = db.GetNFromDatabase();
        db.GenerateXML(fieldsFromDB,path1);
        db.transformXML(pathXSLT,path1,path2);
        System.out.println(db.parseSumFromXML(path2));
    }

}
