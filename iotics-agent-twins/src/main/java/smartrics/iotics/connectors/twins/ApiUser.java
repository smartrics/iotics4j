package smartrics.iotics.connectors.twins;

import smartrics.iotics.host.IoticsApi;

/**
 * Interface for accessing the Iotics API.
 * This interface provides a method to access the main API used for interacting with Iotics services.
 */
public interface ApiUser {

    /**
     * Retrieves the Iotics API instance.
     *
     * @return the IoticsApi instance used for service interactions
     */
    IoticsApi ioticsApi();
}