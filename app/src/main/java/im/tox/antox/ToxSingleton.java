package im.tox.antox;

import android.nfc.Tag;
import android.util.Log;

import java.net.UnknownHostException;

import im.tox.jtoxcore.JTox;
import im.tox.jtoxcore.ToxException;
import im.tox.jtoxcore.callbacks.CallbackHandler;

/**
 * Created by soft on 01/03/14.
 */
public class ToxSingleton {

    private static final String TAG = "im.tox.antox.ToxSingleton";
    public JTox jTox;
    private AntoxFriendList antoxFriendList;
    private CallbackHandler callbackHandler;

    private static volatile ToxSingleton instance = null;

    private ToxSingleton() {
        antoxFriendList = new AntoxFriendList();
        callbackHandler = new CallbackHandler(antoxFriendList);
        try {
            ToxDataFile dataFile = new ToxDataFile();

            /* Choose appropriate constructor depending on if data file exists */
            if(!dataFile.doesFileExist()) {
                Log.d(TAG, "Data file not found");
                jTox = new JTox(antoxFriendList, callbackHandler);
            } else {
                Log.d(TAG, "Data file has been found");
                jTox = new JTox(dataFile.loadFile(), antoxFriendList, callbackHandler);
            }

            jTox.setName(UserDetails.username);
            jTox.setStatusMessage(UserDetails.note);
            jTox.setUserStatus(UserDetails.status);
            jTox.bootstrap(DhtNode.ipv4, Integer.parseInt(DhtNode.port), DhtNode.key);

            /* Save data file */
            Log.d(TAG, "Saving data file");
            dataFile.saveFile(jTox.save());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (ToxException e) {
            e.printStackTrace();
        }
    }

    public static ToxSingleton getInstance() {
        /* Double-checked locking */
        if(instance == null) {
            synchronized (ToxSingleton.class) {
                if(instance == null) {
                    instance = new ToxSingleton();
                }
            }
        }

        return instance;
    }
}
