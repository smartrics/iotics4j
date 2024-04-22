package smartrics.iotics.host;

/**
 * Contains constant URI values used across the application, particularly for IOTICS twins creatoion.
 */
public interface UriConstants {

    // IOTICS-specific URIs
    /**
     * URI for specifying the property that defines a host allow list in IOTICS.
     */
    String IOTICS_PUBLIC_ALLOW_LIST_PROP = "http://data.iotics.com/public#hostAllowList";

    /**
     * URI that represents a value indicating all hosts are allowed; used in conjunction with
     * {@link #IOTICS_PUBLIC_ALLOW_LIST_PROP}.
     */
    String IOTICS_PUBLIC_ALLOW_ALL_VALUE = "http://data.iotics.com/public#allHosts";

    // Semantic web (RDF, RDFS, OWL) URIs
    /**
     * Base URI for RDF schema vocabulary.
     */
    String ON_RDFS = "http://www.w3.org/2000/01/rdf-schema#";

    /**
     * Base URI for RDF syntax namespace.
     */
    String ON_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /**
     * Base URI for OWL (Web Ontology Language).
     */
    String ON_OWL = "http://www.w3.org/2002/07/owl#";

    // Composed URIs for properties and types using the base URIs
    /**
     * URI for the RDF 'type' property.
     */
    String ON_RDF_TYPE_PROP = ON_RDF + "type";

    /**
     * URI for the RDFS 'label' property, typically used to provide a human-readable version of a resource's name.
     */
    String ON_RDFS_LABEL_PROP = ON_RDFS + "label";

    /**
     * URI for the RDFS 'comment' property, used to provide a description of a resource.
     */
    String ON_RDFS_COMMENT_PROP = ON_RDFS + "comment";

    /**
     * URI representing an OWL Class type.
     */
    String ON_OWL_CLASS_VALUE = ON_OWL + "Class";

    /**
     * URI representing an RDFS Class type, generally used to define classes in RDF Schema.
     */
    String ON_RDFS_CLASS_VALUE = ON_RDFS + "Class";

    // Custom search URIs specific to IOTICS data
    /**
     * URI for a custom property used to specify a search query in IOTICS.
     */
    String IOTICS_CUSTOM_SEARCH_VALUE_PROP = "http://data.iotics.com/ont/searchQuery";

    /**
     * URI for a custom property used to specify the type of a search in IOTICS.
     */
    String IOTICS_CUSTOM_SEARCH_TYPE_PROP = "http://data.iotics.com/ont/searchType";
}

