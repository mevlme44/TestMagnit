package com.mycompany.testmagnit;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
public class GenerateFiles 
{
    private String url;
    private String userName;
    private String password;
    private String database;
    private int N;
    
    public GenerateFiles()
    {
        this.url = "";
        this.userName = "";
        this.password = "";
        this.database = "";
        this.N = 0;
    }
    public GenerateFiles(String url, String userName, String password, String database, int N)
    {
        
        this.userName = userName;
        this.password = password;
        this.database = database;
        this.url = "jdbc:mysql://" + url + "/" + database + "?useUnicode=true&serverTimezone=UTC";
        this.N = N; 
    }
    
    public void SetUrl(String url)
    {
        this.url = "jdbc:mysql://" + url + "/" + database + "?useUnicode=true&serverTimezone=UTC";
    }
    
    public void SetUserName(String userName)
    {
        this.userName = userName;
    }
    
    public void SetPassword(String password)
    {
        this.password = password;
    }
    
    public void SetDatabase(String database)
    {
        this.database = database;
    }
    
    public void SetN(int n)
    {
        this.N = n;
    }
    
    public String GetUrl()
    {
        return this.url;
    }
    
    public String GetUserName()
    {
        return this.userName;
    }
    
    public String GetPassword()
    {
        return this.password;
    }
    
    public String GetDatabase()
    {
        return this.database;
    }
    
    public int GetN()
    {
        return this.N;
    }
    
    public int[] GetNFromDatabase()
    {
    	int[] field = {};
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connect = DriverManager.getConnection(url,userName,password);
            Delete(connect);
            Insert(connect);
            field = Select(connect);
            return field;
            
        }
        catch (SQLException e) 
        {
            System.out.println("Error connection");
            e.printStackTrace();
        } 
        catch (ClassNotFoundException ex) 
        {
            System.out.println("Error with driver");
            ex.printStackTrace();
        }
        return field;
    }
    private void Insert(Connection connect)
    {
    	String queryTmp = "INSERT INTO test VALUES";
    	try 
    	{
			
			if(N >= 100)
			{
				int j = 1;
				int i = 0;
				for(int k = 0; k < 20; k++)
				{
					j = 1;
					queryTmp = "INSERT INTO test VALUES";
					if(k != 19)
					{
						while(j <= N/20)
						{
							queryTmp += "("+(i + j)+"),";
							j++;
							
						}
					}
					else 
					{
						while(j <= N/20 + N%20)
						{
							queryTmp += "("+(i+j)+"),";
							j++;
						}
					}
					
					var tmp = queryTmp.toCharArray();
					tmp[tmp.length-1] = ';';
					
					String query = String.valueOf(tmp);
					PreparedStatement prepSt = connect.prepareStatement(query);
	    			prepSt.execute();
	    			i = (k+1)*(j-1);
				}
			}
			else
			{
				for(int i = 0; i<N; i++)
				{
					queryTmp += "("+(i+1)+"),";
				}
				var tmp = queryTmp.toCharArray();
				tmp[tmp.length-1] = ';';
				
				String query = String.valueOf(tmp);
				PreparedStatement prepSt = connect.prepareStatement(query);
    			prepSt.execute();
			}
			
		} 
    	catch (SQLException e) 
    	{
			System.out.println("Insert error");
			e.printStackTrace();
		}
    	
    }
    private void Delete(Connection connect)
    {
    	String query = "DELETE FROM test WHERE field != -1;";
    	try 
    	{
			PreparedStatement prepSt = connect.prepareStatement(query);
	    	prepSt.execute();
			
		} 
    	catch (SQLException e) 
    	{
			System.out.println("Delete error");
			e.printStackTrace();
		}
    	
    }
    private int[] Select(Connection connect)
    {
    	String query = "SELECT * FROM test";
    	int[] field = new int[N];
    	try 
    	{
			PreparedStatement prepSt = connect.prepareStatement(query);
			ResultSet rs = prepSt.executeQuery();
			int tmp = 0;
			while (rs.next())
			{
				field[tmp] = rs.getInt("field");
				tmp++;
			}
			return field;
		} 
    	catch (SQLException e) 
    	{
			System.out.println("Select error");
			e.printStackTrace();
			
		}
    	return null;
    }
    public void GenerateXML(int[] fields,String path)
    {
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try 
        {
        	builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element rootElem = doc.createElement("Entries");
            doc.appendChild(rootElem);
            for(int i =0; i<fields.length;i++)
            {
            	rootElem.appendChild(getEntry(doc,fields[i]));
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult file = new StreamResult(new File(path));
            transformer.transform(source, file);
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
    }
    private Node getEntry(Document doc, int field) {
        Element entry = doc.createElement("Entry");
        entry.setTextContent(String.valueOf(field));
        return entry;
    }

    public void transformXML(String pathXSLT,String path1, String path2)
    {
    	try
    	{
    		TransformerFactory factory = TransformerFactory.newInstance();
    		Source xslt = new StreamSource(new File(pathXSLT));
    		Transformer transformer = factory.newTransformer(xslt);
    		Source xml = new StreamSource(new File(path1));
    		transformer.transform(xml, new StreamResult(new File(path2)));
    	}
    	catch(TransformerException ex)
    	{
    		 ex.printStackTrace();
    	}
    }
    
    
    public Long parseSumFromXML(String path)
    {
    	try
    	{
    	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	    DocumentBuilder db = dbf.newDocumentBuilder();
    	    Document doc = db.parse(new File(path));
    	    Long sum = (long) 0;
    	    NodeList entries = doc.getChildNodes();
    	    NodeList entry = entries.item(0).getChildNodes();
    	    for(int i =0;i<entry.getLength();i++)
    	    {
    	        Node node = entry.item(i);
    	    	NamedNodeMap attributes = node.getAttributes();
    	        Node field;
    	        field = attributes.getNamedItem("field");
    	        sum += Integer.valueOf(field.getNodeValue());
    	    }
    	    return sum;
    	} 
    	catch (ParserConfigurationException e) 
    	{
			e.printStackTrace();
		} 
    	catch (SAXException e) 
    	{
			e.printStackTrace();
		} 
    	catch (IOException e)
    	{
			e.printStackTrace();
		}
    	return (long)0;
    }
}
