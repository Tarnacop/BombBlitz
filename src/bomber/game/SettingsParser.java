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

    private Document document;

    public SettingsParser()
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

    private void initialiseXML(DocumentBuilder builder)
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

    public void storeSettings()
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

    private String getTagText(String tag)
    {
        return document.getElementsByTagName(tag).item(0).getTextContent();
    }

    private String getTagText(Element element, String tag)
    {
        return element.getElementsByTagName(tag).item(0).getTextContent();
    }

    private void setTagText(String tag, String text)
    {
        document.getElementsByTagName(tag).item(0).setTextContent(text);
    }

    public void setPlayerName(String name)
    {
        setTagText("name", name);
    }

    public void setMusicVolume(float volume)
    {
        setTagText("musicVolume", String.valueOf(volume));
    }

    public void setEffectsVolume(float volume)
    {
        setTagText("effectsVolume", String.valueOf(volume));
    }

    public void setShowTutorial(boolean show)
    {
        setTagText("showTutorial", String.valueOf(show));
    }

    // TODO: server add/remove/get

    public String getPlayerName()
    {
        return getTagText("name");
    }

    public float getMusicVolume()
    {
        return Float.parseFloat(getTagText("musicVolume"));
    }

    public float getEffectsVolume()
    {
        return Float.parseFloat(getTagText("effectsVolume"));
    }

    public boolean getShowTutorial()
    {
        return Boolean.parseBoolean(getTagText("showTutorial"));
    }

    private void printSettings()
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

        SettingsParser parser = new SettingsParser();

        parser.printSettings();
    }

}
