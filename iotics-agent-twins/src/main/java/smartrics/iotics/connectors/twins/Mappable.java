package smartrics.iotics.connectors.twins;

/**
 * Interface providing access to a Mapper object.
 * This interface is used in contexts where an object's state needs to be transformed into API request objects,
 * facilitating operations such as upserts or data sharing.
 */
public interface Mappable {
    /**
     * Retrieves the Mapper associated with this object.
     *
     * @return the Mapper responsible for converting object states into request forms.
     */
    Mapper getMapper();
}
