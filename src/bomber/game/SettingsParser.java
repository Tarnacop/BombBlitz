package bomber.game;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;


/**
 * Created by Alexandru Rosu on 05/03/2017.
 */
public class SettingsParser
{

    private static Document document;

    // preventing instantiation, as the class should only be used statically
    private SettingsParser() {}

    public static void init()
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try
        {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }

        builder.setErrorHandler(new SettingsErrorHandler());

        try
        {
            document = builder.parse(new File(Constants.settingsXMLPath));
            document.getDocumentElement().normalize();
        } catch (SAXException e)
        {
            System.err.println("Error in parsing settings.xml."); // TODO Maybe reinitialise it
            e.printStackTrace();
        } catch (IOException e)
        {
            System.out.println("Could not read settings.xml. Initialising default settings");
            initialiseXML(builder);
        }

    }

    private static void initialiseXML(DocumentBuilder builder)
    {
        document = builder.newDocument();

        // root element
        Element root = document.createElement("settings");
        document.appendChild(root);

        // name
        Element name = document.createElement("name");
        name.appendChild(document.createTextNode(Constants.defaultPlayerName));
        root.appendChild(name);

        // audio settings
        Element audio = document.createElement("audio");
        Element musicVolume = document.createElement("musicVolume");
        musicVolume.appendChild(document.createTextNode(String.valueOf(Constants.defaultVolume)));
        audio.appendChild(musicVolume);
        Element effectsVolume = document.createElement("effectsVolume");
        effectsVolume.appendChild(document.createTextNode(String.valueOf(Constants.defaultVolume)));
        audio.appendChild(effectsVolume);
        root.appendChild(audio);

        // show tutorial
        Element showTutorial = document.createElement("showTutorial");
        showTutorial.appendChild(document.createTextNode("true"));
        root.appendChild(showTutorial);

        // servers
        Element servers = document.createElement("servers");
        Element server = document.createElement("server");
        Element serverName = document.createElement("name");
        serverName.appendChild(document.createTextNode(Constants.defaultServerName));
        server.appendChild(serverName);
        Element serverIp = document.createElement("ip");
        serverIp.appendChild(document.createTextNode(Constants.defaultServerIp));
        server.appendChild(serverIp);
        Element serverPort = document.createElement("port");
        serverPort.appendChild(document.createTextNode(String.valueOf(Constants.defaultServerPort)));
        server.appendChild(serverPort);
        servers.appendChild(server);
        root.appendChild(servers);


        // write the content into xml file
        storeSettings();
    }

    public static void storeSettings()
    {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try
        {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e1)
        {
            e1.printStackTrace();
        }
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(Constants.settingsXMLPath));
        try
        {
            transformer.transform(source, result);
        } catch (TransformerException e1)
        {
            e1.printStackTrace();
        }
    }

    private static String getTagText(String tag)
    {
        return document.getElementsByTagName(tag).item(0).getTextContent();
    }

    private static String getTagText(Element element, String tag)
    {
        return element.getElementsByTagName(tag).item(0).getTextContent();
    }

    private static void setTagText(String tag, String text)
    {
        document.getElementsByTagName(tag).item(0).setTextContent(text);
    }

    public static void setPlayerName(String name)
    {
        setTagText("name", name);
    }

    public static void setMusicVolume(float volume)
    {
        setTagText("musicVolume", String.valueOf(volume));
    }

    public static void setEffectsVolume(float volume)
    {
        setTagText("effectsVolume", String.valueOf(volume));
    }

    public static void setShowTutorial(boolean show)
    {
        setTagText("showTutorial", String.valueOf(show));
    }

    // TODO: server add/remove/get

    public static String getPlayerName()
    {
        return getTagText("name");
    }

    public static float getMusicVolume()
    {
        return Float.parseFloat(getTagText("musicVolume"));
    }

    public static float getEffectsVolume()
    {
        return Float.parseFloat(getTagText("effectsVolume"));
    }

    public static boolean getShowTutorial()
    {
        return Boolean.parseBoolean(getTagText("showTutorial"));
    }

    private static void printSettings()
    {
        System.out.println("\nSettings from XML:\n");
        System.out.println("Name: " + getTagText("name"));
        System.out.println("Music volume: " + getTagText("musicVolume"));
        System.out.println("Effects volume: " + getTagText("effectsVolume"));
        System.out.println("Show tutorial: " + Boolean.valueOf(getTagText("showTutorial")));

        System.out.println("Servers:");
        NodeList servers = document.getElementsByTagName("server");
        for (int i = 0; i < servers.getLength(); i++)
        {
            Element server = (Element) servers.item(i);
            System.out.println(getTagText(server, "name") + " - " + getTagText(server, "ip")
                    + ":" + getTagText(server, "port"));
        }
    }

    private static class SettingsErrorHandler implements ErrorHandler
    {

        private String getParseExceptionInfo(SAXParseException spe)
        {
            String systemId = spe.getSystemId();
            if (systemId == null)
            {
                systemId = "null";
            }

            String info = "URI=" + systemId + " Line=" + spe.getLineNumber() +
                    ": " + spe.getMessage();
            return info;
        }

        public void warning(SAXParseException spe) throws SAXException
        {
            System.err.println("Settings parser warning: " + getParseExceptionInfo(spe));
        }

        public void error(SAXParseException spe) throws SAXException
        {
            String message = "Settings parser error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }

        public void fatalError(SAXParseException spe) throws SAXException
        {
            String message = "Settings parser fatal Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }
    }

    public static void main(String[] args)
    {

        // delete settings.xml to check automatic initialisation
        File file = new File(Constants.settingsXMLPath);
        file.delete();
        System.out.println("Deleted settings.xml");

        SettingsParser.init();

        SettingsParser.printSettings();

        SettingsParser.setPlayerName("Test");
        System.out.println(SettingsParser.getPlayerName());

        SettingsParser.storeSettings();
        SettingsParser.init();

        System.out.println(SettingsParser.getPlayerName());
    }

}
