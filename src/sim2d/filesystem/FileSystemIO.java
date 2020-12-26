package sim2d.filesystem;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.sql.Timestamp;

/**
 * @author Mark
 * @version updated by Brendon Lam for the context of Crohn's Disease
 */
public class FileSystemIO 
{

	public static String fileName = "example_parameter_file.xml";

	/**
	 * Gets the file from the resource folder (If using IntelliJ)
	 *
	 * @param fileName - the location of the file, as a string
	 */
	public static File getFileFromResource(String fileName)
	{
		ClassLoader classLoader = FileSystemIO.class.getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());
		return file;
	}

	/**
	 * Opens an XML file from the filesystem as a Document, so that information can be extracted from it.  
	 * 
	 * @param location - the location of the file, as a string.
	 */
	public static Document openXMLFile(String location)
	{
		if (location == null) {
			location = fileName;
		} else {
			fileName = location;
		}

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		
		Document doc = null;
		
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(new File(location));
//			doc = docBuilder.parse(getFileFromResource(location));

			//	normalize text representation
			doc.getDocumentElement().normalize();
			
			// remove any nodes in the DOM document that contain only whitespace. 
			removeWhitespaceNodes(doc);
			removeCommentNodes(doc);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(doc);
		return doc;
	}
	
	/**
	 * Code obtained (and slightly modified, but not much) from http://www.roseindia.net/java/beginners/CopyFile.shtml
	 * @param source
	 * @param dest
	 */
	public static void copyFile(String source, String dest){
	    try{
	      File sf = new File(source);
	      File df = new File(dest);
	      InputStream in = new FileInputStream(sf);
	      
	      OutputStream out = new FileOutputStream(df);

	      byte[] buf = new byte[1024];
	      int len;
	      while ((len = in.read(buf)) > 0){
	        out.write(buf, 0, len);
	      }
	      in.close();
	      out.close();
	      System.out.println("File " + source + " copied to " + dest);
	    }
	    catch(FileNotFoundException ex){
	      System.out.println(ex.getMessage() + " in the specified directory.");
	      System.exit(0);
	    }
	    catch(IOException e){
	      System.out.println(e.getMessage());      
	    }
	  }
	
	/**
	 * Method takes an XML document represented as a DOM document and writes it to the file system. 
	 * 
	 * @param document the Document representation of the XML file
	 * @param location the directory path to the location where the XML file is to be writen to 
	 * @param filename the file name of the file that the XML is to be written in.
	 */
	public static void writeXMLFile(Document document, String location, String filename)
	{
		File directories = new File(location);
		if(directories.exists() == false)									// if the directory structure does not already exist, then create it. 
		{
			try{
				directories.mkdirs();
			} catch (Exception e) {
				System.out.println("FileSystemIO: Failed to create the directory structure in which to write the XML file. " + e.getStackTrace());
			}
		}
		File outputFile = new File(location + File.separator + filename);
		if(outputFile.exists())
			throw new RuntimeException("FileSystemIO: designated output file already exists!");
		

		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();	// transformer converts source to an output. 
			Transformer transformer = tFactory.newTransformer();

			DOMSource source = new DOMSource(document); 					// the source for the transformation.
			StreamResult result = new StreamResult(outputFile); 			// where the transformation is to be sent to.
			transformer.transform(source, result); 							// perform the transformation from source to result. 

		} catch (TransformerConfigurationException tce) {
			// Error generated by the parser
			System.out.println("* Transformer Factory error");
			System.out.println("  " + tce.getMessage());

			// Use the contained exception, if any
			Throwable x = tce;
			if (tce.getException() != null)
				x = tce.getException();
			x.printStackTrace();
		} catch (TransformerException te) {
			// Error generated by the parser
			System.out.println("* Transformation error");
			System.out.println("  " + te.getMessage());

			// Use the contained exception, if any
			Throwable x = te;
			if (te.getException() != null)
				x = te.getException();
			x.printStackTrace();

		}
		
	}

	/**
	 * Code to remove nodes from the DOM tree that contain only whitespace charaters. Adapted from code obtained from 
	 * 
	 * http://stackoverflow.com/questions/978810/how-to-strip-whitespace-only-text-nodes-from-a-dom-before-serialization
	 * 
	 */
	private static Document removeWhitespaceNodes(Document doc)
	{
		XPathFactory xpathFactory = XPathFactory.newInstance();
		try {
			//	 XPath to find empty text nodes.
			XPathExpression xpathExp = xpathFactory.newXPath().compile(
			        "//text()[normalize-space(.) = '']");  
			NodeList emptyTextNodes = (NodeList) 
		        xpathExp.evaluate(doc, XPathConstants.NODESET);

			//	Remove each empty text node from document.
			for (int i = 0; i < emptyTextNodes.getLength(); i++) 
			{	
			    Node emptyTextNode = emptyTextNodes.item(i);
			    emptyTextNode.getParentNode().removeChild(emptyTextNode);
			}
		} catch (Exception e)
		{ 
			System.out.println("exception raised whilst removing whitespace nodes from document : "+ e.getStackTrace());
		}
		
		return doc;
	}
	
	private static Document removeCommentNodes(Document doc)
	{
		Element e = doc.getDocumentElement();
		removeCommentNodesRecursion(e);
		
		return doc;
	}
	
	private static void removeCommentNodesRecursion(Node n)
	{
		if(n instanceof Comment)
		{
			n.getParentNode().removeChild(n);
		} else 	{
			NodeList ns = n.getChildNodes();
			if(ns == null) 	return;
			for(int i = 0; i < ns.getLength(); i++)
			{
				Node child = ns.item(i);
				removeCommentNodesRecursion(child);
			}
		}
	}
	
	public static void printDOMRecursive(int indent, Node n)
	{
		System.out.println();
		for(int i = 0; i < indent; i++)
			System.out.print("  ");
		
		System.out.print(n.getNodeName() + "  __  " + n.getTextContent());
		
		NodeList ns = n.getChildNodes();
		if (ns == null) return;
		for(int i = 0; i < ns.getLength(); i++)
		{
			Node child = ns.item(i);
			printDOMRecursive(indent + 1, child);
		}
	}

	public static File createNewFile(String extension) throws IOException {
		String[] fileNameArray = fileName.split("/");
		String[] baseNameArray = fileNameArray[fileNameArray.length-1].split("\\.");
		String baseName = baseNameArray[0];
		String timeStamp = Long.toString(new Timestamp(System.currentTimeMillis()).getTime());
//		fileNameArray[fileNameArray.length-1] = timeStamp + "-" + baseName + extension;
		fileNameArray[fileNameArray.length-1] = baseName + extension;
		String newFileName = String.join("/", fileNameArray);
		return new File(newFileName);
	}
}
