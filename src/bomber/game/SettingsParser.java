package bomber.game;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Takes care of saving the settings of the game using XML
 *
 * @author Alexandru Rosu
 */
public class SettingsParser
{

    private static Document document;

    /**
     * Private constructor preventing instantiation, as the class should only be used statically
     */
    private SettingsParser() {}

    /**
     * Initialises the settings manager
     */
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
            document = builder.parse(new File(getPath()));
            document.getDocumentElement().normalize();
        } catch (SAXException e)
        {
            System.err.println("Error in parsing settings.xml.");
            e.printStackTrace();
        } catch (IOException e)
        {
            System.out.println("Could not read settings.xml. Initialising default settings");
            initialiseXML(builder);
        }

    }

    /**
     * Writes the default settings into the xml file defined in Constants
     *
     * @param builder The builder used to create the document
     */
    private static void initialiseXML(DocumentBuilder builder)
    {
        document = builder.newDocument();

        // root element
        Element root = document.createElement("settings");
        document.appendChild(root);

        // name
        Element name = document.createElement("name");
        name.appendChild(document.createTextNode(Constants.DEFAULT_PLAYER_NAME));
        root.appendChild(name);

        // audio settings
        Element audio = document.createElement("audio");
        Element musicVolume = document.createElement("musicVolume");
        musicVolume.appendChild(document.createTextNode(String.valueOf(Constants.DEFAULT_VOLUME)));
        audio.appendChild(musicVolume);
        Element effectsVolume = document.createElement("effectsVolume");
        effectsVolume.appendChild(document.createTextNode(String.valueOf(Constants.DEFAULT_VOLUME)));
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
        serverName.appendChild(document.createTextNode(Constants.DEFAULT_SERVER_NAME));
        server.appendChild(serverName);
        Element serverIp = document.createElement("ip");
        serverIp.appendChild(document.createTextNode(Constants.DEFAULT_SERVER_IP));
        server.appendChild(serverIp);
        Element serverPort = document.createElement("port");
        serverPort.appendChild(document.createTextNode(String.valueOf(Constants.DEFAULT_SERVER_PORT)));
        server.appendChild(serverPort);
        servers.appendChild(server);
        root.appendChild(servers);

        // write the content into xml file
        storeSettings();
    }

    /**
     * Writes the settings to the xml file defined in Constants
     */
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

        String path = getPath();

        StreamResult result = new StreamResult(new File(path));
        try
        {
            transformer.transform(source, result);
        } catch (TransformerException e1)
        {
            e1.printStackTrace();
        }
    }

    /**
     * Gets the path of settings.xml
     *
     * @return The absolute path
     */
    private static String getPath()
    {
        String path = null;
        final Class<?> referenceClass = Main.class;
        final URL url =
                referenceClass.getProtectionDomain().getCodeSource().getLocation();
        try{
            final File jarPath = new File(url.toURI()).getParentFile();
            path = jarPath + "/settings.xml";
        } catch(final URISyntaxException e){
            e.printStackTrace();
        }
        return path;
    }

    /**
     * Gets the text of a tag from the document
     *
     * @param tag The tag
     * @return The text inside the tag
     */
    private static String getTagText(String tag)
    {
        return document.getElementsByTagName(tag).item(0).getTextContent();
    }

    /**
     * Sets the text of a tag to a given value
     *
     * @param tag The tag
     * @param text The text to be put inside the tag
     */
    private static void setTagText(String tag, String text)
    {
        document.getElementsByTagName(tag).item(0).setTextContent(text);
    }

    /**
     * Sets a new player name
     *
     * @param name The new name of the player
     */
    public static void setPlayerName(String name)
    {
        setTagText("name", name);
    }

    /**
     * Sets a new music volume
     *
     * @param volume The new volume of the music
     */
    public static void setMusicVolume(float volume)
    {
        setTagText("musicVolume", String.valueOf(volume));
    }

    /**
     * Sets a new sound effects volume
     *
     * @param volume The new volume of the sound effects
     */
    public static void setEffectsVolume(float volume)
    {
        setTagText("effectsVolume", String.valueOf(volume));
    }

    /**
     * Sets whether the interface should highlight the tutorial
     *
     * @param show True if the interface should highlight the tutorial, false if not
     */
    public static void setShowTutorial(boolean show)
    {
        setTagText("showTutorial", String.valueOf(show));
    }

    /**
     * Sets the details of the default server
     *
     * @param ip The ip of the server
     * @param port The port of the server
     */
    public static void setServer(String ip, String port)
    {
        setTagText("ip", ip);
        setTagText("port", port);
    }

    /**
     * Gets the player name stored in the settings
     *
     * @return The name of the player
     */
    public static String getPlayerName()
    {
        return getTagText("name");
    }

    /**
     * Gets the music volume stored in the settings
     *
     * @return The volume of the music
     */
    public static float getMusicVolume()
    {
        return Float.parseFloat(getTagText("musicVolume"));
    }

    /**
     * Gets the sound effects volume stored in the settings
     *
     * @return The volume of the sound effects
     */
    public static float getEffectsVolume()
    {
        return Float.parseFloat(getTagText("effectsVolume"));
    }

    /**
     * Gets whether the interface should highlight the tutorial
     *
     * @return True if the interface should highlight the tutorial, false if not
     */
    public static boolean getShowTutorial()
    {
        return Boolean.parseBoolean(getTagText("showTutorial"));
    }

    /**
     * Gets the server ip stored in the settings
     *
     * @return The ip of the server
     */
    public static String getServerIp()
    {
        return getTagText("ip");
    }

    /**
     * Gets the server port stored in the settings
     *
     * @return The port of the server
     */
    public static String getServerPort()
    {
        return getTagText("port");
    }

    /**
     * Error handler for the DocumentBuilder
     */
    private static class SettingsErrorHandler implements ErrorHandler
    {

        /**
         * Gets the details of a SAX parse exception
         *
         * @param spe The parse exception
         * @return The details
         */
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

        /**
         * Prints a warning
         *
         * @param spe The exception that leads to the warning
         */
        public void warning(SAXParseException spe)
        {
            System.err.println("Settings parser warning: " + getParseExceptionInfo(spe));
        }

        /**
         * Signals an error by throwing an exception
         *
         * @param spe The exception that leads to the error message
         * @throws SAXException Thrown to signal the error. Has the details of the error.
         */
        public void error(SAXParseException spe) throws SAXException
        {
            String message = "Settings parser error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }

        /**
         * Signals a fatal error by throwing an exception
         *
         * @param spe The exception that leads to the error message
         * @throws SAXException Thrown to signal the error. Has the details of the error.
         */
        public void fatalError(SAXParseException spe) throws SAXException
        {
            String message = "Settings parser fatal Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }
    }

}
