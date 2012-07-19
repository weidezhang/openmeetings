package net.java.sip.communicator.plugin.openmeetings;

// import net.java.sip.communicator.impl.protocol.zeroconf.MessageZeroconfImpl;
import net.java.sip.communicator.service.configuration.ConfigurationService;
import net.java.sip.communicator.util.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.Locale;

public class OpenmeetingsConfigManager
{
    Logger logger = Logger.getLogger(OpenmeetingsPluginActivator.class);

    private String server;

    private String protoPrefix;

    private String omUriContext;

    private String login;

    private String password;

    private OpenmeetingsPluginSoapClient soapClient;

    private static BundleContext bundleContext;

    private static ConfigurationService configurationService = null;

    private static OpenmeetingsConfigManager instance;

    private EncryptionEngine encryptionEngine;

    private OpenmeetingsConfigManager()
    {

        super();
        soapClient = new OpenmeetingsPluginSoapClient();
        encryptionEngine = new EncryptionEngine();
    }

    public static OpenmeetingsConfigManager getInstance()
    {
        if (instance == null)
        {
            instance = new OpenmeetingsConfigManager();
        }
        return instance;
    }

    public String createInvitationUrl(String hash) throws Exception
    {
        final String url =
            "http://" + getServer() + "/openmeetings/?invitationHash=" + hash;
        System.out.println("INVITATION URL = " + url);
        return url;
    }

    public String getInvitationUrl(String displayedName) throws Exception
    {
        String protoPrefix = getProtoPrefix();
        String server = getServer();
        String uriContext = getOmUriContext();
        soapClient.setServerUrl(protoPrefix + server + uriContext);

        String invitationHash = null;

        try
        {
            invitationHash =
                soapClient.getInvitationHash(getLogin(), getPassword(),
                    displayedName);
        }
        catch (Exception e)
        {
            logger.error(e);
        }

        if (invitationHash == null)
            return null;

        String invitationUrl = createInvitationUrl(invitationHash);
        return addLanguageTag(invitationUrl);
    }

    private String addLanguageTag(String invitationUrl) {
        String language = Locale.getDefault().getLanguage();
        int id = 1;
        if ("ar".equals(language)) {
            id = 14;
        } else if ("bg".equals(language)) {
            id = 30;
        } else if ("cs".equals(language)) {
            id = 22;
        } else if ("de".equals(language)) {
            id = 2;
        } else if ("el".equals(language)) {
            id = 26;
        } else if ("es".equals(language)) {
            id = 8;
        } else if ("fr".equals(language)) {
            id = 4;
        } else if ("id".equals(language)) {
            id = 16;
        } else if ("it".equals(language)) {
            id = 5;
        } else if ("nl".equals(language)) {
            id = 27;
        } else if ("pl".equals(language)) {
            id = 25;
        } else if ("pt".equals(language)) {
            id = 6;
        } else if ("ro".equals(language)) {
            id = 1;
        } else if ("ru".equals(language)) {
            id = 9;
        } else if ("si".equals(language)) {
            id = 1;
        } else if ("sq".equals(language)) {
            id = 1;
        } else if ("tr".equals(language)) {
            id = 18;
        } else if ("zh".equals(language)) {
            id = 11;
        }
        return invitationUrl + "&language=" + id;
    }

    public static ConfigurationService getConfigurationService()
    {
        if (configurationService == null)
        {
            ServiceReference confReference =
                bundleContext.getServiceReference(ConfigurationService.class
                    .getName());
            configurationService =
                (ConfigurationService) bundleContext.getService(confReference);
        }
        return configurationService;
    }

    public void setServer(String server)
    {
        this.server = server;
        getConfigurationService().setProperty("plugin.openmeetings.SERVER",
            server);
    }

    public String getServer()
    {
        String value =
            (String) getConfigurationService().getProperty(
                "plugin.openmeetings.SERVER");
        if (null == value)
        {
            value = "";
        }
        server = value;
        return server;
    }

    public void setProtoPrefix(String protoPrefix)
    {
        this.protoPrefix = protoPrefix;
        getConfigurationService().setProperty(
            "plugin.openmeetings.PROTOCOL_PREFIX", protoPrefix);
    }

    public String getProtoPrefix()
    {
        String value =
            (String) getConfigurationService().getProperty(
                "plugin.openmeetings.PROTOCOL_PREFIX");
        if (null == value)
        {
            value = "";
        }
        protoPrefix = value;
        return protoPrefix;
    }

    public void setOmUriContext(String omUriContext)
    {
        this.omUriContext = omUriContext;
        getConfigurationService().setProperty(
            "plugin.openmeetings.OM_URI_CONTEXT", omUriContext);
    }

    public String getOmUriContext()
    {
        String value =
            (String) getConfigurationService().getProperty(
                "plugin.openmeetings.OM_URI_CONTEXT");
        if (null == value)
        {
            value = "";
        }
        if (!value.endsWith("/"))
        {
            value += "/";
        }
        omUriContext = value;
        return omUriContext;
    }

    public void setLogin(String login)
    {
        this.login = login;
        getConfigurationService().setProperty("plugin.openmeetings.LOGIN",
            login);
    }

    public String getLogin()
    {
        login = (String) getConfigurationService().getProperty("plugin.openmeetings.LOGIN");
        return login;
    }

    public void setPassword(String password) throws Exception
    {
        this.password = password;
        if (password == null)
            return;
        String encrypted = encryptionEngine.encrypt(password);
        getConfigurationService().setProperty(
            "plugin.openmeetings.ENCRYPTED_PASSWORD", encrypted);
    }

    public String getPassword() throws Exception
    {
        String value =
            (String) getConfigurationService().getProperty(
                "plugin.openmeetings.ENCRYPTED_PASSWORD");
        if (value == null)
            return null;
        password = encryptionEngine.decrypt(value);
        return password;
    }

    public void setContext(BundleContext bc)
    {
        bundleContext = bc;
    }
}
