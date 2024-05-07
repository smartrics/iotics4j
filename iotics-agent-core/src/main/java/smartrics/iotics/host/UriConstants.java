package smartrics.iotics.host;

/**
 * Contains constant URI values used across the application, particularly for IOTICS twins creatoion.
 */
public interface UriConstants {

    interface IOTICSProperties {

        String prefix = "http://data.iotics.com/public#";

        String HostAllowListName = prefix + "hostAllowList";

        enum HostAllowListValues {
            /**
             * URI that represents a value indicating all hosts are allowed; used in conjunction with
             */
            ALL(prefix + "all"),
            /**
             * URI that represents a value indicating no hosts are allowed; used in conjunction with
             */
            NONE(prefix + "none");

            private final String value;

            @Override
            public String toString() {
                return value;
            }

            HostAllowListValues(String v) {
                this.value = v;
            }
        }
    }

    interface RDFSProperty {
        /**
         * Base URI for RDF schema vocabulary.
         */
        String prefix = "http://www.w3.org/2000/01/rdf-schema#";

        /**
         * URI for the RDFS 'label' property, typically used to provide a human-readable version of a resource's name.
         */
        String Label = prefix + "label";

        /**
         * URI for the RDFS 'comment' property, used to provide a description of a resource.
         */
        String Comment = prefix + "comment";

        /**
         * URI representing an RDFS Class type, generally used to define classes in RDF Schema.
         */
        String Class = prefix + "Class";


    }
    interface RDFProperty {
        /**
         * Base URI for RDF schema vocabulary.
         */
        String prefix = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

        /**
         * URI for the RDF 'type' property.
         */
        String Type = prefix + "type";

    }

    interface OWLProperty {
        /**
         * Base URI for OWL schema vocabulary.
         */
        String prefix = "http://www.w3.org/2002/07/owl#";

        /**
         * URI representing an OWL Class type.
         */
        String Class = prefix + "Class";
    }
}

