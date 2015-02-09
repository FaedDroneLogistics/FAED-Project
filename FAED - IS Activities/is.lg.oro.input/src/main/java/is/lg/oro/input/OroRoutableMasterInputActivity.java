package is.lg.oro.input;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import interactivespaces.activity.impl.ros.BaseRoutableRosActivity;

/**
 * A simple Interactive Spaces Java-based activity.
 */
public class OroRoutableMasterInputActivity extends BaseRoutableRosActivity {

    @Override
    public void onActivitySetup() {
        getLog().info("Activity is.lg.oro.galaxy setup");
    }

    @Override
    public void onActivityStartup() {
        getLog().info("Activity is.lg.oro.galaxy startup");
    }

    @Override
    public void onActivityPostStartup() {
        getLog().info("Activity is.lg.oro.galaxy post startup");
    }

    @Override
    public void onActivityActivate() {
        getLog().info("Activity is.lg.oro.galaxy activate");
    }

    @Override
    public void onActivityDeactivate() {
        getLog().info("Activity is.lg.oro.galaxy deactivate");
    }

    @Override
    public void onActivityPreShutdown() {
        getLog().info("Activity is.lg.oro.galaxy pre shutdown");
    }

    @Override
    public void onActivityShutdown() {
        getLog().info("Activity is.lg.oro.galaxy shutdown");
    }

    @Override
    public void onActivityCleanup() {
        getLog().info("Activity is.lg.oro.galaxy cleanup");
    }
    
    @Override
	public void onNewInputJson(String channelName, Map<String, Object> message) {
		// There is only 1 channel for this activity, so don't bother checking
		// which one it is.
		getLog().info("YEAAAHHH message on input channel " + channelName);
        getLog().info(message);
        
        message.toString();
        String key = "message";
   		String value = (String) message.get(key);
   		
   		try{
       		File file= new File("/tmp", "query.txt");
             	file.createNewFile();

             	FileWriter fw = new FileWriter(file.getAbsoluteFile());
              	BufferedWriter bw = new BufferedWriter(fw);
              	bw.write(value);
              	bw.close();           
       		}
       	catch(IOException e){
       		System.err.println("Impossible to create file " + e);
       	}
   		
        
		
	}
    
}
