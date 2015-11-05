###########################################################################
# Logscape Retargeting App 1.0
# Author: Ben Newton
# Created: 30/09/2015
# Purpose: To provide a soft failover option for Logscape 
###########################################################################

This App is designed to provide users with a non-active/active Failover option with Logscape. 

What this app WILL do: Re-target Agents to an alternative Manager - however, the services will need restarting

What the provided scripts CAN do: RestartService.ps1 will (if given a server list) try to restart all Logscape agents it has the power to do.

What this app will NOT do: Provide a failover manager, backup environments etc. 

To use this App, you'll need to download this and put it in a zip file labelled LogscapeFailoverApp-1.0.zip

The Failover Process therefore is:

1. Set up alternative Manager. You have three options here:
 a) Create a clean Manager (Fresh Failover)
 b) Take a backup of an existing Manager and Import the XML to a clean Manager (XML Failover)
 c) Create a Failover Manager, allow it to sync with your current environment, then recommission it as a Manager (Previously Active Failover)

2. Set the config.propertiesin the lib folder to the new Manager host name. If you need to edit the ports from the defaults, you can. 

3. Deploy the app on all Forwarders you wish to move. They should log in with success messages.

4. Restart the services on those hosts. For Windows hosts, the RestartService.ps1 file takes a csv file which will do this for you if you have the privileges. Use a file in the style of serverlist.csv to give it a list of hosts to restart. 

