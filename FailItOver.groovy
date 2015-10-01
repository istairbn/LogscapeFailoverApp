#!/user/bin/end groovy
import groovy.lang.Binding;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;

Properties properties = new Properties()
def props = args.length > 0 ? ".\\lib\\" + args[0] : ".\\lib\\config.properties"
def propertiesFile = new File(props)


if (propertiesFile.exists()){
    propertiesFile.withInputStream{
        properties.load(it)
        }
    }

else
    {
    log("No Properties File Present")
    println System.getProperty("user.dir")
    return 0
    }

def log(line) {
    Now = new Date().format("yyyy-MM-dd HH:mm:ss")
    println "$Now, $line"
}

def managerHostName = properties.managerHostName
def managerPort = properties.managerPort
def webPort = properties.webPort
def path = "../../etc/"

    log( "Proceeding with Network connectivity check.")
    String[] params = new String[3];
    params[0] = managerHostName
    params[1] = managerPort
    params[2] = "11000"

    def roots = ["."] as String[]
    def gse = new GroovyScriptEngine(roots);
    def binding = new Binding();
    binding.setVariable("args",params);
    gse.run("./lib/networkCheck.groovy" , binding);

    if( ! binding['isSuccess'] ){
        log("NetworkCheck:FAILED")
        log("Ensure Management is running, and firewalls allow for network connectivity in both directions [ThisHost:"+params[2]+"<->Manager:11000]")
        System.exit(0)
    } 
    
    else {
        log ("NetworkCheck:SUCCESSFUL")
    }

log( "Management:" +  managerHostName + " ManagerPort:" + managerPort + "WebPort:" +  webPort)


log("Creating back-up of setup.conf")

def FileReplacer(String path,String file,String managerHostName,String managerPort,String webPort){
    confFile = path + file
    bakFile = path + file + ".bak"

    if (!new File(confFile).exists()) {
        log("Given Path:" + path + " -> " + new File(path).getAbsolutePath())
        log("ERROR: Cannot find file:" + new File(confFile).getAbsolutePath())
        System.exit(1);
    }
    new File(confFile).renameTo(new File(bakFile))
    def lsHome = new File ( new File("..\\..").getCanonicalPath() ).getCanonicalPath() .replace("\\","\\\\");
    log("logscape_home=" + lsHome)

    // now filter the contents
    new File(confFile).withWriter { targetFile ->
        new File(bakFile).eachLine {
            line -> targetFile.writeLine(
                    line.
                            replaceFirst(".*MANAGEMENT_HOST\" value=.*", "\t\t<add key=\"MANAGEMENT_HOST\" value=\"" + managerHostName + "\"/>").
                            replaceFirst(".*MANAGEMENT_PORT\" value=.*", "\t\t<add key=\"MANAGEMENT_PORT\" value=\"" + managerPort + "\"/>").
                            replaceFirst(".*vso.base.port\" value=.*", "\t\t<add key=\"vso.base.port\" value=\"" + managerPort + "\"/>").
                            replaceFirst(".*web.app.port.*value=.*",     "\t\t<add key=\"web.app.port\" value=\"" + webPort + "\"/>").
                            replaceFirst("param04=stcp://.*:.*",     "param04=stcp://" + managerHostName + ":" + managerPort).
                            replaceFirst("param04=stcp://.*:.*",     "param04=stcp://" + managerHostName + ":" + managerPort)
            )
        }
    }
}

        FileReplacer(path,"setup.conf",managerHostName,managerPort,webPort)

log("Creating back-up of logscape-service-x64.ini")

FileReplacer(path,"logscape-service-x64.ini",managerHostName,managerPort,webPort)

log("Creating back-up of logscape-service-x86.ini")

FileReplacer(path,"logscape-service-x86.ini",managerHostName,managerPort,webPort)

log("Failover:Complete")
