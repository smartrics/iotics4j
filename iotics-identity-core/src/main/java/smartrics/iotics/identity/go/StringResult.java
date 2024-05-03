package smartrics.iotics.identity.go;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * A structure representing a result containing a value and an error message.
 */
public class StringResult extends Structure implements Structure.ByValue {
    /**
     * The value contained in the result.
     */
    public String value; // must be public for jna to work

    /**
     * The error message, if any.
     */
    public String err;

    /**
     * Constructs an empty StringResult.
     */
    public StringResult() {
    }

    /**
     * Constructs a StringResult with the specified value and error message.
     *
     * @param value The value to be set.
     * @param err   The error message to be set.
     */
    public StringResult(String value, String err) {
        this.value = value;
        this.err = err;
    }

    /**
     * Specifies the order of fields for structure layout.
     *
     * @return A list containing the field names in the order they should be arranged.
     */
    protected List<String> getFieldOrder() {
        return Arrays.asList("value", "err");
    }

    /**
     * Returns a string representation of the StringResult object.
     *
     * @return A string representation containing the value and error message.
     */
    @Override
    public String toString() {
        return "StringResult{" +
                "value='" + value + '\'' +
                ", err='" + err + '\'' +
                '}';
    }
}
