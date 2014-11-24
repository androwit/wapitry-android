package fr.fitoussoft.wapisdk.services;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import fr.fitoussoft.wapisdk.helpers.Log;
import fr.fitoussoft.wapisdk.helpers.WAPIClient;

/**
 * The Connection object to the WAPI service.
 */
public abstract class WAPIServiceConnection implements ServiceConnection {

    protected WAPIClient client;
    protected Activity origin;

    /**
     * Initializes a new instance of WAPIServiceConnection.
     *
     * @param origin the origin activity.
     */
    public WAPIServiceConnection(Activity origin) {
        this.origin = origin;
    }

    /**
     * Gets the WAPI client.
     *
     * @return the WAPI client.
     * @throws NoClientCreatedException
     */
    public WAPIClient getClient() throws NoClientCreatedException {
        if (client == null) {
            throw new NoClientCreatedException();
        }

        return client;
    }

    /**
     * Gets origin activity.
     *
     * @return the origin activity.
     */
    public Activity getOrigin() {
        return origin;
    }

    /**
     * Binds origin to WAPI service.
     */
    public void bindService() {
        Intent intent = new Intent(origin, WAPIService.class);
        origin.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    /**
     * Unbinds the service from origin.
     */
    public void unbindService() {
        origin.unbindService(this);
    }

    /**
     * Disconnects the client.
     */
    public void disconnect() {
        if (client == null) {
            return;
        }

        client.disconnect(origin);
    }

    /**
     * Stores the binder into client field.
     * Authenticates if it's needed.
     * Launch onAuthenticated if the client is authenticated.
     *
     * @param componentName The component name.
     * @param iBinder       The WAPI client.
     */
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.d("onServiceConnected.");
        client = (WAPIClient) iBinder;
        if (client.authenticateIfNeeded(origin)) {
            return;
        }

        onAuthenticated(client);
    }

    /**
     * Triggered when the client is authenticated.
     *
     * @param client the WAPI client.
     */
    protected abstract void onAuthenticated(WAPIClient client);

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.e("onServiceDisconnected");
    }

    /**
     * No client created yet exception.
     */
    public final class NoClientCreatedException extends Exception {
        public NoClientCreatedException() {
            super("WAPI client is null. You have to bind activity to the service by call method \"bindService\" before.");
        }
    }

}
