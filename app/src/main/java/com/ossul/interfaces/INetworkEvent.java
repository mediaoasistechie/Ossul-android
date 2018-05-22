package com.ossul.interfaces;

/**
 * @author Rajan Tiwari
 */
public interface INetworkEvent {
     void onNetworkCallInitiated(String service);

    void onNetworkCallCompleted(String service, String response);

    void onNetworkCallError(String service, String errorMessage);
}
