"""
Scan Client Tools

Helpers for connecting to the Scan Server,
to assemble commands for a scan,
submit them to the server,
monitor the execution.

Shortcuts for 1D, 2D scans.

@author: Kay Kasemir
"""

# -------------------------------------------------------
# Path setup

# For now this is hard coded in here to allow use
# from BOY scripts
import sys
# Path to workspace, when running within IDE
workspace="/Kram/MerurialRepos/cs-studio/products/SNS/plugins/"
sys.path.append(workspace + "org.csstudio.scan/bin")
sys.path.append(workspace + "org.csstudio.scan.client/bin")

# Path to binaries, when running within exported product
install="/Users/Fred/Desktop/CSS/plugins/"
sys.path.append(install + "org.csstudio.scan")
sys.path.append(install + "org.csstudio.scan.client")

# -------------------------------------------------------

import org.csstudio.scan.client.ScanServerConnector as ScanServerConnector
from org.csstudio.scan.command import *
import time


class ScanClient(object):
    """
    Base class for a scan client
    
    Can submit scans to the server and monitor them
    """
    def __init__(self):
        # Connection to the scan server
        self.server = ScanServerConnector.connect()
        # Scan ID
        self.id = -1

    def submit(self, name, command_sequence):
        """
        Submit a CommandSequence to the server for execution
        
        @param name  Name of the scan
        @param command_sequence  CommandSequence
          
        @return Scan ID
        """
        self.id = self.server.submitScan(name, command_sequence.getCommands())
        return self.id

    def waitUntilDone(self, id=-1):
        """
        Wait until a submitted scan has finished
        
        @param id Scan ID, defaulting to the last submitted scan
        """
        if id == -1:
        	id = self.id
        while True:
            info = self.server.getScanInfo(id)
            print info
            if info.isDone():
                break;
            time.sleep(1.0)
            
    def __str__(self):
        return "Scan client, connected to %s" % self.server.getInfo()



class ScanNd(ScanClient):
    """
    N-dimensional scan that logs arbitrary number of readings
    based on nested loops.
    
    Arguments:
    
    * Optional scan name
    * One or more scan specifications: ('device', start, end[, step])
    * Names of device to log in addition to loop'ed devices
    
    Examples:
    
    # Scan 'xpos' from 1 to 10, stepping 1, automatically logging 'xpos'
    scan('My first one', ('xpos', 1, 10) )
    scan( ('xpos', 1, 10) )

    # ... also 'readback'
    scan( ('xpos', 1, 10), 'readback')
    
    # Scan 'xpos' from 1 to 10, stepping 1,
    # inside that looping 'ypos' from 1 to 5 by 0.2,
    # logging 'readback'

    scan( ('xpos', 1, 10), ('ypos', 1, 5, 0.2), 'readback')
    """
    
    def __init__(self):
        ScanClient.__init__(self)

    def _decodeScan(self, parms):
        """ Check for 
                ('device', start, end, step)
             or 
                ('device', start, end)
             for a default step size of 1
        """
        if (len(parms) == 4):
            return (parms[0], parms[1], parms[2], parms[3])
        elif (len(parms) == 3):
            return (parms[0], parms[1], parms[2], 1)
        else:
            raise Exception('Scan parameters should be (''device'', start, end, step), not %s' % str(parms)) 
    
    def __call__(self, *args):
        args = list(args)
        if len(args) > 0  and  isinstance(args[0], str):
            name = args[0]
            args.pop(0)
        else:
            name = "Scan"
        
        # Determine the (nested) scans and the devices to log
        # (doesn't really care if the log devices are listed last)
        scans = []
        log = []
        for arg in args:
            if isinstance(arg, tuple):
                scan = self._decodeScan(arg)
                scans.append(scan)
            else:
                log.append(arg)
        
        # Wrap by scans, going in reverse from inner loop
        scans.reverse()
        cmds = CommandSequence()
        for scan in scans:
            body = CommandSequence()
            # Add loop variable and log
            log.insert(0, scan[0])
            body.log(log)
            
            # Add previous cmds
            body.add(cmds)
            
            # Within loop
            cmds = CommandSequence()
            cmds.loop(scan[0], scan[1], scan[2], scan[3], body)
            
            log = []
            
        self.submit(name, cmds)
        if __name__ == '__main__':
            cmds.dump()
            self.waitUntilDone()

# Create 'scan' command
scan = ScanNd()

        
if __name__ == '__main__':
    print 'Welcome to the scan system'
    # print 'Running in %s' % os.getcwd()
    print 'Connected to %s' % scan.server.getInfo()
    
    # 'Normal' loops
    #scan('Normal 2D', ('xpos', 1, 10), ('ypos', 1, 10, 0.5), 'readback')

    # 'Reversing' inner loop
    #scan('Reversing 2D', ('xpos', 1, 10), ('ypos', 1, 10, -0.5), 'readback')
